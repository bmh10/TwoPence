package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;

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
	
	public void changeMenu(Graphics g, Menu m)
	{
		this.currMenu = m;
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
	}
	
	private void drawNewGameMenu(Graphics g)
	{
	}
	
	private void drawFindOpponentMenu(Graphics g)
	{
		
	}
	
	private void drawOptionsMenu(Graphics g)
	{
		
	}
	
	private void drawCheckMenu(Graphics g)
	{
		
	}
	
	/*
	 * Draws menu header (logo and particles)
	 */
	private void drawMenuHeader(Graphics g)
	{
//		g.setFont(largefont);
//		FontMetrics fm = g.getFontMetrics();
//		g.setColor(Color.YELLOW);
//		g.drawImage(logo, (winSize.width-logo.getWidth(null))/2, 50, null);
//		g.setFont(scorefont);
//		fm = g.getFontMetrics();
//		centerString(g, fm, "by Ben Homer", 160);
	}


}
