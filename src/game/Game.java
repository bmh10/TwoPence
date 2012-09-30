package game;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.Timer;

import utils.Fonts;
import utils.Vector2D;


/*
 * Main class which manages the game
 */
@SuppressWarnings("serial")
public class Game extends Applet implements Runnable {

	Thread engine = null;
	Timer swingTimer;
	Coin[] coins = new Coin[coinCount];
	ArrayList<BallSmokeParticle> ballSmokeParticles;
	ArrayList<BallSmokeParticle> underlineParticles;
	Dimension winSize;
	
	Image dbimage;
	SoundManager backingTrack;
	long initialTime;
	
	MenuSys menuSys;
	boolean rPlayerTurn;
	boolean shotMade;
	//Another shot for hitting selected coin between other coins (without contact)
	boolean anotherShot;
	//Bonus shot due to other player's mistake
	//boolean bonusShot;
	boolean collision;
	Vector2D l1, l2;
	
	int[] scores = {0, 0};
	
	// Player options
	int numPlayers;
	boolean sound;
	boolean mouse;
	boolean ballTrail;
	 // difficulty => 0=easy, 1=med, 2=hard
	int difficulty; 
	int goalWidth;
	
	
	// Game states
	boolean deathMatch;
	boolean deathMatchWinner;
	
	boolean showStats;
	boolean paused;
	
	int state;
	int prevState;
	static final int PLAYING = 0;
//	static final int WAITING = 1;
	static final int MENU = 1;
//	static final int MODEMENU = 3;
//	static final int OPTMENU = 4;
//	static final int GAMEINFO = 5;
//	static final int CHECK = 6;

	public static final int coinCount = 3;
	public static final int goalWidthMed = 500;
	public static final int goalPostSize = 20;
	
	private Vector2D goalPostLT, goalPostLB, goalPostRT, goalPostRB;
	private Rectangle rL, rR;
	private Rectangle[] boundryRects;
	
	public static final int medPause = 8;
	public static final int easyPause = 10;
	public static final int hardPause = 5;
	int pause;
	
	public String getAppletInfo() {
		return "TwoPence by Ben Homer";
	}
	
	/*
	 * Initialises pong game
	 */
	public void init() {
		initialTime = System.currentTimeMillis();
		state = MENU;
		prevState = MENU;

		// Default settings
		rPlayerTurn = true;
		shotMade = false;
		anotherShot = false;
		collision = false;
		goalWidth = goalWidthMed;
		l1 = new Vector2D(0, 0);
		l2 = new Vector2D(0, 0);
		
		sound = true;
		mouse = false;
		ballTrail = true;
		showStats = false;		
		difficulty = 1; // medium
		pause = medPause;
		deathMatch = false;
		deathMatchWinner = false;
		
		setBackground(Color.BLACK);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(dim.width - 10, dim.height - 110);
		Dimension d = winSize = this.getSize();
		menuSys = new MenuSys(this, winSize);

		coins[0] = new Coin(new Vector2D(d.width/2+80, d.height/2), Color.YELLOW);
		coins[1] = new Coin(new Vector2D(d.width/2, d.height/2+40), Color.YELLOW);
		coins[2] = new Coin(new Vector2D(d.width/2, d.height/2-40), Color.YELLOW);
		for (int i = 0; i < coinCount; i++)
		{
			coins[i].setRange(0, d.width-1, 0, d.height-1);
		}
		
		this.goalPostLT = new Vector2D(goalPostSize, winSize.height/2 - goalWidth/2);
		this.goalPostLB = new Vector2D(goalPostSize, winSize.height/2 + goalWidth/2);
		this.goalPostRT = new Vector2D(winSize.width-goalPostSize*2, winSize.height/2 - goalWidth/2);
		this.goalPostRB = new Vector2D(winSize.width-goalPostSize*2, winSize.height/2 + goalWidth/2);
		
		rL = new Rectangle(0, (int)goalPostLT.y+goalPostSize, 2*goalPostSize, goalWidth-goalPostSize);
		rR = new Rectangle(winSize.width-2*goalPostSize, (int)goalPostLT.y+goalPostSize, 2*goalPostSize, goalWidth-goalPostSize);
		
		this.boundryRects = new Rectangle[4];
		//Top left
		boundryRects[0] = new Rectangle(0, 0, 2*goalPostSize, (winSize.height-goalWidth)/2 + goalPostSize);
		//Bottom left
		boundryRects[1] = new Rectangle(rL.x, rL.y+goalWidth - goalPostSize, 2*goalPostSize, (winSize.height-goalWidth)/2 + goalPostSize);
		//Top right
		boundryRects[2] = new Rectangle(winSize.width-2*goalPostSize, 0, 2*goalPostSize, (winSize.height-goalWidth)/2 + goalPostSize);
		//Bottom right
		boundryRects[3] = new Rectangle(rR.x, rR.y+goalWidth - goalPostSize, 2*goalPostSize, (winSize.height-goalWidth)/2 + goalPostSize);
		
		
		
		
		
		ballSmokeParticles = new ArrayList<BallSmokeParticle>();
		underlineParticles = new ArrayList<BallSmokeParticle>();

		
		dbimage = createImage(d.width, d.height);
		SoundManager.init();
		backingTrack = SoundManager.selectRandomBackgroundTrack();
		Fonts.initFonts();
		
		// add key/mouse listeners
		this.addKeyListener(keyListener);
		this.addMouseMotionListener(mouseMoveListener);
		this.addMouseListener(mouseClickListener);
	}


