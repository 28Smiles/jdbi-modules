package jdbi_modules.realistic.module;

import jdbi_modules.QueryModifier;
import jdbi_modules.base.BeanBinding;
import jdbi_modules.base.StructuredSqlGenerator;
import jdbi_modules.bean.Master;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author Leon Camus
 * @since 27.05.2019
 */
public class BeanBindingFilteredMasterModule extends MasterModule {
    private final Master master;

    public BeanBindingFilteredMasterModule(final Master master) {
        this.master = master;
    }

    @NotNull
    @Override
    public Set<QueryModifier> queryModifiers() {
        return Set.of(
                new BeanBinding("master", master)
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
                return "{{master}}.id = :master.id AND {{master}}.name = :master.name";
            }
        };
    }
}
