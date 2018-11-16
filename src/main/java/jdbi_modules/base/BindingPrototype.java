package jdbi_modules.base;

import javax.validation.constraints.NotNull;

/**
 * @since 12.11.2018
 */
public abstract class BindingPrototype extends QueryModifier<Object> {
    /**
     * @param name  the name of the binding
     * @param value the value of the binding
     */
    public BindingPrototype(@NotNull final String name, @NotNull final Object value) {
        super(name, value);
    }

    @NotNull
    @Override
    public final String getInSql() {
        return ':' + getName();
    }

    @Override
    public final int hashCode() {
        return getName().hashCode();
    }

    @Override
    public final boolean equals(final Object obj) {
        if (obj instanceof BindingPrototype) {
            final QueryModifier<?> queryModifier = (QueryModifier) obj;
            return queryModifier.getName().equals(getName());
        }
        return false;
    }
}
