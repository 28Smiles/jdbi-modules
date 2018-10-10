package jdbi_modules;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @param <CollectionType> the collection type
 * @param <Type>           the type of the elements of the collection
 * @since 14.04.2018
 */
public interface Collector<CollectionType extends Collection<Type>, Type> {
    /**
     * @return the collection currently stored in the collector
     */
    CollectionType get();

    /**
     * @param comparator the comparator to use
     * @return this
     */
    Collector<CollectionType, Type> setComparator(@NotNull BiFunction<Type, Type, Boolean> comparator);

    /**
     * Appends a object to the collection if it's not already contained in the collection,
     * and applies the function on the added or found object.
     *
     * @param type    the object to append
     * @param applier a consumer to apply on the accessed object
     * @return this
     */
    Collector<CollectionType, Type> appendUnique(@Nullable Type type, @NotNull Consumer<Type> applier);

    /**
     * Appends a object to the collection if it's not already contained in the collection.
     *
     * @param type the object to append
     * @return this
     */
    Collector<CollectionType, Type> appendUnique(@Nullable Type type);

    /**
     * Appends a object to the collection and applies the function on the added object.
     *
     * @param type    the object to append
     * @param applyer a consumer to apply on the object
     * @return this
     */
    Collector<CollectionType, Type> append(@Nullable Type type, @NotNull Consumer<Type> applyer);

    /**
     * Appends a object to the collection.
     *
     * @param type the object to append
     * @return this
     */
    Collector<CollectionType, Type> append(@Nullable Type type);

    /**
     * Appends a object fetched from the row-view to the collection if it's not already contained in the collection,
     * and applies the function on the added or found object.
     *
     * @param clazz   type of the row-mapper to use
     * @param applier a consumer to apply on the accessed object
     * @return this
     */
    Collector<CollectionType, Type> appendUniqueWithRowView(@NotNull Class<Type> clazz, @NotNull Consumer<Type> applier);

    /**
     * Appends a object fetched from the row-view to the collection if it's not already contained in the collection.
     *
     * @param clazz type of the row-mapper to use
     * @return this
     */
    Collector<CollectionType, Type> appendUniqueWithRowView(@NotNull Class<Type> clazz);

    /**
     * Appends a object fetched from the row-view to the collection and applies the function on the added object.
     *
     * @param clazz   type of the row-mapper to use
     * @param applier a consumer to apply on the added object
     * @return this
     */
    Collector<CollectionType, Type> appendWithRowView(@NotNull Class<Type> clazz, @NotNull Consumer<Type> applier);

    /**
     * Appends a object fetched from the row-view to the collection.
     *
     * @param clazz type of the row-mapper to use
     * @return this
     */
    Collector<CollectionType, Type> appendWithRowView(@NotNull Class<Type> clazz);

    /**
     * Appends a object fetched from the row-mapper to the collection if it's not already contained in the collection,
     * and applies the function on the added or found object.
     *
     * @param rowMapper type of the row-mapper to use
     * @param applier   a consumer to apply on the accessed object
     * @return this
     * @throws SQLException a possible exception during the mapping
     */
    Collector<CollectionType, Type> appendUniqueWithRowMapper(@NotNull RowMapper<Type> rowMapper, @NotNull Consumer<Type> applier) throws SQLException;

    /**
     * Appends a object fetched from the row-mapper to the collection if it's not already contained in the collection.
     *
     * @param rowMapper the row-mapper to use
     * @return this
     * @throws SQLException a possible exception during the mapping
     */
    Collector<CollectionType, Type> appendUniqueWithRowMapper(@NotNull RowMapper<Type> rowMapper) throws SQLException;

    /**
     * Appends a object fetched from the row-mapper to the collection and applies the function on the added object.
     *
     * @param rowMapper the row-mapper to use
     * @param applier   a consumer to apply on the accessed object
     * @return this
     * @throws SQLException a possible exception during the mapping
     */
    Collector<CollectionType, Type> appendWithRowMapper(@NotNull RowMapper<Type> rowMapper, @NotNull Consumer<Type> applier) throws SQLException;

    /**
     * Appends a object fetched from the row-mapper to the collection.
     *
     * @param rowMapper row-mapper to use
     * @return this
     * @throws SQLException a possible exception during the mapping
     */
    Collector<CollectionType, Type> appendWithRowMapper(@NotNull RowMapper<Type> rowMapper) throws SQLException;
}
