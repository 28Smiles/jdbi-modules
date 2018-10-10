package jdbi_modules.extension;

import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import javax.sql.DataSource;

/**
 * @since 14.04.2018
 */
public class JdbiExtension implements BeforeEachCallback, ParameterResolver {
    private Jdbi jdbi;

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        final ExtensionContext.Store store = context.getStore(ExtensionContext.Namespace.GLOBAL);
        final DataSource dataSource = (DataSource) store.get("dataSource");

        jdbi = Jdbi.create(dataSource).installPlugins();
    }

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(Jdbi.class);
    }

    @Override
    public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(Jdbi.class) ? jdbi : null;
    }
}
