package jdbi_modules.base;

import jdbi_modules.QueryModifier;
import jdbi_modules.SqlType;
import jdbi_modules.base.lexer.QueryModifierLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.jdbi.v3.core.statement.Query;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Consumer;

import static org.antlr.v4.runtime.Recognizer.EOF;

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

    StructuredSql(final String cte, final String select, final String from, final String joins, final String filter, final String sortOrder) {
        this.cte = cte;
        this.select = select;
        this.from = from;
        this.joins = joins;
        this.filter = filter;
        this.sortOrder = sortOrder;
    }

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
    public Consumer<Query> applyQueryModifier(@NotNull final QueryModifier queryModifier, @NotNull final Iterator<String> queryModifierNameGenerator) {
        final String queryModifierName = queryModifierNameGenerator.next();
        cte = this.applyQueryModifier(queryModifier, queryModifierName, cte);
        select = this.applyQueryModifier(queryModifier, queryModifierName, select);
        from = this.applyQueryModifier(queryModifier, queryModifierName, from);
        joins = this.applyQueryModifier(queryModifier, queryModifierName, joins);
        filter = this.applyQueryModifier(queryModifier, queryModifierName, filter);
        sortOrder = this.applyQueryModifier(queryModifier, queryModifierName, sortOrder);
        return query -> queryModifier.apply(query, queryModifierName);
    }

    private String applyQueryModifier(final QueryModifier queryModifier, final String queryModifierName, final String sql) {
        StringBuilder parsedSql = new StringBuilder();
        final String inSql = queryModifier.getInSql();
        final String renamed = inSql.replace(queryModifier.getName(), queryModifierName);
        final QueryModifierLexer lexer = new QueryModifierLexer(CharStreams.fromString(sql));
        Token ct = lexer.nextToken();
        while (ct.getType() != EOF) {
            if ((ct.getType() == QueryModifierLexer.BINDING || ct.getType() ==  QueryModifierLexer.DEFINITION)
                    && ct.getText().equals(inSql)) {
                parsedSql.append(renamed);
            } else {
                parsedSql.append(ct.getText());
            }
            ct = lexer.nextToken();
        }
        return parsedSql.toString();
    }

    private String conditionalConcat(final String prepend, final String str) {
        return str.isEmpty() ? "" : (prepend + str);
    }
}
