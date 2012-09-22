package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
	};
	
	private final int NUM_IMAGES = 10;
	private final int NUM_BOXES  = 4;
	private final int SP = 20;
	
	Game game;
	Menu currMenu;
	Dimension winSize;
	Box[] boxs;
	Image[] imgs;
	String[] imgFiles;
	//Add another dimension for different languages
	String[][] txt =  { 
							{"NEW GAME", "FIND OPPONENT", "OPTIONS", "QUIT"},  //Main menu
							{"", "SINGLE PLAYER", "LOCAL MULTIPLAYER", "BACK"},  //New game
							{"", "", "", "BACK"},   //Find opponent
							{"SOUND", "LANGUAGE", "", "BACK"}, //Options
							{"QUIT?", "YES", "NO", ""}, //Check
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
		this.winSize = winSize;
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
		for (int i = 0; i < NUM_BOXES; i++)
		{
			boxs[i] = new Box();
		}
		
		boxs[0].setPos(new Vector2D((winSize.width-Box.size)/2,   (winSize.height-Box.size*3)/2));
		boxs[1].setPos(new Vector2D((winSize.width-Box.size*3)/2, (winSize.height-Box.size)/2));
		boxs[2].setPos(new Vector2D((winSize.width+Box.size)/2,   (winSize.height-Box.size)/2));
		boxs[3].setPos(new Vector2D((winSize.width-Box.size)/2,   (winSize.height+Box.size)/2));
	}
	
	private void changeMenu(int i)
	{
		Menu m = nxtMenu[currMenu.ordinal()][i];
		if (m!=Menu.NONE)
		{
			this.currMenu = m;
		}
		else
		{
			//TODO: put in special cases for things like 'quit' and options(sound, langage etc.) here
			
		}
		
		SoundManager.MENUCLICK.play();
	}
	
	/*
	 * Draws correct menu depending on current state
	 */
	public void draw(Graphics g)
	{
		drawMenuHeader(g);
		setTextAndDraw(g, currMenu.ordinal());
	}
	
	private void setTextAndDraw(Graphics g, int i)
	{
		g.setFont(Fonts.smallfont);
		for (int n = 0; n < NUM_BOXES; n++)
		{
			boxs[n].setText(txt[i][n]).draw(g);
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
		g.drawImage(imgs[0], (winSize.width-imgs[0].getWidth(null))/2, 50, null);
		g.setFont(Fonts.scorefont);
		fm = g.getFontMetrics();
		centerString(g, fm, "by Ben Homer", 160);
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
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {}
	};
	
}
