package game;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Log {

	private static BufferedWriter log;
	private static boolean SYS_OUT = true;
	
	private static void openLog() throws IOException {
		log = new BufferedWriter(new FileWriter(Game.BASE_PATH+"log.txt"));
	}
	
	public static void close() {
		try {
			log.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public static void print(String s) {
		if (SYS_OUT) {
			System.out.println(s);
		}
		try {
			if (log==null) {
				openLog();
			}
			log.write(s+"\n");
		} catch (IOException e) {e.printStackTrace();}
	}
}
