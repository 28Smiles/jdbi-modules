package jdbi_modules.internal;

import jdbi_modules.Scene1;
import jdbi_modules.bean.Master;
import jdbi_modules.bean.Pool;
import jdbi_modules.bean.Worker;
import jdbi_modules.extension.JdbiExtension;
import jdbi_modules.extension.PostgresExtension;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.generic.GenericType;
import org.jdbi.v3.core.mapper.MappingException;
import org.jdbi.v3.core.mapper.NoSuchMapperException;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.mapper.reflect.FieldMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @since 14.04.2018
 */
@ExtendWith({PostgresExtension.class, JdbiExtension.class})
public class TestRowView {
    private Scene1 scene = new Scene1();

    @BeforeEach
    void before(final Jdbi jdbi) {
        scene.build(jdbi);
    }

    private void build(final Handle handle, boolean open, Consumer<RowView> rowViewConsumer) {
        handle.select("SELECT id, name FROM master WHERE id = " + scene.getMasters().get(0).getId()).scanResultSet((resultSetSupplier, ctx) -> {
            ResultSet resultSet = resultSetSupplier.get();
            if (open) {
                resultSet.next();
            }
            rowViewConsumer.accept(new RowView("", Map.of(
                    Master.class, prefix -> FieldMapper.of(Master.class, prefix),
                    Worker.class, prefix -> (RowMapper<Object>) (rs, ctx1) -> {
                        throw new SQLException();
                    }
            ), new ConcurrentHashMap<>(), resultSet, ctx));
            return null;
        });
    }

    @Test
    void testGetRowType(final Jdbi jdbi) {
        jdbi.useHandle(handle -> build(handle, true, rowView -> {
            assertThat(rowView.getRow(new GenericType<Master>() {
            })).isEqualTo(scene.getMasters().get(0));
        }));
    }

    @Test
    void testGetRow(final Jdbi jdbi) {
        jdbi.useHandle(handle -> build(handle, true, rowView -> {
            assertThat(rowView.getRow(Master.class)).isEqualTo(scene.getMasters().get(0));
        }));
    }

    @Test
    void testGetRowFailing(final Jdbi jdbi) {
        jdbi.useHandle(handle -> build(handle, true, rowView -> {
            assertThatThrownBy(() -> rowView.getRow(Worker.class)).isInstanceOf(MappingException.class);
        }));
    }

    @Test
    void testGetRowNotFound(final Jdbi jdbi) {
        jdbi.useHandle(handle -> build(handle, true, rowView -> {
            assertThatThrownBy(() -> rowView.getRow(Pool.class)).isInstanceOf(NoSuchMapperException.class);
        }));
    }

    @Test
    void testGetColumnByName(final Jdbi jdbi) {
        jdbi.useHandle(handle -> build(handle, true, rowView -> {
            assertThat(rowView.getColumn("id", Long.class)).isEqualTo(scene.getMasters().get(0).getId());
        }));
    }

    @Test
    void testGetColumnById(final Jdbi jdbi) {
        jdbi.useHandle(handle -> build(handle, true, rowView -> {
            assertThat(rowView.getColumn(1, Long.class)).isEqualTo(scene.getMasters().get(0).getId());
        }));
    }

    @Test
    void testGetGenericColumnByName(final Jdbi jdbi) {
        jdbi.useHandle(handle -> build(handle, true, rowView -> {
            assertThat(rowView.getColumn("id", new GenericType<Long>() {
            })).isEqualTo(scene.getMasters().get(0).getId());
        }));
    }

    @Test
    void testGetGenericColumnById(final Jdbi jdbi) {
        jdbi.useHandle(handle -> build(handle, true, rowView -> {
            assertThat(rowView.getColumn(1, new GenericType<Long>() {
            })).isEqualTo(scene.getMasters().get(0).getId());
        }));
    }

    @Test
    void testGetColumnByNameMappingError(final Jdbi jdbi) {
        jdbi.useHandle(handle -> build(handle, false, rowView -> {
            assertThatThrownBy(() -> rowView.getColumn("id", Long.class)).isInstanceOf(MappingException.class);
        }));
    }

    @Test
    void testGetColumnByIdMappingError(final Jdbi jdbi) {
        jdbi.useHandle(handle -> build(handle, false, rowView -> {
            assertThatThrownBy(() -> rowView.getColumn(1, Long.class)).isInstanceOf(MappingException.class);
        }));
    }

    @Test
    void testGetGenericColumnByNameMappingError(final Jdbi jdbi) {
        jdbi.useHandle(handle -> build(handle, false, rowView -> {
            assertThatThrownBy(() -> rowView.getColumn("id", new GenericType<Long>() {
            })).isInstanceOf(MappingException.class);
        }));
    }

    @Test
    void testGetGenericColumnByIdMappingError(final Jdbi jdbi) {
        jdbi.useHandle(handle -> build(handle, false, rowView -> {
            assertThatThrownBy(() -> rowView.getColumn(1, new GenericType<Long>() {
            })).isInstanceOf(MappingException.class);
        }));
    }

    @Test
    void testGetGenericColumnByIdNoSuchMappingError(final Jdbi jdbi) {
        jdbi.useHandle(handle -> build(handle, false, rowView -> {
            assertThatThrownBy(() -> rowView.getColumn(1, new GenericType<Master>() {
            })).isInstanceOf(NoSuchMapperException.class);
        }));
    }
}