	/*
	 * Updates players' scores
	 */
	public void updateScore(int p) {
//		if (p==0)
//			lplayer.incScore();
//		else
//			rplayer.incScore();
	}

	
	public void startNewGame()
	{
		//Set starting position
		float n = (rPlayerTurn) ? 80 : -80;
		coins[0].setStartPos(new Vector2D(winSize.width/2+n, winSize.height/2));
		
		for (int i = 0; i < coinCount; i++)
		{
			coins[i].startPlay();
		}
		
		Coin selCoin = coins[0];
		for (int i = 1; i < coinCount; i++)
		{
			if (checkSelected(coins[i], selCoin))
			{
				selCoin = coins[i];
			}
		}
		selCoin.setSelected(true);
		state = PLAYING;
	}
	
	
	/*
	 *  Main game loop 
	 */
	@Override
	public void run() {
		SoundManager.INTRO.play();
		while(true) {
			try {
				for(int i=0; i!=5; i++)
					step();
				repaint();
				Thread.currentThread();
				Thread.sleep(pause);  // Change pause to alter game speed
			}
			catch (Exception e) {}
		}

	}
	
	
	/*
	 * Single game step
	 */
	public void step() {
		
		// Background music control
		if(state == PLAYING)
			backingTrack.play();
		else
			backingTrack.stopLoop();
		
		boolean allZero = true;
		//Move coins, and check that velocities of all coins are zero
		for (int i = 0; i < coinCount; i++)  
		{  
			coins[i].move(this);
			if (!coins[i].getVel().isZero())
			{
				allZero = false;
			}
		}
		
		//Check if circle passes through line
		if (shotMade)
		{
			Coin selCoin = coins[0];
			Vector2D p1 = null;
			Vector2D p2 = null;
			
			for (int i = 0; i < coinCount; i++)
			{
//				if (!coins[i].getVel().isZero())
//				{
//					allZero = false;
//				}
				
				if (!coins[i].isSelected())
				{
					if (p1 == null)
					{
						p1 = coins[i].getPos();
					}
					else
					{
						p2 = coins[i].getPos();
					}
				}
				else
				{
					selCoin = coins[i];
				}
			}
			l1 = p1;
			l2 = p2;
			
			//If selected coin passes through line get another turn
			if (selCoin.intersectsLine(l1, l2))
			{
				anotherShot = true;
			}
			
			//Resolve collisions between colliding coins
			for (int i = 0; i < coinCount; i++)  
			{  
			    for (int j = i + 1; j < coinCount; j++)  
			    {  
			        if (coins[i].colliding(coins[j]))  
			        {
			            coins[i].resolveCollision(coins[j]);
			            collision = true;
			        }
			    }
			}
			
			//Resolve collisions between coins and walls
			for (int i = 0; i < coinCount; i++)  
			{
				for (int j = 0; j < boundryRects.length; j++)
				{
				//NB testing with rL for now
					if (coins[i].colliding(boundryRects[j]))
					{
						coins[i].resolveCollision(boundryRects[j]);
					}
				}
			}
		
			//If coin has passed through other two coins with no collision
			//&& a goal is scored then increment score
			if (anotherShot && !collision)
			{
				if (rL.contains(selCoin.getPos().x, selCoin.getPos().y))
				{
					//Goal to right player
					scores[1]++;
					rPlayerTurn = !rPlayerTurn;
					startNewGame();
				}
				else if (rR.contains(selCoin.getPos().x, selCoin.getPos().y))
				{
					//Goal to left player
					scores[0]++;
					rPlayerTurn = !rPlayerTurn;
					startNewGame();
				}
			}
			
		}
		

		
		//Post shot update
		//Select correct coin depending on which player's turn (only when all coins are stationary and shot been made)
		if (shotMade && allZero)
		{
			if (collision)
			{
				// If collision has occured player cannot have another shot
				anotherShot = false;
			}
			
			if (!anotherShot)// && !bonusShot)
			{
				rPlayerTurn = !rPlayerTurn;
				if (!collision)
				{
					//If no collisions (&& have not earned another shot) other player gets a penalty
					setupPenalty();
				}
			}
			else
			{
				anotherShot = false;
				//bonusShot = false;
			}
			

			
			Coin selCoin = coins[0];
			coins[0].setSelected(false);
			for (int i = 1; i < coinCount; i++)
			{
				coins[i].setSelected(false);
				if (checkSelected(coins[i], selCoin))
				{
					selCoin = coins[i];
				}
			}
			
			selCoin.setSelected(true);
			shotMade = false;
			collision = false;
		}
		
		

		
		//if(ballTrail)
			//makeBallTrail(50);
		
		if (state != PLAYING) {
			//recalibratePaddles();
//			if (state != WAITING) {
				titleUnderlineParticles(500);
//				if(state != CHECK)
//					deathMatchWinner = false;
//			}
		}
		

	}
	
