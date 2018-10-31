package jdbi_modules.util;

import java.util.Objects;

/**
 * @since 14.04.2018
 */
public class Zipped<A, B> {
    private final A first;
    private final B second;

    public Zipped(final A first, final B second) {
        this.first = first;
        this.second = second;
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Zipped)) return false;
        final Zipped<?, ?> zipped = (Zipped<?, ?>) o;
        return Objects.equals(first, zipped.first) &&
                Objects.equals(second, zipped.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
