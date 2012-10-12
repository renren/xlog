package com.renren.dp.xlog.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import xlog.slice.LogData;

import com.renren.dp.xlog.agent.AgentAdapter;
import com.renren.dp.xlog.client.exception.InitializationException;

public abstract class XlogClient {

  public static final String CACHE_FILE_DIR = "cache.file.dir";
  public static final String XLOG_AGENT_ADDRESS = "xlog.agent.address";
  public static final String CACHE_QUEUE_SIZE = "cache.queue.size";

  public static final String CLIENT_PROTOCOL_TYPE = "client.protocol.type";

  protected AgentAdapter agentAdapter = null;
  protected List<LogData[]> logDataBQ = null;
  protected boolean isInit = false;
  protected String[] agentAddresses = null;

  protected ReentrantLock lock = new ReentrantLock();

  public abstract void initialize() throws InitializationException;

  public abstract void initialize(String cacheFileDir, int cacheQueueSize, ProtocolType pt)
      throws InitializationException;

  protected Properties loadProp(String fileName) throws InitializationException {
    Properties prop = new Properties();
    InputStream is = XlogClient.class.getClassLoader().getResourceAsStream(fileName);
    try {
      prop.load(is);
    } catch (IOException e) {
      throw new InitializationException("Fail to load client property file!", e);
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return prop;
  }

  // protected abstract void initialize(Properties prop) throws
  // InitializationException;

  public abstract boolean doSend(LogData[] logDatas);

  public enum ProtocolType {
    UDP, TCP
  }

  public enum ClientType {
    ASYNC_CLIENT, SYNC_CLIENT
  }
}
