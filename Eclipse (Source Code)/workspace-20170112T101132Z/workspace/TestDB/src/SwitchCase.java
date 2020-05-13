
public class SwitchCase {

	public SwitchCase() {
		// TODO Auto-generated constructor stub
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int month = 8;
        String monthString;
        switch (month) {
            case 1:  monthString = "January";
                     break;
            case 2:  monthString = "February";
                     break;
            case 3:  monthString = "March";
                     break;
            case 4:  monthString = "April";
                     break;
            case 5:  monthString = "May";
                     break;
            case 6:  monthString = "June";
                     break;
            case 7:  monthString = "July";
                     break;
            case 8:  monthString = "August";
                     break;
            case 9:  monthString = "September";
                     break;
            case 10: monthString = "October";
                     break;
            case 11: monthString = "November";
                     break;
            case 12: monthString = "December";
                     break;
            default: monthString = "Invalid month";
                     break;
        }
        System.out.println(monthString);
        
        String s = "Test";
        
        System.out.println(s.charAt(0));
        
        
        String example      = "something";
        String firstLetter  = "";

        firstLetter = String.valueOf(example.charAt(0));
        System.out.println(firstLetter);
        
        
        String AWB = "1234123412344"; 
        int index = 0;
        
        String[] anArray;
        anArray = new String[12];
        
        int d1, d2, d3, d4, d5, d6, d7, d8, d9, d10, d11, d12;        

        
        while(index < 12)
        {
        	   
        	
        	anArray[index] = String.valueOf(AWB.charAt(index));
        	
        	System.out.println(anArray[index]);
        	
        	
        	
        	switch(anArray[index])
     	   {
     	   
     	   case "1" : d1 = 1;
     		   break;
     		   
     		   
     	   default: AWB = "Invalid month";
            break;
     	   
     	   }
        	
        	index++;
        }
        System.out.println(anArray[0]);
        
        
        
 
			}
}
