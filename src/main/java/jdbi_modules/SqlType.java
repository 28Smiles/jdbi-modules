package jdbi_modules;

import javax.validation.constraints.NotNull;

/**
 * @since 14.04.2018
 */
public interface SqlType {
    /**
     * @return a query
     */
    @NotNull
    String toQuery();
}
