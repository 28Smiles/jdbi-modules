package jdbi_modules.base;

import org.jdbi.v3.core.statement.Query;

/**
 * @since 14.04.2018
 */
public class Definition extends DefinitionPrototype<Object> {
    /**
     * @param name  the name of the binding
     * @param value the value of the binding
     */
    public Definition(final String name, final Object value) {
        super(name, value);
    }

    /**
     * @param query   the query
     * @param newName the new generic name of the query modifier
     */
    @Override
    public void apply(final Query query, final String newName) {
        query.define(newName, getValue());
    }
}
