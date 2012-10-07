package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.TextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import network.Client;

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
	
	private final int NUM_BOXES  = 4;
	private final int NUM_SUB_BOXES  = 15; //TODO: change to max num used
	private final int NUM_LANGUAGES  = 6;
	private final int NUM_TEXT_FIELDS  = 2;
	
	Game game;
	Menu currMenu, currSubMenu;
	Dimension winSize;
	boolean subMenuVisible;
	int  drawSubmenuScreen;
	
	Box centralBox;
	//boxs=main boxes, subBoxes=slide in submenu boxes, subMenuBoxs=boxes used in submenu screens
	Box[] boxs, subBoxs, subMenuBoxs;
	Vector2D boxStartPos, subBoxStartPos, centralBoxSubmenuPos;
	TextField[] textFields;
	
//	int[] subMenuBoxMax = {NUM};
//	Vector2D[] boxStartSize;
//	Vector2D subBoxStartSize;
	
	//Assign array to this depending on language
	String[][] txt;//, txtOpt;
	
	String[] langs = {"ENG", "FR", "GER", "ITL", "POL", "CHI"};
	
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
							{"", "SEUL JOUEUR", "MULTIJOUEUR EN LOCAL", "RETOUR"},  //New game
							{"", "", "", "ARRIèRE"},   //Find opponent
							{"GENERAL", "GAMEPLAY", "CONNECTIVITÉ", "RETOUR"}, //Options
							{"QUITTER?", "OUI", "NO", ""}, //Check
							{"", "", "", ""},
							{"Son", "Langue", "Animation", "Thème"},  //General
							{"Friction", "Règle Bord", "", ""},  //Gameplay
							{"Connexion", "Stats", "Amis", "Tableau de bord"},  //Connectivity
					     };
	
	//Defines menu flow
	Menu[][] nxtMenu =  { 
							{Menu.NEW_GAME, Menu.NONE, Menu.OPTIONS, Menu.CHECK},  //Main menu
							{Menu.NONE, Menu.NONE,Menu.NONE, Menu.MAIN},  //New game
							{Menu.NONE, Menu.NONE, Menu.NONE, Menu.MAIN},   //Find opponent
							{Menu.NONE, Menu.NONE, Menu.NONE, Menu.MAIN}, //Options
							{Menu.NONE, Menu.NONE, Menu.MAIN, Menu.NONE}, //Check
							{Menu.NONE, Menu.NONE, Menu.MAIN, Menu.NONE}, //None
							//{Menu.SOUND, Menu.LANGUAGE, Menu.ANIMATION, Menu.THEME}, //General
							{Menu.NONE, Menu.NONE, Menu.MAIN, Menu.NONE}, //Gameplay
							{Menu.NONE, Menu.NONE, Menu.MAIN, Menu.NONE}, //Connectivity
					    };
					 
	
	public	MenuSys(Game game, Dimension winSize)
	{
		this.game = game;
		this.currMenu = Menu.MAIN;
		this.currSubMenu = Menu.NONE;
		this.subMenuVisible = false;
		this.drawSubmenuScreen = -1;
		this.winSize = winSize;
		//TODO: Get country connected on and set language (default to English)
		this.txt = english;
//		this.txtOpt = englishOpt;
//		loadGraphics();
		setupBoxes();
		
		//Init mouse and key listeners
		game.addMouseListener(menuClickListener);
		game.addMouseMotionListener(menuMoveListener);
	}
	
