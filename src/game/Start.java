package game;

import java.awt.Frame;

/*
 * Performance Management Engine application entry point.
 */
public class Start {
	
	private final static int WIDTH_OFFSET = 10;
	private final static int HEIGHT_OFFSET = 80;
	
	public static void main (String[] args) {
		Frame myFrame = new AppFrame();
		myFrame.setBounds(10, 10, Game.WIN_WIDTH+WIDTH_OFFSET, Game.WIN_HEIGHT+HEIGHT_OFFSET);
		myFrame.setVisible(true);
	}
}


