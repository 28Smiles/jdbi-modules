package jdbi_modules.base;

import jdbi_modules.QueryModifier;
import org.jdbi.v3.core.statement.Query;
import org.jetbrains.annotations.NotNull;

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
    Pattern PATTERN = Pattern.compile("\\{\\{\\s*((\\d*)\\s*,\\s*)?([A-Za-z0-9_. \\-><'\"\\x7f-\\xff]+)\\s*}}");

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
    default String getSelect() {
        return "";
    }

    /**
     * @return the part of the cte clause of the query.
     */
    @NotNull
    default String getCte() {
        return "";
    }

    /**
     * @return the part of the from clause of the query.
     */
    @NotNull
    default String getFrom() {
        return "";
    }

    /**
     * @return the joins of the query.
     */
    @NotNull
    default String getJoins() {
        return "";
    }

    /**
     * @return the sort orders of the query.
     */
    @NotNull
    default String getSortOrder() {
        return "";
    }

    /**
     * @return the filters of the query.
     */
    @NotNull
    default String getFilter() {
        return "";
    }

    @NotNull
    @Override
    default StructuredSql sql(@NotNull final Set<Consumer<Query>> queryModifierApplier,
                              @NotNull final Iterator<String> queryModifierNameGenerator,
                              @NotNull final Set<jdbi_modules.QueryModifier> queryModifiers,
                              @NotNull final Stack<String> modulePrefix) {
        final StructuredSql structuredSql = new StructuredSql(
                route(modulePrefix, getCte()),
                route(modulePrefix, getSelect()),
                route(modulePrefix, getFrom()),
                route(modulePrefix, getJoins()),
                route(modulePrefix, getFilter()),
                route(modulePrefix, getSortOrder()));
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
        sqlType.cte += (!sqlType.cte.isEmpty() && !getCte().isEmpty() ? ',' : "") + route(modulePrefix, getCte());
        sqlType.select += (!sqlType.select.isEmpty() && !getSelect().isEmpty() ? ',' : "") + route(modulePrefix, getSelect());
        sqlType.from += (!sqlType.from.isEmpty() && !getFrom().isEmpty() ? ' ' : "") + route(modulePrefix, getFrom());
        sqlType.joins += (!sqlType.joins.isEmpty() && !getJoins().isEmpty() ? ' ' : "") + route(modulePrefix, getJoins());
        sqlType.filter += (!sqlType.filter.isEmpty() && !getFilter().isEmpty() ? " AND " : "") + route(modulePrefix, getFilter());
        sqlType.sortOrder += (!sqlType.sortOrder.isEmpty() && !getSortOrder().isEmpty() ? ',' : "") + route(modulePrefix, getSortOrder());
        queryModifiers.stream().map(qm -> sqlType.applyQueryModifier(qm, queryModifierNameGenerator)).forEach(queryModifierApplier::add);
        return sqlType;
    }
}
