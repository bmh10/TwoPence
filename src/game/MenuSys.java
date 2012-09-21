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
	private final int SP = 20;
	
	Game game;
	Menu currMenu;
	Dimension winSize;
	Image[] imgs;
	String[] imgFiles;
	
	
	public	MenuSys(Game game, Dimension winSize)
	{
		this.game = game;
		this.currMenu = Menu.MAIN;
		this.winSize = winSize;
		imgs = new Image[NUM_IMAGES];
		imgFiles = new String[NUM_IMAGES];
		loadGraphics();
		
		//Init mouse and key listeners
		game.addMouseListener(menuClickListener);
		game.addKeyListener(menuKeyListener);
	}
	
	private void loadGraphics()
	{
		setFileNames();
		for (int i = 0; i < NUM_IMAGES; i++)
		{
			imgs[i] = game.getImage(game.getDocumentBase(), imgFiles[i]);
		}
	}
	
	private void setFileNames()
	{
		imgFiles[0] = "pong_logo.gif";
	}
	
	private void changeMenu(Menu m)
	{
		this.currMenu = m;
	}
	
	public void draw(Graphics g)
	{
		drawMenuHeader(g);
		g.setFont(Fonts.smallfont);
		
		switch (currMenu)
		{
		case MAIN:
			drawMainMenu(g); break;
		case NEW_GAME:
			drawNewGameMenu(g); break;
		case FIND_OPPONENT:
			drawFindOpponentMenu(g); break;
		case OPTIONS:
			drawOptionsMenu(g); break;
		case CHECK:
			drawCheckMenu(g); break;
		}
	}
	
	private void drawMainMenu(Graphics g)
	{
		FontMetrics fm = g.getFontMetrics();
		int s = 300;
		centerString(g, fm, "1. NEW GAME", s);
		centerString(g, fm, "2. FIND OPPONENT", s+=SP);
		centerString(g, fm, "3. OPTIONS", s+=SP);
		centerString(g, fm, "4. QUIT", s+=SP);
	}
	
	private void drawNewGameMenu(Graphics g)
	{
		FontMetrics fm = g.getFontMetrics();
		int s = 300;
		centerString(g, fm, "1. SINGLE PLAYER", s);
		centerString(g, fm, "2. LOCAL MULTIPLAYER", s+=SP);
		centerString(g, fm, "3. BACK", s+=SP);
	}
	
	private void drawFindOpponentMenu(Graphics g)
	{
		FontMetrics fm = g.getFontMetrics();
		int s = 300;
		centerString(g, fm, "3. BACK", s);
	}
	
	private void drawOptionsMenu(Graphics g)
	{
		FontMetrics fm = g.getFontMetrics();
		int s = 300;
		centerString(g, fm, "1. SOUND", s);
		centerString(g, fm, "2. WALL BOUNCING", s+=SP);
		centerString(g, fm, "3. THEME", s+=SP);
		centerString(g, fm, "4. BACK", s+=SP);
	}
	
	private void drawCheckMenu(Graphics g)
	{
		FontMetrics fm = g.getFontMetrics();
		int s = 300;
		centerString(g, fm, "Are you sure you want to quit?", s);
		centerString(g, fm, "Y/N", s+=SP);
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
