package network;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import org.jasypt.util.password.BasicPasswordEncryptor;

import utils.Vector2D;
public class Client {

	private static boolean DEBUG = true;

	//NB This is actually 1 more than number of db cols as we also store calculated rank locally
	private static final int DB_NUM_COLS = 8;
	private static final int DB_HIGHSCORE_DISP_COUNT = 10;

	private static Connection connection;
	private static BasicPasswordEncryptor bpe;
	public static boolean loggedIn;
	private static String localUsername;
	private static String matchUsername;
	private static int GID;
	private static int matchRanking;
	private static String[] localStore;
	public static boolean waiting;
	//True if local player is playing from left to right
	public static boolean localOnLeft;
	public static boolean isLeftPlayersTurn;
	
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
        	//TODO: this need changing from localhost to static server ip
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
//        	tryLogin("Zee", "zee123");
//        	matchWithOpponent();
//        	matchWithOpponent();
//       		saveUserData();
//       		 printLocalStore();
        
       		 //newUser("Fred", "fred123");
        }
       
    }
    
    private static boolean init()
    {
    	localUsername = "";
    	GID = -1;
    	localStore = new String[DB_NUM_COLS];
    	loggedIn = false;
    	bpe = new BasicPasswordEncryptor();
    	waiting = false;
    	localOnLeft = true;
    	isLeftPlayersTurn = false;
    	
    	
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
                	//Check password (encrypt then check against db)
                	BasicPasswordEncryptor bpe = new BasicPasswordEncryptor();
					if (bpe.checkPassword(pass, resset.getString("Password")))
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
				
				String encryptedPass = bpe.encryptPassword(pass);
        		statement.executeUpdate("INSERT INTO players VALUES ('"+username+"','"+encryptedPass+"','0', '0', '0', '0', '"+dateFormat.format(date)+"')");
        		System.out.println(username+" added to db on "+dateFormat.format(date));
        		//TODO: check that user has actually been inserted into table
        		
        		//New user made, now actually log into account (with non encrypted pass)
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
            	Client.waiting = true;
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
            Client.waiting = false;
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
     * Ideally opponent should signal to us that match has begun via server
     * for now we just poll the server.
     */
    public static boolean hasMatchStarted()
    {
    	boolean started = false;
   	    
    	Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("SELECT gid, unameL, unameR FROM `games` WHERE unameL = '"+localUsername+"' OR unameR = '"+localUsername+"'");
            
            ResultSet resset = statement.getResultSet();
            //If not empty result -> match must have started
            if (resset.next())
            {
            	//Get opponents user name
            	String oppUname = resset.getString("unameR");
            	Client.localOnLeft = true;
            	if (oppUname.equals(Client.localUsername))
            	{
            		oppUname = resset.getString("unameL");
            		Client.localOnLeft = false;
            	}
            	String gid = resset.getString("gid");
            	
            	System.out.println("Match ("+gid+") started between "+resset.getString("unameL")+" and "+resset.getString("unameR"));
            	Client.waiting = false;
            	matchUsername = oppUname;
            	Client.GID = Integer.parseInt(gid);
            	started = true;
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
        
        return started;
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
            statement.execute("INSERT INTO `games` VALUES ('"+gid+"', '"+localUsername+"', '"+matchUsername+"', '0', 'null', '')");
            System.out.println("Added game to game table: "+gid+", "+localUsername+", "+matchUsername);
            Client.localOnLeft = true;
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
     * Sends velocity to games table
     */
    public static boolean sendVelocity(Vector2D vel)
    {
		boolean success = false;
		String v = String.valueOf(vel.x)+","+String.valueOf(vel.y);
   	    
    	Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate("UPDATE `games` SET Velocity = '"+v+"' WHERE gid = '"+GID+"'");
            System.out.println("Updated velocity in match "+GID+" to "+v);
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
     * Gets opponent velocity to games table
     */
    public static Vector2D getOpponentVelocity()
    {
    	Vector2D res = null;
    	Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("SELECT Velocity FROM `games` WHERE gid = '"+GID+"'");
            ResultSet resset = statement.getResultSet();

            if (resset.next())
            {
            	String vel = resset.getString("Velocity");
           		System.out.println("Recieved opponent velocity in match "+GID+": "+vel);
           		res = parseVelocity(vel);
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
        
        return res;
    }
    
    public static String getCoinPositions()
    {
    	String res = null;
    	Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("SELECT positions FROM `games` WHERE gid = '"+GID+"'");
            ResultSet resset = statement.getResultSet();

            if (resset.next())
            {
            	String poss = resset.getString("positions");
           		System.out.println("Recieved positions in match "+GID+": "+poss);
           		res = poss;
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
        
        return res;
    }
    
    public static boolean sendCoinPositions(String poss)
    {
    	boolean success = false;
   	    
    	Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate("UPDATE `games` SET positions = '"+poss+"' WHERE gid = '"+GID+"'");
            System.out.println("Updated coins possitions in match "+GID+" to "+poss);
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
     * Returns true if it is the local players turn
     */
    public static boolean isOurTurn()
    {
    	return ((Client.localOnLeft && Client.isLeftPlayersTurn) || (!Client.localOnLeft && !Client.isLeftPlayersTurn));
    }
    
    /*
     * Switches turns in db and also updates isLeftPlayersTurn variable
     */
    public static boolean switchTurns()
    {
    	boolean success = false;
    	isLeftPlayersTurn = !isLeftPlayersTurn;
    	int i = (isLeftPlayersTurn) ? 1 : 0;
   	    
    	Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate("UPDATE `games` SET lplayerTurn = '"+i+"' WHERE gid = '"+GID+"'");
            System.out.println("Updated player turn in match "+GID+" to (1=left, 0=right) "+isLeftPlayersTurn);
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
     * Sets velocity to null
     */
    public static boolean wipeVelocity()
    {
    	boolean success = false;
    	Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate("UPDATE `games` SET Velocity = '"+null+"' WHERE gid = '"+GID+"'");
            System.out.println("Wiped velocity");
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
     * Converts velocity in string form into 2d vector required by game
     */
    private static Vector2D parseVelocity(String vel)
    {
    	String s[];
    	s = vel.split(",");
    	return new Vector2D(Float.parseFloat(s[0]), Float.parseFloat(s[1]));
    	
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
        Client.GID = GID;
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