package jdbi_modules.base;

import jdbi_modules.internal.PrefixGenerator;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @since 14.04.2018
 */
class TestStructuredSqlGenerator {

    @Test
    void testAppend() {
        new None().append(new HashSet<>(), new PrefixGenerator(""), Set.of(),
                new None().sql(new HashSet<>(), new PrefixGenerator(""), Set.of(), new Stack<>()), new Stack<>());
        new None().append(new HashSet<>(), new PrefixGenerator(""), Set.of(),
                new Full().sql(new HashSet<>(), new PrefixGenerator(""), Set.of(), new Stack<>()), new Stack<>());
        new Full().append(new HashSet<>(), new PrefixGenerator(""), Set.of(new Binding("lu", 12)),
                new Full().sql(new HashSet<>(), new PrefixGenerator(""), Set.of(new Binding("lu", 12)), new Stack<>()), new Stack<>());
        new Full().append(new HashSet<>(), new PrefixGenerator(""), Set.of(),
                new None().sql(new HashSet<>(), new PrefixGenerator(""), Set.of(), new Stack<>()), new Stack<>());
    }

    @ParameterizedTest
    @ValueSource(strings = {"\"user\"", ""})
    void testRoutingRejecting(final String value) {
        final Stack<String> stack = new Stack<>();
        stack.push("mod0");
        assertEquals("{{" + value + "}}", new DynamicSortOrderGenerator(value).sql(new HashSet<>(), new PrefixGenerator(""), Set.of(), stack).sortOrder);
    }

    @ParameterizedTest
    @ValueSource(strings = {"id", "mod0exercise.name->>'de'", "exercise.id", "exercise_id", "ÄÖÜßäöü", "_", "-", "0123456789", "a<b", "created - updated",
            "function()"})
    void testRoutingSuccessful(final String value) {
        final Stack<String> stack = new Stack<>();
        stack.push("mod0");
        assertEquals("\"mod0" + value + "\"", new DynamicSortOrderGenerator(value).sql(new HashSet<>(), new PrefixGenerator(""), Set.of(), stack).sortOrder);
    }

    private class None implements StructuredSqlGenerator {
        @NotNull
        @Override
        public String getSelect() {
            return "";
        }

        @NotNull
        @Override
        public String getFrom() {
            return "";
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
    }

    private class Full implements StructuredSqlGenerator {
        @NotNull
        @Override
        public String getSelect() {
            return "A";
        }

        @NotNull
        @Override
        public String getFrom() {
            return "A";
        }

        @NotNull
        @Override
        public String getJoins() {
            return "A";
        }

        @NotNull
        @Override
        public String getSortOrder() {
            return "A";
        }

        @NotNull
        @Override
        public String getFilter() {
            return "A";
        }
    }

    private class DynamicSortOrderGenerator implements StructuredSqlGenerator {
        private final String sortOrder;

        private DynamicSortOrderGenerator(String sortOrder) {
            this.sortOrder = sortOrder;
        }

        @NotNull
        @Override
        public String getSortOrder() {
            return "{{" + this.sortOrder + "}}";
        }
    }
}
