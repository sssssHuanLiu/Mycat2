package io.mycat.hbt4.executor;

import com.google.common.collect.ImmutableList;
import io.mycat.MetaClusterCurrent;
import io.mycat.MycatWorkerProcessor;
import io.mycat.NameableExecutor;
import io.mycat.api.collector.ComposeFutureRowBaseIterator;
import io.mycat.api.collector.RowBaseIterator;
import io.mycat.beans.mycat.MycatRowMetaData;
import io.mycat.calcite.MycatSqlDialect;
import io.mycat.calcite.resultset.MyCatResultSetEnumerator;
import io.mycat.hbt4.DataSourceFactory;
import io.mycat.hbt4.Executor;
import io.mycat.hbt4.ExplainWriter;
import io.mycat.mpp.Row;
import lombok.SneakyThrows;
import org.apache.calcite.sql.util.SqlString;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.mycat.hbt4.executor.MycatPreparedStatementUtil.executeQuery;

public class TmpSqlExecutor implements Executor {
    private MycatRowMetaData mycatRowMetaData;
    final String sql;
    final String target;
    final DataSourceFactory factory;

    public static TmpSqlExecutor create(MycatRowMetaData mycatRowMetaData, String target, String sql, DataSourceFactory factory) {
        return new TmpSqlExecutor( mycatRowMetaData,target,sql, factory);
    }

    protected TmpSqlExecutor(MycatRowMetaData mycatRowMetaData, String target, String sql, DataSourceFactory factory) {
        this.mycatRowMetaData = mycatRowMetaData;
        this.sql = sql;
        this.target = target;
        this.factory = factory;
        factory.registered(ImmutableList.of(target));
    }

    private MyCatResultSetEnumerator myCatResultSetEnumerator;

    @Override
    @SneakyThrows
    public void open() {
        if (myCatResultSetEnumerator != null) {
            myCatResultSetEnumerator.close();
        }
        MycatRowMetaData calciteRowMetaData =mycatRowMetaData;
        MycatWorkerProcessor mycatWorkerProcessor = MetaClusterCurrent.wrapper(MycatWorkerProcessor.class);
        NameableExecutor mycatWorker = mycatWorkerProcessor.getMycatWorker();
        LinkedList<Future<RowBaseIterator>> futureArrayList = new LinkedList<>();
        Connection mycatConnection = factory.getConnection(target).unwrap(Connection.class);
        SqlString sqlString = new SqlString(MycatSqlDialect.DEFAULT,sql);
        futureArrayList.add(mycatWorker.submit(() -> executeQuery(mycatConnection, calciteRowMetaData, sqlString, ImmutableList.of())));
        AtomicBoolean flag = new AtomicBoolean();
        ComposeFutureRowBaseIterator composeFutureRowBaseIterator = new ComposeFutureRowBaseIterator(calciteRowMetaData, futureArrayList);
        this.myCatResultSetEnumerator = new MyCatResultSetEnumerator(flag, composeFutureRowBaseIterator);
    }

    @Override
    public Row next() {
        return myCatResultSetEnumerator.moveNext() ? Row.of(myCatResultSetEnumerator.current()) : null;
    }

    @Override
    public void close() {
        if (myCatResultSetEnumerator != null) {
            myCatResultSetEnumerator.close();
        }
    }

    @Override
    public boolean isRewindSupported() {
        return false;
    }

    @Override
    public ExplainWriter explain(ExplainWriter writer) {
        ExplainWriter explainWriter = writer.name(this.getClass().getName())
                .into();
        writer.item("sql",sql);
        writer.item("target",target);
        return explainWriter.ret();
    }
}