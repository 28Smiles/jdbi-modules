package jdbi_modules.realistic.module;

import jdbi_modules.QueryModifier;
import jdbi_modules.base.Binding;
import jdbi_modules.base.ListDefinition;
import jdbi_modules.base.StructuredSqlGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * @author Leon Camus
 * @since 27.05.2019
 */
public class BindingFilteredMasterModule extends MasterModule {
    private final List<Long> ids;

    public BindingFilteredMasterModule(final List<Long> ids) {
        this.ids = ids;
    }

    @NotNull
    @Override
    public Set<QueryModifier> queryModifiers() {
        return IntStream.range(0, ids.size()).mapToObj(
                i -> new Binding("b" + IntStream.range(0, i + 1).mapToObj(j -> "1").collect(Collectors.joining()), ids.get(i))).collect(Collectors.toSet());
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
                return "{{master}}.id IN (" + IntStream.range(0, ids.size()).mapToObj(
                        i -> ":b" + IntStream.range(0, i + 1).mapToObj(j -> "1").collect(Collectors.joining()))
                        .collect(Collectors.joining(", ")) + ")";
            }
        };
    }
}
