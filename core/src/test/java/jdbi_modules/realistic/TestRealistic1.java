package jdbi_modules.realistic;

import jdbi_modules.extension.JdbiExtension;
import jdbi_modules.extension.PostgresExtension;
import jdbi_modules.bean.Bean;
import jdbi_modules.bean.Master;
import jdbi_modules.bean.Pool;
import jdbi_modules.bean.Worker;
import jdbi_modules.realistic.module.MasterModule;
import jdbi_modules.realistic.module.PoolModule;
import jdbi_modules.realistic.module.WorkerModule;
import jdbi_modules.util.Zipped;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.paumard.streams.StreamsUtils.zip;

/**
 * @since 14.04.2018
 */
@ExtendWith({PostgresExtension.class, JdbiExtension.class})
public class TestRealistic1 {
    private static final String insertMaster = "INSERT INTO master (id, name) VALUES (:id, :name)";
    private static final String insertPool = "INSERT INTO pool (id, master_id, name) VALUES (:id, :master.id, :name)";
    private static final String insertWorker = "INSERT INTO worker (id, pool_id, position, name) VALUES (:id, :pool.id, :position, :name)";

    private List<Master> masters = new ArrayList<>();

    @BeforeEach
    void before(final Jdbi jdbi) {
        jdbi.useHandle(handle -> {
            handle.createUpdate("CREATE TABLE master (id BIGINT PRIMARY KEY, name TEXT)").execute();
            handle.createUpdate("CREATE TABLE pool (id BIGINT PRIMARY KEY, master_id BIGINT, name TEXT)").execute();
            handle.createUpdate("CREATE TABLE worker (id BIGINT PRIMARY KEY, pool_id BIGINT, position INT, name TEXT)").execute();
        });

        jdbi.useHandle(handle -> {
            masters.add(new Master(1, "Ralf"));
            masters.add(new Master(2, "Roland"));
            masters.add(new Master(3, "Reinhard"));

            int i = 0;
            for (final Master master : masters) {
                final int off = (int) (Math.random() * 16);
                IntStream.range(i, i + off).mapToObj(id -> new Pool(id, "pool" + id, master)).forEach(master.getPools()::add);
                i += off;
            }
            i = 0;
            for (final Pool pool : masters.stream().map(Master::getPools).map(Set::stream).flatMap(s -> s).collect(Collectors.toList())) {
                final int off = (int) (Math.random() * 16);
                IntStream.range(i, i + off).mapToObj(id -> new Worker(id, "pool" + id, pool, (int) (Math.random() * 99999999))).forEach(pool.getWorkers()::add);
                pool.getWorkers().sort(Comparator.comparingInt(Worker::getPosition));
                i += off;
            }

            masters.forEach(master -> {
                handle.createUpdate(insertMaster).bindBean(master).execute();
                master.getPools().forEach(pool -> {
                    handle.createUpdate(insertPool).bindBean(pool).execute();
                    pool.getWorkers().forEach(worker -> {
                        handle.createUpdate(insertWorker).bindBean(worker).execute();
                    });
                });
            });
        });
    }

    @Test
    void realisticTestSingle(final Jdbi jdbi) {
        final Set<Master> masters = jdbi.withHandle(handle -> new MasterModule().run(handle, new HashSet<>()));
        assertThat(masters).containsExactlyInAnyOrderElementsOf(this.masters);
    }

    @RepeatedTest(20)
    void realisticTestDouble(final Jdbi jdbi) {
        final Set<Master> masters = jdbi.withHandle(handle -> new MasterModule().addModule(new PoolModule()).run(handle, new HashSet<>()));
        assertThat(masters).containsExactlyInAnyOrderElementsOf(masters);
        zip(masters.stream().sorted(Comparator.comparingLong(Bean::getId)).map(Master::getPools), this.masters.stream().map(Master::getPools), Zipped::new).forEach(zipped -> {
            assertThat(zipped.getFirst()).containsExactlyInAnyOrderElementsOf(zipped.getSecond());
        });
    }

    @RepeatedTest(40)
    void realisticTestTriple(final Jdbi jdbi) {
        final Set<Master> masters = jdbi.withHandle(handle -> new MasterModule().addModule(new PoolModule().addModule(new WorkerModule())).run(handle, new HashSet<>()));
        assertThat(masters).containsExactlyInAnyOrderElementsOf(masters);
        zip(masters.stream().sorted(Comparator.comparingLong(Bean::getId)).map(Master::getPools),
                this.masters.stream().map(Master::getPools), Zipped::new).forEach(zippedPools -> {
            zip(zippedPools.getFirst().stream().sorted(Comparator.comparingLong(Bean::getId)).map(Pool::getWorkers),
                    zippedPools.getSecond().stream().sorted(Comparator.comparingLong(Bean::getId)).map(Pool::getWorkers), Zipped::new).forEach(zippedWorker -> {
                assertThat(zippedWorker.getFirst()).containsExactlyElementsOf(zippedWorker.getSecond());
            });
            assertThat(zippedPools.getFirst()).containsExactlyInAnyOrderElementsOf(zippedPools.getSecond());
        });
    }
}
