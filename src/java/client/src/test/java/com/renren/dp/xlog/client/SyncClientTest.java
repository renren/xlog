package com.renren.dp.xlog.client;

import xlog.slice.LogData;

import com.renren.dp.xlog.client.XlogClient.ProtocolType;
import com.renren.dp.xlog.client.exception.InitializationException;

import junit.framework.TestCase;

public class SyncClientTest extends TestCase{

	private XlogClient client=null;
	  private  LogData logData1,logData2,logData3;
	  
	  protected void setUp(){
	    try {
	    	client=XlogClientFactory.getInstance(true);
	    	
	    	client.initialize("tmp",0,ProtocolType.UDP);
	    } catch (InitializationException e) {
			e.printStackTrace();
		}
	    
	    logData1=new LogData();
	    logData1.categories=new String[]{"3g","access"};
	    logData1.checkSum="123";
	    logData1.logs=new String[]{"hello21","hi21"};
	    
	    logData2=new LogData();
	    logData2.categories=new String[]{"som","cdn"};
	    logData2.checkSum="222";
	    logData2.logs=new String[]{"hello31","hi31"};
	    
	    logData3=new LogData();
	    logData3.categories=new String[]{"sds","feed"};
	    logData3.checkSum="333";
	    logData3.logs=new String[]{"hello41","hi41"};
	  }
	  
	  public void testDoSend(){
	    try {
	      client.doSend(new LogData[]{logData1,logData2,logData3});
	      Thread.sleep(1000*10);
	    } catch (Exception e) {
	    	fail();
	    	e.printStackTrace();
	    }
	  }
}
