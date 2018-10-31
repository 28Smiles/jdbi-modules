package jdbi_modules;

import jdbi_modules.internal.ModuleMetaGenerator;
import jdbi_modules.internal.PrefixGenerator;
import jdbi_modules.internal.RowView;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @param <Type>         The Type this module maps to
 * @param <KeyType>      The Type of the Key, to access this modules submodules
 * @param <SqlType>      The SqlType of the SqlGenerator
 * @param <SqlGenerator> The Type of the SqlGenerator
 * @since 14.04.2018
 */
public abstract class Module<Type, KeyType, SqlType extends jdbi_modules.SqlType, SqlGenerator extends jdbi_modules.SqlGenerator<SqlType>> {
    private Map<KeyType, Module> submodules = new HashMap<>();
    private Map<KeyType, Fallback<Object>> fallbacks = new HashMap<>();

    /**
     * @return the submodules of this module
     */
    @NotNull
    public Map<KeyType, Module> submodules() {
        return submodules;
    }

    /**
     * @return the fallbacks of this module, called in case there is no submodule added
     */
    @NotNull
    public Map<KeyType, Fallback<Object>> fallbacks() {
        return fallbacks;
    }

    /**
     * @return a set of query modifiers this module uses
     */
    @NotNull
    public abstract Set<QueryModifier> queryModifiers();

    /**
     * The returned row-mappers will be available in the row-view. The Type has to equal the row-mapper type.
     *
     * @return the map of row-mappers of this module
     */
    @NotNull
    public abstract Map<java.lang.reflect.Type, Function<String, RowMapper<?>>> rowMapper();

    /**
     * @return the SqlGenerator of this module
     */
    @NotNull
    public abstract SqlGenerator getSqlGenerator();

    /**
     * A prepare function, allowing the developer to create objects and put them into the store once at the beginning of the mapping.
     *
     * @param modulePrefix the module meta, containing information
     * @param store        the store
     */
    public void prepare(@NotNull final String modulePrefix, @NotNull final Store store) {
        // Nothing
    }

    /**
     * @param collector  the collector
     * @param moduleMeta the meta of the module
     * @param rowView    the row-view
     * @param store      the store
     */
    public abstract void map(@NotNull Collector<Collection<Type>, Type> collector,
                             @NotNull ModuleMeta<Type, KeyType> moduleMeta,
                             @NotNull RowView rowView,
                             @NotNull Store store);

    /**
     * Runs the module.
     *
     * @param handle the handle to use
     * @param seed   the seed to map to
     * @param <C>    the type of the collection
     * @return the collection
     */
    public <C extends Collection<Type>> C run(final Handle handle, final C seed) {
        final PrefixGenerator prefixGenerator = new PrefixGenerator("mod");
        return new ModuleMetaGenerator<>(prefixGenerator, this, Map.of(), new ConcurrentHashMap<>()).run(handle, seed);
    }
}
