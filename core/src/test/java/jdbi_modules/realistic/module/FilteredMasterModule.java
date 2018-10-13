package jdbi_modules.realistic.module;

import jdbi_modules.QueryModifier;
import jdbi_modules.base.ListDefinition;
import jdbi_modules.base.StructuredSqlGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * @since 14.04.2018
 */
public class FilteredMasterModule extends MasterModule {
    private final List<Long> ids;

    public FilteredMasterModule(final List<Long> ids) {
        this.ids = ids;
    }

    @NotNull
    @Override
    public Set<QueryModifier> queryModifiers() {
        return Set.of(
                new ListDefinition("ids", ids)
        );
    }

    @NotNull
    @Override
    public StructuredSqlGenerator getSqlGenerator() {
        return new StructuredSqlGenerator() {
            @NotNull
            @Override
            public String getSelect() {
                return "{{master}}.id AS {{id}}, {{master}}.name AS {{name}}";
            }

            @NotNull
            @Override
            public String getFrom() {
                return "master {{master}}";
            }

            @NotNull
            @Override
            public String getJoins() {
                return "";
            }

            @NotNull
            @Override
            public String getSortOrder() {
                return "";
            }

            @NotNull
            @Override
            public String getFilter() {
                return "{{master}}.id IN (<ids>)";
            }
        };
    }
}
