package jdbi_modules.base;

import jdbi_modules.QueryModifier;
import org.jdbi.v3.core.statement.Query;

import javax.validation.constraints.NotNull;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * @since 14.04.2018
 */
public interface StructuredSqlGenerator extends jdbi_modules.SqlGenerator<StructuredSql> {
    Pattern PATTERN = Pattern.compile("\\{\\{\\s*((\\d*)\\s*,\\s*)?([A-Za-z0-9_. \\-><'()\\x7f-\\xff]+)\\s*}}");

    /**
     * @param modulePrefix the prefix-stack
     * @param sql          the sql
     * @return the routed sql
     */
    private static String route(@NotNull final Stack<String> modulePrefix, @NotNull final String sql) {
        return PATTERN.matcher(sql).replaceAll(matchResult -> {
            final String id = matchResult.group(2);
            final String name = matchResult.group(3);
            return '"' + modulePrefix.get(modulePrefix.size() - 1 - (Objects.isNull(id) ? 0 : Integer.parseInt(id))) + name + '"';
        });
    }

    /**
     * @return the select part of the query.
     */
    @NotNull
    default String select() {
        return "";
    }

    /**
     * @return the part of the cte clause of the query.
     */
    @NotNull
    default String cte() {
        return "";
    }

    /**
     * @return the part of the from clause of the query.
     */
    @NotNull
    default String from() {
        return "";
    }

    /**
     * @return the joins of the query.
     */
    @NotNull
    default String joins() {
        return "";
    }

    /**
     * @return the sort orders of the query.
     */
    @NotNull
    default String sortBy() {
        return "";
    }

    /**
     * @return the filter of the query.
     */
    @NotNull
    default String filter() {
        return "";
    }

    @NotNull
    @Override
    default StructuredSql sql(@NotNull final Set<Consumer<Query>> queryModifierApplier,
                              @NotNull final Iterator<String> queryModifierNameGenerator,
                              @NotNull final Set<jdbi_modules.QueryModifier> queryModifiers,
                              @NotNull final Stack<String> modulePrefix) {
        final StructuredSql structuredSql = new StructuredSql(
                route(modulePrefix, cte()),
                route(modulePrefix, select()),
                route(modulePrefix, from()),
                route(modulePrefix, joins()),
                route(modulePrefix, filter()),
                route(modulePrefix, sortBy()));
        queryModifiers.stream().map(qm -> structuredSql.applyQueryModifier(qm, queryModifierNameGenerator)).forEach(queryModifierApplier::add);
        return structuredSql;
    }

    @NotNull
    @Override
    default StructuredSql append(@NotNull final Set<Consumer<Query>> queryModifierApplier,
                                 @NotNull final Iterator<String> queryModifierNameGenerator,
                                 @NotNull final Set<QueryModifier> queryModifiers,
                                 @NotNull final StructuredSql sqlType,
                                 @NotNull final Stack<String> modulePrefix) {
        sqlType.cte += (!sqlType.cte.isEmpty() && !cte().isEmpty() ? ", " : "") + route(modulePrefix, cte());
        sqlType.select += (!sqlType.select.isEmpty() && !select().isEmpty() ? ',' : "") + route(modulePrefix, select());
        sqlType.from += (!sqlType.from.isEmpty() && !from().isEmpty() ? ' ' : "") + route(modulePrefix, from());
        sqlType.joins += (!sqlType.joins.isEmpty() && !joins().isEmpty() ? ' ' : "") + route(modulePrefix, joins());
        sqlType.filter += (!sqlType.filter.isEmpty() && !filter().isEmpty() ? " AND " : "") + route(modulePrefix, filter());
        sqlType.sortOrder += (!sqlType.sortOrder.isEmpty() && !sortBy().isEmpty() ? ", " : "") + route(modulePrefix, sortBy());
        queryModifiers.stream().map(qm -> sqlType.applyQueryModifier(qm, queryModifierNameGenerator)).forEach(queryModifierApplier::add);
        return sqlType;
    }
}
