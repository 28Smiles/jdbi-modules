package jdbi_modules;

import java.util.Map;

/**
 * @since 14.04.2018
 */
public interface Store {
    /**
     * @param key the type of object to access from the store
     * @param <B> the type accessed
     * @return the object found
     */
    <B> B require(Class<B> key);

    /**
     * @param key the type of object to access from the store
     * @param value the value to store
     * @param <B> the type of the object
     */
    <B> void place(Class<B> key, B value);

    /**
     * @param storeMap the map to create the store of
     * @return the created store
     */
    static Store of(final Map<Class<?>, Object> storeMap) {
        return new Store() {
            @Override
            @SuppressWarnings("unchecked")
            public <B> B require(final Class<B> key) {
                return (B) storeMap.get(key);
            }

            @Override
            public <B> void place(final Class<B> key, final B value) {
                storeMap.put(key, value);
            }
        };
    }
}
