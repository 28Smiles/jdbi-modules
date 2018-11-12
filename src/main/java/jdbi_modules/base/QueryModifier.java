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
    public final String getName() {
        return name;
    }

    public Type getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * @param queryModifier the query modifier to check
     * @return is equal
     */
    public abstract boolean equals(QueryModifier<?> queryModifier);

    /**
     * ! Equals ignores carried information !
     * @param obj the object to check equality for
     * @return is equal (in sql)
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof QueryModifier) {
            final QueryModifier<?> queryModifier = (QueryModifier) obj;
            if (queryModifier.getName().equals(name)) {
                return equals(queryModifier);
            }
        }
        return false;
    }
}
