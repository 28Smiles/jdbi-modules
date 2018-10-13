package jdbi_modules.base;

/**
 * @param <Type> the type of the value of the query modifier
 * @since 14.04.2018
 */
abstract class QueryModifier<Type> implements jdbi_modules.QueryModifier {
    private String name;
    private Type value;

    QueryModifier(final String name, final Type value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public final String getInSql() {
        return ':' + name;
    }

    @Override
    public final String getName() {
        return name;
    }

    public Type getValue() {
        return value;
    }
}
