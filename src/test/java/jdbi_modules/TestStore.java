package jdbi_modules;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @since 14.04.2018
 */
public class TestStore {

    @Test
    void test() {
        final Store store = Store.of(new HashMap<>());
        store.place(Integer.class, 12);
        assertThat(store.require(Integer.class)).isEqualTo(12);
    }
}
