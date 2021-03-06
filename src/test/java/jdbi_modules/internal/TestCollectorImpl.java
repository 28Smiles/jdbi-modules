package jdbi_modules.internal;

import jdbi_modules.bean.Pool;
import jdbi_modules.bean.User;
import jdbi_modules.bean.Worker;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @since 14.04.2018
 */
class TestCollectorImpl {
    private Worker worker1 = new Worker(1, "Gunter", null, 1, null);
    private Worker worker2 = new Worker(1, "Gunter", null, 1, null);
    private Worker worker3 = new Worker(2, "Gert", null, 2, null);
    private final List<Worker> workers = List.of(worker1, worker2, worker3);

    @Test
    void testAppend() {
        final CollectorMock collectorMock = new CollectorMock();
        workers.forEach(collectorMock.collector::append);
        collectorMock.collector.append(null);
        assertThat(collectorMock.collector.get()).containsExactlyElementsOf(workers);
    }

    @Test
    void testAppendWithApply() {
        final CollectorMock collectorMock = new CollectorMock();
        workers.forEach(worker -> collectorMock.collector.append(worker, workerSupp -> assertThat(workerSupp).isSameAs(worker)));
        collectorMock.collector.append(null, worker -> fail("Null should not be added"));
        assertThat(collectorMock.collector.get()).containsExactlyElementsOf(workers);
    }

    @Test
    void testAppendUnique() {
        final CollectorMock collectorMock = new CollectorMock();
        workers.forEach(collectorMock.collector::appendUnique);
        collectorMock.collector.appendUnique(null);
        assertThat(collectorMock.collector.get()).containsExactly(worker1, worker3);
    }

    @Test
    void testCustomComparator() {
        final CollectorMock collectorMock = new CollectorMock();
        collectorMock.collector.setComparator((w1, w2) -> w1 == w2);
        workers.forEach(collectorMock.collector::appendUnique);
        collectorMock.collector.appendUnique(null, worker -> fail("Null should not be added"));
        assertThat(collectorMock.collector.get()).containsExactly(worker1, worker2, worker3);
    }

    @Test
    void testAppendUniqueWithApply() {
        final CollectorMock collectorMock = new CollectorMock();
        collectorMock.collector.appendUnique(worker1, worker -> assertThat(worker).isSameAs(worker1));
        collectorMock.collector.appendUnique(worker2, worker -> assertThat(worker).isSameAs(worker1));
        collectorMock.collector.appendUnique(worker3, worker -> assertThat(worker).isSameAs(worker3));
        assertThat(collectorMock.collector.get()).containsExactly(worker1, worker3);
    }

    @Test
    void testAppendWithRowView() {
        final CollectorMock collectorMock = new CollectorMock();
        workers.forEach(worker -> {
            when(collectorMock.rowView.getRow(Worker.class)).thenReturn(worker);
            collectorMock.collector.appendWithRowView(Worker.class);
        });
        assertThat(collectorMock.collector.get()).containsExactlyElementsOf(workers);
    }

    @Test
    void testAppendWithRowViewWithApply() {
        final CollectorMock collectorMock = new CollectorMock();
        workers.forEach(worker -> {
            when(collectorMock.rowView.getRow(Worker.class)).thenReturn(worker);
            collectorMock.collector.appendWithRowView(Worker.class, workerSupp -> assertThat(workerSupp).isSameAs(worker));
        });
        assertThat(collectorMock.collector.get()).containsExactlyElementsOf(workers);
    }

    @Test
    void testAppendUniqueWithRowView() {
        final CollectorMock collectorMock = new CollectorMock();
        workers.forEach(worker -> {
            when(collectorMock.rowView.getRow(Worker.class)).thenReturn(worker);
            collectorMock.collector.appendUniqueWithRowView(Worker.class);
        });
        assertThat(collectorMock.collector.get()).containsExactly(worker1, worker3);
    }

    @Test
    void testAppendUniqueWithRowViewWithApply() {
        final CollectorMock collectorMock = new CollectorMock();
        when(collectorMock.rowView.getRow(Worker.class)).thenReturn(worker1);
        collectorMock.collector.appendUniqueWithRowView(Worker.class, workerSupp -> assertThat(workerSupp).isSameAs(worker1));
        when(collectorMock.rowView.getRow(Worker.class)).thenReturn(worker2);
        collectorMock.collector.appendUniqueWithRowView(Worker.class, workerSupp -> assertThat(workerSupp).isSameAs(worker1));
        when(collectorMock.rowView.getRow(Worker.class)).thenReturn(worker3);
        collectorMock.collector.appendUniqueWithRowView(Worker.class, workerSupp -> assertThat(workerSupp).isSameAs(worker3));
        assertThat(collectorMock.collector.get()).containsExactly(worker1, worker3);
    }

    @Test
    void testAppendWithRowMapper() throws SQLException {
        final CollectorMock collectorMock = new CollectorMock();
        for (Worker worker : workers) {
            collectorMock.collector.appendWithRowMapper(new RowMapperMock(worker));
        }
        assertThat(collectorMock.collector.get()).containsExactlyElementsOf(workers);
    }

