package jdbi_modules.internal;

import jdbi_modules.Fallback;
import jdbi_modules.Store;
import org.jdbi.v3.core.statement.StatementContext;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @param <Type> the type the fallback maps to
 * @since 14.04.2018
 */
class FallbackMeta<Type> {
    private final StatementContext statementContext;
    private final ResultSet resultSet;
    private final Fallback<Type> prototype;
    private final RowView rowView;
    private final Store store;

    @Null
    private CollectorImpl<Collection<Type>, Type> collector = null;

    FallbackMeta(@NotNull final Fallback<Type> prototype,
                 @NotNull final RowView rowView,
                 @NotNull final Map<Class<?>, Object> store) {
        this.prototype = prototype;
        this.store = Store.of(new HashMap<>(store));
        this.resultSet = this.store.require(ResultSet.class);
        this.statementContext = this.store.require(StatementContext.class);
        this.rowView = rowView;
    }

    public <CollectionType extends Collection<Type>> void call(final CollectionType collection) {
        if (Objects.isNull(collector)) {
            collector = new CollectorImpl<>(collection, rowView, resultSet, statementContext);
        }
        collector.useCollection(collection);
        prototype.map(collector, rowView, store);
    }

    public <CollectionType extends Collection<Type>> void call(@NotNull final CollectionType collection,
                                                               @NotNull final Consumer<Type> enricher) {
        this.call(collection);
        Objects.requireNonNull(collector).applyOnAdded(enricher);
    }

    public <CollectionType extends Collection<Type>> void call(@NotNull final CollectionType collection,
                                                               @NotNull final Consumer<Type> enricher,
                                                               @NotNull final Consumer<Type> accessed) {
        this.call(collection, enricher);
        Objects.requireNonNull(collector).applyOnAccessed(accessed);
    }
}
