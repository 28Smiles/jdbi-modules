package jdbi_modules.bean;

/**
 * @since 14.04.2018
 */
public final class Worker extends Bean {
    private Pool pool;
    private int position;

    public Worker(final long id, final String name, final Pool pool, final int position) {
        super(id, name);
        this.pool = pool;
        this.position = position;
    }

    public Worker() {
        super();
    }

    public Pool getPool() {
        return pool;
    }

    public void setPool(final Pool pool) {
        this.pool = pool;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(final int position) {
        this.position = position;
    }
}
