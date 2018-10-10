package jdbi_modules;

/**
 * @since 14.04.2018
 */
public interface SqlType {
    /**
     * @return a query
     */
    String toQuery();
}
