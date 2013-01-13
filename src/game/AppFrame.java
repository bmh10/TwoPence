package game;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/*
 * Creates custom frame and inserts main Performance Management Engine Applet
 * into this frame. (Avoids using AppletViewer application built into Eclipse).
 */
@SuppressWarnings("serial")
public class AppFrame extends Frame implements ActionListener {
	public AppFrame() {
	    super("TwoPence");
	    MenuBar mb = new MenuBar();
	    setMenuBar(mb);
	    Menu fileMenu = new Menu("File");
	    mb.add(fileMenu);
	    MenuItem exitMenuItem = new MenuItem("Exit");
	    fileMenu.add(exitMenuItem);
	    exitMenuItem.addActionListener (this);
	    // Make window close button work as normal
	    this.addWindowListener(new WindowAdapter() {
	    	public void windowClosing(WindowEvent e) {
	    		System.exit(0);
	    	}
	    });
	    
	    //Create instance of main Applet and add to frame
	    Game game = new Game();
	    add(game, BorderLayout.CENTER);
	    
	    //Initialise and start application
	    game.init();
	    game.start();
	}
	  
	  public void actionPerformed(ActionEvent evt) {
	    if (evt.getSource() instanceof MenuItem) {
	      String menuLabel = ((MenuItem)evt.getSource()).getLabel();
	      if(menuLabel.equals("Exit")) {
	        // Close application, when 'exit' is selected
	        dispose();
	        System.exit(0);
	      }
	    }
	  }
	}