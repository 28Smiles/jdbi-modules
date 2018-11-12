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
    public final boolean equals(final QueryModifier<?> obj) {
        return obj instanceof Definition;
    }
}
