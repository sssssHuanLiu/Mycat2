package io.mycat.assemble;

import io.mycat.config.SqlCacheConfig;
import io.mycat.hint.CreateSqlCacheHint;
import io.mycat.hint.DropSqlCacheHint;
import io.mycat.hint.ShowSqlCacheHint;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.concurrent.NotThreadSafe;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

@NotThreadSafe
@net.jcip.annotations.NotThreadSafe
public class SqlResultSetTest implements MycatTest {
    @Test
    public void testCreateSqlCache() throws Exception {
        try (Connection connection = getMySQLConnection(8066)) {
            execute(connection, RESET_CONFIG);
            SqlCacheConfig sqlCacheConfig = new SqlCacheConfig();

            List<Map<String, Object>> res;
            List<Map<String, Object>> maps = executeQuery(connection, CreateSqlCacheHint.create(sqlCacheConfig));
            Assert.assertEquals(1,maps.size());
            res = executeQuery(connection, ShowSqlCacheHint.create());
            Assert.assertEquals(1, res.size());
            Assert.assertTrue(res.iterator().next().toString().contains("hasCache:true"));
            execute(connection, DropSqlCacheHint.create(sqlCacheConfig.getName()));

            Assert.assertEquals(0, executeQuery(connection, ShowSqlCacheHint.create()).size());
        }
    }
}