	/*
	 * Compare a coin to currently selected coin.
	 * If coin if closer to player than selected coin, returns true.
	 */
	private boolean checkSelected(Coin c1, Coin selCoin)
	{
		if (rPlayerTurn)
		{
			return c1.getPos().x > selCoin.getPos().x;
		}
		else
		{
			return c1.getPos().x < selCoin.getPos().x;
		}
	}
	
	/*
	 * Sets up penalty position for correct player
	 */
	private void setupPenalty()
	{
		if (rPlayerTurn)
		{
			coins[0].setPos(new Vector2D(rL.x+100, rL.y+150));
			coins[1].setPos(new Vector2D(rL.x+100, rL.y+rL.height-150));
			coins[2].setPos(new Vector2D(rR.x-600, rL.y+rL.height/2));
		}
		else
		{
			coins[0].setPos(new Vector2D(rR.x-100, rR.y+150));
			coins[1].setPos(new Vector2D(rR.x-100, rR.y+rR.height-150));
			coins[2].setPos(new Vector2D(rL.x+600, rR.y+rR.height/2));
		}
	}
	
	
	/*
	 * Displays game statistics
	 */
	public void displayStats(Graphics g, int s) {
		g.setFont(Fonts.tinyfont);
		g.setColor(Color.GREEN);
		FontMetrics fm = g.getFontMetrics();

		String time = Long.toString(System.currentTimeMillis() - initialTime);

		String rpx, rpy, lpx, lpy;
		
		String bx = Integer.toString( (int)coins[0].getPos().x);
		String by = Integer.toString( (int)coins[0].getPos().y);
		String bvx = Integer.toString( (int)coins[0].getVel().x);
		String bvy = Integer.toString( (int)coins[0].getVel().y);
		String st = null;
		switch(state) {
		case 0: st = "PLAYING"; break;
		case 1: st = "WAITING"; break;
		case 2: st = "MAINMENU"; break;
		case 3: st = "MODEMENU"; break;
		case 4: st = "OPTMENU"; break;
		case 5: st = "GAMEINFO"; break;
		case 6: st = "CHECK"; break;
		}
		String snd = (sound) ? "ON" : "OFF";
		String control = (mouse) ? "MOUSE" : "KEYS";
		String wrp = (Player.getWrap()) ? "ON" : "OFF";
		String btrail = (ballTrail) ? "ON" : "OFF";
		String diff = null;
		switch(difficulty) {
		case 0: diff = "EASY"; break;
		case 1: diff = "MEDIUM"; break;
		case 2: diff = "HARD"; break;
		}
		String gSpeed = Integer.toString(pause);
		String gType = (deathMatch) ? "Death Match" : "Classic";
		String dmWinner = (deathMatchWinner) ? "YES" : "NO";
		String pause = (paused) ? "YES" : "NO";
		
		
		leftString(g, fm, "Stats", s);
		leftString(g, fm, "Game run-time: " + time , s=space(s)+10);
		leftString(g, fm, "State: " + st, s=space(s));
		leftString(g, fm, "Paused: " + pause, s=space(s));
		leftString(g, fm, "Ball coordinates: " + bx + ", " + by, s=space(s)+10);
		leftString(g, fm, "Ball velocity: " + bvx + ", " + bvy, s=space(s));
		leftString(g, fm, "Player Options", s=space(s)+10);
		leftString(g, fm, "Game Type: " + gType, s=space(s)+10);
		leftString(g, fm, "Sound: " + snd, s=space(s));
		leftString(g, fm, "Control: " + control, s=space(s));
		leftString(g, fm, "Wrapping: " + wrp, s=space(s));
		leftString(g, fm, "Ball Trail: " + btrail, s=space(s));
		leftString(g, fm, "Difficulty: " + diff, s=space(s));
		leftString(g, fm, "Game Speed: " + gSpeed, s=space(s));
		leftString(g, fm, "DeathMatch winner: " + dmWinner, space(s));
	}
	
	
	/*
	 * Draws and centers a string on the game screen at the specified y-position
	 */
	private void centerString(Graphics g, FontMetrics fm, String str, int ypos) {
		g.drawString(str, (winSize.width - fm.stringWidth(str))/2, ypos);
	}
	
