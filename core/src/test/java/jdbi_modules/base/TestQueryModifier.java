package jdbi_modules.base;

import jdbi_modules.extension.JdbiExtension;
import jdbi_modules.extension.PostgresExtension;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * @since 14.04.2018
 */
@ExtendWith({PostgresExtension.class, JdbiExtension.class})
public class TestQueryModifier {

    @Test
    void testBeanBinding(final Jdbi jdbi) {
        final Object bean = new Object();
        final BeanBinding beanBinding = new BeanBinding("name1", bean);
        jdbi.useHandle(handle -> {
            final Query query = spy(handle.select("SELECT TRUE"));
            final String replacement = "repl";
            beanBinding.apply(query, replacement);
            verify(query).bindBean(replacement, bean);
            assertThat(beanBinding.getInSql()).isEqualTo(":name1");
            assertThat(beanBinding.getName()).isEqualTo("name1");
            assertThat(beanBinding.getValue()).isSameAs(bean);
        });

    }

    @Test
    void testBinding(final Jdbi jdbi) {
        final int number = 12;
        final Binding binding = new Binding("name1", number);
        jdbi.useHandle(handle -> {
            final Query query = spy(handle.select("SELECT :repl"));
            final String replacement = "repl";
            binding.apply(query, replacement);
            assertThat(binding.getInSql()).isEqualTo(":name1");
            assertThat(binding.getName()).isEqualTo("name1");
            assertThat(binding.getValue()).isSameAs(number);
            assertThat(query.mapTo(Integer.class).findOnly()).isEqualTo(number);
        });
    }

    @Test
    void testListDefinition(final Jdbi jdbi) {
        final List<Integer> ints = new LinkedList<>(List.of(1, 2, 3, 4));
        final ListDefinition listDefinition = new ListDefinition("name1", ints);
        jdbi.useHandle(handle -> {
            final Query query = spy(handle.select("SELECT [<repl>]"));
            final String replacement = "repl";
            listDefinition.apply(query, replacement);
            verify(query).defineList(replacement, ints);
            assertThat(listDefinition.getInSql()).isEqualTo("<name1>");
            assertThat(listDefinition.getName()).isEqualTo("name1");
            assertThat(listDefinition.getValue()).isSameAs(ints);
        });
    }

    @Test
    void testDefinition(final Jdbi jdbi) {
        final int number = 12;
        final Definition definition = new Definition("name1", number);
        jdbi.useHandle(handle -> {
            final Query query = spy(handle.select("SELECT <repl> = 12"));
            final String replacement = "repl";
            definition.apply(query, replacement);
            verify(query).define(replacement, number);
            assertThat(definition.getInSql()).isEqualTo("<name1>");
            assertThat(definition.getName()).isEqualTo("name1");
            assertThat(definition.getValue()).isSameAs(number);
        });
    }
}
