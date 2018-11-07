package jdbi_modules.bean;

/**
 * @since 14.04.2018
 */
public class Worker extends Bean {
    private Pool pool;
    private int position;
    private User user;

    public Worker(final long id, final String name, User user, final int position, final Pool pool) {
        super(id, name);
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
