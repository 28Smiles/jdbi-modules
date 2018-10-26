package jdbi_modules;

import org.jdbi.v3.core.statement.Query;

/**
 * @since 14.04.2018
 */
public interface QueryModifier {
    /**
     * @return the in query representation of the query modifier
     */
    String getInSql();

    /**
     * @return the name of the query modifier
     */
    String getName();

    /**
     * @param query   the query
     * @param newName the new generic name of the query modifier
     */
    void apply(Query query, String newName);
}
