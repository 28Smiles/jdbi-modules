package jdbi_modules.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 14.04.2018
 */
public final class Pool extends Bean {
    private Master master;
    private List<Worker> workers = new ArrayList<>();

    public Pool(final long id, final String name, final Master master) {
        super(id, name);
        this.master = master;
    }

    public Pool() {
        super();
    }

    public Master getMaster() {
        return master;
    }

    public void setMaster(final Master master) {
        this.master = master;
    }

    public List<Worker> getWorkers() {
        return workers;
    }
}