    @Test
    void testAppendWithRowMapperWithApply() throws SQLException {
        final CollectorMock collectorMock = new CollectorMock();
        for (Worker worker : workers) {
            collectorMock.collector.appendWithRowMapper(new RowMapperMock(worker), workerSupp -> assertThat(workerSupp).isSameAs(worker));
        }
        assertThat(collectorMock.collector.get()).containsExactlyElementsOf(workers);
    }

    @Test
    void testAppendUniqueWithRowMapper() throws SQLException {
        final CollectorMock collectorMock = new CollectorMock();
        collectorMock.collector.appendUniqueWithRowMapper(new RowMapperMock(worker1));
        collectorMock.collector.appendUniqueWithRowMapper(new RowMapperMock(worker2));
        collectorMock.collector.appendUniqueWithRowMapper(new RowMapperMock(worker3));
        assertThat(collectorMock.collector.get()).containsExactly(worker1, worker3);
    }

    @Test
    void testAppendUniqueWithRowMapperWithApply() throws SQLException {
        final CollectorMock collectorMock = new CollectorMock();
        collectorMock.collector.appendUniqueWithRowMapper(new RowMapperMock(worker1), workerSupp -> assertThat(workerSupp).isSameAs(worker1));
        collectorMock.collector.appendUniqueWithRowMapper(new RowMapperMock(worker2), workerSupp -> assertThat(workerSupp).isSameAs(worker1));
        collectorMock.collector.appendUniqueWithRowMapper(new RowMapperMock(worker3), workerSupp -> assertThat(workerSupp).isSameAs(worker3));
        assertThat(collectorMock.collector.get()).containsExactly(worker1, worker3);
    }

    @Test
    void testStream() {
        final CollectorMock collectorMock = new CollectorMock();
        workers.forEach(collectorMock.collector::appendUnique);
        collectorMock.collector.appendUnique(null);
        assertThat(collectorMock.collector.stream()).containsExactly(worker1, worker3);
    }

    @Test
    void testStreamFilter() {
        final CollectorMock collectorMock = new CollectorMock();
        workers.forEach(collectorMock.collector::appendUnique);
        final WorkerChild1 workerChild1 = new WorkerChild1(1245, null, null, 0, null);
        collectorMock.collector.appendUnique(workerChild1);
        final WorkerChild2 workerChild2 = new WorkerChild2(4545, null, null, 0, null);
        collectorMock.collector.appendUnique(workerChild2);
        assertThat(collectorMock.collector.stream(Worker.class)).containsExactlyInAnyOrder(worker1, worker3, workerChild1, workerChild2);
        assertThat(collectorMock.collector.stream(WorkerChild1.class)).containsExactlyInAnyOrder(workerChild1);
        assertThat(collectorMock.collector.get(WorkerChild2.class)).containsExactlyInAnyOrder(workerChild2);
    }

    @Test
    void testListFilter() {
        final CollectorMock collectorMock = new CollectorMock();
        workers.forEach(collectorMock.collector::appendUnique);
        final WorkerChild1 workerChild1 = new WorkerChild1(1245, null, null, 0, null);
        collectorMock.collector.appendUnique(workerChild1);
        final WorkerChild2 workerChild2 = new WorkerChild2(4545, null, null, 0, null);
        collectorMock.collector.appendUnique(workerChild2);
        assertThat(collectorMock.collector.get(Worker.class)).containsExactlyInAnyOrder(worker1, worker3, workerChild1, workerChild2);
        assertThat(collectorMock.collector.get(WorkerChild1.class)).containsExactlyInAnyOrder(workerChild1);
        assertThat(collectorMock.collector.get(WorkerChild2.class)).containsExactlyInAnyOrder(workerChild2);
    }

    private class RowMapperMock implements RowMapper<Worker> {
        private final Worker worker;

        public RowMapperMock(final Worker worker) {
            this.worker = worker;
        }

        @Override
        public Worker map(ResultSet rs, StatementContext ctx) throws SQLException {
            return worker;
        }
    }

    private static class WorkerChild1 extends Worker {
        public WorkerChild1(long id, String name, User user, int position, Pool pool) {
            super(id, name, user, position, pool);
        }

        public WorkerChild1() {
            super();
        }
    }

    private static class WorkerChild2 extends Worker {
        public WorkerChild2(long id, String name, User user, int position, Pool pool) {
            super(id, name, user, position, pool);
        }

        public WorkerChild2() {
            super();
        }
    }

    private class CollectorMock {
        final RowView rowView;
        final ResultSet resultSet;
        final StatementContext statementContext;
        final CollectorImpl<List<Worker>, Worker> collector;

        CollectorMock() {
            rowView = mock(RowView.class);
            resultSet = mock(ResultSet.class);
            statementContext = mock(StatementContext.class);
            collector = new CollectorImpl<>(
                    new LinkedList<>(),
                    rowView,
                    resultSet,
                    statementContext);
        }

        public RowView getRowView() {
            return rowView;
        }

        public ResultSet getResultSet() {
            return resultSet;
        }

        public StatementContext getStatementContext() {
            return statementContext;
        }

        public CollectorImpl<List<Worker>, Worker> getCollector() {
            return collector;
        }
    }
}
