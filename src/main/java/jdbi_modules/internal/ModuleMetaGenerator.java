package jdbi_modules.internal;

import jdbi_modules.Module;
import jdbi_modules.ModuleMeta;
import jdbi_modules.SqlGenerator;
import jdbi_modules.Store;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.generic.GenericType;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.Query;
import org.jdbi.v3.core.statement.StatementContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @param <Type>      The Type this module maps to
 * @param <KeyType>   The Type of the Key, to access this modules submodules
 * @param <SqlType>   The SqlType of the SqlGenerator
 * @param <Generator> The Type of the SqlGenerator
 * @since 14.04.2018
 */
public class ModuleMetaGenerator<Type, KeyType, SqlType extends jdbi_modules.SqlType, Generator extends SqlGenerator<SqlType>> {
    private final String modulePrefix;
    private final Module<Type, KeyType, SqlType, Generator> prototype;
    private final Map<Class<?>, Object> baseStore;
    private final Map<java.lang.reflect.Type, ColumnMapper<?>> commonColumnMapperMap;
    private final Map<KeyType, ModuleMetaGenerator<Object, Object, SqlType, SqlGenerator<SqlType>>> submodules;

    /**
     * @param prefixGenerator       a prefix generator
     * @param prototype             the module
     * @param store                 the public store
     * @param commonColumnMapperMap the common shared column mapper map
     */
    @SuppressWarnings("unchecked")
    public ModuleMetaGenerator(final Iterator<String> prefixGenerator,
                               final Module<Type, KeyType, SqlType, Generator> prototype,
                               final Map<Class<?>, Object> store,
                               final Map<java.lang.reflect.Type, ColumnMapper<?>> commonColumnMapperMap) {
        this.modulePrefix = prefixGenerator.next();
        this.prototype = prototype;
        this.baseStore = store;
        this.commonColumnMapperMap = commonColumnMapperMap;
        this.submodules = prototype.submodules().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new ModuleMetaGenerator<>(prefixGenerator, e.getValue(), store, commonColumnMapperMap)));
    }

    private SqlType genSql(final Set<Consumer<Query>> queryModifierApplyer) {
        final Iterator<String> prefixGenerator = new PrefixGenerator("m");
        final Stack<String> prefixStack = new Stack<>();
        prefixStack.push(this.modulePrefix);
        SqlType sql = prototype.sqlGenerator().sql(queryModifierApplyer, prefixGenerator, prototype.queryModifiers(), prefixStack);
        sql = this.appendSqlRecursive(sql, prefixGenerator, queryModifierApplyer, prefixStack);
        return sql;
    }

    private SqlType appendSqlRecursive(final SqlType sql, final Iterator<String> prefixGenerator, final Set<Consumer<Query>> queryModifierApplier, final Stack<String> prefixStack) {
        SqlType sqlAccu = sql;
        for (final ModuleMetaGenerator<Object, Object, SqlType, SqlGenerator<SqlType>> moduleMeta : submodules.values()) {
            prefixStack.push(moduleMeta.modulePrefix);
            sqlAccu = moduleMeta.prototype.sqlGenerator().append(queryModifierApplier,
                    prefixGenerator, moduleMeta.prototype.queryModifiers(), sqlAccu, prefixStack);
            sqlAccu = moduleMeta.appendSqlRecursive(sqlAccu, prefixGenerator, queryModifierApplier, prefixStack);
            prefixStack.pop();
        }
        return sqlAccu;
    }

    private ModuleMetaImpl<Type, KeyType, SqlType, Generator> initialize(final @NotNull ResultSet resultSet, final @NotNull StatementContext statementContext) {
        final Store store = Store.of(new HashMap<>(baseStore));
        store.place(ResultSet.class, resultSet);
        store.place(StatementContext.class, statementContext);
        final RowView rowView = new RowView(modulePrefix, prototype.rowMapper(), commonColumnMapperMap, resultSet, statementContext);
        store.place(RowView.class, rowView);
        final Map<KeyType, FallbackMeta<Object>> fallbacks = prototype.fallbacks().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new FallbackMeta<>(e.getValue(), rowView, baseStore)));
        return new ModuleMetaImpl<>(prototype, modulePrefix, resultSet, statementContext, rowView, store, fallbacks,
                submodules.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().initialize(resultSet, statementContext))));
    }

    private Query createQuery(final Handle handle) {
        final Set<Consumer<Query>> queryModifierApplyer = new HashSet<>();
        final Query query = handle.select(genSql(queryModifierApplyer).toQuery());
        queryModifierApplyer.forEach(qm -> qm.accept(query));
        return query;
    }

    /**
     * Executes the generator, creates the query, sends the request and maps.
     *
     * @param handle the handle to create the query from
     * @param seed   the seed of the root module
     * @param <C>    the seed type
     * @return the seed, filled with the mapped values
     */
    public <C extends Collection<Type>> C run(final Handle handle, final C seed) {
        return createQuery(handle).scanResultSet(((resultSetSupplier, ctx) -> {
            final ResultSet resultSet = resultSetSupplier.get();
            final ModuleMetaImpl<Type, KeyType, SqlType, Generator> initialize = initialize(resultSet, ctx);

            while (resultSet.next()) {
                initialize.call(seed);
            }
            return seed;
        }));
    }

    /**
     * @param <Type>      The Type this module maps to
     * @param <KeyType>   The Type of the Key, to access this modules submodules
     * @param <SqlType>   The SqlType of the SqlGenerator
     * @param <Generator> The Type of the SqlGenerator
     */
    @SuppressWarnings("WeakerAccess")
    static final class ModuleMetaImpl<Type, KeyType, SqlType extends jdbi_modules.SqlType, Generator extends SqlGenerator<SqlType>> implements ModuleMeta<KeyType> {
        private final String prefix;
        private final ResultSet resultSet;
        private final StatementContext statementContext;
        private final RowView rowView;
        private final Store store;
        private final Map<KeyType, FallbackMeta<Object>> fallbacks;
        private final Map<KeyType, ModuleMetaImpl<Object, Object, SqlType, SqlGenerator<SqlType>>> submodules;
        private final Module<Type, KeyType, SqlType, Generator> prototype;

        @Nullable
        private CollectorImpl<Collection<Type>, Type> collector = null;

        private ModuleMetaImpl(final Module<Type, KeyType, SqlType, Generator> prototype,
                               final String prefix,
                               final ResultSet resultSet,
                               final StatementContext statementContext,
                               final RowView rowView,
                               final Store store,
                               final Map<KeyType, FallbackMeta<Object>> fallbacks,
                               final Map<KeyType, ModuleMetaImpl<Object, Object, SqlType, SqlGenerator<SqlType>>> submodules) {
            this.prototype = prototype;
            this.prefix = prefix;
            this.resultSet = resultSet;
            this.statementContext = statementContext;
            this.rowView = rowView;
            this.store = store;
            this.fallbacks = fallbacks;
            this.submodules = submodules;
            this.prototype.prepare(this.prefix, store);
        }

        @NotNull
        @Override
        public String getModulePrefix() {
            return prefix;
        }

        @NotNull
        @Override
        public Store getStore() {
            return store;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T, CollectionType extends Collection<T>> ModuleMeta<KeyType> callSubmodule(final @NotNull KeyType key, final @NotNull CollectionType collection) {
            final ModuleMetaImpl<T, KeyType, SqlType, SqlGenerator<SqlType>> module = (ModuleMetaImpl<T, KeyType, SqlType, SqlGenerator<SqlType>>) submodules.get(key);
            if (Objects.nonNull(module)) {
                module.call(collection);
                return this;
            }
            final FallbackMeta<T> fallbackMeta = (FallbackMeta<T>) fallbacks.get(key);
            if (Objects.nonNull(fallbackMeta)) {
                fallbackMeta.call(collection);
            }
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T, CollectionType extends Collection<T>> ModuleMeta<KeyType> callSubmodule(final @NotNull KeyType key,
                                                                                           final @NotNull CollectionType collection,
                                                                                           final @NotNull Consumer<T> enricher) {
            final ModuleMetaImpl<T, KeyType, SqlType, SqlGenerator<SqlType>> module = (ModuleMetaImpl<T, KeyType, SqlType, SqlGenerator<SqlType>>) submodules.get(key);
            if (Objects.nonNull(module)) {
                module.call(collection, enricher);
                return this;
            }
            final FallbackMeta<T> fallbackMeta = (FallbackMeta<T>) fallbacks.get(key);
            if (Objects.nonNull(fallbackMeta)) {
                fallbackMeta.call(collection, enricher);
            }
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T, CollectionType extends Collection<T>> ModuleMeta<KeyType> callSubmodule(final @NotNull KeyType key,
                                                                                           final @NotNull CollectionType collection,
                                                                                           final @NotNull Consumer<T> enricher,
                                                                                           final @NotNull Consumer<T> accessed) {
            final ModuleMetaImpl<T, KeyType, SqlType, SqlGenerator<SqlType>> module = (ModuleMetaImpl<T, KeyType, SqlType, SqlGenerator<SqlType>>) submodules.get(key);
            if (Objects.nonNull(module)) {
                module.call(collection, enricher, accessed);
                return this;
            }
            final FallbackMeta<T> fallbackMeta = (FallbackMeta<T>) fallbacks.get(key);
            if (Objects.nonNull(fallbackMeta)) {
                fallbackMeta.call(collection, enricher, accessed);
            }
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T callSubmodule(final @NotNull KeyType key, final @NotNull Class<T> type) {
            final ModuleMetaImpl<T, KeyType, SqlType, SqlGenerator<SqlType>> module = (ModuleMetaImpl<T, KeyType, SqlType, SqlGenerator<SqlType>>) submodules.get(key);
            if (Objects.nonNull(module)) {
                LinkedList<T> list = new LinkedList<>();
                module.call(list);
                return list.stream().findFirst().orElse(null);
            }
            final FallbackMeta<T> fallbackMeta = (FallbackMeta<T>) fallbacks.get(key);
            if (Objects.nonNull(fallbackMeta)) {
                LinkedList<T> list = new LinkedList<>();
                fallbackMeta.call(list);
                return list.stream().findFirst().orElse(null);
            }
            return null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T callSubmodule(final @NotNull KeyType key, final @NotNull GenericType<T> type) {
            return (T) callSubmodule(key, Object.class);
        }

        public <CollectionType extends Collection<Type>> void call(final @NotNull CollectionType collection) {
            if (Objects.isNull(collector)) {
                collector = new CollectorImpl<>(collection, rowView, resultSet, statementContext);
            }
            collector.useCollection(collection);
            prototype.map(collector, this, rowView, store);
        }

        public <CollectionType extends Collection<Type>> void call(final @NotNull CollectionType collection,
                                                                   final @NotNull Consumer<Type> enricher) {
            this.call(collection);
            assert collector != null;
            collector.applyOnAdded(enricher);
        }

        public <CollectionType extends Collection<Type>> void call(final @NotNull CollectionType collection,
                                                                   final @NotNull Consumer<Type> enricher,
                                                                   final @NotNull Consumer<Type> accessed) {
            this.call(collection, enricher);
            assert collector != null;
            collector.applyOnAccessed(accessed);
        }
    }
}
