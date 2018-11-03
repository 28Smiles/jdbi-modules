package jdbi_modules.base;

import jdbi_modules.internal.PrefixGenerator;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * @since 14.04.2018
 */
public class TestStructuredSqlGenerator {

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

    private class None implements StructuredSqlGenerator {
        @NotNull
        @Override
        public String select() {
            return "";
        }

        @NotNull
        @Override
        public String from() {
            return "";
        }

        @NotNull
        @Override
        public String joins() {
            return "";
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
    }

    private class Full implements StructuredSqlGenerator {
        @NotNull
        @Override
        public String select() {
            return "A";
        }

        @NotNull
        @Override
        public String from() {
            return "A";
        }

        @NotNull
        @Override
        public String joins() {
            return "A";
        }

        @NotNull
        @Override
        public String sortOrder() {
            return "A";
        }

        @NotNull
        @Override
        public String filter() {
            return "A";
        }
    }
}
