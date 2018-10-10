package jdbi_modules;

import jdbi_modules.internal.RowView;

import java.util.Collection;

/**
 * @param <Type> the type the fallback maps to
 * @since 14.04.2018
 */
public interface Fallback<Type> {
    /**
     * @param collector the collector
     * @param rowView   the row view
     * @param store     the store
     */
    void map(Collector<Collection<Type>, Type> collector, RowView rowView, Store store);
}