//	/*
//	 * Loads graphics files using file names stored in imgFiles array
//	 */
//	private void loadGraphics()
//	{
//		imgs = new Image[NUM_IMAGES];
//		imgFiles = new String[NUM_IMAGES];
//		setFileNames();
//		for (int i = 0; i < NUM_IMAGES; i++)
//		{
//			imgs[i] = game.getImage(game.getDocumentBase(), imgFiles[i]);
//		}
//	}
//	
//	/*
//	 * Sets all graphics file names
//	 */
//	private void setFileNames()
//	{
//		imgFiles[0] = "title.png";
//		imgFiles[1] = "logo.png";
//		//TODO: Add new graphics here
//	}
	
	/*
	 * Setups up menus boxes in correct positions
	 */
	private void setupBoxes()
	{
		int yoffset = 90;
		this.boxs = new Box[NUM_BOXES];
		this.subBoxs = new Box[NUM_BOXES];
		this.subMenuBoxs = new Box[NUM_SUB_BOXES];
		this.textFields = new TextField[NUM_TEXT_FIELDS];
		boxStartPos = new Vector2D((winSize.width-Box.size)/2,   (winSize.height-Box.size)/2+yoffset);
		
		this.centralBox = new Box(boxStartPos, "").setColor(Color.DARK_GRAY, Color.BLACK).setImage(game.imgs[1]);
		
		//TODO: Change dimensions
		subBoxStartPos = new Vector2D(winSize.width/2-Box.size*3,   (winSize.height-Box.size)/2);
		
		for (int i = 0; i < NUM_BOXES; i++)
		{
			boxs[i] = new Box().setPos(boxStartPos).setSizeBoth(Box.size, Box.size).setText(txt[currMenu.ordinal()][i]);		
			subBoxs[i] = new Box().setPos(subBoxStartPos).setSizeBoth(Box.subSizeW, Box.subSizeH);
		}
		
		for (int i = 0; i < NUM_SUB_BOXES; i++)
		{
			subMenuBoxs[i] = new Box().setColor(Color.BLACK, Color.WHITE).setSizeBoth(Box.iconSize, Box.iconSize);
		}
		
		for (int i = 0; i < NUM_TEXT_FIELDS; i++)
		{
			textFields[i] = new TextField();
			textFields[i].setVisible(false);
			game.add(textFields[i]);
		}
		

		//Set positions to end in after animation
		
		boxs[0].setFinalPos(new Vector2D((winSize.width-Box.size)/2,   (winSize.height-Box.size*3)/2+yoffset));
		boxs[1].setFinalPos(new Vector2D((winSize.width-Box.size*3)/2, (winSize.height-Box.size)/2+yoffset));
		boxs[2].setFinalPos(new Vector2D((winSize.width+Box.size)/2,   (winSize.height-Box.size)/2+yoffset));
		boxs[3].setFinalPos(new Vector2D((winSize.width-Box.size)/2,   (winSize.height+Box.size)/2+yoffset));
		
		
		
		//SubMenuBoxes[0] is always back button
		Vector2D p = centralBoxSubmenuPos = new Vector2D(boxs[0].getFinalPos().x-Box.size/2, boxs[0].getFinalPos().y+Box.size/2);
		subMenuBoxs[0] = new Box(new Vector2D(p.x+2*Box.size/2-50, p.y+ 2*Box.size-30), "BACK")
			.setSizeBoth(100, 30).setColor(Color.BLACK, Color.WHITE);
		
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
				boxs[n].setPos(boxStartPos).setText(txt[currMenu.ordinal()][n]);//.setSize(boxStartSize[1]);	
			}
		}
		else
		{
			//TODO: put in special cases for things like 'quit' and options(sound, language etc.) here
			switch (currMenu)
			{
				case NONE:
				case MAIN:
					//When click on find opponent
					currSubMenu = currMenu;
					submenuSelected(0);
				break;
				case NEW_GAME:
					switch(i)
					{
						case 1:
							game.gameType = Game.SINGLE_PLY;
							game.startNewGame(); break;
						case 2:
							game.gameType = Game.LOCAL_MULTI;
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
		animateSubBoxesAndSetText(idx);
	}
	
	/*
	 * Called when submenu option is selected. Moves to submenu specific screen
	 */
	private void submenuSelected(int i)
	{
		//TODO:THIS METHOD GETS CALLED WHEN SUBMENU OPTION IS SELECTED
		// Use currMenu, currSubMenu and i (index of button clicked) to perform correct action

		//TODO: DO PROPERLY
		centralBox.setFinalPos(centralBoxSubmenuPos);
		centralBox.setFinalSize(2*Box.size, 2*Box.size);
		centralBox.setImageVisible(false);
		Vector2D p = centralBoxSubmenuPos;
		
		//Set all suboxes invisible (except for back button)
		for (int j = 1; j < NUM_SUB_BOXES; j++)
		{
			subMenuBoxs[j].reset();
		}
		//TODO: update positions on subMenuBoxs here
		switch(currMenu)
		{
			case MAIN:
				//Find opponent
				if (Client.loggedIn)
				{
					if (Client.matchWithOpponent())
					{
						int s = 30;
						int y = (int) (centralBoxSubmenuPos.y+20);
						subMenuBoxs[1].setVisible(true).setPosBoth(new Vector2D ((int)centralBoxSubmenuPos.x, y)).setSizeBoth(Box.size, 30).onlyShowText(true).setText(Client.getName());
						subMenuBoxs[2].setVisible(true).setPosBoth(new Vector2D ((int)centralBoxSubmenuPos.x, y+s)).setSizeBoth(Box.size, 30).onlyShowText(true).setText(String.valueOf(Client.getRanking()));
						subMenuBoxs[3].setVisible(true).setPosBoth(new Vector2D ((int)centralBoxSubmenuPos.x+Box.size/2, y+s/2)).setSizeBoth(Box.size, 30).onlyShowText(true).setText("VS");
						subMenuBoxs[4].setVisible(true).setPosBoth(new Vector2D ((int)centralBoxSubmenuPos.x+Box.size, y)).setSizeBoth(Box.size, 30).onlyShowText(true).setText(Client.getOpponentName());
						subMenuBoxs[5].setVisible(true).setPosBoth(new Vector2D ((int)centralBoxSubmenuPos.x+Box.size, y+s)).setSizeBoth(Box.size, 30).onlyShowText(true).setText(String.valueOf(Client.getOpponentRanking()));
						
						subMenuBoxs[6].setVisible(true).setPosBoth(new Vector2D ((int)centralBoxSubmenuPos.x, y+4*s)).setSizeBoth(Box.size*2, 30).setText("PLAY");
					}
					else
					{
						subMenuBoxs[1].setVisible(true).setPosBoth(new Vector2D ((int)centralBoxSubmenuPos.x+20, (int)centralBoxSubmenuPos.y+20)).setSizeBoth(Box.size*2-40, 30).onlyShowText(true).setText("Waiting for opponent...");
					}
				}
				else
				{
					subMenuBoxs[1].setVisible(true).setPosBoth(new Vector2D ((int)centralBoxSubmenuPos.x+20, (int)centralBoxSubmenuPos.y+20)).setSizeBoth(Box.size*2-40, 30).onlyShowText(true).setText("You must login before you can find an opponent");
				}
				
			break;
			case OPTIONS:
				switch (currSubMenu)
				{
					case GENERAL:
						switch (i)
						{
							//Sound
							case 0:
								subMenuBoxs[1].setVisible(true).setPosBoth(new Vector2D (p.x+20, p.y+50)).setSizeBoth(Box.iconSize,  Box.iconSize).setToCheckbox(game.imgs[4], game.imgs[5]);
								subMenuBoxs[2].setVisible(true).setPosBoth(new Vector2D (p.x+2*Box.size-20-Box.iconSize, p.y+50)).setSizeBoth(Box.iconSize,  Box.iconSize).setToCheckbox(game.imgs[4], game.imgs[5]);
								subMenuBoxs[3].setVisible(true).setPosBoth(new Vector2D (p.x+20, p.y+80)).setSizeBoth(Box.iconSize,  Box.iconSize).onlyShowText(true).setText("ON");
								subMenuBoxs[4].setVisible(true).setPosBoth(new Vector2D (p.x+2*Box.size-20-Box.iconSize, p.y+80)).setSizeBoth(Box.iconSize,  Box.iconSize).onlyShowText(true).setText("OFF");
								int n = (game.sound) ? 1 : 2 ;
									subMenuBoxs[n].invertChecked();
							break;
							//Language
							case 1:
								//Sets positions of sub menu boxes
								for (int j = 0; j < NUM_LANGUAGES; j++)
								{
									subMenuBoxs[j+1].setVisible(true).setText(langs[j]).setSizeBoth(Box.iconSize, Box.iconSize).setPosBoth(new Vector2D(p.x+Box.iconSize+2*((j)%3)*Box.iconSize, p.y+Box.iconSize+2*(j/3)*Box.iconSize));
								}
							//Animation
							case 2:
								subMenuBoxs[1].setVisible(true).setPosBoth(new Vector2D (p.x+20, p.y+50)).setSizeBoth(Box.iconSize,  Box.iconSize).setToCheckbox(game.imgs[4], game.imgs[5]);
								subMenuBoxs[2].setVisible(true).setPosBoth(new Vector2D (p.x+2*Box.size-20-Box.iconSize, p.y+50)).setSizeBoth(Box.iconSize,  Box.iconSize).setToCheckbox(game.imgs[4], game.imgs[5]);
								subMenuBoxs[3].setVisible(true).setPosBoth(new Vector2D (p.x+20, p.y+80)).setSizeBoth(Box.iconSize,  Box.iconSize).onlyShowText(true).setText("ON");
								subMenuBoxs[4].setVisible(true).setPosBoth(new Vector2D (p.x+2*Box.size-20-Box.iconSize, p.y+80)).setSizeBoth(Box.iconSize,  Box.iconSize).onlyShowText(true).setText("OFF");
								int m = (game.animation) ? 1 : 2 ;
									subMenuBoxs[m].invertChecked();
								
							break;
						}
					
					break;
					case CONNECTIVITY:
					switch (i)
					{
						//Sign in
						case 0:
							//TODO: Setup login screen w/ tickbox for new users
							
							textFields[0].setText("username");
							textFields[0].setBackground(Color.BLUE);
							textFields[0].setForeground(Color.WHITE);
							textFields[0].setBounds((int)centralBoxSubmenuPos.x+20, (int)centralBoxSubmenuPos.y+20, Box.size*2-40, 30);
							
							textFields[1].setText("password");
							textFields[1].setBackground(Color.BLUE);
							textFields[1].setForeground(Color.WHITE);
							textFields[1].setBounds((int)centralBoxSubmenuPos.x+20, (int)centralBoxSubmenuPos.y+100, Box.size*2-40, 30);
							
							subMenuBoxs[1].setVisible(true).setPosBoth(new Vector2D (p.x+20, p.y+150)).setSizeBoth(Box.iconSize,  Box.iconSize).setToCheckbox(game.imgs[4], game.imgs[5]); //setSizeBoth(Box.size*2-40, 30).setText("Login as new user");
							subMenuBoxs[2].setVisible(true).setPosBoth(new Vector2D (p.x+20, p.y+210)).setSizeBoth(Box.size*2-40, 30).setText("LOGIN");
							subMenuBoxs[3].setVisible(true).setPosBoth(new Vector2D (p.x+50, p.y+155)).setSizeBoth(Box.size*2-40, 30).onlyShowText(true).setText("Check to login as new player");
						break;
						//Player stats
						case 1:
						if (Client.loggedIn)
						{
							int s = 30;
							int y = (int) (centralBoxSubmenuPos.y+20);
							int[] wdl = Client.getWDL();
							subMenuBoxs[1].setVisible(true).setPosBoth(new Vector2D ((int)centralBoxSubmenuPos.x+20, y)).setSizeBoth(Box.size*2-40, 30).onlyShowText(true).setText("Player name: "+Client.getName());
							subMenuBoxs[2].setVisible(true).setPosBoth(new Vector2D ((int)centralBoxSubmenuPos.x+20, y+=s)).setSizeBoth(Box.size*2-40, 30).onlyShowText(true).setText("Highscore: "+Client.getHighscore());
							subMenuBoxs[3].setVisible(true).setPosBoth(new Vector2D ((int)centralBoxSubmenuPos.x+20, y+=s)).setSizeBoth(Box.size*2-40, 30).onlyShowText(true).setText("Wins: "+wdl[0]);
							subMenuBoxs[4].setVisible(true).setPosBoth(new Vector2D ((int)centralBoxSubmenuPos.x+20, y+=s)).setSizeBoth(Box.size*2-40, 30).onlyShowText(true).setText("Draws: "+wdl[1]);
							subMenuBoxs[5].setVisible(true).setPosBoth(new Vector2D ((int)centralBoxSubmenuPos.x+20, y+=s)).setSizeBoth(Box.size*2-40, 30).onlyShowText(true).setText("Losses: "+wdl[2]);
							subMenuBoxs[6].setVisible(true).setPosBoth(new Vector2D ((int)centralBoxSubmenuPos.x+20, y+=s)).setSizeBoth(Box.size*2-40, 30).onlyShowText(true).setText("Last login: "+Client.getLastLogin());
							subMenuBoxs[7].setVisible(true).setPosBoth(new Vector2D ((int)centralBoxSubmenuPos.x+20, y+=s)).setSizeBoth(Box.size*2-40, 30).onlyShowText(true).setText("Ranking: "+Client.getRanking());
						}
						else
						{
							subMenuBoxs[1].setVisible(true).setPosBoth(new Vector2D ((int)centralBoxSubmenuPos.x+20, (int)centralBoxSubmenuPos.y+20)).setSizeBoth(Box.size*2-40, 30).onlyShowText(true).setText("You must login before you can see player stats");
						}
						break;
						//Friends
						case 2:
						break;
						//Highscores/rankings
						case 3:
							LinkedHashMap<String, Integer> hsTable = Client.getHighScoreTable();
							Set<String> names = hsTable.keySet();
							int n = 0;
							int s = 20;
							int y = (int) (centralBoxSubmenuPos.y-10);
							for (String name : names)
							{
								subMenuBoxs[++n].setVisible(true).setPosBoth(new Vector2D ((int)centralBoxSubmenuPos.x+20, y+=s)).setSizeBoth(Box.size*2-40, 30).onlyShowText(true).setText(n+". "+name+" - "+hsTable.get(name));
							}
							
							
							
						break;
					}
					
					break;
				}
			break;
			 
		}
		
		drawSubmenuScreen = i;
	}
	
	private void handleSubMenuClick(int i)
	{
		if (i == 0)
		{
			//back button
			centralBox.setFinalPos(new Vector2D(boxs[0].getPos().x, boxs[0].getPos().y+Box.size));
			centralBox.setFinalSize(Box.size, Box.size);
			centralBox.setImageVisible(true);
			//No longer draw submenu
			drawSubmenuScreen = -1;
			
			setTextBoxesVisible(false);
			
			//Special text cases i.e. depending on logged in/out
			//TODO: set up arrays for altTxt in different languages
			if (currSubMenu==Menu.CONNECTIVITY && Client.loggedIn)
			{
				subBoxs[0].setText("Sign out");
			}
			
			//If going back from find opponent screen then remove player from waiting table 
			if (currSubMenu==Menu.MAIN)
			{
				Client.removeFromWaitingTable();
			}
		}
		else
		{
			switch(currMenu)
			{
				case MAIN:
					//Find opponent 'play' button pressed
					Client.createNewGame();
					game.gameType = Game.ONLINE_MULTI;
					game.rPlayerTurn = true;
					game.startNewGame();
				break;
				case OPTIONS:
					switch (currSubMenu)
					{
						case GENERAL:
							switch (drawSubmenuScreen)
							{
								//Sound
								case 0:
									switch (i)
									{
										case 1:
											subMenuBoxs[1].invertChecked();
											subMenuBoxs[2].invertChecked();
											game.sound = true;
											SoundManager.unmute();
										break;
										case 2:
											subMenuBoxs[1].invertChecked();
											subMenuBoxs[2].invertChecked();
											game.sound = false;
											SoundManager.mute();
										break;										
									}
								break;
								//Language
								case 1:
									switch (i)
									{
										case 1:
											txt = english;
										break;
										case 2:
											txt = french;
										break;
									}
									
									changeLanguage();
								break;
								//Animation
								case 2:
									switch (i)
									{
										case 1:
											subMenuBoxs[1].invertChecked();
											subMenuBoxs[2].invertChecked();
											game.animation = true;
										break;
										case 2:
											subMenuBoxs[1].invertChecked();
											subMenuBoxs[2].invertChecked();
											game.animation = false;
									}
								break;
							}
						
						break;
						case CONNECTIVITY:
							switch (drawSubmenuScreen)
							{
								//Login
								case 0:
								//TODO: encrpyt pass in db
									switch (i)
									{
										//Checkbox
										case 1:
											subMenuBoxs[1].invertChecked();
										break;
										case 2:
											if (subMenuBoxs[1].isChecked())
											{
												if (Client.newUser(textFields[0].getText(), textFields[1].getText()))
													handleSubMenuClick(0);
											}
											else
											{
												if (Client.tryLogin(textFields[0].getText(), textFields[1].getText()))
													handleSubMenuClick(0);
											}
										break;
										
									}
								break;
							}
						break;
					}
				break;
				 
			}
		}
	}
	
	private void changeLanguage()
	{
		for (int i = 0; i < NUM_BOXES; i++)
		{
			boxs[i].setText(txt[currMenu.ordinal()][i]);
			subBoxs[i].setText(txt[currSubMenu.ordinal()][i]);
		}
//		for (int i = 0; i < NUM_SUB_BOXES; i++)
//		{
//			subMenuBoxs[i].setText(t);
//		}
		
	}
	
	/*
	 * Animates subboxes depending on which main box was pressed
	 */
	private void animateSubBoxesAndSetText(int i)
	{
		//Set final position depending on main box pressed
		Vector2D p = null;
		switch (i)
		{
		case 0:
		case 3:
			p = boxs[i].getFinalPos();
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
			p = boxs[i].getFinalPos();
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
			subBoxs[n].setPos(subBoxStartPos).setText(txt[currSubMenu.ordinal()][n]);//.setSize(subBoxStartSize);	
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
		drawBoxes(g);
		
		if (drawSubmenuScreen > -1)
		{
			drawSubmenuScreen(g);
		}
//		else
//		{
//			setTextBoxesVisible(false);
//		}
		
	}
	
	private void drawBoxes(Graphics g)
	{
		//Draw subboxes (behind main boxes)
		for (int n = 0; n < NUM_BOXES; n++)
		{
			subBoxs[n].draw(g).setVisible(subMenuVisible);
		}
		//Draw main boxes
		for (int n = 0; n < NUM_BOXES; n++)
		{
			//If box has no text make it invisible
			boxs[n].draw(g).setVisible(!boxs[n].hasNoText());
		}

		
		centralBox.draw(g);
	}
	
	private void drawSubmenuScreen(Graphics g)
	{
		//Draw submenu screen once central box has finish animating
		if (centralBox.animate())
		{
			for (int i = 0; i < NUM_SUB_BOXES; i++)
			{
				subMenuBoxs[i].draw(g);
			}
			
			if (currSubMenu==Menu.CONNECTIVITY && drawSubmenuScreen==0)
			{
				setTextBoxesVisible(true);
			}
		}
//			switch (currMenu)
//			{
//				case OPTIONS:
//					switch (currSubMenu)
//					{
//						case GENERAL:
//							switch (drawSubmenuScreen)
//							{
//								//Sound
//								case 0:
//									//drawSound menu();
//									
//								break;
//								//Language
//								case 1:
//									//drawSound menu();
//									drawSubmenuLanguage(g);
//									
//								break;
//								//Animation
//								case 2:
//									//drawSound menu();
//									
//								break;
//								//Theme
//								case 3:
//									//drawSound menu();
//									
//								break;
//							}
//							break;
//						case CONNECTIVITY:
//							switch (drawSubmenuScreen)
//							{
//								//Login
//								case 0:
//									 drawSubmenuLogin(g);
//								break;
//							}
//						
//						break;
//					}	
//				break;
//			}
				
//			subMenuBoxs[0].draw(g);
//		}
	}
	
	private void setTextBoxesVisible(boolean vis)
	{
		for (int i = 0; i < NUM_TEXT_FIELDS; i++)
		{
			textFields[i].setVisible(vis);
		}
	}
	
//	private void drawSubmenuSound(Graphics g)
//	{
//		//TODO: using subMenuBoxs[1+]
//	}
//	
//	private void drawSubmenuLanguage(Graphics g)
//	{
//		//TODO: using subMenuBoxs[1+] make into 1 for loop
//		for (int i = 1; i < 7; i++)
//		{
//			subMenuBoxs[i].draw(g);
//		}
//	}
//	
//	private void drawSubmenuLogin(Graphics g)
//	{
//		//TODO: using subMenuBoxs[1+] make into 1 for loop
//		for (int i = 1; i < 7; i++)
//		{
//			subMenuBoxs[i].draw(g);
//		}
//	}
	
	/*
	 * Draws menu header (logo and particles)
	 */
	private void drawMenuHeader(Graphics g)
	{
		g.setFont(Fonts.largefont);
		FontMetrics fm = g.getFontMetrics();
		g.setColor(Color.YELLOW);
		//Draw logo
		Image img = game.imgs[0];
		g.drawImage(img, (winSize.width-img.getWidth(null))/2, 30, null);
		g.setFont(Fonts.smallfont);
		fm = g.getFontMetrics();
		g.drawString("by Ben Homer", 10, winSize.height-20);
		
		if (Client.loggedIn)
		{
			g.drawString("Logged in as: "+Client.getName(), 10, 30);
		}
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
				if (drawSubmenuScreen == -1)
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
								submenuSelected(i);
								break;
							}
						}
					}
				}
				else
				{
					//In submenu screen
					for (int i = 0; i < NUM_SUB_BOXES; i++)
					{
						if (subMenuBoxs[i].getRect().contains(e.getPoint()))
						{
							//TODO: method which manages what to do on click (store in array?)
							handleSubMenuClick(i);
							//testing
							if (i==0)
							{
								
							}
//							if (i==1)
//								txt = english;
//							if (i==2)
//								txt = french;
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
	 * Highlights menu components when hovered over
	 */
	MouseMotionListener menuMoveListener = new MouseMotionListener() {
		@Override
		public void mouseMoved(MouseEvent e) {
			if (game.state == Game.MENU)
			{
				if (drawSubmenuScreen == -1)
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
				else
				{
					//In submenu screen
					for (int i = 0; i < NUM_SUB_BOXES; i++)
					{
						if (subMenuBoxs[i].getRect().contains(e.getPoint()))
						{
							subMenuBoxs[i].setColor(Color.RED, Color.BLACK);
						}
						else
						{
							subMenuBoxs[i].setColor(Color.BLACK, Color.WHITE);
						}
					}
				}
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {}
	};
	
}
