package jdbi_modules;

import org.jdbi.v3.core.generic.GenericType;
import org.jetbrains.annotations.NotNull;

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
     */
    <T, CollectionType extends Collection<T>> ModuleMeta<KeyType> callSubmodule(@NotNull KeyType key, @NotNull CollectionType collection);

    /**
     * @param key              the key
     * @param collection       the collection
     * @param enricher         the consumer to apply after adding something to the collection
     * @param <T>              the type of the elements of the collection
     * @param <CollectionType> the type of the collection
     */
    <T, CollectionType extends Collection<T>> ModuleMeta<KeyType> callSubmodule(@NotNull KeyType key, @NotNull CollectionType collection, Consumer<T> enricher);

    /**
     * @param key  the key
     * @param type the class of the type expected
     * @param <T>  the type expected
     * @return the value fetched
     */
    <T> T callSubmodule(@NotNull KeyType key, @NotNull Class<T> type);

    /**
     * @param key  the key
     * @param type the class of the type expected
     * @param <T>  the type expected
     * @return the value fetched
     */
    <T> T callSubmodule(@NotNull KeyType key, @NotNull GenericType<T> type);

    /**
     * @param key  the key
     * @param type the class of the type expected
     * @param <T>  the type expected
     * @param enricher         the consumer to apply after adding something to the collection
     * @return the value fetched
     */
    default <T> T callSubmodule(@NotNull KeyType key, @NotNull Class<T> type, Consumer<T> enricher) {
        final T t = callSubmodule(key, type);
        enricher.accept(t);
        return t;
    }

    /**
     * @param key  the key
     * @param type the class of the type expected
     * @param <T>  the type expected
     * @param enricher         the consumer to apply after adding something to the collection
     * @return the value fetched
     */
    default <T> T callSubmodule(@NotNull KeyType key, @NotNull GenericType<T> type, Consumer<T> enricher) {
        final T t = callSubmodule(key, type);
        enricher.accept(t);
        return t;
    }
}
