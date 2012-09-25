package com.renren.dp.xlog.client;

import xlog.slice.LogData;

import com.renren.dp.xlog.client.exception.InitializationException;

import junit.framework.TestCase;

public class AsyncClientTest extends TestCase {

  private XlogClient client = null;
  private LogData logData1, logData2, logData3, logData4;

  protected void setUp() {
    try {
      client = XlogClientFactory.getInstance(true);
      client.initialize();
      // Properties prop=new Properties();
      // prop.setProperty(XlogClient.CACHE_FILE_DIR,"/home/xianquanzhang/xlog/src/java/client/tmp");
      // prop.setProperty(XlogClient.CACHE_QUEUE_SIZE,"20000");
      // prop.setProperty(XlogClient.CLIENT_PROTOCOL_TYPE,ProtocolType.UDP.toString());
      // prop.setProperty(XlogClient.XLOG_AGENT_ADDRESS,"10.9.18.43:10001,10.9.18.44:10001,10.9.18.45:10001");
      // client.initialize(prop);
    } catch (InitializationException e) {
      e.printStackTrace();
    }
    /**
     * 3g/access,
     * 1818258875308280_8:test1:AdEngineU36@10.3.19.184||123.147.249.163||2012-02-07
     * 17:25:00||
     * "GET /show?userid=247361632&tt=0&type=.js&adzoneid=100000000065&count=1&next_load_time=&refresh_idx=&rowoffset=0&ip=123.147.249.163&jbc=cookie|a|b&ubc=1000000_247361632|1|1990-01-01|22|2|0086510100000000|400000015004_0086510100000000|42|0|0|0086500000000000HTTP/1.0"
     * ||200||undefined||1393145099059607590||undefined||10||36||none||
     * "http%3A%2F%2Fwww%2Erenren%2Ecom%2Fhome%3Frefresh%5Fsource%3D0%26refresh%5Fidx%3D0"
     * ||100000000065^B^B^B^B^B^B^B||1000037785003200001^B100000017330^
     * B1000037785003200002^B0^B150^B
     * "http%3A%2F%2Fwww%2Erenren%2Ecom%2Fhome%3Frefresh%5Fsource%3D0%26refresh%5Fidx%3D0"
     * ^B1^B100001^B2^B-1||4||rr_REMAIN_2_98 3g/access,
     * 1818258875308280_9:test1:AdEngineU36@10.3.19.184||123.147.249.163||2012-02-07
     * 17:25:00||
     * "GET /show?userid=247361632&tt=0&type=.js&adzoneid=100000000065&count=1&next_load_time=&refresh_idx=&rowoffset=0&ip=123.147.249.163&jbc=cookie|a|b&ubc=1000000_247361632|1|1990-01-01|22|2|0086510100000000|400000015004_0086510100000000|42|0|0|0086500000000000HTTP/1.0"
     * ||200||undefined||1393145099059607590||undefined||10||36||none||
     * "http%3A%2F%2Fwww%2Erenren%2Ecom%2Fhome%3Frefresh%5Fsource%3D0%26refresh%5Fidx%3D0"
     * ||100000000065^B^B^B^B^B^B^B||1000037785003200001^B100000017330^
     * B1000037785003200002^B0^B150^B
     * "http%3A%2F%2Fwww%2Erenren%2Ecom%2Fhome%3Frefresh%5Fsource%3D0%26refresh%5Fidx%3D0"
     * ^B1^B100001^B2^B-1||4||rr_REMAIN_2_98 3g/access,
     * 1818258875308280_7:test1:AdEngineU36@10.3.19.184||123.147.249.163||2012-02-07
     * 17:25:00||
     * "GET /show?userid=247361632&tt=0&type=.js&adzoneid=100000000065&count=1&next_load_time=&refresh_idx=&rowoffset=0&ip=123.147.249.163&jbc=cookie|a|b&ubc=1000000_247361632|1|1990-01-01|22|2|0086510100000000|400000015004_0086510100000000|42|0|0|0086500000000000HTTP/1.0"
     * ||200||undefined||1393145099059607590||undefined||10||36||none||
     * "http%3A%2F%2Fwww%2Erenren%2Ecom%2Fhome%3Frefresh%5Fsource%3D0%26refresh%5Fidx%3D0"
     * ||100000000065^B^B^B^B^B^B^B||1000037785003200001^B100000017330^
     * B1000037785003200002^B0^B150^B
     * "http%3A%2F%2Fwww%2Erenren%2Ecom%2Fhome%3Frefresh%5Fsource%3D0%26refresh%5Fidx%3D0"
     * ^B1^B100001^B2^B-1||4||rr_REMAIN_2_98
     */
    // logData1=new LogData();
    // logData1.categories=new String[]{"3g","access"};
    // logData1.checkSum="123";
    // logData1.logs=new
    // String[]{"好阿xxxxxqqqqqqqqqqqqq:test1:AdEngineU36@10.3.19.184||123.147.249.163||2012-02-07 17:25:00||\"GET /show?userid=247361632&tt=0&type=.js&adzoneid=100000000065&count=1&next_load_time=&refresh_idx=&rowoffset=0&ip=123.147.249.163&jbc=cookie|a|b&ubc=1000000_247361632|1|1990-01-01|22|2|0086510100000000|400000015004_0086510100000000|42|0|0|0086500000000000HTTP/1.0\"||200||undefined||1393145099059607590||undefined||10||36||none||\"http%3A%2F%2Fwww%2Erenren%2Ecom%2Fhome%3Frefresh%5Fsource%3D0%26refresh%5Fidx%3D0\"||100000000065^B^B^B^B^B^B^B||1000037785003200001^B100000017330^B1000037785003200002^B0^B150^B\"http%3A%2F%2Fwww%2Erenren%2Ecom%2Fhome%3Frefresh%5Fsource%3D0%26refresh%5Fidx%3D0\"^B1^B100001^B2^B-1||4||rr_REMAIN_2_98","ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss"};
    //
    // logData2=new LogData();
    // logData2.categories=new String[]{"som","cdn"};
    // logData2.checkSum="222";
    // logData2.logs=new String[]{"hello31","hi31","好阿"};
    //
    // logData3=new LogData();
    // logData3.categories=new String[]{"sds","feed"};
    // logData3.checkSum="333";
    // logData3.logs=new String[]{"hello41","hi41"};

    logData4 = new LogData();
    logData4.categories = new String[] { "test", "3g", "access", "async" };
    logData4.checkSum = "123";
    logData4.logs = new String[] {
        "eeeee好阿eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee222222",
        "kkkkkkkkkkkkkkkkkkkkkkkkkkkkkk" };

    logData4.logs = new String[] {
        "eeeee好阿","eeeee好阿","eeeee好阿","eeeee好阿","eeeee好阿","eeeee好阿","eeeee好阿","eeeee好阿","eeeee好阿","eeeee好阿","eeeee好阿","eeeee好阿","eeeee好阿","eeeee好阿","eeeee好阿","eeeee好阿","eeeee好阿","eeeee好阿","eeeee好阿","eeeee好阿","eeeee好阿","eeeee好阿",
        "kkkkkkkkkkkkkkkkkkkkkkkkkkkkkk" };
  }

  public void testDoSend() {
    try {
      // for(int i =0 ; i < 100; i++){
      while (true) {
        client.doSend(new LogData[] { logData4, logData4, logData4 });
      }
//      Thread.sleep(1000 * 100);
    } catch (Exception e) {
      fail();
      e.printStackTrace();
    }
  }
}
