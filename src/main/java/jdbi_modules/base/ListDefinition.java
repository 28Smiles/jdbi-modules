package jdbi_modules.base;

import org.jdbi.v3.core.statement.Query;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @since 14.04.2018
 */
public final class ListDefinition extends DefinitionPrototype<List<?>> {
    /**
     * @param name  the name of the binding
     * @param value the value of the binding
     */
    public ListDefinition(final @NotNull String name, final @NotNull List<?> value) {
        super(name, value);
    }

    @Override
    public void apply(final @NotNull Query query, final @NotNull String newName) {
        query.defineList(newName, getValue());
    }
}
