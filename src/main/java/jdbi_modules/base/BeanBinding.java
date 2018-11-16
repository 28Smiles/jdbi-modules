package jdbi_modules.base;

import org.jdbi.v3.core.statement.Query;

import javax.validation.constraints.NotNull;

/**
 * @since 14.04.2018
 */
public final class BeanBinding extends BindingPrototype {
    /**
     * @param name  the name of the binding
     * @param value the value of the binding
     */
    public BeanBinding(@NotNull final String name, @NotNull final Object value) {
        super(name, value);
    }

    @Override
    public void apply(@NotNull final Query query, @NotNull final String newName) {
        query.bindBean(newName, getValue());
    }
}
