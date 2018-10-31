package jdbi_modules.realistic;

import jdbi_modules.Scene1;
import jdbi_modules.bean.Bean;
import jdbi_modules.bean.Master;
import jdbi_modules.extension.JdbiExtension;
import jdbi_modules.extension.PostgresExtension;
import jdbi_modules.realistic.module.FilteredMasterModule;
import org.assertj.core.api.Assertions;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @since 14.04.2018
 */
@ExtendWith({PostgresExtension.class, JdbiExtension.class})
public class TestRealisticBinding {
    private Scene1 scene = new Scene1();

    @BeforeEach
    void before(final Jdbi jdbi) {
        scene.build(jdbi);
    }

    @Test
    void realisticTest1(final Jdbi jdbi) {
        final List<Master> mastersF = scene.getMasters().subList(0, 1);
        final Set<Master> masters = jdbi.withHandle(
                handle -> new FilteredMasterModule(mastersF.stream().map(Bean::getId).collect(Collectors.toList())).run(handle, new HashSet<>()));
        assertThat(masters).containsExactlyInAnyOrderElementsOf(mastersF);
    }

    @Test
    void realisticTest2(final Jdbi jdbi) {
        final List<Master> mastersF = scene.getMasters().subList(1, 2);
        final Set<Master> masters = jdbi.withHandle(
                handle -> new FilteredMasterModule(mastersF.stream().map(Bean::getId).collect(Collectors.toList())).run(handle, new HashSet<>()));
        assertThat(masters).containsExactlyInAnyOrderElementsOf(mastersF);
    }
}
