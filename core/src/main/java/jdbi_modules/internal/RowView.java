package jdbi_modules.internal;

import org.jdbi.v3.core.generic.GenericType;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.mapper.MappingException;
import org.jdbi.v3.core.mapper.NoSuchMapperException;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A custom row view, similar to {@link org.jdbi.v3.core.result.RowView}.
 *
 * @since 14.04.2018
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class RowView {
    private final String modulePrefix;

    private final StatementContext ctx;
    private final ResultSet rs;

    private final Map<Type, RowMapper<?>> rowMappers = new ConcurrentHashMap<>();
    private final Map<Type, ColumnMapper<?>> columnMappers;

    RowView(final String modulePrefix, final Map<Type, Function<String, RowMapper>> rowMapperFactorys,
            final Map<Type, ColumnMapper<?>> commonColumnMapperMap,
            final ResultSet rs,
            final StatementContext ctx) {
        this.modulePrefix = modulePrefix;
        this.rs = rs;
        this.ctx = ctx;
        this.columnMappers = commonColumnMapperMap;
        rowMappers.putAll(rowMapperFactorys.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().apply(modulePrefix))));
    }

    /**
     * Use a row mapper to extract a type from the current ResultSet row.
     *
     * @param <T>     the type to map
     * @param rowType the Class of the type
     * @return the materialized T
     */
    public <T> T getRow(final Class<T> rowType) {
        return rowType.cast(getRow((Type) rowType));
    }

    /**
     * Use a row mapper to extract a type from the current ResultSet row.
     *
     * @param <T>     the type to map
     * @param rowType the GenericType of the type
     * @return the materialized T
     */
    @SuppressWarnings("unchecked")
    public <T> T getRow(final GenericType<T> rowType) {
        return (T) getRow(rowType.getType());
    }

    /**
     * Use a row mapper to extract a type from the current ResultSet row.
     *
     * @param type the type to map
     * @return the materialized object
     */
    public Object getRow(final Type type) {
        try {
            return rowMapperFor(type).map(rs, ctx);
        } catch (final SQLException e) {
            throw new MappingException(e);
        }
    }

    private RowMapper<?> rowMapperFor(final Type type) {
        if (rowMappers.containsKey(type)) {
            return rowMappers.get(type);
        }

        throw new NoSuchMapperException("No row mapper registered for " + type);
    }

    /**
     * Use a column mapper to extract a type from the current ResultSet row.
     *
     * @param <T>    the type to map
     * @param column the column name
     * @param type   the Class of the type
     * @return the materialized T
     */
    public <T> T getColumn(final String column, final Class<T> type) {
        return type.cast(getColumn(column, (Type) type));
    }

    /**
     * Use a column mapper to extract a type from the current ResultSet row.
     *
     * @param <T>    the type to map
     * @param column the column index
     * @param type   the Class of the type
     * @return the materialized T
     */
    public <T> T getColumn(final int column, final Class<T> type) {
        return type.cast(getColumn(column, (Type) type));
    }

    /**
     * Use a column mapper to extract a type from the current ResultSet row.
     *
     * @param <T>    the type to map
     * @param column the column name
     * @param type   the GenericType of the type
     * @return the materialized T
     */
    @SuppressWarnings("unchecked")
    public <T> T getColumn(final String column, final GenericType<T> type) {
        return (T) getColumn(column, type.getType());
    }

    /**
     * Use a column mapper to extract a type from the current ResultSet row.
     *
     * @param <T>    the type to map
     * @param column the column index
     * @param type   the GenericType of the type
     * @return the materialized T
     */
    @SuppressWarnings("unchecked")
    public <T> T getColumn(final int column, final GenericType<T> type) {
        return (T) getColumn(column, type.getType());
    }

    /**
     * Use a column mapper to extract a type from the current ResultSet row.
     *
     * @param column the column name
     * @param type   the Type of the type
     * @return the materialized object
     */
    public Object getColumn(final String column, final Type type) {
        try {
            return columnMapperFor(type).map(rs, modulePrefix + column, ctx);
        } catch (final SQLException e) {
            throw new MappingException(e);
        }
    }

    /**
     * Use a column mapper to extract a type from the current ResultSet row.
     *
     * @param column the column name
     * @param type   the Class of the type
     * @return the materialized object
     */
    public Object getColumn(final int column, final Type type) {
        try {
            return columnMapperFor(type).map(rs, column, ctx);
        } catch (final SQLException e) {
            throw new MappingException(e);
        }
    }

    private ColumnMapper<?> columnMapperFor(final Type type) {
        return columnMappers.computeIfAbsent(type, t ->
                ctx.findColumnMapperFor(t)
                        .orElseThrow(() -> new NoSuchMapperException("No column mapper registered for " + t)));
    }
}
