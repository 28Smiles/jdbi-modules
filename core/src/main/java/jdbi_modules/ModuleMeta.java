package jdbi_modules;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * @param <Type>    The type the corresponding module maps to
 * @param <KeyType> The type of the key the corresponding module uses
 * @since 14.04.2018
 */
public interface ModuleMeta<Type, KeyType> {
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
    <T, CollectionType extends Collection<T>> void callSubmodule(@NotNull KeyType key, @NotNull CollectionType collection);

    /**
     * @param key              the key
     * @param collection       the collection
     * @param enricher         the consumer to apply after adding something to the collection
     * @param <T>              the type of the elements of the collection
     * @param <CollectionType> the type of the collection
     */
    <T, CollectionType extends Collection<T>> void callSubmodule(@NotNull KeyType key, @NotNull CollectionType collection, Consumer<T> enricher);
}
