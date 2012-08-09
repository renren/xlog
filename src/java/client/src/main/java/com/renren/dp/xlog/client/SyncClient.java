package com.renren.dp.xlog.client;

import java.util.Properties;

import xlog.slice.LogData;

import com.renren.dp.xlog.agent.impl.DefaultAgentAdapter;
import com.renren.dp.xlog.client.exception.InitializationException;
import com.renren.dp.xlog.client.exception.XlogClientException;

public class SyncClient extends XlogClient{

	public void initialize() throws InitializationException {
		Properties prop=new Properties();
		prop.setProperty(CLIENT_PROTOCOL_TYPE, "udp");
		
		initialize(null,0,ProtocolType.UDP);
	}
	
	public void initialize(String cacheFileDir,int cacheQueueSize,ProtocolType pt) throws InitializationException {
		lock.lock();
		if(!isInit){
			isInit=true;
			Properties defaultProp=loadProp("conf/client.properties");
			String agentAddress=defaultProp.getProperty(XLOG_AGENT_ADDRESS);
			agentAddresses=agentAddress.split(",");
			
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
		}
		lock.unlock();
	}

	@Override
	public boolean doSend(LogData[] logDatas) {
		// TODO Auto-generated method stub
		try {
			return agentAdapter.send(logDatas);
		} catch (XlogClientException e) {
			e.printStackTrace();
			return false;
		}
	}

}