	/*
	 * Draws a string on the left side of the game screen at the specified y-position
	 */
	private void leftString(Graphics g, FontMetrics fm, String str, int ypos) {
		g.drawString(str, (winSize.width)/8, ypos);
	}
	
	/*
	 * Allows for easy formatting of string in a vertical column
	 */
	private int space(int s) {
		s += 20;
		return s;
	}
	
	/*
	 * Draws game banner at the start of a new game
	 */
	public void drawBanner(Graphics g) {
		// Change colours later
//		g.setFont(Fonts.largefont);
//		FontMetrics fm = g.getFontMetrics();
//		g.setColor(Color.YELLOW);
//		g.drawImage(logo, (winSize.width-logo.getWidth(null))/2, 50, null);
//		g.setFont(Fonts.scorefont);
//		fm = g.getFontMetrics();
//		centerString(g, fm, "by Ben Homer", 160);
//		g.setFont(Fonts.smallfont);
//		fm = g.getFontMetrics();
//
//		if(state == GAMEINFO)
//			openGameInfo(g, fm, 270);
//		else if(state == CHECK)
//			openCheck(g, fm);
//		else {
//			centerString(g, fm, "Type the number of the menu option you want:", 270);
//
//			if (state == MAINMENU)
//				openMainMenu(g, fm, 300);
//
//			else if (state == OPTMENU)
//				openOptionsMenu(g, fm, 300);
//
//			else if (state == MODEMENU)
//				openModeMenu(g, fm, 300);
//		}
	}
	
	/* 
	 * Draws game main menu 
	 */
	public void openMainMenu(Graphics g, FontMetrics fm, int s) {
//		centerString(g, fm, "1. Single Player", s);
//		centerString(g, fm, "2. Two Player", s=space(s));
//		centerString(g, fm, "3. Options", s=space(s));
//		centerString(g, fm, "4. Quit", space(s));
	}
	
