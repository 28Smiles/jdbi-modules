package jdbi_modules.bean;

/**
 * @since 14.04.2018
 */
public final class User extends Bean {

    public User(long id, String name) {
        super(id, name);
    }

    public User() {
        super();
    }
}
