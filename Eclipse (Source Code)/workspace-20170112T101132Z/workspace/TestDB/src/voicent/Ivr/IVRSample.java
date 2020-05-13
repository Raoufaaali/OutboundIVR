package voicent.Ivr;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class IVRSample {

	public IVRSample() {
		// Default constructor used to instantiate new objects
	}
	
	public int getDigit()
	{
		//Dummy method
		int num = 3*0;
		System.out.println(num);
				return num;										
				
	}
	
	public Properties getAWB(String calledNumber)
	{
		//Invoked by IVR Studio
		
		//Set default String value <to check execution path and avoid null pointer exceptions>
		String WaybillNumber = "";
		String RSCAudioFile = "";
				
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
			ResultSet myRs = myStmt.executeQuery("SELECT TOP 1 * FROM [IVROutboundCalls] WHERE Mobile =  "+ calledNumber +" order by id desc");
			 while(myRs.next())
			{
				
				 WaybillNumber = myRs.getString("AWB");
				 RSCAudioFile = myRs.getString("AudioFileID");
				 
				 //System.out.println(WaybillNumber);					
			};
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//Create a property object, fill it then access it from IVR Studio
		
		Properties ProAWB = new Properties();
		
		ProAWB.setProperty("one", WaybillNumber.substring(0, 1));
		ProAWB.setProperty("two", WaybillNumber.substring(1, 2));
		ProAWB.setProperty("three", WaybillNumber.substring(2, 3));
		ProAWB.setProperty("four", WaybillNumber.substring(3, 4));
		ProAWB.setProperty("five", WaybillNumber.substring(4, 5));
		ProAWB.setProperty("sex", WaybillNumber.substring(5, 6));
		ProAWB.setProperty("seven", WaybillNumber.substring(6, 7));
		ProAWB.setProperty("eight", WaybillNumber.substring(7, 8));
		ProAWB.setProperty("nine", WaybillNumber.substring(8, 9));
		ProAWB.setProperty("ten", WaybillNumber.substring(9, 10));
		ProAWB.setProperty("eleven", WaybillNumber.substring(10, 11));
		ProAWB.setProperty("twelve", WaybillNumber.substring(11, 12));
		ProAWB.setProperty("rscAudioFile", RSCAudioFile);
		
		
		for (String key : ProAWB.stringPropertyNames()) {
			
            String value = ProAWB.getProperty(key);

            System.out.println("The Digit #  " + key + " is: " + value);

        }
		
		
		return ProAWB;		
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		IVRSample testobj = new IVRSample();
		testobj.getAWB("0551633166");
				
		
		

		
	}

}