	/*
	 * Creates particle space used to underline the main title
	 */
	public void titleUnderlineParticles(int numParticles) {
	
	//TODO: Turned particles off for now, do something with them later
		
//		// Keep number of particles constant
//		if(underlineParticles.size()>numParticles)
//			underlineParticles.remove(0);
//
//		// Make particle offset random
//		int areax = 300;
//		int areay = 50;
//		int randx = (int) (Math.random()*areax);
//		int randy = (int) (Math.random()*areay);
//
//		// Make the particles stay within specified area 
//		Point p = new Point((winSize.width - areax)/2 + randx, 230 - randy);
//		BallSmokeParticle b = new BallSmokeParticle(p);
//		underlineParticles.add(b);
//		
//		for(int i = 0; i<numParticles/2; i++)
//			underlineParticles.get(i).setColor(Color.YELLOW);
	}
	
	/*
	 * Creates ball particle trail during play
	 */
	public void makeBallTrail(int numParticles) {
		// Keep number of particles constant
		if(ballSmokeParticles.size()>numParticles)
			ballSmokeParticles.remove(0);

		// Make particle offset random
		int randx = (int) (Math.random()*20);
		int randy = (int) (Math.random()*20);

		// make the particles stay behind the ball direction of movement within a band
		Point p = new Point((int)(coins[0].getPos().x - coins[0].getVel().x*coins[0].getSize()/2 - coins[0].getVel().x*randx) , (int)(coins[0].getPos().y - coins[0].getVel().y*coins[0].getSize()/2 - coins[0].getVel().y*randy));
		BallSmokeParticle b = new BallSmokeParticle(p);
		ballSmokeParticles.add(b);

		
		for(int i = 0; i<20; i++)
			ballSmokeParticles.get(i).setColor(Color.RED);
	}
		
	
	/*
	 * Draws game mode menu
	 */
	public void openModeMenu(Graphics g, FontMetrics fm, int s) {
//		centerString(g, fm, "1. Classic", s);
//		centerString(g, fm, "2. Death Match", s=space(s));
//		centerString(g, fm, "3. Back", space(s));
	}
	
	/*
	 * Draws game options menu
	 */
	public void openOptionsMenu(Graphics g, FontMetrics fm, int l) {
//		String s, c, d, p, t, o;
//		if (sound) s = "ON";
//		else s = "OFF";
//		
//		if (mouse) c = "MOUSE";
//		else c = "KEYS";
//		
//		switch(difficulty) {
//		case 0: d = "EASY"; break;
//		case 2: d = "HARD"; break;
//		default: d = "MEDIUM";
//		}
//		
//		if(Player.getWrap()) p = "ON";
//		else p = "OFF";
//
//		if(ballTrail) t = "ON";
//		else t = "OFF";
//
//		
//		centerString(g, fm, "1. Game Info", l);
//		centerString(g, fm, "2. Sound  " + s , l=space(l));
//		centerString(g, fm, "3. Controller  " + c , l=space(l));
//		centerString(g, fm, "4. Difficulty  " + d , l=space(l));
//		centerString(g, fm, "5. Paddle Wrapping  " + p , l=space(l));
//		centerString(g, fm, "6. Ball Trail  " + t , l=space(l));
//		centerString(g, fm, "7. Game Orientation  " , l=space(l));
//		centerString(g, fm, "8. Back", space(l));
	}
	
	/*
	 * Draws game info
	 */
	public void openGameInfo(Graphics g, FontMetrics fm, int s) {
		
//		// Paragraph about game, background info, modes and controls
//		centerString(g, fm, "An emulation of an old classic with a few extras", s);
//		centerString(g, fm, "Go to the options menu to adjust game settings", s=space(s));
//		centerString(g, fm, "Please send any questions or comments to bensblogx@gmail.com", s=space(s));
//		centerString(g, fm, "Controls for horiontal play:", s=space(s)+20);
//		centerString(g, fm, "Right player: LEFT/RIGHT", s=space(s));
//		centerString(g, fm, "Left player: A/S", s=space(s));
//		centerString(g, fm, "Controls for vertical play:", s=space(s)+20);
//		centerString(g, fm, "Right player: UP/DOWN", s=space(s));
//		centerString(g, fm, "Left player: A/Z", s=space(s));
//		centerString(g, fm, "Press P during play to pause the game", s=space(s)+20);
//		centerString(g, fm, "Press BACKSPACE at any time for game stats", s=space(s)+20);
//		
//		centerString(g, fm, "Press ENTER to go back", s=space(s)+20);
	}

