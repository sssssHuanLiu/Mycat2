package io.mycat.proxy.monitor;

import io.mycat.beans.mycat.MycatDataNode;
import io.mycat.beans.mysql.MySQLAutoCommit;
import io.mycat.beans.mysql.MySQLIsolation;
import io.mycat.proxy.packet.MySQLPacketResolver;
import io.mycat.proxy.session.MySQLClientSession;
import io.mycat.proxy.session.MycatSession;
import io.mycat.proxy.session.Session;
import io.mycat.util.DumpUtil;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jamie12221 date 2019-05-20 11:52
 **/
public class MycatMonitorLogCallback implements MycatMonitorCallback {

  protected final static Logger LOGGER = LoggerFactory.getLogger(MycatMonitor.class);
  final static boolean record = true;
  final static boolean recordDump = false;

  @Override
  public void onMySQLSessionServerStatus(Session session, int serverStatus) {
    if (record) {

      boolean hasFatch = MySQLPacketResolver.hasFatch(serverStatus);
      boolean hasMoreResult = MySQLPacketResolver.hasMoreResult(serverStatus);
      boolean hasTranscation = MySQLPacketResolver.hasTrans(serverStatus);
      LOGGER.info("session id:{}  serverStatus:{} hasFatch:{} hasMoreResult:{} hasTranscation:{}",
          session.sessionId(), serverStatus, hasFatch, hasMoreResult, hasTranscation);
    }
  }

  public void onOrginSQL(Session session, String sql) {
    if (record) {
      LOGGER.info("session id:{}  orginSQL:{} ", session.sessionId(), sql);
    }
  }

  @Override
  public void onRoute(Session session, String dataNode, byte[] payload) {
    if (record) {
      LOGGER.info("session id:{} dataNode:{}  payload:{} ", session.sessionId(), dataNode,
          new String(payload));
    }
  }

  public final void onFrontRead(Session session, ByteBuffer view, int startIndex, int len) {
    if (recordDump) {
      DumpUtil.printAsHex(view, startIndex, len);
    }
  }

  public final void onBackendWrite(Session session, ByteBuffer view, int startIndex,
      int len) {
    if (recordDump) {
      DumpUtil.printAsHex(view, startIndex, len);
    }
  }

  public final void onBackendRead(Session session, ByteBuffer view, int startIndex,
      int len) {
    if (recordDump) {
      DumpUtil.printAsHex(view, startIndex, len);
    }
  }

  public final void onFrontWrite(Session session, ByteBuffer view, int startIndex, int len) {
    if (recordDump) {
      DumpUtil.printAsHex(view, startIndex, len);
    }
  }

  public final void onAllocateByteBuffer(ByteBuffer buffer) {
    if (record) {
      //    Thread.dumpStack();
      LOGGER.debug("{}  {}", MycatMonitorCallback.getSession(), buffer);
    }
  }

  public final void onSynchronizationState(MySQLClientSession session) {
    MySQLAutoCommit automCommit = session.isAutomCommit();
    String characterSetResult = session.getCharacterSetResult();
    String charset = session.getCharset();
    MySQLIsolation isolation = session.getIsolation();
    MycatDataNode dataNode = session.getDataNode();
    if (record) {
      //    Thread.dumpStack();
      LOGGER.debug(
          "sessionId:{} dataNode:{} isolation: {} charset:{} automCommit:{} characterSetResult:{}",
          session.sessionId(), dataNode,
          isolation, charset, automCommit, characterSetResult);
    }
  }

  public final void onRecycleByteBuffer(ByteBuffer buffer) {
    if (record) {
      LOGGER.debug("{}  {}", MycatMonitorCallback.getSession(), buffer);
    }
  }

  public final void onExpandByteBuffer(ByteBuffer buffer) {
    if (record) {
      LOGGER.debug("{}  {}", MycatMonitorCallback.getSession(), buffer);
    }
  }

  public final void onNewMycatSession(MycatSession session) {
    if (record) {
      LOGGER.debug("{}", session);
    }
  }

  public final void onBindMySQLSession(MycatSession mycat, MySQLClientSession session) {
    if (record) {
      LOGGER.debug("{} {}", mycat, session);
    }
  }

  public final void onUnBindMySQLSession(MycatSession mycat, MySQLClientSession session) {
    if (record) {
      LOGGER.debug("{} {}", mycat, session);
    }
  }

  public final void onCloseMycatSession(MycatSession session) {
    if (record) {
      LOGGER.debug("{}", session);
    }
  }

  public final void onNewMySQLSession(MySQLClientSession session) {
    if (record) {
      LOGGER.debug("sessionId:{} dataSourceName:{}", session.sessionId(),
          session.getDatasource().getName());
    }
  }

  public final void onAddIdleMysqlSession(MySQLClientSession session) {
    if (record) {
      LOGGER.debug("sessionId:{} dataSourceName:{}", session.sessionId(),
          session.getDatasource().getName());
    }
  }

  public final void onGetIdleMysqlSession(MySQLClientSession session) {
    if (record) {
      LOGGER.debug("sessionId:{} dataSourceName:{}", session.sessionId(),
          session.getDatasource().getName());
    }
  }

  public final void onCloseMysqlSession(MySQLClientSession session) {
    if (record) {
      LOGGER.debug("{}", session);
    }
  }

}