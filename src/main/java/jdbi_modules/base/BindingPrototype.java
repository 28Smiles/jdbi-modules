package jdbi_modules.base;

/**
 * @since 12.11.2018
 */
public abstract class BindingPrototype extends QueryModifier<Object> {
    /**
     * @param name  the name of the binding
     * @param value the value of the binding
     */
    public BindingPrototype(final String name, final Object value) {
        super(name, value);
    }

    @Override
    public final String getInSql() {
        return ':' + getName();
    }

    @Override
    public final boolean equals(final QueryModifier<?> obj) {
        return obj instanceof Binding;
    }
}
