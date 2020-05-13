
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.net.HttpURLConnection;
import java.io.PrintWriter;
import java.io.InputStream;


public class Voicent
{
  /**
   * Constructor with default localhost:8155
   */
  public Voicent()
  {
    host_ = "172.30.100.84";
    port_ = 8155;
   
    
  }

  /**
   * Constructor with Voicent gateway hostname and port.
   * @param host Voicent gateway host machine
   * @param port Voicent gateway port number
   */
  public Voicent(String host, int port)
  {
    host_ = host;
    port_ = port;
  }

  /**
   * Make a call to the number specified and play the text message
   * using text-to-speech engine.
   *
   * @param phoneno Phone number to call, exactly as it should be dialed
   * @param text Text to play over the phone using text-to-speech
   * @param selfdelete After the call, delete the call request automatically if set to 1
   * @return Call request ID
   */
  public String callText(String phoneno, String text, boolean selfdelete)
  {
    // call request url
    String urlstr = "/ocall/callreqHandler.jsp";

    // setting the http post string
    String poststr = "info=";
    poststr += URLEncoder.encode("Simple Text Call " + phoneno);

    poststr += "&phoneno=";
    poststr += phoneno;

    poststr += "&firstocc=10";

    poststr += "&selfdelete=";
    poststr += (selfdelete ? "1" : "0");

    poststr += "&txt=";
    poststr += URLEncoder.encode(text);

    // Send Call Request
    String rcstr = postToGateway(urlstr, poststr);

    return getReqId(rcstr); 
}

  /**
   * Make a call to the number specified and use the IVR app for call control
   *
   * @param phoneno Phone number to call, exactly as it should be dialed
   * @param appname deployed IVR application name on Voicent Gateway
   * @param selfdelete After the call, delete the call request automatically if set to 1
   * @return Call request ID
   */
  @SuppressWarnings("deprecation")
public String callIvr(String phoneno, String appname, boolean selfdelete)
  {
    // call request url
    String urlstr = "/ocall/callreqHandler.jsp";

    // setting the http post string
    String poststr = "info=";
    poststr += URLEncoder.encode("Simple Text Call " + phoneno);

    poststr += "&phoneno=";
    poststr += phoneno;

    poststr += "&firstocc=10";

    poststr += "&selfdelete=";
    poststr += (selfdelete ? "1" : "0");

    poststr += "&startapp=";
    poststr += URLEncoder.encode(appname);

    // Send Call Request
    String rcstr = postToGateway(urlstr, poststr);

    return getReqId(rcstr); 
}
  
  

  /**
   * Make a call to the number specified and play the audio file. The
   * audio file should be of PCM 8KHz, 16bit, mono.
   *
   * @param phoneno Phone number to call, exactly as it should be dialed
   * @param audiofile Audio file path name
   * @param selfdelete After the call, delete the call request automatically if set to 1
   * @return Call request ID
   */
  public String callAudio(String phoneno, String audiofile, boolean selfdelete)
  {
    // call request url
    String urlstr = "/ocall/callreqHandler.jsp";

    // setting the http post string
    String poststr = "info=";
    poststr += URLEncoder.encode("Simple Audio Call " + phoneno);

    poststr += "&phoneno=";
    poststr += phoneno;

    poststr += "&firstocc=10";

    poststr += "&selfdelete=";
    poststr += (selfdelete ? "1" : "0");

    poststr += "&audiofile=";
    poststr += URLEncoder.encode(audiofile);

    // Send Call Request
    String rcstr = postToGateway(urlstr, poststr);

    return getReqId(rcstr);
}

  /**
   * Get call status of the call with the reqID.
   *
   * @param reqID Call request ID on the gateway
   * @return call status
   */
  public String callStatus(String reqID)
  {
    // call status url
    String urlstr = "/ocall/callstatusHandler.jsp";

    // setting the http post string
    String poststr = "reqid=";
    poststr += URLEncoder.encode(reqID);

    // Send Call Request
    String rcstr = postToGateway(urlstr, poststr);

    return getCallStatus(rcstr);
  }

  /**
   * Get call status of the call with the reqID.
   *
   * @param reqID Call request ID on the gateway
   * @param responses Touch tone responses name-value pair, name is IVR element name
   * @return call status
   */
  public String callStatus(String reqID, HashMap responses)
  {
    // call status url
    String urlstr = "/ocall/callstatusHandler.jsp";

    // setting the http post string
    String poststr = "reqid=";
    poststr += URLEncoder.encode(reqID);

    // Send Call Request
    String rcstr = postToGateway(urlstr, poststr);

    return getCallStatus(rcstr, responses);
  }

  /**
   * Remove all request from the gateway
   *
   * @param reqID Call request ID on the gateway
   * @return call status
   */
  public void callRemove(String reqID)
  {
    // call status url
    String urlstr = "/ocall/callremoveHandler.jsp";

    // setting the http post string
    String poststr = "reqid=";
    poststr += URLEncoder.encode(reqID);

    // Send Call remove post
    postToGateway(urlstr, poststr);
  }

