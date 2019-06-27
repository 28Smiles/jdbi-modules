package jdbi_modules;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
     * @return this
     */
    <T> ModuleMeta<KeyType> callSubmodule(@NotNull KeyType key, @NotNull Collection<T> collection);

    /**
     * @param key              the key
     * @param collection       the collection
     * @param enricher         the consumer to apply after adding something to the collection
     * @param <T>              the type of the elements of the collection
     * @return this
     */
    <T> ModuleMeta<KeyType> callSubmodule(@NotNull KeyType key, @NotNull Collection<T> collection, Consumer<T> enricher);

    /**
     * @param key              the key
     * @param collection       the collection
     * @param enricher         the consumer to apply after adding something to the collection
     * @param accessed         the consumer to apply on the accessed elements
     * @param <T>              the type of the elements of the collection
     * @param <CollectionType> the type of the collection
     * @return this
     */
    <T, CollectionType extends Collection<T>> ModuleMeta<KeyType> callSubmodule(@NotNull KeyType key, @NotNull CollectionType collection, Consumer<T> enricher, Consumer<T> accessed);

    /**
     * @param key    the key
     * @param getter the getter
     * @param <T>    the type expected
     * @return the value fetched
     */
    <T> T callSubmodule(@NotNull KeyType key, @NotNull Supplier<T> getter);

    /**
     * @param key    the key
     * @param getter the getter
     * @param setter a setter
     * @param <T>    the type expected
     * @return the value fetched
     */
    default <T> T callSubmodule(@NotNull KeyType key, @NotNull Supplier<T> getter, Consumer<T> setter) {
        final T t = callSubmodule(key, getter);
        setter.accept(t);
        return t;
    }
}