	/*
	 * Draws checker screen (checks if user want to exit)
	 */
	public void openCheck(Graphics g, FontMetrics fm) {
//		centerString(g, fm, "Are you sure?", 270);
//		centerString(g, fm, "Y/N", 320);
	}
	
	/*
	 * Displays the scores of both players and also checks for a winner in a death match
	 */
	public void displayScores(Graphics g) {
		g.setFont(Fonts.scorefont);
		g.setColor(Color.YELLOW);
		FontMetrics fm = g.getFontMetrics();
		String rscore = Integer.toString(scores[1]);
		String lscore = Integer.toString(scores[0]);
		centerString(g, fm, "Score" , 100);
		centerString(g, fm, lscore + "    " + rscore , 150);
		g.setFont(Fonts.smallfont);
		fm = g.getFontMetrics();
//		if (rplayer.getScore() == 0 && lplayer.getScore() == 0) {
//			if (deathMatch)
//				centerString(g, fm, "This is a Death Match: your paddle size will decrease if you lose a point" , 200);
//			centerString(g, fm, "You can change your control device in the options menu" , 230);
//			centerString(g, fm, "Press the up key or click the mouse to begin" , 260);
//		}
//		else {
//			if (deathMatch) {
//				if(rplayer.getLength() <= 0) {
//					deathMatchWinner = true;
//					centerString(g, fm, "Red player is the winner" , 200);
//				}
//				else if(lplayer.getLength() <= 0) {
//					deathMatchWinner = true;
//					centerString(g, fm, "Blue player is the winner" , 200);
//				}
//				if(deathMatchWinner) {
//					centerString(g, fm, "Press the up key or click the mouse to go back to main menu" , 250);
//					return;
//				}
//			}
			// If no death match winner or in classic game
			centerString(g, fm, "Press the up key or click the mouse to continue" , 200);
			centerString(g, fm, "Press escape to go back to the main menu" , 250);
//		}
	}
	
	public void drawIngameScores(Graphics g) {
		g.setFont(Fonts.scorefont);
		g.setColor(Color.YELLOW);
		FontMetrics fm = g.getFontMetrics();
		String rscore = Integer.toString(scores[1]);
		String lscore = Integer.toString(scores[0]);
		
		centerString(g, fm, lscore + "    " + rscore , 50);
	}
	
	/*
	 * Updates graphics every step
	 */
	public void update(Graphics realg) {
		Graphics g = dbimage.getGraphics();
		g.setColor(getBackground());
		g.fillRect(0, 0, winSize.width, winSize.height);
		g.setColor(getForeground());
		
		if (state==MENU) //!coins[0].inPlay) {
		{ 
			menuSys.draw(g);
		}
		else
		{

			// Re-draw game screen
			for (int i = 0; i < coinCount; i++)
			{
				coins[i].draw(g);
			}
			
			//debug - draw goal score area and outer areas
			g.setColor(Color.WHITE);
			g.fillRect(rL.x, rL.y, rL.width, rL.height);
			g.fillRect(rR.x, rR.y, rR.width, rR.height);
			g.setColor(Color.BLUE);
			for (int i = 0; i < boundryRects.length; i++)
			{
				g.fillRect(boundryRects[i].x, boundryRects[i].y, boundryRects[i].width, boundryRects[i].height);
			}
				
			
			//Draw goal posts
			g.setColor(Color.YELLOW);
			Vector2D p = this.goalPostLB;
			g.fillRect((int)p.x, (int)p.y, goalPostSize, goalPostSize);
			p = this.goalPostLT;
			g.fillRect((int)p.x, (int)p.y, goalPostSize, goalPostSize);
			p = this.goalPostRB;
			g.fillRect((int)p.x, (int)p.y, goalPostSize, goalPostSize);
			p = this.goalPostRT;
			g.fillRect((int)p.x, (int)p.y, goalPostSize, goalPostSize);
	
			
			//Draw line between 2 unselected coins
	
			Coin selCoin = null;
			for (int i = 0; i < coinCount; i++)
			{
				if (coins[i].isSelected())
				{
					selCoin = coins[i];
				}
			}
	
			
			if (state==PLAYING && shotMade)
			{
				g.drawLine((int)l1.x, (int)l1.y, (int)l2.x, (int)l2.y);
				
				//debug - draw complete line
				float gradient = (l2.y-l1.y)/(l2.x-l1.x);
				float c = l1.y - gradient*l1.x;
				g.setColor(Color.WHITE);
				//g.drawLine(-1000, (int) (gradient*-1000+c), 1000, (int) (gradient*1000+c));
				
				//debug - draw normal to line
				g.setColor(Color.BLUE);
				if (selCoin!= null && selCoin.ip != null)
					g.drawLine((int)selCoin.ip.x, (int)selCoin.ip.y, (int) selCoin.getPos().x, (int) selCoin.getPos().y);
			}
			
			
			
			if (ballTrail && state == PLAYING) {
				for (int i = 0; i < ballSmokeParticles.size(); i++)
				{
					BallSmokeParticle particle = ballSmokeParticles.get(i);
					if (particle != null)
						particle.draw(g);
				}
			}
			
			if (coins[0].inPlay)
			{
				drawIngameScores(g);
				int x = (!rPlayerTurn) ? 50 : winSize.width-70;
				g.fillRect(x, 10,20, 20);
			}
			
			if (paused)
				centerString(g, g.getFontMetrics(),"PAUSED" , 300);
			
		}

		if (showStats)
			displayStats(g, 150);

		if (state != PLAYING) {
			for (int i = 0; i < underlineParticles.size(); i++)
			{
				BallSmokeParticle particle = underlineParticles.get(i);
				if (particle != null)
					particle.draw(g);
			}
		}
		realg.drawImage(dbimage, 0, 0, this);
	}

