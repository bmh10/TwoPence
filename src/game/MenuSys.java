package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
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
		
		SOUND,
		LANGUAGE,
		ANIMATION,
		THEME,
	};
	
	private final int NUM_IMAGES = 10;
	private final int NUM_BOXES  = 4;
	
	Game game;
	Menu currMenu, currSubMenu;
	Dimension winSize;
	int timer;
	boolean subMenuVisible;
	
	Box centralBox;
	Box[] boxs, subBoxs;
	Vector2D boxStartPos, subBoxStartPos;
//	Vector2D[] boxStartSize;
//	Vector2D subBoxStartSize;
	
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
							{"", "SEUL JOUEUR", "MULTIJOUEUR EN LOCAL", "ARRI�RE"},  //New game
							{"", "", "", "ARRI�RE"},   //Find opponent
							{"SON", "LANGUE", "", "ARRI�RE"}, //Options
							{"QUITTER?", "OUI", "NO", ""}, //Check
					     };
	
	//Defines menu flow
	Menu[][] nxtMenu =  { 
							{Menu.NEW_GAME, Menu.FIND_OPPONENT, Menu.OPTIONS, Menu.CHECK},  //Main menu
							{Menu.NONE, Menu.NONE,Menu.NONE, Menu.MAIN},  //New game
							{Menu.NONE, Menu.NONE, Menu.NONE, Menu.MAIN},   //Find opponent
							{Menu.NONE, Menu.NONE, Menu.NONE, Menu.MAIN}, //Options
							{Menu.NONE, Menu.NONE, Menu.MAIN, Menu.NONE}, //Check
							{Menu.NONE, Menu.NONE, Menu.MAIN, Menu.NONE}, //None
							{Menu.SOUND, Menu.LANGUAGE, Menu.ANIMATION, Menu.THEME}, //General
							{Menu.NONE, Menu.NONE, Menu.MAIN, Menu.NONE}, //Gameplay
							{Menu.NONE, Menu.NONE, Menu.MAIN, Menu.NONE}, //Connectivity
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
//		boxStartSize = new Vector2D[2];
//		boxStartSize[0] = new Vector2D(10, Box.size);
//		boxStartSize[1] = new Vector2D(Box.size, 10);
		
		this.centralBox = new Box(boxStartPos, "").setColor(Color.DARK_GRAY, Color.BLACK);
		
		//TODO: Change dimensions
		subBoxStartPos = new Vector2D(winSize.width/2-Box.size*3,   (winSize.height-Box.size)/2);
//		subBoxStartSize = new Vector2D(100, 20);
		
		for (int i = 0; i < NUM_BOXES; i++)
		{
			boxs[i] = new Box();
//			if (i==0 || i ==3)
//				boxs[i].setPos(boxStartPos).setSize(boxStartSize[0]);
//			else
				boxs[i].setPos(boxStartPos).setSizeBoth(Box.size, Box.size);
				
			subBoxs[i] = new Box().setPos(subBoxStartPos).setSizeBoth(Box.subSizeW, Box.subSizeH);
		}
		
		//Set positions to end in after animation
		boxs[0].setFinalPos(new Vector2D((winSize.width-Box.size)/2,   (winSize.height-Box.size*3)/2));
		boxs[1].setFinalPos(new Vector2D((winSize.width-Box.size*3)/2, (winSize.height-Box.size)/2));
		boxs[2].setFinalPos(new Vector2D((winSize.width+Box.size)/2,   (winSize.height-Box.size)/2));
		boxs[3].setFinalPos(new Vector2D((winSize.width-Box.size)/2,   (winSize.height+Box.size)/2));
		
		//TODO: change
//		subBoxs[0].setFinalPos(new Vector2D(winSize.width/2-Box.size*3,   (winSize.height-Box.size)/2));
//		subBoxs[1].setFinalPos(new Vector2D(winSize.width/2-Box.size*3,   (winSize.height-Box.size)/2+40));
//		subBoxs[2].setFinalPos(new Vector2D(winSize.width/2-Box.size*3,   (winSize.height-Box.size)/2+80));
//		subBoxs[3].setFinalPos(new Vector2D(winSize.width/2-Box.size*3,   (winSize.height-Box.size)/2+120));
		
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
//				if (n==0 || n ==3)
//					boxs[n].setPos(boxStartPos).setSize(boxStartSize[0]);
//				else
					boxs[n].setPos(boxStartPos);//.setSize(boxStartSize[1]);	
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
					changeSubmenu(Menu.GENERAL.ordinal(), i);
					
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
	 * Displays submenu options depending on currMenu and index of button pressed
	 */
	private void changeSubmenu(int currMenu, int idx)
	{
		this.currSubMenu = calcMenuFromId(currMenu+idx);
		subMenuVisible = true;
		animateSubBoxes(idx);
	}
	
	private void submenuOptionClicked(int i)
	{
		//if (currMenu==Menu.OPTIONS && i==Menu.LANGUAGE.ordinal())
		//TODO: DO PROPERLY
		centralBox.setFinalPos(new Vector2D(boxs[0].getPos().x-Box.size, boxs[0].getPos().y));
		centralBox.setFinalSize(3*Box.size, 3*Box.size);
	}
	
	/*
	 * Animates subboxes depending on which main box was pressed
	 */
	private void animateSubBoxes(int i)
	{
		//Set final position depending on main box pressed
		Point p = null;
		switch (i)
		{
		case 0:
		case 3:
			p = boxs[i].getPos();
			for (int j = 0; j < NUM_BOXES; j++)
			{
				if (j < 2)
					subBoxs[j].setFinalPos(new Vector2D(p.x-Box.subSizeW, p.y+6+j*(Box.subSizeH+6)));
				else
					subBoxs[j].setFinalPos(new Vector2D(p.x+Box.size, p.y+6+(j-2)*(Box.subSizeH+6)));
			}
			
		break;
		case 1:
		case 2:
			int ofst = (i==2) ? 1 : -1;
			p = boxs[i].getPos();
			for (int j = 0; j < NUM_BOXES; j++)
			{
				subBoxs[j].setFinalPos(new Vector2D(p.x+ofst*Box.subSizeW, p.y+6+j*(Box.subSizeH+6)));
			}
		break;
		}
		
		subBoxStartPos = new Vector2D(p.x, p.y);
		
		//Animate subBoxes
		for (int n = 0; n < NUM_BOXES; n++)
		{
//				if (n==0 || n ==3)
//					boxs[n].setPos(boxStartPos).setSize(boxStartSize[0]);
//				else
			subBoxs[n].setPos(subBoxStartPos);//.setSize(subBoxStartSize);	
		}
	}
	
	/*
	 * Finds menu in Menu enum with specified index
	 */
	private Menu calcMenuFromId(int i)
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
		
		centralBox.draw(g);
	}
	
	private void setTextAndDraw(Graphics g, int i, int j)
	{
		timer++;
		g.setFont(Fonts.smallfont);
		//Draw subboxes (behind main boxes)
		for (int n = 0; n < NUM_BOXES; n++)
		{
			//TODO: SubBoxes - change
			subBoxs[n].setText(txt[j][n]).draw(g);
			subBoxs[n].setVisible(subMenuVisible);
		}
		//Draw main boxes
		for (int n = 0; n < NUM_BOXES; n++)
		{
			boxs[n].setText(txt[i][n]).draw(g);
			//If box has no text make it invisible
			boxs[n].setVisible(!boxs[n].hasNoText());
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
				if (subMenuVisible)
				{
					//Get rectangle which has been clicked
					for (int i = 0; i < NUM_BOXES; i++)
					{
						if (subBoxs[i].getRect().contains(e.getPoint()))
						{
							submenuOptionClicked(i);
							break;
						}
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
