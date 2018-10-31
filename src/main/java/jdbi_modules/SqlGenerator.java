package jdbi_modules;

import org.jdbi.v3.core.statement.Query;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;

/**
 * @param <SqlType> the {@link jdbi_modules.SqlType} the generator generates
 * @since 14.04.2018
 */
public interface SqlGenerator<SqlType extends jdbi_modules.SqlType> {
    /**
     * @param queryModifierApplier       the functions to apply to the query
     * @param queryModifierNameGenerator a generator for generic query modifier names
     * @param queryModifiers             the query modifiers to apply
     * @param modulePrefix               the prefix-stack of the module
     * @return the generated sql type
     */
    @NotNull
    SqlType sql(@NotNull Set<Consumer<Query>> queryModifierApplier,
                @NotNull Iterator<String> queryModifierNameGenerator,
                @NotNull Set<QueryModifier> queryModifiers,
                @NotNull Stack<String> modulePrefix);

    /**
     * @param queryModifierApplier       the functions to apply to the query
     * @param queryModifierNameGenerator a generator for generic query modifier names
     * @param queryModifiers             the query modifiers to apply
     * @param sqlType                    the sql type to append to
     * @param modulePrefix               the prefix-stack of the module
     * @return the generated sql type
     */
    @NotNull
    SqlType append(@NotNull Set<Consumer<Query>> queryModifierApplier,
                   @NotNull Iterator<String> queryModifierNameGenerator,
                   @NotNull Set<QueryModifier> queryModifiers,
                   @NotNull SqlType sqlType,
                   @NotNull Stack<String> modulePrefix);
}
