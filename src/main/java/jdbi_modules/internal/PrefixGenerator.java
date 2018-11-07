package jdbi_modules.internal;

import java.util.Iterator;

/**
 * @since 14.04.2018
 */
public final class PrefixGenerator implements Iterator<String> {
    private final String prefix;
    private int i = 0;

    /**
     * @param prefix the prefix of the generator
     */
    public PrefixGenerator(final String prefix) {
        this.prefix = prefix;
    }

    /**
     * @param prefix the prefix of the generator
     * @param start the value to start from
     */
    PrefixGenerator(final String prefix, final int start) {
        this.prefix = prefix;
        this.i = start;
    }

    @Override
    public boolean hasNext() {
        return i < Integer.MAX_VALUE;
    }

    @Override
    public String next() {
        return prefix + i++;
    }
}
