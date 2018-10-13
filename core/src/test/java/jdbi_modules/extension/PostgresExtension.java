package jdbi_modules.extension;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.postgresql.ds.PGSimpleDataSource;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;
import ru.yandex.qatools.embed.postgresql.distribution.Version;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

/**
 * @since 14.04.2018
 */
public class PostgresExtension implements BeforeEachCallback, AfterEachCallback, BeforeAllCallback, AfterAllCallback {
    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        final ExtensionContext.Store store = context.getStore(ExtensionContext.Namespace.GLOBAL);
        final DataSource dataSource = (DataSource) store.get("dataSource");
        dropAll(dataSource, EmbeddedPostgres.DEFAULT_USER);
    }

    @Override
    public void afterEach(final ExtensionContext context) {
        final ExtensionContext.Store store = context.getStore(ExtensionContext.Namespace.GLOBAL);
        final DataSource dataSource = (DataSource) store.get("dataSource");
        dropAll(dataSource, EmbeddedPostgres.DEFAULT_USER);
    }

    private static void dropAll(final DataSource dataSource, final String user) {
        try(final Connection connection = dataSource.getConnection()) {
            try(final Statement statement = connection.createStatement()) {
                statement.execute("DROP SCHEMA public CASCADE;CREATE SCHEMA public;GRANT ALL ON SCHEMA public TO "
                        + user
                        + ";GRANT ALL ON SCHEMA public TO public;");
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterAll(final ExtensionContext context) throws Exception {
        final ExtensionContext.Store store = context.getStore(ExtensionContext.Namespace.GLOBAL);
        final EmbeddedPostgres postgres = (EmbeddedPostgres) store.get("postgres");
        if (postgres != null) {
            postgres.stop();
        }
    }

    @Override
    public void beforeAll(final ExtensionContext context) throws Exception {
        final ExtensionContext.Store store = context.getStore(ExtensionContext.Namespace.GLOBAL);
        final Dotenv dotenv = Dotenv.load();
        if (dotenv.get("DEDICATED_TEST_DATABASE_HOST") == null) {
            final EmbeddedPostgres postgres = new EmbeddedPostgres(Version.V10_3);
            postgres.start(EmbeddedPostgres.DEFAULT_HOST, 5422, EmbeddedPostgres.DEFAULT_DB_NAME);
            final PGSimpleDataSource dataSource = new PGSimpleDataSource();
            dataSource.setServerName(EmbeddedPostgres.DEFAULT_HOST);
            dataSource.setPortNumber(5422);
            dataSource.setDatabaseName(EmbeddedPostgres.DEFAULT_DB_NAME);
            dataSource.setUser(EmbeddedPostgres.DEFAULT_USER);
            dataSource.setPassword(EmbeddedPostgres.DEFAULT_PASSWORD);

            dropAll(dataSource, EmbeddedPostgres.DEFAULT_USER);
            store.put("postgres", postgres);
            store.put("dataSource", dataSource);
        } else {
            final PGSimpleDataSource dataSource = new PGSimpleDataSource();
            dataSource.setServerName(dotenv.get("DEDICATED_TEST_DATABASE_HOST"));
            dataSource.setPortNumber(Integer.parseInt(Objects.requireNonNull(dotenv.get("DEDICATED_TEST_DATABASE_PORT"))));
            dataSource.setDatabaseName(dotenv.get("DEDICATED_TEST_DATABASE_NAME"));
            dataSource.setUser(dotenv.get("DEDICATED_TEST_DATABASE_USER"));
            dataSource.setPassword(dotenv.get("DEDICATED_TEST_DATABASE_PASSWORD"));
            store.put("dataSource", dataSource);
        }
    }
}
