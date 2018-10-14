package jdbi_modules.base;

import jdbi_modules.internal.PrefixGenerator;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.Stack;

/**
 * @since 14.04.2018
 */
public class TestStructuredSqlGenerator {

    @Test
    void testAppend() {
        new None().append(Set.of(), new PrefixGenerator(""), Set.of(),
                new None().sql(Set.of(), new PrefixGenerator(""), Set.of(), new Stack<>()), new Stack<>());
        new None().append(Set.of(), new PrefixGenerator(""), Set.of(),
                new Full().sql(Set.of(), new PrefixGenerator(""), Set.of(), new Stack<>()), new Stack<>());
        new Full().append(Set.of(), new PrefixGenerator(""), Set.of(),
                new Full().sql(Set.of(), new PrefixGenerator(""), Set.of(), new Stack<>()), new Stack<>());
        new Full().append(Set.of(), new PrefixGenerator(""), Set.of(),
                new None().sql(Set.of(), new PrefixGenerator(""), Set.of(), new Stack<>()), new Stack<>());
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
}
