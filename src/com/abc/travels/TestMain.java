package com.abc.travels;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class TestMain {
	 private static final String url = "jdbc:mysql://localhost:3306/abc_travels";
	    private static final String username = "root";
	    private static final String password = "root";

	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		
		 try{
	            Class.forName("com.mysql.cj.jdbc.Driver");
	        }catch (ClassNotFoundException e){
	            System.out.println(e.getMessage());
	        }
		 
		Scanner sc=new Scanner(System.in);
		boolean flag=true;
		
			try {
				Connection connection = DriverManager.getConnection(url, username, password);
				Operations.logo();
				while(flag) {
					Operations.menu();
					int n=sc.nextInt();
					switch(n)
					{
					 case 1:
						 Operations.signup(connection,sc);
						 break;
					 case 2:
						 Operations.login(connection,sc);
						 break;
					 case 3:
						 Operations.bookJourney(connection,sc);
						 break;
					 case 4:
						  Operations.viewYourBookings(connection);
						  break;
					 case 5:
						 Operations.rescheduleYourJourney(connection,sc);
						 break;
					 case 6:
						 Operations.unlockAccount(connection,sc);
						 break;
					 case 7:
						  System.out.println("Thank you for visiting");
						  flag=false;
						  break;
					 default:
						 System.out.println("You have Entered Invalid Option.Please Check!");
						 break;
						 
					}
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			}
		
		

	}
	
	
	
	

}
