package voicent.Ivr;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CallStatus {

	ArrayList<String> ReqIDList;
	static HashMap<String, String> hmap;
		
	
	
	private String host_;
	private int port_;
	 
	
	
	public CallStatus() {
		// TODO Auto-generated constructor stub
		ReqIDList = new ArrayList<>();
		hmap = new HashMap<String, String>();

		
		
		host_ = "172.30.100.84";
		 port_ = 8155;
		
	}
	
	
	public void populateReqIDList()
	{
		//Connect to DB and set AWB if there are any records:
		String dbUrl = "jdbc:sqlserver://172.30.100.84:1433; DataBaseName=OutboundIVR";
		//jdbc:sqlserver://server:port
		String usr = "Rali";	String pswrd = "Smsa1234";
		
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Connection myConn = DriverManager.getConnection(dbUrl, usr, pswrd);
			Statement myStmt = myConn.createStatement();		
			ResultSet myRs = myStmt.executeQuery("SELECT TOP 100 [RequestID] FROM [IVROutboundCalls] WHERE [RequestID] IS NOT NULL AND [CallStatus] = 1 AND [StatusString] IS NULL AND CONVERT(VARCHAR(10), GetDate(), 120) = CONVERT(VARCHAR(10), [CalledOn], 120) AND [CalledOn] <= DateADD(MINUTE, -20, GetDate()) ORDER BY CalledOn" );
			 while(myRs.next())
			{						
				
				 ReqIDList.add(myRs.getString("RequestID"));
				 System.out.println(myRs.getString("RequestID"));
				 
			};
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	

public void updateCallStatus() throws Exception
{
	
	//Connect to DB and set AWB if there are any records:
	String dbUrl = "jdbc:sqlserver://172.30.100.84:1433; DataBaseName=OutboundIVR";
	//jdbc:sqlserver://server:port
	String usr = "Rali";	String pswrd = "Smsa1234";
	
		for (String reqID : ReqIDList)
		{
			
			String status = callStatus(reqID);
			//callStatus(reqID, hmap );
						
			if(status == null || status.isEmpty()) 
			{ 
				status = "No Call Record";		
				
			}
			
			System.out.println(reqID + ": " + status);
			
			/*for (String key : hmap.keySet()) {
			    System.out.println(key + " " + hmap.get(key));		   
			} */
			
			
			
			
			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Connection myConn = DriverManager.getConnection(dbUrl, usr, pswrd);
				Statement myStmt = myConn.createStatement();
				int myRs = myStmt.executeUpdate("Update [IVROutboundCalls] SET [CallStatus] = (CASE WHEN '" + status  + "' = 'Call Made' " + " THEN 7 ELSE [CallStatus] END), [StatusString] = '" + status + "' WHERE [RequestID] = " + reqID  );
				System.out.println(reqID + ": " + status);
			
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		
}



	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		CallStatus testObject = new CallStatus();
		//testObject.populateReqIDList();
		//testObject.updateCallStatus();
		
		//String st = testObject.callStatus("1472377205431");
		//System.out.println("Status is : " + st);
				
		//testObject.callStatus("1472377205431", hmap );
		
				
		String res = testObject.callStatus("1472538024326", hmap);
		//System.out.println(res);
		//hmap.put("a", "1");

		
		for (String key : hmap.keySet()) {
		    System.out.println(key + " " + hmap.get(key));		   
		}
	   	 
		for (String key : hmap.keySet()) {
	    System.out.println(key + " " + hmap.get(key));		   
	    }	
		
	    System.out.println("Is HashMap Empty? "+hmap.isEmpty());			
		//testObject.updateCallStatus();

	}
	

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

}
