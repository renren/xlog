package com.renren.dp.xlog.log4j;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import xlog.slice.LogData;

import com.renren.dp.xlog.client.XlogClient;
import com.renren.dp.xlog.client.XlogClient.ProtocolType;
import com.renren.dp.xlog.client.XlogClientFactory;
import com.renren.dp.xlog.client.exception.InitializationException;

/**
 * XLog Log4j Appender
 * 
 * @author Zhancheng Deng <b>{@link zhancheng.deng@renren-inc.com}</b>
 * @since 6:56:48 PM Sep 12, 2012
 */
public class XLogAppender extends AppenderSkeleton {

  public boolean initialized = false;
  private XlogClient client;
  // parameters with the logger
  private String cacheFileDir = "cache.data.dir";
  private int cacheQueueSize;
  private ProtocolType protocolType = ProtocolType.UDP;
  private boolean async = true;
  private static Map<String, String[]> categoriesMapCache = new ConcurrentHashMap<String, String[]>(10);
  private static String loggerCommonSplitName = "\\.";
  private static String loggerErrorSplitName = " ";
  
  public XLogAppender() {
  }

  public XLogAppender(Layout layout) {
    this.layout = layout;
  }

  public void setCacheFileDir(String cacheFileDir) {
    this.cacheFileDir = cacheFileDir;
  }

  public void setAsync(String async) {
    if (null != async) {
      this.async = Boolean.parseBoolean(async);
    }
  }

  public void setCacheQueueSize(int cacheQueueSize) {
    this.cacheQueueSize = cacheQueueSize;
  }

  public void setProtocolType(String protocolType) {
    if (protocolType != null && protocolType.equalsIgnoreCase("tcp")) {
      this.protocolType = ProtocolType.TCP;
    }
  }

  @Override
  public void activateOptions() {
    LogLog.debug("Xlog Appender (" + this + ") parameters: cacheFileDir=" + cacheFileDir
        + ", cacheQueueSize=" + cacheQueueSize + ", protocolType=" + protocolType + ", async=" + async);
    client = XlogClientFactory.getInstance(async);
    try {
      client.initialize(cacheFileDir, cacheQueueSize, protocolType);
      initialized = true;
    } catch (InitializationException e) {
      initialized = false;
      LogLog.error("XLog Log4j Appender initialized failed.", e);
    }
  }

  @Override
  public void close() {
    // do nothing
  }

  @Override
  public boolean requiresLayout() {
    return true;
  }

  private static String[] getCategories(String loggerName, String regex) {
    if (!categoriesMapCache.containsKey(loggerName)) {
      categoriesMapCache.put(loggerName, loggerName.split(regex));
    }
    return categoriesMapCache.get(loggerName);
  }
  
  @Override
  protected void append(LoggingEvent event) {
    if (!initialized) {
      return;
    }
    LogData ld = new LogData();
    if (event.getThrowableInformation() == null) {
      ld.categories = getCategories(event.getLoggerName(), loggerCommonSplitName);
      ld.logs = new String[] { layout.format(event) };
    } else {
      ld.categories = getCategories(event.getLoggerName(), loggerErrorSplitName);
      StringBuffer info = new StringBuffer();
      info.append(layout.format(event));
      for (String o : event.getThrowableStrRep()) {
        info.append(o).append("~");
      }
      ld.logs = new String[] { info.toString() };
    }
    client.doSend(new LogData[] { ld });
  }
}
