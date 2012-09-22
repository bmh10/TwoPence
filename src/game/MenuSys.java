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
	String[][] txt =  { {"NEW GAME", "FIND OPPONENT", "OPTIONS", "QUIT"},  //Main menu
						{"", "SINGLE PLAYER", "LOCAL MULTIPLAYER", "BACK"},  //New game
						{"", "", "", "BACK"},   //Find opponent
						{"SOUND", "LANGUAGE", "", "BACK"}, //Option
						{"QUIT?", "YES", "NO", ""}, //CHECK
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
		game.addKeyListener(menuKeyListener);
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
		
		boxs[0].setPos(new Vector2D((winSize.width-Box.size)/2, (winSize.height-Box.size*3)/2));
		boxs[1].setPos(new Vector2D((winSize.width-Box.size*3)/2, (winSize.height-Box.size)/2));
		boxs[2].setPos(new Vector2D((winSize.width+Box.size)/2, (winSize.height-Box.size)/2));
		boxs[3].setPos(new Vector2D((winSize.width-Box.size)/2, (winSize.height+Box.size)/2));
	}
	
	private void changeMenu(Menu m)
	{
		this.currMenu = m;
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
			
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
		}
	};
	
	/*
	 * Allows user to use keyboard as game controller
	*/
	KeyListener menuKeyListener = new KeyAdapter() {
		
	@SuppressWarnings("deprecation")
	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		
		switch(currMenu) {
			
			case MAIN:
				switch(key) {
					case '1':
						SoundManager.MENUCLICK.play();
						changeMenu(Menu.NEW_GAME);
						break;
					case '2':
						SoundManager.MENUCLICK.play();
						changeMenu(Menu.FIND_OPPONENT);
						break;
					case '3':
						SoundManager.MENUCLICK.play();
						changeMenu(Menu.OPTIONS);
						break;
					case '4': case Event.ESCAPE:
						SoundManager.MENUCLICK.play();
						//prevState = MAINMENU;
						changeMenu(Menu.CHECK);
						break;
				}
			break;
			
			case NEW_GAME:
				switch(key) {
					case '1':
						// Single Player
						SoundManager.MENUCLICK.play();
//						backingTrack = SoundManager.selectRandomBackgroundTrack();
//						deathMatch = false;
						game.startNewGame();
						break;
					case '2':
						// Local Multiplayer
						SoundManager.MENUCLICK.play();
//						backingTrack = SoundManager.selectRandomBackgroundTrack();
//						deathMatch = true;
						game.startNewGame();
						break;
					case '3':
						//Back
						SoundManager.MENUCLICK.play();
						changeMenu(Menu.MAIN);
						break;
				}
			break;

			case FIND_OPPONENT:
				break;
			case OPTIONS:
				switch(key) {
					case '1':
						SoundManager.MENUCLICK.play();
						// Show game info text
						//state = GAMEINFO;
						break;
					case '2':
						SoundManager.unmute();
						SoundManager.MENUCLICK.play();
						// Toggle sound ON/OFF
//						if (sound) {
//							sound = false;
//							SoundManager.mute();
//						}
//						else {
//							sound = true;
//							SoundManager.unmute();
//						}
						break;
					
					case '3':
						SoundManager.MENUCLICK.play();
						// Toggle game orientation HORIZ/VERT
						
						break;
					case '4':
						SoundManager.MENUCLICK.play();
						changeMenu(Menu.MAIN);
						break;
				}
			break;
			
//			case GAMEINFO:
//				switch(key) {
//					case Event.ENTER:
//						SoundManager.MENUCLICK.play();
//						state = OPTMENU;
//						break;
//				}
//			break;
			
			case CHECK:
				switch(key) {
					case 'Y': case 'y':
						SoundManager.MENUCLICK.play();
//						if(prevState == WAITING) {
//							
//							state = MAINMENU;
//						}
//						else
							System.exit(0);
						break;
					case 'N': case 'n':
						SoundManager.MENUCLICK.play();
//						state = prevState;
						changeMenu(Menu.MAIN);
						break;
				}
			break;
			}
		}
	};
}