  /**
   * Invoke BroadcastByPhone and start the call-till-confirm process
   *
   * @param vcastexe Executable file vcast.exe, BroadcastByPhone path name
   * @param vocfile BroadcastByPhone call list file
   * @param wavfile Audio file used for the broadcast
   * @param ccode Confirmation code
   */
  public void callTillConfirm(String vcastexe, String vocfile, String wavfile, String ccode)
  {
    // call request url
    String urlstr = "/ocall/callreqHandler.jsp";

    // setting the http post string
    String poststr = "info=";
    poststr += URLEncoder.encode("Simple Call till Confirm");

    poststr += "&phoneno=1111111"; // any number

    poststr += "&firstocc=10";
    poststr += "&selfdelete=0";

    poststr += "&startexec=";
    poststr += URLEncoder.encode(vcastexe);

    String cmdline = "\"";
    cmdline += vocfile;
    cmdline += "\"";
    cmdline += " -startnow";
    cmdline += " -confirmcode ";
    cmdline += ccode;
    cmdline += " -wavfile ";
    cmdline += "\"";
    cmdline += wavfile;
    cmdline += "\"";

    // add -cleanstatus if necessary

    poststr += "&cmdline=";
    poststr += URLEncoder.encode(cmdline);

    // Send like a Call Request
    postToGateway(urlstr, poststr);
  }

  private String postToGateway(String urlstr, String poststr)
  {
    try {
      URL url = new URL("http", host_, port_, urlstr);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();

      conn.setDoInput(true);
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");

      PrintWriter out = new PrintWriter(conn.getOutputStream());
      out.print(poststr);
      out.close();

      InputStream in = conn.getInputStream();

      StringBuffer rcstr = new StringBuffer();
      byte[] b = new byte[4096];
      int len;
      while ((len = in.read(b)) != -1)
        rcstr.append(new String(b, 0, len));
      return rcstr.toString();
    }
    catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }

  private String getReqId(String rcstr)
  {
    int index1 = rcstr.indexOf("[ReqId=");
    if (index1 == -1)
      return "";
    index1 += 7;

    int index2 = rcstr.indexOf("]", index1);
    if (index2 == -1)
      return "";

    return rcstr.substring(index1, index2);
  }

  private String getCallStatus(String rcstr)
  {
    if (rcstr.indexOf("^made^") != -1)
      return "Call Made";

    if (rcstr.indexOf("^failed^") != -1)
      return "Call Failed";

    if (rcstr.indexOf("^retry^") != -1)
      return "Call Will Retry";

    return "";
  }

  private String getCallStatus(String rcstr, HashMap responses)
  {
    responses.clear();

    int startIndex = 0; 
    for (int i = 0; i <= 7; i++) { 
      int index = rcstr.indexOf("^", startIndex); 
      if (index == -1) break; 
      startIndex = index + 1; 
      if (i < 7) continue; 

      String respstr = rcstr.substring(index+1); 
      index = respstr.indexOf("^"); 
      if (index != -1) 
        respstr = respstr.substring(0, index); 
      parseCallResponses(respstr, responses);
    } 
    
    return getCallStatus(rcstr);
  } 

  private void parseCallResponses(String respstr, HashMap responses) 
  { 
    while (! respstr.isEmpty()) { 
      int index = respstr.indexOf('|'); 
      String nvstr = respstr; 
      if (index != -1) 
        nvstr = respstr.substring(0, index).trim(); 

      int index2 = nvstr.indexOf('='); 
      if (index2 == -1) 
        responses.put("__response___", nvstr); 
      else { 
        String key = nvstr.substring(0, index2).trim(); 
        String value = nvstr.substring(index2+1).trim(); 
        responses.put(key, value); 
      } 

      if (index == -1) 
        break; 
      respstr = respstr.substring(index+1).trim();
    } 
  }

  /* test usage */
  public static void main(String args[])
    throws InterruptedException
  {
    //String mynumber = "1112222"; // replace with your own

    Voicent voicent = new Voicent();
    
    Voicent v = new Voicent();
    String reqID = v.callIvr("0551633166", "SMSA_Staging", false);
    
    System.out.println(reqID);
    /*String reqId = voicent.callText(mynumber, "hello, how are you", true);
    System.out.println("callText: " + reqId);
    
    String reqId2 = voicent.callText(mynumber, "hello, how are you", true);
    voicent.callIvr("90551633166", "SMSA", false);
    System.out.println("reqID 2: " + reqId2);
    reqId = voicent.callAudio(mynumber, "C:/Program    Files/Voicent/MyRecordings/sample_message.wav", false);
    System.out.println("callAudio: " + reqId);

    while (true) {
      Thread.currentThread().sleep(30000);
      String status = voicent.callStatus(reqId);
      if (status.length() > 0) {
        System.out.println(status);
        voicent.callRemove(reqId);
        break;
      }
    }

    voicent.callTillConfirm("C:/Program    Files/Voicent/BroadcastByPhone/bin/vcast.exe",
          "C:/temp/testctf.voc",
          "C:/Program Files/Voicent/MyRecordings/sample_message.wav",
          "1234");
          */
}


  private String host_;
 
  private int port_;



}