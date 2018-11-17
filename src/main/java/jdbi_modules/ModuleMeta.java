package jdbi_modules;

import org.jdbi.v3.core.generic.GenericType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * @param <KeyType> The type of the key the corresponding module uses
 * @since 14.04.2018
 */
public interface ModuleMeta<KeyType> {
    /**
     * @return the module prefix
     */
    @NotNull
    String getModulePrefix();

    /**
     * @return the module store
     */
    @NotNull
    Store getStore();

    /**
     * @param key              the key
     * @param collection       the collection
     * @param <T>              the type of the elements of the collection
     * @param <CollectionType> the type of the collection
     * @return this
     */
    @NotNull
    <T, CollectionType extends Collection<T>> ModuleMeta<KeyType> callSubmodule(@NotNull KeyType key, @NotNull CollectionType collection);

    /**
     * @param key              the key
     * @param collection       the collection
     * @param enricher         the consumer to apply after adding something to the collection
     * @param <T>              the type of the elements of the collection
     * @param <CollectionType> the type of the collection
     * @return this
     */
    @NotNull
    <T, CollectionType extends Collection<T>> ModuleMeta<KeyType> callSubmodule(@NotNull KeyType key, @NotNull CollectionType collection, Consumer<T> enricher);

    /**
     * @param key              the key
     * @param collection       the collection
     * @param enricher         the consumer to apply after adding something to the collection
     * @param accessed         the consumer to apply on the accessed elements
     * @param <T>              the type of the elements of the collection
     * @param <CollectionType> the type of the collection
     * @return this
     */
    @NotNull
    <T, CollectionType extends Collection<T>> ModuleMeta<KeyType> callSubmodule(@NotNull KeyType key, @NotNull CollectionType collection, Consumer<T> enricher, Consumer<T> accessed);

    /**
     * @param key  the key
     * @param type the class of the type expected
     * @param <T>  the type expected
     * @return the value fetched
     */
    @Null
    <T> T callSubmodule(@NotNull KeyType key, @NotNull Class<T> type);

    /**
     * @param key  the key
     * @param type the class of the type expected
     * @param <T>  the type expected
     * @return the value fetched
     */
    @Null
    <T> T callSubmodule(@NotNull KeyType key, @NotNull GenericType<T> type);

    /**
     * @param key      the key
     * @param type     the class of the type expected
     * @param <T>      the type expected
     * @param enricher the consumer to apply after adding something to the collection
     * @return this
     */
    @NotNull
    default <T> ModuleMeta<KeyType> callSubmodule(@NotNull KeyType key, @NotNull Class<T> type, @NotNull Consumer<T> enricher) {
        final T t = callSubmodule(key, type);
        enricher.accept(t);
        return this;
    }

    /**
     * @param key      the key
     * @param type     the class of the type expected
     * @param <T>      the type expected
     * @param enricher the consumer to apply after adding something to the collection
     * @return this
     */
    @NotNull
    default <T> ModuleMeta<KeyType> callSubmodule(@NotNull KeyType key, @NotNull GenericType<T> type, @NotNull Consumer<T> enricher) {
        final T t = callSubmodule(key, type);
        enricher.accept(t);
        return this;
    }
}
