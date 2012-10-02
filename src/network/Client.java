package network;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
public class Client {

	private static final int DB_NUM_COLS = 7;

	private static Connection connection;
	static String localUsername;
	static String[] localStore;
	
    public static void startConnection()
    {
    	init();
        Class driver;
        driver = null;
        
        try {
            driver = Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Found driver " + driver);
        
        connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/players","root","");
        }
        catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        
        try {
            System.out.println("Established connection to " + connection.getMetaData().getURL());
        }
        catch (SQLException el) {
            el.printStackTrace();
        }
        
        tryLogin("Zee", "zee123");
        saveUserData();
        //printLocalStore();
        
        //newUser("Fred", "fred123");
    }
    
    private static boolean init()
    {
    	localUsername = "";
    	localStore = new String[DB_NUM_COLS];
    	
    	
    	return true;
    }
    
    /*
     * Check db to see if player exists and password matches
     */
    public static boolean tryLogin(String username, String pass)
    {
    	boolean success = false;
    	//Check to see if user exists
    	Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("SELECT Name, Password FROM players WHERE Name = '"+username+"'");
            
            ResultSet resset = statement.getResultSet();
            //Assuming only one row for a username (therefore use 'if' not 'while'
            if (resset.next())
            {
//                System.out.print(resset.getRow());
//                System.out.print("\n");
//                System.out.println("Name:" + resset.getString("Name"));
//                System.out.println("Pass:" + resset.getString("Password"));
//                System.out.print("\n");
                
                //If user exists (this check is redundant seen as usernames are unique but best to do for safety
                if (resset.getString("Name").equals(username))
                {
                	//Check password
                	if (resset.getString("Password").equals(pass))
                	{
                	    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						Date date = new Date();
                		success = true;
                		//Update last login date
                		statement.executeUpdate("UPDATE players SET LastLogin = '"+dateFormat.format(date)+"' WHERE Name = '"+username+"'");
                		System.out.println("Login successful on "+dateFormat.format(date));
                		//Store name of logged in user and get user data to store locally
                		localUsername = username;
                		getUserData();
                	}
                	else
                	{
                		System.out.print("Login failed - incorrect password"); 
                	}
                
                }                
            }
            else
            {
            	System.out.print("Login failed - incorrect username");
            }
            resset.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (statement != null)
            {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return success;
    }
    
    /*
     * Add new user to db returns true if successful
     */
    public static boolean newUser(String username, String pass)
    {
    	boolean success = false;
    	//Input sanitation
    	//TODO: Add more checks on chars used
    	if (username.equals(""))
    	{
    		System.out.println("Username cannot be blank");
    		return success;
    	}
    	if (pass.length() > 4)
    	{
    		System.out.println("Password must be at least 5 characters long");
    		return success;
    	}
    	
    	Statement statement = null;
        try {
            statement = connection.createStatement();
            //Check username is not already in use
            statement.execute("SELECT Name, Password FROM players WHERE Name = '"+username+"'");
            if (statement.getResultSet().next())
            {
            	System.out.println(username+" already in db");
            	success = false;
            }
            else
            {
        		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date date = new Date();
        		statement.executeUpdate("INSERT INTO players VALUES ('"+username+"','"+pass+"','0', '0', '0', '0', '"+dateFormat.format(date)+"')");
        		System.out.println(username+" added to db on "+dateFormat.format(date));
        		//TODO: check that user has actually been inserted into table
        		success = true;
            }
            
           
            //resset.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (statement != null)
            {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
		return success;
    }
    
    /*
     * Sort db by highscore
     */
    public static boolean getHighScoreTable()
    {
    	
    	
    	
    	return true;
    }
    
    public static boolean checkForNewHishscore()
    {
    	return false;
    }
    
    /*
     * At start of session get all user data from db and store locally for efficiency
     */
    public static boolean getUserData()
    {
   	    boolean success = false;
   	    if (localUsername.equals(""))
   	    {
   	    	return success;
   	    }
    	//Check to see if user exists
    	Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("SELECT * FROM players WHERE Name = '"+localUsername+"'");
            
            ResultSet resset = statement.getResultSet();
            //Assuming only one row for a username (therefore use 'if' not 'while'
            if (resset.next())
            {
            	localStore[0] = resset.getString("Name");
            	localStore[1] = resset.getString("Password");
            	localStore[2] = resset.getString("Highscore");
            	localStore[3] = resset.getString("Wins");
            	localStore[4] = resset.getString("Draws");
            	localStore[5] = resset.getString("Losses");
            	localStore[6] = resset.getString("LastLogin");
            	
//                System.out.print(resset.getRow());
				
                System.out.println("Player data stored locally");
                success = true;
            }
            resset.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (statement != null)
            {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return success;
    }
    
    /*
     * Copy locally stored user data into db (at end of game/session)
     */
     //TODO: atm this copies all data from local store into db, should probably not allow username to be changed/saved (should be persistenet)
    public static boolean saveUserData()
    {
    	boolean success = false;
    	Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("SELECT Name, Password FROM players WHERE Name = '"+localUsername+"'");
            
            ResultSet resset = statement.getResultSet();
            //Assuming only one row for a username (therefore use 'if' not 'while'
            if (resset.next())
            {
        		//Update user data in db from local store
        		statement.executeUpdate("UPDATE players SET Name = '"+localStore[0]+"', Password = '"+localStore[1]+"', Highscore = '"+localStore[2]+"', Wins = '"+localStore[3]+"', Draws = '"+localStore[4]+"', Losses = '"+localStore[5]+"', LastLogin = '"+localStore[6]+"' WHERE Name = '"+localUsername+"'");
        		success = true;
        		System.out.println("Local store saved to db");
            }
            else
            {
            	System.out.print("Cannot save to db");
            }
            resset.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (statement != null)
            {
                try {
                    statement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return success;
    }
    
    
    public static void printLocalStore()
    {
    	for (int i = 0; i < DB_NUM_COLS; i++)
    	{
    		System.out.print(localStore[i]+" ; ");
    	}
    	System.out.println();
    }
   
}