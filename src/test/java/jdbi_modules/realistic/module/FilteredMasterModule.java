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
    public StructuredSqlGenerator sqlGenerator() {
        return new StructuredSqlGenerator() {
            @NotNull
            @Override
            public String select() {
                return "{{master}}.id AS {{id}}, {{master}}.name AS {{name}}";
            }

            @NotNull
            @Override
            public String from() {
                return "master {{master}}";
            }

            @NotNull
            @Override
            public String joins() {
                return "";
            }

            @NotNull
            @Override
            public String sortOrder() {
                return "";
            }

            @NotNull
            @Override
            public String filter() {
                return "{{master}}.id IN (<ids>)";
            }
        };
    }
}
