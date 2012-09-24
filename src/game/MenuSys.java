package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import utils.Fonts;
import utils.Vector2D;

public class MenuSys {
	
	enum Menu
	{
		MAIN,
		NEW_GAME,
		FIND_OPPONENT,
		OPTIONS,
		CHECK,
		NONE,
		
		//SUBMENUS
		//Options
		GENERAL,
		GAMEPLAY,
		CONNECTIVITY,
	};
	
	private final int NUM_IMAGES = 10;
	private final int NUM_BOXES  = 4;
	
	Game game;
	Menu currMenu, currSubMenu;
	Dimension winSize;
	int timer;
	boolean subMenuVisible;
	
	Box[] boxs, subBoxs;
	Vector2D boxStartPos, subBoxStartPos;
	Vector2D[] boxStartSize;
	Vector2D subBoxStartSize;
	
	Image[] imgs;
	String[] imgFiles;
	
	//Assign array to this depending on language
	String[][] txt;//, txtOpt;
	
	String[][] english =  { 
							{"NEW GAME", "FIND OPPONENT", "OPTIONS", "QUIT"},  //Main menu
							{"", "SINGLE PLAYER", "LOCAL MULTIPLAYER", "BACK"},  //New game
							{"", "", "", "BACK"},   //Find opponent
							{"GENERAL", "GAMEPLAY", "CONNECTIVITY", "BACK"}, //Options
							{"QUIT?", "YES", "NO", ""}, //Check
							{"", "", "", ""},
							{"Sound", "Language", "Animation", "Theme"},  //General
							{"Friction", "Edge Rule", "", ""},  //Gameplay
							{"Sign in", "Stats", "Friends", "Scoreboard"},  //Connectivity
					  	  };
	
//	//Options submenu text
//	String[][] englishOpt =  { 
//								{"ON", "OFF", "", ""},  //Sound
//								{"ENGLISH", "FRENCH", "GERMAN", ""},  //Language
//				  	  		 };
					  
	String[][] french =  { 
							{"NOUVEAU JEU", "TROUVER ADVERSAIRE", "OPTIONS DE", "QUITTER"},  //Main menu
							{"", "SEUL JOUEUR", "MULTIJOUEUR EN LOCAL", "ARRIËRE"},  //New game
							{"", "", "", "ARRIËRE"},   //Find opponent
							{"SON", "LANGUE", "", "ARRIËRE"}, //Options
							{"QUITTER?", "OUI", "NO", ""}, //Check
					     };
	
	//Defines menu flow
	Menu[][] nxtMenu =  { 
							{Menu.NEW_GAME, Menu.FIND_OPPONENT, Menu.OPTIONS, Menu.CHECK},  //Main menu
							{Menu.NONE, Menu.NONE,Menu.NONE, Menu.MAIN},  //New game
							{Menu.NONE, Menu.NONE, Menu.NONE, Menu.MAIN},   //Find opponent
							{Menu.NONE, Menu.NONE, Menu.NONE, Menu.MAIN}, //Options
							{Menu.NONE, Menu.NONE, Menu.MAIN, Menu.NONE}, //Check
					    };
					 
	
	public	MenuSys(Game game, Dimension winSize)
	{
		this.game = game;
		this.currMenu = Menu.MAIN;
		this.currSubMenu = Menu.NONE;
		this.subMenuVisible = false;
		this.winSize = winSize;
		this.timer = 0;
		//TODO: Get country connected on and set language (default to English)
		this.txt = english;
//		this.txtOpt = englishOpt;
		loadGraphics();
		setupBoxes();
		
		//Init mouse and key listeners
		game.addMouseListener(menuClickListener);
		game.addMouseMotionListener(menuMoveListener);
	}
	
	/*
	 * Loads grpahics files using file names stored in imgFiles array
	 */
	private void loadGraphics()
	{
		imgs = new Image[NUM_IMAGES];
		imgFiles = new String[NUM_IMAGES];
		setFileNames();
		for (int i = 0; i < NUM_IMAGES; i++)
		{
			imgs[i] = game.getImage(game.getDocumentBase(), imgFiles[i]);
		}
	}
	
	/*
	 * Sets all graphics file names
	 */
	private void setFileNames()
	{
		imgFiles[0] = "pong_logo.gif";
		//TODO: Add new graphics here
	}
	
	/*
	 * Setups up menus boxes in correct positions
	 */
	private void setupBoxes()
	{
		this.boxs = new Box[NUM_BOXES];
		this.subBoxs = new Box[NUM_BOXES];
		boxStartPos = new Vector2D((winSize.width-Box.size)/2,   (winSize.height-Box.size)/2);
		boxStartSize = new Vector2D[2];
		boxStartSize[0] = new Vector2D(10, Box.size);
		boxStartSize[1] = new Vector2D(Box.size, 10);
		
		//TODO: Change dimensions
		subBoxStartPos = new Vector2D(winSize.width/2-Box.size*3,   (winSize.height-Box.size)/2);
		subBoxStartSize = new Vector2D(100, 50);
		
		for (int i = 0; i < NUM_BOXES; i++)
		{
			boxs[i] = new Box();
			if (i==0 || i ==3)
				boxs[i].setPos(boxStartPos).setSize(boxStartSize[0]);
			else
				boxs[i].setPos(boxStartPos).setSize(boxStartSize[1]);
				
			subBoxs[i] = new Box();
			subBoxs[i].setPos(subBoxStartPos).setSize(subBoxStartSize).setFinalSize(Box.subSize, 10);
		}
		
		//Set positions to end in after animation
		boxs[0].setFinalPos(new Vector2D((winSize.width-Box.size)/2,   (winSize.height-Box.size*3)/2));
		boxs[1].setFinalPos(new Vector2D((winSize.width-Box.size*3)/2, (winSize.height-Box.size)/2));
		boxs[2].setFinalPos(new Vector2D((winSize.width+Box.size)/2,   (winSize.height-Box.size)/2));
		boxs[3].setFinalPos(new Vector2D((winSize.width-Box.size)/2,   (winSize.height+Box.size)/2));
		
		//TODO: change
		subBoxs[0].setFinalPos(new Vector2D(winSize.width/2-Box.size*3,   (winSize.height-Box.size)/2));
		subBoxs[1].setFinalPos(new Vector2D(winSize.width/2-Box.size*3,   (winSize.height-Box.size)/2+56));
		subBoxs[2].setFinalPos(new Vector2D(winSize.width/2-Box.size*3,   (winSize.height-Box.size)/2+112));
		subBoxs[3].setFinalPos(new Vector2D(winSize.width/2-Box.size*3,   (winSize.height-Box.size)/2+168));
		
		//TODO: animate subboxes - at moment just appear when required
	}
	
