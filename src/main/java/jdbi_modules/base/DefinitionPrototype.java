package jdbi_modules.base;

/**
 * @since 12.11.2018
 * @param <Type> the type of the value of the modifier
 */
public abstract class DefinitionPrototype<Type> extends QueryModifier<Type> {
    /**
     * @param name  the name of the binding
     * @param value the value of the binding
     */
    public DefinitionPrototype(final String name, final Type value) {
        super(name, value);
    }

    @Override
    public final String getInSql() {
        return '<' + getName() + '>';
    }

    @Override
    public final int hashCode() {
        return getName().hashCode();
    }

    @Override
    public final boolean equals(final Object obj) {
        if (obj instanceof DefinitionPrototype) {
            final QueryModifier<?> queryModifier = (QueryModifier) obj;
            return queryModifier.getName().equals(getName());
        }
        return false;
    }
}
