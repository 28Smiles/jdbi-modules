package jdbi_modules.realistic.module;

import jdbi_modules.Collector;
import jdbi_modules.Module;
import jdbi_modules.ModuleMeta;
import jdbi_modules.QueryModifier;
import jdbi_modules.Store;
import jdbi_modules.base.StructuredSql;
import jdbi_modules.base.StructuredSqlGenerator;
import jdbi_modules.bean.User;
import jdbi_modules.internal.RowView;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.mapper.reflect.FieldMapper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @since 14.04.2018
 */
public class UserModule extends Module<User, Class<?>, StructuredSql, StructuredSqlGenerator> {
    @NotNull
    @Override
    public Set<QueryModifier> queryModifiers() {
        return Set.of();
    }

    @NotNull
    @Override
    public Map<Type, Function<String, RowMapper<?>>> rowMapper() {
        return Map.of(
                User.class, prefix -> FieldMapper.of(User.class, prefix)
        );
    }

    @NotNull
    @Override
    public StructuredSqlGenerator sqlGenerator() {
        return new StructuredSqlGenerator() {
            @NotNull
            @Override
            public String select() {
                return "{{user}}.id AS {{id}}, {{user}}.name AS {{name}}";
            }

            @NotNull
            @Override
            public String from() {
                return "";
            }

            @NotNull
            @Override
            public String joins() {
                return "LEFT JOIN \"user\" {{user}} ON {{1, worker}}.user_id = {{user}}.id";
            }

            @NotNull
            @Override
            public String sortOrder() {
                return "";
            }

            @NotNull
            @Override
            public String filter() {
                return "";
            }
        };
    }

    @Override
    public void map(@NotNull final Collector<Collection<User>, User> collector,
                    @NotNull final ModuleMeta<Class<?>> moduleMeta,
                    @NotNull final RowView rowView,
                    @NotNull final Store store) {
        if (rowView.getColumn("id", Long.class) != null) {
            collector.appendUniqueWithRowView(User.class);
        }
    }
}
