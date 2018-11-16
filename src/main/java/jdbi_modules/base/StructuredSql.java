package jdbi_modules.base;

import jdbi_modules.QueryModifier;
import jdbi_modules.SqlType;
import org.jdbi.v3.core.statement.Query;

import javax.validation.constraints.NotNull;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * @since 14.04.2018
 */
public class StructuredSql implements SqlType {
    @SuppressWarnings("VisibilityModifier")
    String cte;

    @SuppressWarnings("VisibilityModifier")
    String select;

    @SuppressWarnings("VisibilityModifier")
    String from;

    @SuppressWarnings("VisibilityModifier")
    String joins;

    @SuppressWarnings("VisibilityModifier")
    String filter;

    @SuppressWarnings("VisibilityModifier")
    String sortOrder;

    StructuredSql(@NotNull final String cte,
                  @NotNull final String select,
                  @NotNull final String from,
                  @NotNull final String joins,
                  @NotNull final String filter,
                  @NotNull final String sortOrder) {
        this.cte = cte;
        this.select = select;
        this.from = from;
        this.joins = joins;
        this.filter = filter;
        this.sortOrder = sortOrder;
    }

    @NotNull
    @Override
    public final String toQuery() {
        return (cte.isEmpty() ? "" : "WITH RECURSIVE " + cte + " ") + "SELECT " + select + " FROM " + from
                + conditionalConcat(" ", joins)
                + conditionalConcat(" WHERE ", filter)
                + conditionalConcat(" ORDER BY ", sortOrder);
    }

    /**
     * Applies a query modifier to the sql.
     *
     * @param queryModifier              the query modifier to apply
     * @param queryModifierNameGenerator a generator to generate a generic name for the query modifier
     * @return a consumer of a query to apply the query modifier to the query later
     */
    public Consumer<Query> applyQueryModifier(@NotNull final QueryModifier queryModifier,
                                              @NotNull final Iterator<String> queryModifierNameGenerator) {
        final String queryModifierName = queryModifierNameGenerator.next();
        final String inSql = queryModifier.getInSql();
        final String name = queryModifier.getName();
        cte = cte.replace(inSql, inSql.replace(name, queryModifierName));
        select = select.replace(inSql, inSql.replace(name, queryModifierName));
        from = from.replace(inSql, inSql.replace(name, queryModifierName));
        joins = joins.replace(inSql, inSql.replace(name, queryModifierName));
        filter = filter.replace(inSql, inSql.replace(name, queryModifierName));
        sortOrder = sortOrder.replace(inSql, inSql.replace(name, queryModifierName));
        return query -> queryModifier.apply(query, queryModifierName);
    }

    @NotNull
    private String conditionalConcat(final @NotNull String prepend, final @NotNull String str) {
        return str.isEmpty() ? "" : (prepend + str);
    }
}
