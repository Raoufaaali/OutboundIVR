package IVR.SDC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;



public class BranchGetter {

	public BranchGetter() {
		// TODO Auto-generated constructor stub
	}

	
	
	
	public Properties getBranch(String calledNumber)
	{

		
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
			ResultSet myRs = myStmt.executeQuery("SELECT TOP 1 * FROM [IVROutboundCalls_SDC] WHERE Mobile =  "+ calledNumber +" order by id desc");
			 while(myRs.next())
			{
				
				
				 RSCAudioFile = myRs.getString("AudioFileID");
				 
								
			};
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//Create a property object, fill it then access it from IVR Studio
		
		Properties ProAWB = new Properties();		
		
		ProAWB.setProperty("rscAudioFile", RSCAudioFile);
		
		
		for (String key : ProAWB.stringPropertyNames()) {
			
            String value = ProAWB.getProperty(key);

            System.out.println("The Branch  " + key + " is: " + value);

        }
		
		
		return ProAWB;		
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		BranchGetter testobj = new BranchGetter();
		testobj.getBranch("0551633166");
				
		
		

		
	}
	
	

}
