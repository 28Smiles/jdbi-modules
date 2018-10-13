package jdbi_modules.base;

import org.jdbi.v3.core.statement.Query;

import java.util.List;

/**
 * @since 14.04.2018
 */
public final class ListDefinition extends QueryModifier<List<?>> {
    /**
     * @param name  the name of the binding
     * @param value the value of the binding
     */
    public ListDefinition(final String name, final List<?> value) {
        super(name, value);
    }

    @Override
    public void apply(final Query query, final String newName) {
        query.defineList(newName, getValue());
    }
}
