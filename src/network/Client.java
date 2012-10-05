package network;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
public class Client {

	private static boolean DEBUG = false;

	//NB This is actually 1 more than number of db cols as we also store calculated rank locally
	private static final int DB_NUM_COLS = 8;
	private static final int DB_HIGHSCORE_DISP_COUNT = 10;

	private static Connection connection;
	public static boolean loggedIn;
	private static String localUsername;
	private static String matchUsername;
	private static int matchRanking;
	private static String[] localStore;
	
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
        
        //Use for testing db functionality
        if (DEBUG)
        {
//        getHighScoreTable();
        	tryLogin("Zee", "zee123");
        	matchWithOpponent();
//       		saveUserData();
//       		 printLocalStore();
        
       		 //newUser("Fred", "fred123");
        }
       
    }
    
    private static boolean init()
    {
    	localUsername = "";
    	localStore = new String[DB_NUM_COLS];
    	loggedIn = false;
    	
    	
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
                		System.out.println("Login successful as '"+username+"' on "+dateFormat.format(date));
                		//Store name of logged in user and get user data to store locally
                		localUsername = username;
                		loggedIn = true;
                		getUserData();
                	}
                	else
                	{
                		System.out.println("Login failed - incorrect password"); 
                	}
                
                }                
            }
            else
            {
            	System.out.println("Login failed - incorrect username");
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
    	if (pass.length() < 4)
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
        		
        		//New user made now actually log into account
        		success = Client.tryLogin(username, pass);
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
     * Calculate players rank
     */
    private static int calculateRank(String uname)
    {
   	  	int rank = -1;
    	Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("SELECT Name FROM `players` ORDER BY Highscore DESC");
            
            ResultSet resset = statement.getResultSet();
            while (resset.next())
            {
            	if (uname.equals(resset.getString("Name")))
            	{
            		rank = resset.getRow();
            		break;
            	}
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
        
        return rank;
    }
    
    /*
     * Sort db by highscore to get top 10 players
     */
    public static LinkedHashMap<String, Integer> getHighScoreTable()
    {
    	int i = 0;
    	LinkedHashMap<String, Integer> hsTable = new LinkedHashMap<String, Integer>();
   	  
    	Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("SELECT Name, Highscore FROM `players` ORDER BY Highscore DESC");
            
            ResultSet resset = statement.getResultSet();
            while (resset.next() && i <  DB_HIGHSCORE_DISP_COUNT)
            {
            	i++;
            	hsTable.put(resset.getString("Name"), Integer.parseInt(resset.getString("Highscore")));
            	
                System.out.println(resset.getRow()+", "+resset.getString("Name")+", "+Integer.parseInt(resset.getString("Highscore")));
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
        
        return hsTable;
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
            	localStore[7] = Integer.toString(Client.calculateRank(localUsername));
            	
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
    
    public static String getName()
    {
    	return localStore[0];
    }
    
    public static String getOpponentName()
    {
    	return matchUsername;
    }
    
    public static int getHighscore()
    {
    	return Integer.parseInt(localStore[2]);
    }
    
    public static int[] getWDL()
    {
    	int[] wdl = new int[3];
    	for (int i = 0; i < 3; i++)
    	{
    		wdl[i] = Integer.parseInt(localStore[3+i]);
    	}
    	return wdl;
    }
    
    public static String getLastLogin()
    {
    	return localStore[6];
    }
    
    public static int getRanking()
    {
    	return Integer.parseInt(localStore[7]);
    }
    
    public static int getOpponentRanking()
    {
    	return calculateRank(matchUsername);
    }
    
    /*
     * TODO: This probably needs to be a critical section - need to do some kind of handshaking procedure
     * Matches local player with another player who is searching for a match
     */
    public static boolean matchWithOpponent()
    {
    	boolean success = false;
   	    
    	//Search for players seeking a match
    	Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("SELECT * FROM `waiting`");
            
            ResultSet resset = statement.getResultSet();
            //Take the first one if table not empty
            //TODO: Implement some kind of ordering system so players dont wait indefinately -> use timestamp for each player
            if (resset.next())
            {
            	String uname = resset.getString("Name");
            	//Remove this player from the waiting table
            	statement.execute("DELETE FROM `waiting` WHERE Name = '"+uname+"'");
            	
            	
//            	if (!uname.equals(localUsername))
//            	{
            		//TODO: do some kind of handshaking voodoo
            		
            		//TODO: Set ours and opponents linking bit to 0 (no longer searching)
            		System.out.println("Matched: "+localUsername+" with "+uname);
                	success = true;
                	matchUsername = uname;
//                	break;
//            	}
            }
            else
            {
            	System.out.println("Cannot find opponent, placed "+localUsername+" into waiting table");
            	//Insert local player into table (waiting for opponent)
            	statement.executeUpdate("INSERT INTO `waiting` VALUES ('"+localUsername+"')");
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
     * Called when player leaves 'find opponent menu'
     */
    public static boolean removeFromWaitingTable()
    {
		boolean success = false;
   	    
    	Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("DELETE FROM `waiting` WHERE Name = '"+localUsername+"'");
            System.out.println("Removed "+localUsername+" from waiting table");
            success = true;
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
     * Adds a new active game to games table
     */
    public static boolean createNewGame()
    {
		boolean success = false;
		int gid = generateGID();
   	    if (gid == -1)
   	    {
   	    	return success;
   	    }
   	    
    	Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("INSERT INTO `games` VALUES ('"+gid+"', '"+localUsername+"', '"+matchUsername+"')");
            System.out.println("Added game to game table: "+gid+", "+localUsername+", "+matchUsername);
            success = true;
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
     * Gets highest gid in games table and adds one
     * TODO: better way of doing this - gids will become 'fragmented' as more people play and quit over time
     */
    private static int generateGID()
    {
    	int GID = -1;
    	Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("SELECT gid FROM `games` ORDER BY gid DESC");
             ResultSet resset = statement.getResultSet();
             //Get highest gid in table and add 1
             if (resset.next())
             {
             	GID = Integer.parseInt(resset.getString("gid"))+1;
             }
             else
             {
             	GID = 0;
             }
            
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
        
        return GID;
    }

	/*
	 * Removes game from games table
	 */
	public static boolean endGame()
	{
		boolean success = false;
   	    
    	Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("DELETE FROM `games` WHERE unameL = '"+localUsername+"' OR unameR = '"+localUsername+"'");
            System.out.println("Removed "+localUsername+" from games table");
            success = true;
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
}