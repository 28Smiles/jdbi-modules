package jdbi_modules;

import jdbi_modules.bean.Master;
import jdbi_modules.bean.Pool;
import jdbi_modules.bean.Worker;
import org.jdbi.v3.core.Jdbi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @since 14.04.2018
 */
public class Scene1 {
    private static final String insertMaster = "INSERT INTO master (id, name) VALUES (:id, :name)";
    private static final String insertPool = "INSERT INTO pool (id, master_id, name) VALUES (:id, :master.id, :name)";
    private static final String insertWorker = "INSERT INTO worker (id, pool_id, position, name) VALUES (:id, :pool.id, :position, :name)";

    private List<Master> masters = new ArrayList<>();
    private List<Pool> pools = new ArrayList<>();
    private List<Worker> workers = new ArrayList<>();

    public void build(final Jdbi jdbi) {
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
                IntStream.range(i, i + off).mapToObj(id -> new Pool(id, "pool" + id, master)).forEach(pool -> {
                    master.getPools().add(pool);
                    pools.add(pool);
                });
                i += off;
            }
            i = 0;
            for (final Pool pool : masters.stream().map(Master::getPools).map(Set::stream).flatMap(s -> s).collect(Collectors.toList())) {
                final int off = (int) (Math.random() * 16);
                IntStream.range(i, i + off).mapToObj(id -> new Worker(id, "pool" + id, pool, (int) (Math.random() * 99999999))).forEach(worker -> {
                    pool.getWorkers().add(worker);
                    workers.add(worker);
                });
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

    public List<Master> getMasters() {
        return masters;
    }

    public List<Pool> getPools() {
        return pools;
    }

    public List<Worker> getWorkers() {
        return workers;
    }
}
