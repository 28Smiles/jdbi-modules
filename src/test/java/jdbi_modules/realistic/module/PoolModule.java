package jdbi_modules.realistic.module;

import jdbi_modules.Collector;
import jdbi_modules.Module;
import jdbi_modules.ModuleMeta;
import jdbi_modules.QueryModifier;
import jdbi_modules.base.StructuredSql;
import jdbi_modules.base.StructuredSqlGenerator;
import jdbi_modules.internal.RowView;
import jdbi_modules.bean.Pool;
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
public class PoolModule extends Module<Pool, Class<?>, StructuredSql, StructuredSqlGenerator> {
    @NotNull
    @Override
    public Set<QueryModifier> queryModifiers() {
        return Set.of();
    }

    @NotNull
    @Override
    public Map<Type, Function<String, RowMapper<?>>> rowMapper() {
        return Map.of(
                Pool.class, prefix -> FieldMapper.of(Pool.class, prefix)
        );
    }

    public PoolModule addModule(final Module<Worker, ?, StructuredSql, StructuredSqlGenerator> workerModule) {
        submodules().put(Worker.class, workerModule);
        return this;
    }

    @NotNull
    @Override
    public StructuredSqlGenerator sqlGenerator() {
        return new StructuredSqlGenerator() {
            @NotNull
            @Override
            public String select() {
                return "{{pool}}.id AS {{id}}, {{pool}}.name AS {{name}}";
            }

            @NotNull
            @Override
            public String from() {
                return "";
            }

            @NotNull
            @Override
            public String joins() {
                return "LEFT JOIN pool {{pool}} ON {{1, master}}.id = {{pool}}.master_id";
            }

            @NotNull
            @Override
            public String sortBy() {
                return "";
            }

            @NotNull
            @Override
            public String where() {
                return "";
            }
        };
    }

    @Override
    public void map(@NotNull final Collector<Collection<Pool>, Pool> collector,
                    @NotNull final ModuleMeta<Class<?>> moduleMeta,
                    @NotNull final RowView rowView) {
        if (rowView.getColumn("id", Long.class) != null) {
            collector.appendUniqueWithRowView(Pool.class, pool -> {
                moduleMeta.callSubmodule(Worker.class, pool.getWorkers(), worker -> {
                    worker.setPool(pool);
                });
            });
        }
    }
}
