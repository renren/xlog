package com.renren.dp.xlog.log4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
  private int MAX_SEND_SIZE = 0;
  private int DEFAULT_MAX_SEND_SIZE = 50000;
  private ProtocolType protocolType = ProtocolType.UDP;
  private boolean async = true;
  private Map<String, String[]> categoriesMapCache;
  private int cacheQueueSize;

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

  public void setMaxSendSize(int maxSendSize) {
    if (maxSendSize < DEFAULT_MAX_SEND_SIZE) {
      this.MAX_SEND_SIZE = maxSendSize;
    } else {
      this.MAX_SEND_SIZE = DEFAULT_MAX_SEND_SIZE;
    }
  }

  public void setProtocolType(String protocolType) {
    if (protocolType != null && protocolType.equalsIgnoreCase("tcp")) {
      this.protocolType = ProtocolType.TCP;
    }
  }

  @Override
  public void activateOptions() {
    LogLog.debug("Xlog Appender (" + this + ") parameters: cacheFileDir=" + cacheFileDir + ", cacheQueueSize="
        + cacheQueueSize + ", maxSendSize=" + MAX_SEND_SIZE + ", protocolType=" + protocolType + ", async=" + async);
    LogLog.debug("the tutorial & Reference link: http://wiki.d.xiaonei.com/pages/viewpage.action?pageId=14846863");
    client = XlogClientFactory.getInstance(async);
    try {
      client.initialize(cacheFileDir, cacheQueueSize, protocolType);
      logMap = new HashMap<String, List<String>>();
      lengthMap = new HashMap<String, Integer>();
      categoriesMapCache = new HashMap<String, String[]>(10);
      initialized = true;
    } catch (InitializationException e) {
      initialized = false;
      LogLog.error("XLog Log4j Appender initialized failed.", e);
    }
  }

  @Override
  public void close() {
    Set<String> categories = logMap.keySet();

    for (String c : categories) {
      LogData ld = new LogData();
      List<String> logList = logMap.get(c);
      ld.categories = getCategories(c);
      ld.logs = logList.toArray(new String[0]);
      client.doSend(new LogData[] { ld });
      logMap.remove(c);
      lengthMap.remove(c);
    }
    logMap.clear();
    logMap = null;
    lengthMap.clear();
    lengthMap = null;
    categoriesMapCache.clear();
    categoriesMapCache = null;
    client = null;
  }

  @Override
  public boolean requiresLayout() {
    return true;
  }

  private String[] getCategories(String loggerName) {
    if (!categoriesMapCache.containsKey(loggerName)) {
      categoriesMapCache.put(loggerName, loggerName.split("(\\.| )"));
    }
    return categoriesMapCache.get(loggerName);
  }

  private Map<String, List<String>> logMap;
  private Map<String, Integer> lengthMap;

  @Override
  protected void append(LoggingEvent event) {
    if (!initialized) {
      return;
    }
    String categories = event.getLoggerName();
    List<String> logList = logMap.get(categories);
    int logLength = 0;
    if (null != logList) {
      logLength = lengthMap.get(categories);
    } else {
      logList = new ArrayList<String>();
    }
    String log = layout.format(event);
    logList.add(log);
    logLength += log.length();
    if (event.getThrowableInformation() != null) {
      for (String o : event.getThrowableStrRep()) {
        logList.add(o);
        logLength += o.length();
      }
    }
    if (logLength < MAX_SEND_SIZE) {
      logMap.put(categories, logList);
      lengthMap.put(categories, logLength);
    } else {
      LogData ld = new LogData();
      ld.categories = getCategories(categories);
      ld.logs = logList.toArray(new String[0]);
      client.doSend(new LogData[] { ld });
      logList.clear();
      logMap.put(categories, logList);
      lengthMap.put(categories, 0);
    }

  }

}
