package jdbi_modules.base;

import javax.validation.constraints.NotNull;

/**
 * @param <Type> the type of the value of the query modifier
 * @since 14.04.2018
 */
abstract class QueryModifier<Type> implements jdbi_modules.QueryModifier {
    private String name;
    private Type value;

    QueryModifier(final @NotNull String name, final @NotNull Type value) {
        this.name = name;
        this.value = value;
    }

    @NotNull
    @Override
    public final String getName() {
        return name;
    }

    @NotNull
    public Type getValue() {
        return value;
    }
}
