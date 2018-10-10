package jdbi_modules.internal;

import jdbi_modules.Collector;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @param <CollectionType> the type of the collection the collector handles
 * @param <Type>           the type of elements in the collection
 * @since 14.04.2018
 */
class CollectorImpl<CollectionType extends Collection<Type>, Type> implements Collector<CollectionType, Type> {
    private CollectionType collection;
    private final RowView rowView;
    private final ResultSet resultSet;
    private final StatementContext context;
    private BiFunction<Type, Type, Boolean> comparator = Object::equals;
    private List<Type> added = new ArrayList<>();

    CollectorImpl(final CollectionType collection, final RowView rowView, final ResultSet resultSet, final StatementContext context) {
        this.collection = collection;
        this.rowView = rowView;
        this.resultSet = resultSet;
        this.context = context;
    }

    @Override
    public Collector<CollectionType, Type> setComparator(@NotNull final BiFunction<Type, Type, Boolean> comparator) {
        this.comparator = comparator;
        return this;
    }

    @Override
    public CollectionType get() {
        return collection;
    }

    @Override
    public CollectorImpl<CollectionType, Type> appendUnique(@Nullable final Type type, @NotNull final Consumer<Type> applier) {
        if (Objects.isNull(type)) {
            return this;
        }
        applier.accept(collection.stream().filter(b -> comparator.apply(b, type)).findFirst().orElseGet(() -> {
            collection.add(type);
            added.add(type);
            return type;
        }));
        return this;
    }

    @Override
    public CollectorImpl<CollectionType, Type> appendUnique(@Nullable final Type type) {
        if (Objects.isNull(type)) {
            return this;
        }
        collection.stream().filter(b -> comparator.apply(b, type)).findFirst().orElseGet(() -> {
            collection.add(type);
            added.add(type);
            return type;
        });
        return this;
    }

    @Override
    public CollectorImpl<CollectionType, Type> append(@Nullable final Type type, @NotNull final Consumer<Type> applyer) {
        if (Objects.isNull(type)) {
            return this;
        }
        collection.add(type);
        applyer.accept(type);
        added.add(type);
        return this;
    }

    @Override
    public CollectorImpl<CollectionType, Type> append(@Nullable final Type type) {
        if (Objects.isNull(type)) {
            return this;
        }
        collection.add(type);
        added.add(type);
        return this;
    }

    @Override
    public CollectorImpl<CollectionType, Type> appendUniqueWithRowView(@NotNull final Class<Type> clazz, @NotNull final Consumer<Type> applier) {
        final Type type = rowView.getRow(clazz);
        return appendUnique(type, applier);
    }

    @Override
    public CollectorImpl<CollectionType, Type> appendUniqueWithRowView(@NotNull final Class<Type> clazz) {
        final Type type = rowView.getRow(clazz);
        return appendUnique(type);
    }

    @Override
    public CollectorImpl<CollectionType, Type> appendWithRowView(@NotNull final Class<Type> clazz, @NotNull final Consumer<Type> applier) {
        final Type type = rowView.getRow(clazz);
        return append(type, applier);
    }

    @Override
    public CollectorImpl<CollectionType, Type> appendWithRowView(@NotNull final Class<Type> clazz) {
        final Type type = rowView.getRow(clazz);
        return append(type);
    }

    @Override
    public CollectorImpl<CollectionType, Type> appendWithRowMapper(@NotNull final RowMapper<Type> rowMapper, @NotNull final Consumer<Type> applier) throws SQLException {
        final Type type = rowMapper.map(resultSet, context);
        return append(type, applier);
    }

    @Override
    public CollectorImpl<CollectionType, Type> appendWithRowMapper(@NotNull final RowMapper<Type> rowMapper) throws SQLException {
        final Type type = rowMapper.map(resultSet, context);
        return append(type);
    }

    @Override
    public Collector<CollectionType, Type> appendUniqueWithRowMapper(@NotNull final RowMapper<Type> rowMapper, @NotNull final Consumer<Type> applier) throws SQLException {
        final Type type = rowMapper.map(resultSet, context);
        return appendUnique(type, applier);
    }

    @Override
    public Collector<CollectionType, Type> appendUniqueWithRowMapper(@NotNull final RowMapper<Type> rowMapper) throws SQLException {
        final Type type = rowMapper.map(resultSet, context);
        return appendUnique(type);
    }

    void useCollection(final CollectionType collection) {
        this.collection = collection;
        this.added.clear();
    }

    void applyOnAdded(@NotNull final Consumer<Type> consumer) {
        added.forEach(consumer);
    }
}
