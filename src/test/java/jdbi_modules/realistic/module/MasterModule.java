package jdbi_modules.realistic.module;

import jdbi_modules.Collector;
import jdbi_modules.Module;
import jdbi_modules.ModuleMeta;
import jdbi_modules.QueryModifier;
import jdbi_modules.Store;
import jdbi_modules.base.StructuredSql;
import jdbi_modules.base.StructuredSqlGenerator;
import jdbi_modules.internal.RowView;
import jdbi_modules.bean.Master;
import jdbi_modules.bean.Pool;
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
public class MasterModule extends Module<Master, Class<?>, StructuredSql, StructuredSqlGenerator> {
    @NotNull
    @Override
    public Set<QueryModifier> queryModifiers() {
        return Set.of();
    }

    @NotNull
    @Override
    public Map<Type, Function<String, RowMapper<?>>> rowMapper() {
        return Map.of(
                Master.class, prefix -> FieldMapper.of(Master.class, prefix)
        );
    }

    @Override
    public void map(@NotNull final Collector<Collection<Master>, Master> collector,
                    @NotNull final ModuleMeta<Class<?>> moduleMeta,
                    @NotNull final RowView rowView,
                    @NotNull final Store store) {
        if (rowView.getColumn("id", Long.class) != null) {
            collector.appendUniqueWithRowView(Master.class, master -> {
                moduleMeta.callSubmodule(Pool.class, master.getPools(), pool -> {
                    pool.setMaster(master);
                });
            });
        }
    }

    public MasterModule addModule(final Module<Pool, ?, StructuredSql, StructuredSqlGenerator> poolModule) {
        submodules().put(Pool.class, poolModule);
        return this;
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
                return "";
            }
        };
    }
}
