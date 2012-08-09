package com.renren.dp.xlog.client;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;

import com.renren.dp.xlog.agent.impl.DefaultAgentAdapter;
import com.renren.dp.xlog.client.exception.InitializationException;
import com.renren.dp.xlog.client.exception.XlogClientException;
import com.renren.dp.xlog.client.io.LogWriter;

import xlog.slice.LogData;

public class AsyncClient extends XlogClient {

	private int cacheQueueCapacity = 100000;
	private LogWriter logWriter = null;
	
	protected AsyncClient() {
	}

	public boolean doSend(LogData[] logDatas) {
		if (logDataBQ.size() == cacheQueueCapacity) {
			logWriter.write(logDatas);
			return logWriter.write(logDatas);
		} else {
			return logDataBQ.offer(logDatas);
		}
	}

	protected float getQueueUseRate() {
		return (float) logDataBQ.size() / (float) cacheQueueCapacity;
	}

	private class Sender extends Thread {
		public void run() {
			LogData[] logDatas = null;
			while (true) {
				try {
					logDatas = logDataBQ.take();
					boolean res = agentAdapter.send(logDatas);
					if (!res) {
						logWriter.write(logDatas);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					continue;
				} catch (XlogClientException e) {
					e.printStackTrace();
					continue;
				}
				logDatas = null;
			}
		}
	}

	@Override
	public void initialize(String cacheFileDir,int cacheQueueSize,ProtocolType pt) throws InitializationException {
		lock.lock();
		if(!isInit){
			isInit=true;
			Properties defaultProp=loadProp("conf/client.properties");
			String agentAddress=defaultProp.getProperty(XLOG_AGENT_ADDRESS);
			agentAddresses=agentAddress.split(",");
			
			if(cacheQueueSize>0){
				this.cacheQueueCapacity = cacheQueueSize;
			}
			if(cacheFileDir==null){
				throw new InitializationException(
						"There is no available cache log directory.Cache log file path:" + cacheFileDir);
			}
			File cacheLogFile = new File(cacheFileDir);
			if (!cacheLogFile.exists()) {
				if (!cacheLogFile.mkdirs()) {
					throw new InitializationException(
							"Can't create cache log directory:" + cacheFileDir);
				}
			}
			
			ProtocolType protocol=null;
			if(pt==null){
				protocol=ProtocolType.UDP;
			}else{
				protocol=pt;
			}
			agentAdapter=new DefaultAgentAdapter();
			
			if (!agentAdapter.init(agentAddresses, protocol)) {
				throw new InitializationException(
						"Fail to initialise agent adapter,please check agent address parameter!");
			}
			try {
				logWriter = new LogWriter(cacheFileDir);
			} catch (IOException e) {
				throw new InitializationException(
						"Can't initialize cache log writer!");
			}

			logDataBQ = new ArrayBlockingQueue<LogData[]>(cacheQueueCapacity);
			LogSyncHandler logSyncHandler=new LogSyncHandler(cacheFileDir,this);
			logSyncHandler.setDaemon(true);
			logSyncHandler.start();
			
			Sender sender=new Sender();
			sender.setDaemon(true);
			sender.start();
		}
		lock.unlock();
	}

	@Override
	public void initialize() throws InitializationException {
		initialize("cache.data.dir",0,ProtocolType.UDP);
	}
}