	/*
	 * Starts a game thread
	 */
	public void start() {
		if(engine == null) {
			engine = new Thread(this);
			engine.start();
		}
	}
	
	/*
	 * Stops the current game thread
	 */
	@SuppressWarnings("deprecation")
	public void stop() {
		if (engine != null && engine.isAlive())
			engine.stop();
		engine = null;
	}
	
	/*
	 * Allows user to use mouse as game controller
	 */
	MouseMotionListener mouseMoveListener = new MouseMotionListener() {
		@Override
		public void mouseMoved(MouseEvent e) {
			switch(state) {
			case PLAYING:
				break;
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			switch(state) {
			case PLAYING:
				// If player drags mouse after clicking on selected coin, update power line
				for (int i = 0; i < coinCount; i++)
				{
					if (coins[i].isSelected())
					{
						coins[i].updatePowerLine(new Vector2D(e.getX(), e.getY()));
						break;
					}
				}
		
				break;
			}
		}
	};

	MouseListener mouseClickListener = new MouseAdapter() {

		@Override
		public void mousePressed(MouseEvent e) {
			switch(state) {
			case PLAYING:
				//If player clicks on coins and that piece is selected for next move then set up the power line
				for (int i = 0; i < coinCount; i++)
				{
					if (coins[i].isSelected() && coins[i].checkClicked(new Vector2D(e.getX(), e.getY())))
					{
						coins[i].setPowerLineDrawn(true, new Vector2D(e.getX(), e.getY()));
					}
				}
				break;
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			switch(state) {
			case PLAYING:
				//Makes shot with selected coin on mouse release
				for (int i = 0; i < coinCount; i++)
				{
					if (coins[i].isSelected() && coins[i].powerLineDrawn())
					{
						coins[i].makeShot();
						shotMade = true;
						break;
					}
				}
				break;
			}
		}
	};
	 
	/*
	 * Allows user to use keyboard as game controller
	*/
	KeyListener keyListener = new KeyAdapter() {
		
	@SuppressWarnings("deprecation")
	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		
		if (key == KeyEvent.VK_BACK_SPACE) {
			if (showStats) showStats = false;
			else showStats = true;
		}
		
		switch(state) {
			case PLAYING:
				if (key == KeyEvent.VK_P)
				{
					if (paused) { coins[0].flash(); paused = false; engine.resume(); }
					else { coins[0].flash(); paused = true; engine.suspend(); }
				}
				else if (key == KeyEvent.VK_ESCAPE)
				{
					state = MENU;
				}
				
	
			}
		}
		

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		switch(state) {
		case PLAYING:
		}
	}
	};
	
}
	