	private void changeMenu(int i)
	{
		Menu m = nxtMenu[currMenu.ordinal()][i];
		if (m!=Menu.NONE)
		{
			this.currMenu = m;
			subMenuVisible = false;
			//Animate boxes
			for (int n = 0; n < NUM_BOXES; n++)
			{
				if (n==0 || n ==3)
					boxs[n].setPos(boxStartPos).setSize(boxStartSize[0]);
				else
					boxs[n].setPos(boxStartPos).setSize(boxStartSize[1]);	
			}
		}
		else
		{
			//TODO: put in special cases for things like 'quit' and options(sound, language etc.) here
			switch (currMenu)
			{
				case NONE:
				case MAIN:
				break;
				case NEW_GAME:
					switch(i)
					{
						case 1:
							game.startNewGame(); break;
						case 2:
							game.startNewGame(); break;
					}
				break;
				case FIND_OPPONENT:
					switch(i)
					{
					}
				break;
				case OPTIONS:
					this.currSubMenu = calcSubmenu(Menu.GENERAL.ordinal()+i);
					subMenuVisible = true;
				break;
				case CHECK:
					switch(i)
					{
						case 1:
							System.exit(0); break;
					}
				break;
			}
			
		}
		
		SoundManager.MENUCLICK.play();
	}
	
	/*
	 * Finds menu in Menu enum with specifed index
	 */
	private Menu calcSubmenu(int i)
	{
		for (Menu m : Menu.values() )
		{
			if (m.ordinal()==i)
			{
				return m;
			}
		}
		return Menu.NONE;
	}
	
	/*
	 * Draws correct menu depending on current state
	 */
	public void draw(Graphics g)
	{
		//TODO: at the moment this is called every frame which is unnecessary
		//can stop calling when animation finished and mouse is not over a box
		drawMenuHeader(g);
		setTextAndDraw(g, currMenu.ordinal(), currSubMenu.ordinal());
		
		new Box(boxStartPos, "").setColor(Color.DARK_GRAY, Color.BLACK).draw(g);
	}
	
	private void setTextAndDraw(Graphics g, int i, int j)
	{
		timer++;
		g.setFont(Fonts.smallfont);
		for (int n = 0; n < NUM_BOXES; n++)
		{
			boxs[n].setText(txt[i][n]).draw(g);
			//If box has no text make it invisible
			boxs[n].setVisible(!boxs[n].hasNoText());
			
			//TODO: SubBoxes - change
			subBoxs[n].setText(txt[j][n]).draw(g);
			subBoxs[n].setVisible(subMenuVisible);
		}
	}
	
	/*
	 * Draws menu header (logo and particles)
	 */
	private void drawMenuHeader(Graphics g)
	{
		g.setFont(Fonts.largefont);
		FontMetrics fm = g.getFontMetrics();
		g.setColor(Color.YELLOW);
		//Draw logo
		g.drawImage(imgs[0], (winSize.width-imgs[0].getWidth(null))/2, 30, null);
		g.setFont(Fonts.smallfont);
		fm = g.getFontMetrics();
		centerString(g, fm, "by Ben Homer", winSize.height-50);
	}
	
	/*
	 * Draws and centers a string on the game screen at the specified y-position
	 */
	private void centerString(Graphics g, FontMetrics fm, String str, int ypos) {
		g.drawString(str, (winSize.width - fm.stringWidth(str))/2, ypos);
	}

	
	MouseListener menuClickListener = new MouseAdapter() {

		@Override
		public void mousePressed(MouseEvent e) {
			if (game.state == Game.MENU)
			{
				//Get rectangle which has been clicked
				for (int i = 0; i < NUM_BOXES; i++)
				{
					if (boxs[i].getRect().contains(e.getPoint()))
					{
						changeMenu(i);
						break;
					}
				}
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
		}
	};
	
		/*
	 * Allows user to use mouse as game controller
	 */
	MouseMotionListener menuMoveListener = new MouseMotionListener() {
		@Override
		public void mouseMoved(MouseEvent e) {
			if (game.state == Game.MENU)
			{
				//Get rectangle which has been moved over
				for (int i = 0; i < NUM_BOXES; i++)
				{
					if (boxs[i].getRect().contains(e.getPoint()))
					{
						boxs[i].setColor(Color.RED, Color.WHITE);
					}
					else
					{
						boxs[i].setColor(Color.GRAY, Color.BLACK);
					}
				}
				if (subMenuVisible)
				{
					for (int i = 0; i < NUM_BOXES; i++)
					{
						if (subBoxs[i].getRect().contains(e.getPoint()))
						{
							subBoxs[i].setColor(Color.RED, Color.WHITE);
						}
						else
						{
							subBoxs[i].setColor(Color.GRAY, Color.BLACK);
						}
					}
				}
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {}
	};
	
}
