package IVR.SDC;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;



public class HttpClient {


	ArrayList<String> mobileList;
	private String host_;
	private int port_;
	 
	
public HttpClient() {
		// TODO Auto-generated constructor stub
	mobileList = new ArrayList<>();	
	host_ = "172.30.100.84";
	 port_ = 8155;

	}

public void populateMobileList()
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
		ResultSet myRs = myStmt.executeQuery("SELECT DISTINCT Mobile FROM ( SELECT TOP (select 320 - case when (SELECT COUNT(*) cc FROM [OutboundIVR].[dbo].[IVROutboundCalls]  where isCallAttempted < 5 and DateTimeStamp > dateadd(hour,-48,GETDATE()) and callstatus <> 6 and CustomerSelection is null  and ( calledon is null or calledon < dateadd(hour,-3,GETDATE()))) > 319 then 320 else (SELECT COUNT(*) cc FROM [OutboundIVR].[dbo].[IVROutboundCalls]  where isCallAttempted < 5 and DateTimeStamp > dateadd(hour,-48,GETDATE()) and callstatus <> 6 and CustomerSelection is null  and ( calledon is null or calledon < dateadd(hour,-3,GETDATE()))) end) Mobile  FROM [IVROutboundCalls_SDC] WHERE DatePart(HOUR,GetDate()) BETWEEN 9 AND 21 AND Mobile LIKE '05%' AND IsNumeric(Mobile) = 1  AND GetDate() < DateAdd(HOUR,48,[datetimestamp]) AND ID > (SELECT MAX(ID) - 6000 FROM [OutboundIVR].[dbo].[IVROutboundCalls_SDC] ) AND [isCallAttempted] < 3 and ([CallStatus] = 0 or ([CallStatus] = 1 and [CalledOn] < DateAdd(HOUR,-3,GetDate())) OR ([CallStatus] = 7 AND [CalledOn] < DateAdd(HOUR,-3,GetDate()) AND [isCallAttempted] < 2 )) AND [CustomerSelection] IS NULL and AudioFileID in(SELECT AudioFileID FROM BranchAudioFiles WHERE ZoneID NOT IN (SELECT ZoneID FROM [CallTimeExceptions] WHERE CONVERT(VARCHAR(5),getDate(),108) BETWEEN [ExceptionTimeFrom] and [ExceptionTimeTo]))) SubQuery");
		while(myRs.next())
		{						
			
			 mobileList.add(myRs.getString("Mobile"));
			 //System.out.println(Mobile);					
		};
		
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}


for (String mob : mobileList)
{
System.out.println(mob);
}



}




public void invokeHttpRequest() throws Exception
{
	
	//Connect to DB and set AWB if there are any records:
	String dbUrl = "jdbc:sqlserver://172.30.100.84:1433; DataBaseName=OutboundIVR";
	//jdbc:sqlserver://server:port
	String usr = "Rali";	String pswrd = "Smsa1234";
	
		for (String mob : mobileList)
		{
			//callIvr(mob, "SMSA", false);			
			String ReqID = callIvr(mob, "SDC", false);
			System.out.println(ReqID);
			System.out.println();	
			
			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Connection myConn = DriverManager.getConnection(dbUrl, usr, pswrd);
				Statement myStmt = myConn.createStatement();
				int myRs = myStmt.executeUpdate("Update [IVROutboundCalls_SDC] SET [StatusString] = NULL, [RequestID] = " + ReqID + "WHERE [Mobile] = " + mob   );
			
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/*
			URL url = new URL("http://172.30.100.84:8155/ocall/callreqHandler.jsp?info=JavaClientTest&phoneno="+ mob +"&firstocc=1&startapp=SMSA&selfdelete=false");
			  HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			  httpCon.setDoOutput(true);
			  httpCon.setRequestMethod("POST");
			  OutputStreamWriter out = new OutputStreamWriter(
			      httpCon.getOutputStream());
			  System.out.println(httpCon.getResponseCode());
			  System.out.println(httpCon.getResponseMessage());
			  out.close();
			
		System.out.println(mob);
		*/
		}
		
		
}



		

public String callIvr(String phoneno, String appname, boolean selfdelete)

{
	
		
	
	 // call request url
	 String urlstr = "/ocall/callreqHandler.jsp";

	 // setting the http post string
	 String poststr = "info=";
	 poststr += URLEncoder.encode("SDC App: " + phoneno);

	 poststr += "&phoneno=";
	 poststr += phoneno;

	 poststr += "&firstocc=0";
	 
	 poststr += "&selfdelete=";
	 poststr += (selfdelete ? "1" : "0");

	 poststr += "&detection=3";
	 
	 poststr += "&startapp=";
	 poststr += URLEncoder.encode(appname);
	 

	 // Send Call Request
	 String rcstr = postToGateway(urlstr, poststr);

	 return getReqId(rcstr);
}
		
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		
		//System.out.println(LocalDateTime.now());		
		//Thread.currentThread();
		//httpclient.mobileList.add("0503201603");
		//httpclient.mobileList.add("0551633166");
		//httpclient.mobileList.add("0504390796");
		//httpclient.mobileList.add("0502224296");
		//httpclient.mobileList.add("0566634707");
		//httpclient.mobileList.add("0566993738");		
		
		
		HttpClient httpclient = new HttpClient();
		
		/*
		httpclient.mobileList.add("0503201603");
		httpclient.mobileList.add("0551633166");
		httpclient.mobileList.add("0539311456");
		httpclient.mobileList.add("0500243542");
		*/
		
		httpclient.populateMobileList();		
		httpclient.invokeHttpRequest();
		
		
		//httpclient.mobileList.add("0503201603");
		//httpclient.mobileList.add("0504390796");
		//httpclient.mobileList.add("0551633166");
			
		//httpclient.invokeHttpRequest();
		
		/*
		CallStatus testObject = new CallStatus();
		testObject.populateReqIDList();
		testObject.updateCallStatus();
		*/
		//System.out.println(httpclient.callStatus("1470307035380"));
		//System.out.println(httpclient.callStatus("1470307035522"));
	    //Thread.sleep(2000); 
		//System.out.println(LocalDateTime.now());
				
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
	


}
