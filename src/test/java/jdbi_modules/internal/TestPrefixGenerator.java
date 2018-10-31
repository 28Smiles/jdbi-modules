package jdbi_modules.internal;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @since 14.04.2018
 */
public class TestPrefixGenerator {

    @Test
    void test() {
        PrefixGenerator prefixGenerator = new PrefixGenerator("");
        assertThat(prefixGenerator.hasNext()).isTrue();
        assertThat(prefixGenerator.next()).isEqualTo("0");
    }
}
