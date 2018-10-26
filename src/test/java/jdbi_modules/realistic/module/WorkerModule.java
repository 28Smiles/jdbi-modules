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
import jdbi_modules.bean.Worker;
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
public class WorkerModule extends Module<Worker, Class<?>, StructuredSql, StructuredSqlGenerator> {
    public WorkerModule() {
        fallbacks().put(User.class, (collector, rowView, store) -> collector.append(new User(rowView.getColumn("user_id", Long.class), null)));
    }

    public WorkerModule addModule(final Module<User, ?, StructuredSql, StructuredSqlGenerator> userModule) {
        submodules().put(User.class, userModule);
        return this;
    }

    @NotNull
    @Override
    public Set<QueryModifier> queryModifiers() {
        return Set.of();
    }

    @NotNull
    @Override
    public Map<Type, Function<String, RowMapper<?>>> rowMapper() {
        return Map.of(
            Worker.class, prefix -> FieldMapper.of(Worker.class, prefix)
        );
    }

    @NotNull
    @Override
    public StructuredSqlGenerator getSqlGenerator() {
        return new StructuredSqlGenerator() {
            @NotNull
            @Override
            public String getSelect() {
                return "{{worker}}.id AS {{id}}, {{worker}}.name AS {{name}}, {{worker}}.position AS {{position}}, {{worker}}.user_id AS {{user_id}}";
            }

            @NotNull
            @Override
            public String getFrom() {
                return "";
            }

            @NotNull
            @Override
            public String getJoins() {
                return "LEFT JOIN worker {{worker}} ON {{1, pool}}.id = {{worker}}.pool_id";
            }

            @NotNull
            @Override
            public String getSortOrder() {
                return "{{position}} ASC";
            }

            @NotNull
            @Override
            public String getFilter() {
                return "";
            }
        };
    }

    @Override
    public void map(@NotNull final Collector<Collection<Worker>, Worker> collector, @NotNull final ModuleMeta<Worker, Class<?>> moduleMeta, @NotNull final RowView rowView, @NotNull final Store store) {
        if (rowView.getColumn("id", Long.class) != null) {
            collector.appendUniqueWithRowView(Worker.class, worker -> {
                worker.setUser(moduleMeta.callSubmodule(User.class, User.class));
            });
        }
    }
}
