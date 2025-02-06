package com.abc.travels;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Operations {
	static int loggedinUserId=0;
    static int bookingId=0;
    
    //this method represents the Logo
	public static void logo() throws Exception
	{
		 FileReader fr=new FileReader("C:\\Users\\laksh\\Downloads\\logo.txt");    
         int i;    
         while((i=fr.read())!=-1) {   
         System.out.print((char)i);
         }
         System.out.println();
         fr.close();      
	}
	
	
	//It displays the Menu of all Operations 
	public static void menu()
	{
		System.out.println("Enter the option to Select");
		System.out.println("1.Register for SignUp the Account");
		System.out.println("2.Login");
		System.out.println("3.Book the Journey");
		System.out.println("4.View Your Bookings");
		System.out.println("5.Reschedule your Journey");
		System.out.println("6.unlock your Account");
		System.out.println("7.Exit");
	}
	
	
	//Signup Implmentation
	public static void signup(Connection connection,Scanner sc)
	{
		try
		{
			System.out.println("Enter your First Name:");
			String firstName=sc.next();
			System.out.println("Enter your Last Name:");
			String lastName=sc.next();
			System.out.println("Enter your Mobile Number:");
			String mobileNumber=sc.next();
			System.out.println("Enter your Gender:");
			String Gender=sc.next();
			System.out.println("Enter your Email Address:");
			String emailId=sc.next();
			System.out.println("Enter your password:");
			String Password=sc.next();
			boolean check=isPasswordValid(Password);
			if (!check) {
	            System.out.println("Password must contain at least 8 characters, one uppercase letter, and one special character.");
	            return;
	        }
			//DatabaseConnections.storevalues(firstName, lastName, mobileNumber, Gender, emailId, Password);
			
			 if (isEmailUnique(emailId, connection)) {
                 String insertQuery = "INSERT INTO user_details (firstName, lastName, MobileNumber, Gender, emailId, password) VALUES (?, ?, ?, ?, ?, ?)";
                 try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                     preparedStatement.setString(1, firstName);
                     preparedStatement.setString(2, lastName);
                     preparedStatement.setString(3, mobileNumber);
                     preparedStatement.setString(4, Gender);
                     preparedStatement.setString(5, emailId);
                     preparedStatement.setString(6, Password);

                     int rowsInserted = preparedStatement.executeUpdate();
                     if (rowsInserted > 0) {
                         System.out.println("Sign-up successful! Your data has been stored.");
                     } else {
                         System.out.println("Sign-up failed. Please try again.");
                     }
                 }
             } else {
                 System.out.println("Email ID already exists. Please use a different email.");
             }
         
         }
         catch(SQLException e)
         {
        	System.out.println(e.getMessage());
         }
			
		
	}
	
	
	//checks whether the password is valid or not for Signup
	 public static boolean isPasswordValid(String password) {
	        // At least 8 characters, one uppercase letter, and one special character
	        return password.matches("^(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$");
	    }
	
	//checks whether the given mail is already exits or not in the signup operation
	private static boolean isEmailUnique(String email, Connection connection) throws SQLException {
        String query = "SELECT COUNT(*) FROM user_details WHERE emailId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) == 0;
                }
            }
        }
        return false;
    }
	//Signup Implementation Ends
	
	
	//Login implementation Starts
	public static void login(Connection connection,Scanner sc) throws SQLException
	{
		try
		{
			System.out.println("Enter the Email:");
			String userName=sc.next();
			System.out.println("Enter the Password:");
			String Password=sc.next();
			int failedCount=failedCount(connection,userName);
			boolean accountStatus=currentAccountStatus(connection,userName);
			
			 String login_query = "SELECT * FROM user_details WHERE emailId = ?";
		        try{
		        	
		            PreparedStatement preparedStatement = connection.prepareStatement(login_query);
		            preparedStatement.setString(1, userName);
		            //preparedStatement.setString(2, Password);
		        	//Statement stmt = connection.createStatement();
		            ResultSet resultSet = preparedStatement.executeQuery();
		            if(resultSet.next())
		            {
		            	
		            	String dbEmail=resultSet.getString("emailId");
		            	String dbPassword = resultSet.getString("password");
		            	
		            	
		            	 
			            	if(dbEmail.equals(userName) && dbPassword.equals(Password) && failedCount!=5)
			            	{
//			            		if(checkEmailexists(connection,userName))
//			            		{
//			            			updatefailedCountandaccountStatus(connection,dbEmail,failedCount,accountStatus);
//			            		}
//			            		else {
//			            		failedCount=0;
//			            		updateLoginStatus(connection,userName,failedCount,accountStatus);
//			            	
//			            		}
			            		
			            		failedCount=0;
			            		updateLoginStatus(connection,userName,failedCount,accountStatus);
			            		System.out.println("Login Succussful");
			            		loggedinUserId=resultSet.getInt("id");		
			            		 
			            	}
			            	
			            	else if(accountStatus==false)
			            	{
			            		System.out.println("Your Account has been Locked Due to entered wrong Password 5 times");
			            	}
			            	
			            	else if(dbEmail.equals(userName))
			            	{
			            		
			            		failedCount+=1;
			            		UpdatefailedCount(connection,userName,failedCount,accountStatus);
			            		System.out.println("Your Password is Incorrect");
			            		if(failedCount==5)
			            		{
			            			accountStatus=false;
			            			UpdatefailedCount(connection,userName,failedCount,accountStatus);
			            			
			            		}
			            	}
			            	
			            	
		            	
		            	
		            }
		            else {
	            		System.out.println("No account found please Create an Account for Sign in");
	            	}
		            
		         }catch(SQLException ex) {
		        	 System.out.println(ex.getMessage());
		         }
		        
			
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		
	}
	
	
	
//	public static boolean checkEmailexists(Connection con,String email) throws SQLException{
//		String query="SELECT * FROM login_status WHERE emailID = ?";
//		try {
//		  PreparedStatement preparedStatement = con.prepareStatement(query);
//          preparedStatement.setString(1, email);
//      	//Statement stmt = connection.createStatement();
//          ResultSet resultSet = preparedStatement.executeQuery();
//          if(resultSet.next()){
//          	String dbEmail=resultSet.getString("emailID");
//          	if(dbEmail!=null) {
//		      return true;
//          	}
//          }
//		}
//		catch (SQLException e){
//            System.out.println(e.getMessage());
//        }
//        return false;
//			
//	}
	
//	public static void updatefailedCountandaccountStatus(Connection con,String email,int count,boolean accountStatus) {
//		String query = "UPDATE login_status set failedCount = ? ,accountStatus=? where emailID = ? ";
//        try (PreparedStatement pstmt = con.prepareStatement(query)) {
//            pstmt.setInt(1,count); // set input parameter 1
//            pstmt.setBoolean(2,accountStatus);
//            pstmt.setString(3,email); // set input parameter 2
//            pstmt.executeUpdate(); // execute update statement
//            //con.commit();
//            System.out.println("Login Successful");
//        }catch (SQLException exp) {
//            exp.printStackTrace();
//        }	
//		
//	}
	
	
	
	//Update failedCount and accountstatus if login succussful
	public static void updateLoginStatus(Connection con,String email,int count,boolean accountStatus)
	{
			String insertQuery = "UPDATE user_details set failedCount = ? ,accountStatus=? where emailID = ? ";
			try (PreparedStatement preparedStatement = con.prepareStatement(insertQuery)) {
                preparedStatement.setInt(1, count);
                preparedStatement.setBoolean(2, accountStatus);
                preparedStatement.setString(3, email);  
                preparedStatement.executeUpdate();

//                int rowsInserted = preparedStatement.executeUpdate();
//                if (rowsInserted > 0) {
//                	System.out.println("Login Successful");
//                }
            }
			catch(SQLException ex) {
				System.out.println(ex.getMessage());
			}	
	}
	
	
	//updates the failedcount if it becomes 5 and changes the active status of account as false to stop logging in 
	public static void UpdatefailedCount(Connection conn,String email,int count,boolean accountStatus) throws SQLException
	{
		String query = "UPDATE user_details set failedCount = ? ,accountStatus=? where emailId = ? ";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1,count); // set input parameter 1
            pstmt.setBoolean(2,accountStatus);
            pstmt.setString(3,email); // set input parameter 2
            pstmt.executeUpdate(); // execute update statement
            //con.commit();
            
        }catch (SQLException exp) {
            System.out.println(exp.getMessage());
        }
	}
	
	//checks the failed count of that user to count the wrong password
	public static int failedCount(Connection con,String email)
	{
		String query="SELECT * FROM user_details WHERE emailId = ?";
		try {
			  PreparedStatement preparedStatement = con.prepareStatement(query);
	          preparedStatement.setString(1, email);
	      	//Statement stmt = connection.createStatement();
	          ResultSet resultSet = preparedStatement.executeQuery();
	          if(resultSet.next()){
	          	String dbEmail=resultSet.getString("emailId");
	          	int failedcount=resultSet.getInt("failedCount");
	          	if(dbEmail!=null) {
			      return failedcount;
	          	}
	          }
			}
			catch (SQLException e){
	            e.printStackTrace();
	        }
	        return 0;
	
	}
	
	//it checks and gets the current status of the account whether it is active or not
	public static boolean currentAccountStatus(Connection con,String email) {
		String query="SELECT * FROM user_details WHERE emailId=? ";
		try {
			  PreparedStatement preparedStatement = con.prepareStatement(query);
	          preparedStatement.setString(1, email);
	      	//Statement stmt = connection.createStatement();
	          ResultSet resultSet = preparedStatement.executeQuery();
	          if(resultSet.next()){
	          	String dbEmail=resultSet.getString("emailId");
	          	int failedcount=resultSet.getInt("failedCount");
	          	boolean Accountstatus=resultSet.getBoolean("accountStatus");
	          	return Accountstatus;
	          }
		}catch (SQLException e){
            e.printStackTrace();
        }
		return true;	
	}
	//Login Implmentation Ends
	
	
	//To start the booking 
	public static void bookJourney(Connection con,Scanner sc)
	{
	    //it will become true if the user is logged in	
		if(loggedinUserId!=0)
		{
			System.out.println("Enter your Source Station");
			String Source=sc.next();
			System.out.println("Enter Your Destination Station");
			String destination=sc.next();
			System.out.println("Enter Your Travel Date:");
			String travelDate=sc.next();
			System.out.println("Enter the Number of Passengers");
			int numberOfPassengers=sc.nextInt();
			checkDetailsPresent(con,Source,destination,travelDate,numberOfPassengers);
			
			
			
		}
		//it will ask user to Login IN  
		else {
			System.out.println("Please Login first to Plan your Journey");
		}
		
	}
	
	//checking the given details present in the route_details table
	public static void checkDetailsPresent(Connection con,String source,String destination,String travelDate,int numberOfPassengers) {
		String query="SELECT * FROM route_details WHERE source=? AND destination=? AND availableDate=? AND noOfSeats>=?";
		try {
			  Scanner sc=new Scanner(System.in);
			  PreparedStatement preparedStatement = con.prepareStatement(query);
	          preparedStatement.setString(1, source);
	          preparedStatement.setString(2, destination);
	          preparedStatement.setString(3, travelDate);
	          preparedStatement.setInt(4, numberOfPassengers);
	      	//Statement stmt = connection.createStatement();
	          ResultSet resultSet = preparedStatement.executeQuery();
	          if(resultSet.next()){
	          	String dbSource=resultSet.getString("source");
	          	String dbDestination=resultSet.getString("destination");
	          	String dbAvailableDate=resultSet.getString("availableDate");
	          	int availableSeats=resultSet.getInt("noOfSeats");
	          	if(source.equalsIgnoreCase(dbSource) && destination.equalsIgnoreCase(dbDestination) && travelDate.equalsIgnoreCase(dbAvailableDate) && availableSeats>=numberOfPassengers) {
	          		
	          		boolean weekendOrNot=checkWeekendOrNot(travelDate);
	          		int price=resultSet.getInt("price");
	                int discount=resultSet.getInt("discount");
	                double totalPrice=0.0;
	          		if(weekendOrNot)
	          		{
	          			price+=200;
	          			totalPrice = (price - (price * (discount / 100.0)))*numberOfPassengers;
	          		}
	          		else {
	          			totalPrice = (price - (price * (discount / 100.0)))*numberOfPassengers;	
	          		}
	          		System.out.println("Total Price for "+numberOfPassengers+" Passengers is:"+totalPrice);
	          		System.out.println("Press 1 to Confirm Your Booking");
	          		int confirmBooking=sc.nextInt();
	          		if(confirmBooking==1)
	          		{
	          			int seats=availableSeats-numberOfPassengers;
	          			int RouteId=resultSet.getInt("routeId");
	          			updateValuesInRouteDetails(con,RouteId,seats);
	          			updateDetailsinBookingTable(con,RouteId,dbSource,dbDestination,totalPrice,numberOfPassengers,dbAvailableDate);
	          			
	          		}
	          		
	          		
	          	}
	          	else {
					System.out.println("No Buses are Found");
				}
	          	
	          }
			}
			catch (SQLException e){
	            System.out.println(e.getMessage());
	        }
		
	}
	
	//to check whether the given day is weekend or not for the Extra Charge
	public static boolean checkWeekendOrNot(String travelDate)
	{

        // Get date input from user
       

        // Parse the date string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date = LocalDate.parse(travelDate, formatter);

        // Check if the date is a weekend
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        boolean isWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;

        // Print the result
        if (isWeekend) {
            return true;
        } else {
            return false;
        }

	}
	
	//update the available seats in the Routes Table after booking the tickets Succussfully
	public static void updateValuesInRouteDetails(Connection con,int RouteId,int seats) {
		String query="UPDATE route_details set noOfSeats = ? where routeId = ?";
		try {
			  PreparedStatement preparedStatement = con.prepareStatement(query);
	          preparedStatement.setInt(1, seats);
	          preparedStatement.setInt(2, RouteId);
	      	//Statement stmt = connection.createStatement();
	          preparedStatement.executeUpdate(); // execute update statement
		}catch (SQLException e){
			 System.out.println(e.getMessage());
      }
	}
	
	//After Booking the Ticket Update the booking details in the Booking Table
	public static void updateDetailsinBookingTable(Connection con,int routeId,String source,String destination,double price,int passengers,String date)
	{
		String insertQuery = "INSERT INTO booking_details (userId, routeId, source, destination, totalPrice, noOfPassengers,travellingDate) VALUES (?, ?, ?, ?, ?, ?,?)";
        try (PreparedStatement preparedStatement = con.prepareStatement(insertQuery)) {
            preparedStatement.setInt(1, loggedinUserId);
            preparedStatement.setInt(2, routeId);
            preparedStatement.setString(3, source);
            preparedStatement.setString(4, destination);
            preparedStatement.setDouble(5, price);
            preparedStatement.setInt(6, passengers);
            preparedStatement.setString(7, date);
            
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
            	System.out.println("Booking Succussful"); 
              }
          }

		  catch(SQLException e)
		   {
			 System.out.println(e.getMessage());
		   }

		
	}
	
	//Generate the Bill
	public static void GenerateBill(Connection con)
	{
		String query="select * from booking_details where userId=?";
		try {
			PreparedStatement preparedStatement = con.prepareStatement(query);
			preparedStatement.setInt(1,loggedinUserId);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
			System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+-------------------------+");
            System.out.println("| Booking ID | Source           | Destination   | Date Of Travelling      | No.of Passengers        | Total Price             |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+-------------------------+");
            System.out.println("| "+resultSet.getInt("BookingId")+"      |    "+resultSet.getString("source")+"          |    " +resultSet.getString("destination")+"     |        "+resultSet.getString("travellingDate")+"        |     "+resultSet.getString("noOfPassengers")+"      |     "+resultSet.getDouble("totalPrice"));
           
			}
			
		}
		catch(SQLException ex) {
			System.out.println(ex.getMessage());
		}
		
	}
	//Booking Ends
	
	//To view the Bookings of that User 
	public static void viewYourBookings(Connection con) {
		if(loggedinUserId!=0) {
		GenerateBill(con);
		}
		else {
			System.out.println("Please Login to check your Bookings");
		}
		
	}
	//View Bookings Ends
	
	
	//Reschedule Journey
	//To reschedule the Journey to next Coming Days If it is Available
	public static void rescheduleYourJourney(Connection con,Scanner sc)
	{
		if(loggedinUserId!=0)
		{
			System.out.println("Enter your Booking ID to reschedule Your Journey:");
			bookingId=sc.nextInt();
			String query="SELECT * FROM booking_details WHERE BookingId=?";
			try {
				PreparedStatement preparedStatement=con.prepareStatement(query);
				preparedStatement.setInt(1, bookingId);
				ResultSet resultSet = preparedStatement.executeQuery();
				while(resultSet.next())
				{
					int dbBookingId=resultSet.getInt("BookingId");
					if(bookingId==dbBookingId)
					{
						String travelDate=resultSet.getString("travellingDate");
						String dbSource=resultSet.getString("source");
						String dbdestination=resultSet.getString("destination");
						int passengers=resultSet.getInt("noOfPassengers");
						UpdateJourney(con,dbSource,dbdestination,passengers);
					}
					else {
						System.out.println("No Booking ID Found!!!");
					}
				}
			
				
			}
			catch(SQLException e) {
			System.out.println(e.getMessage());	
			}
		}
	}
	
	
	//To Check for the Next Available Dates for that Journey
	public static void UpdateJourney(Connection con,String dbSource,String dbdestination,int passengers)
	{
		String query="SELECT * FROM route_details WHERE source=? AND destination=? AND noOfSeats>=?";
		try {
			PreparedStatement preparedStatement=con.prepareStatement(query);
			preparedStatement.setString(1, dbSource);
			preparedStatement.setString(2, dbdestination);
			preparedStatement.setInt(3, passengers);
			ResultSet resultSet = preparedStatement.executeQuery();
			//ArrayList<String> date=new ArrayList<>();
			Map<Integer,String> map = new LinkedHashMap<>();
			int availableSeats=0;
			int rowcount=0;
			while(resultSet.next())
			{
				String mapvalue=resultSet.getString("availableDate");
				int mapkey=resultSet.getInt("routeId");
				map.put(mapkey, mapvalue);
				System.out.println("The Next Available Date are:"+mapvalue);
				availableSeats=resultSet.getInt("noOfSeats")-passengers;
				String source=resultSet.getString("source");
				String destination=resultSet.getString("destination");
				rowcount++;
				
			}
			if(rowcount<=1) {
				System.out.println("No Dates are available for Reschedule!!!");
			}
			
			Scanner sc=new Scanner(System.in);
			System.out.println("Enter the available Date for you:-");
			String newDate=sc.next();
			
			 // Iterating over Map
	        for (Map.Entry<Integer,String> e : map.entrySet()) {

	            
	            if(newDate.equals(e.getValue()))
	            {
	            	int updateKey=e.getKey();
	            	UpdateinBookingTableforNewDate(con,updateKey,newDate,availableSeats);
	            	
	            }
	        }
			
			
		
			
		}
		catch(SQLException e) {
		System.out.println(e.getMessage());	
		}
		
	}
	
	//If Journey is Available for Next new Dates Update them in the in the Booking Details Table
	public static void UpdateinBookingTableforNewDate(Connection con,int updateKey,String newDate,int availableSeats) {
		String query="UPDATE booking_details SET routeId=?,travellingDate=? WHERE BookingId=?";
		try
		{
			PreparedStatement preparedStatement=con.prepareStatement(query);
			preparedStatement.setInt(1,updateKey);
			preparedStatement.setString(2,newDate);
			preparedStatement.setInt(3,bookingId);
			preparedStatement.executeUpdate(); // execute update statement
			System.out.println("Your Journey Has Been Rescheduled");
			updateSeatsinRoutesTable(con,availableSeats,updateKey);
		
			
		}catch(SQLException ex) {
			System.out.println(ex.getMessage());
		}
		
	}
	
	
	//After Updating the Journey Date update the seats in the route table also
	public static void updateSeatsinRoutesTable(Connection con,int seats,int key) {
		String query="UPDATE route_details SET noOfSeats=? WHERE routeId=?";
		try {
			PreparedStatement preparedStatement=con.prepareStatement(query);
			preparedStatement.setInt(1,seats);
			preparedStatement.setInt(2,key);
			preparedStatement.executeUpdate(); // execute update statement
		}catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		
	}
	//Reschedule Journey Implmentation Ends
	
	
	//To unlock the Account If the User Tries the Wrong Password For 5 Times
	public static void unlockAccount(Connection con,Scanner sc) throws SQLException
	{
		System.out.println("Enter your Email Address:-");
		String userName=sc.next();
		System.out.println("Enter your registered Mobile Number:-");
		String number=sc.next();
		
		boolean emailpresent=checkEmailexists(con,userName,number);
		
		if(emailpresent==true)
		{
			
			if(activateAccountIfFound(con,userName,number)==true);
			System.out.println("Your Account is UnLocked");
		}
		else {
			System.out.println("No Account Found with Given Details");
		}

		
	}
	
	//Check the Email is available or not to Unlock it
	public static boolean checkEmailexists(Connection con,String email,String mobileNumber) throws SQLException{
		String query="SELECT * FROM user_details WHERE emailId = ? AND MobileNumber=?";
		try {
		  PreparedStatement preparedStatement = con.prepareStatement(query);
          preparedStatement.setString(1, email);
          preparedStatement.setString(2,mobileNumber );
      	//Statement stmt = connection.createStatement();
          ResultSet resultSet = preparedStatement.executeQuery();
          if(resultSet.next()){
          	String dbEmail=resultSet.getString("emailId");
          	String dbNumber=resultSet.getString("MobileNumber");
          	if(dbEmail!=null) {
		      return true;
          	}
          }
		}
		catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
			
	}
	
	//If the User Account Found Means Unlock it 
	public static boolean activateAccountIfFound(Connection con,String email,String mobileNumber)
	{
		String query="UPDATE user_details set failedCount = ? ,accountStatus=? where emailId = ? AND MobileNumber=?";
		try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1,0); // set input parameter 1
            pstmt.setBoolean(2,true);
            pstmt.setString(3,email);
            pstmt.setString(4,mobileNumber);// set input parameter 2
            pstmt.executeUpdate(); // execute update statement
            //con.commit();
            return true;
        }catch (SQLException exp) {
            exp.printStackTrace();
        }
		return false;
		
	}
	//Unlock Account Implementation Ends
	
	
}
