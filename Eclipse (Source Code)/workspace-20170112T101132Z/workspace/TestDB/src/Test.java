
import java.sql.*;
public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String dbUrl = 
				"jdbc:sqlserver://localhost:1433; DataBaseName=Dummy";
		//jdbc:sqlserver://server:port
		String usr = "Raouf";
		String pswrd = "1234";
		
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Connection myConn = DriverManager.getConnection(dbUrl, usr, pswrd);
			Statement myStmt = myConn.createStatement();
			ResultSet myRs = myStmt.executeQuery("SELECT TOP 1 * FROM T ");
			
			 while(myRs.next())
			{
				String ID = myRs.getString("ID");
				String Mob = myRs.getString("Mobile");
				String First = myRs.getString("First");
				 System.out.println(ID + " " + Mob + " " + First  );
				
				
			};
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
	}

}
