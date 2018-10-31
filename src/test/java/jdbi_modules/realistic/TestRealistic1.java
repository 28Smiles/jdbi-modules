package jdbi_modules.realistic;

import jdbi_modules.Scene1;
import jdbi_modules.bean.Bean;
import jdbi_modules.bean.Master;
import jdbi_modules.bean.Pool;
import jdbi_modules.bean.Worker;
import jdbi_modules.extension.JdbiExtension;
import jdbi_modules.extension.PostgresExtension;
import jdbi_modules.realistic.module.FilteredMasterModule;
import jdbi_modules.realistic.module.MasterModule;
import jdbi_modules.realistic.module.PoolModule;
import jdbi_modules.realistic.module.UserModule;
import jdbi_modules.realistic.module.WorkerModule;
import jdbi_modules.util.Zipped;
import org.assertj.core.api.Assertions;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.paumard.streams.StreamsUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.paumard.streams.StreamsUtils.zip;

/**
 * @since 14.04.2018
 */
@ExtendWith({PostgresExtension.class, JdbiExtension.class})
public class TestRealistic1 {
    private Scene1 scene = new Scene1();

    @BeforeEach
    void before(final Jdbi jdbi) {
        scene.build(jdbi);
    }

    @Test
    void realisticTestSingle(final Jdbi jdbi) {
        final Set<Master> masters = jdbi.withHandle(handle -> new MasterModule().run(handle, new HashSet<>()));
        Assertions.assertThat(masters).containsExactlyInAnyOrderElementsOf(this.scene.getMasters());
    }

    @RepeatedTest(20)
    void realisticTestDouble(final Jdbi jdbi) {
        final Set<Master> masters = jdbi.withHandle(
                handle -> new MasterModule()
                        .addModule(new PoolModule()).run(handle, new HashSet<>()));
        Assertions.assertThat(masters).containsExactlyInAnyOrderElementsOf(masters);
        StreamsUtils.zip(masters.stream().sorted(Comparator.comparingLong(Bean::getId)).map(Master::getPools), this.scene.getMasters().stream().map(Master::getPools), Zipped::new).forEach(zipped -> {
            Assertions.assertThat(zipped.getFirst()).containsExactlyInAnyOrderElementsOf(zipped.getSecond());
        });
    }

    @RepeatedTest(40)
    void realisticTestTriple(final Jdbi jdbi) {
        final Set<Master> masters = jdbi.withHandle(
                handle -> new MasterModule()
                        .addModule(new PoolModule()
                                .addModule(new WorkerModule())).run(handle, new HashSet<>()));
        Assertions.assertThat(masters).containsExactlyInAnyOrderElementsOf(masters);
        StreamsUtils.zip(masters.stream().sorted(Comparator.comparingLong(Bean::getId)).map(Master::getPools),
                this.scene.getMasters().stream().map(Master::getPools), Zipped::new).forEach(zippedPools -> {
            StreamsUtils.zip(zippedPools.getFirst().stream().sorted(Comparator.comparingLong(Bean::getId)).map(Pool::getWorkers),
                    zippedPools.getSecond().stream().sorted(Comparator.comparingLong(Bean::getId)).map(Pool::getWorkers), Zipped::new).forEach(zippedWorker -> {
                Assertions.assertThat(zippedWorker.getFirst()).containsExactlyElementsOf(zippedWorker.getSecond());
            });
            Assertions.assertThat(zippedPools.getFirst()).containsExactlyInAnyOrderElementsOf(zippedPools.getSecond());
        });
    }

    @RepeatedTest(40)
    void realisticTestQuadruple(final Jdbi jdbi) {
        final Set<Master> masters = jdbi.withHandle(
                handle -> new MasterModule()
                        .addModule(new PoolModule()
                                .addModule(new WorkerModule()
                                        .addModule(new UserModule()))).run(handle, new HashSet<>()));
        Assertions.assertThat(masters).containsExactlyInAnyOrderElementsOf(masters);
        StreamsUtils.zip(masters.stream().sorted(Comparator.comparingLong(Bean::getId)).map(Master::getPools),
                this.scene.getMasters().stream().map(Master::getPools), Zipped::new).forEach(zippedPools -> {
            StreamsUtils.zip(zippedPools.getFirst().stream().sorted(Comparator.comparingLong(Bean::getId)).map(Pool::getWorkers),
                    zippedPools.getSecond().stream().sorted(Comparator.comparingLong(Bean::getId)).map(Pool::getWorkers), Zipped::new).forEach(zippedWorker -> {
                Assertions.assertThat(zippedWorker.getFirst()).containsExactlyElementsOf(zippedWorker.getSecond());
                Assertions.assertThat(zippedWorker.getFirst().stream().map(Worker::getUser).collect(Collectors.toList()))
                        .containsExactlyElementsOf(zippedWorker.getSecond().stream().map(Worker::getUser).collect(Collectors.toList()));
            });
            Assertions.assertThat(zippedPools.getFirst()).containsExactlyInAnyOrderElementsOf(zippedPools.getSecond());
        });
    }

    @Test
    void realisticTestTripleFiltered1(final Jdbi jdbi) {
        final Set<Master> masters = jdbi.withHandle(handle -> new FilteredMasterModule(new ArrayList<>(List.of(
                scene.getMasters().get(0).getId()))).addModule(new PoolModule().addModule(new WorkerModule())).run(handle, new HashSet<>()));
        Assertions.assertThat(masters).containsExactlyInAnyOrder(scene.getMasters().get(0));
    }

    @Test
    void realisticTestTripleFiltered2(final Jdbi jdbi) {
        final Set<Master> masters = jdbi.withHandle(handle -> new FilteredMasterModule(new ArrayList<>(List.of(
                scene.getMasters().get(0).getId(),
                scene.getMasters().get(1).getId()))).addModule(new PoolModule().addModule(new WorkerModule())).run(handle, new HashSet<>()));
        Assertions.assertThat(masters).containsExactlyInAnyOrder(scene.getMasters().get(0), scene.getMasters().get(1));
    }
}
