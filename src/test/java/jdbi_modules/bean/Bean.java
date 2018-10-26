package jdbi_modules.bean;

/**
 * @since 14.04.2018
 */
public class Bean {
    private long id;
    private String name;

    public Bean(final long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public Bean() {
        super();
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Bean)) {
            return false;
        }
        return ((Bean) obj).id == id;
    }
}
