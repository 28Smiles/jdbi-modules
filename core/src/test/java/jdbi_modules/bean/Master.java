package jdbi_modules.bean;


import java.util.HashSet;
import java.util.Set;

/**
 * @since 14.04.2018
 */
public final class Master extends Bean {
    private Set<Pool> pools = new HashSet<>();

    public Master(final long id, final String name) {
        super(id, name);
    }

    public Master() {
        super();
    }

    public Set<Pool> getPools() {
        return pools;
    }
}
