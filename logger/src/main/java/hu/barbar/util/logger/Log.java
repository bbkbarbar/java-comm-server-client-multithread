package hu.barbar.util.logger;

import java.text.SimpleDateFormat;
import java.util.Date;

//import hu.barbar.tasker.util.Env;
import hu.barbar.util.FileHandler;

public class Log {

	public static class Level {
		
		public static final int DEBUG = 4,
								INFO  = 3,
								WARN  = 2,
								ERROR = 1,
								ALL   = 9,
								WARNING = 2,
								UNDEFINED = 10;

		public static String getChar(int level) {
			switch (level) {
			case DEBUG:
				return "D";
			case INFO:
				return "I";
			case WARN:
				return "W";
			case ERROR:
				return "E";
			case ALL:
				return "A";
			default:
				return "?";
			}
		}
		
	}
	
	public static class Destination {
		
		public static final int NONE = 0,
								STD_OUT = 1,
								FILE = 2,
								ALL = 3;
		
	}
	
	private static final String DATE_TIME_FORMAT = "yyyy.MM.dd HH:mm:ss";
	
	private static SimpleDateFormat sdf = null;
	
	
	private static int levelOfOutout = Level.UNDEFINED;
	
	private static int levelOfFileLogs = Level.UNDEFINED;
	
	
	private static final String DEFAULT_FILENAME = "tasker.log";
	
	private static String logFilePath = null;
	
	private static boolean isInitialized = false;
	
	
	/**
	 * 
	 * @param outputPath e.g.: Env.getDataFolderPath() + "logs/"
	 * @param levelOfStandardOutput e.g.: Log.Level.DEBUG
	 * @param levelOfFileLogs e.g.: Log.Level.WARNING
	 */
	public static void init(String outputPath, int levelOfStandardOutput, int levelOfFileLogs){
		Log.sdf = new SimpleDateFormat(Log.DATE_TIME_FORMAT);
		Log.levelOfOutout = levelOfStandardOutput;
		Log.levelOfFileLogs = levelOfFileLogs;
		
		//Output path Env.getDataFolderPath() + "logs/"
		Log.logFilePath = outputPath + DEFAULT_FILENAME;
		
		Log.isInitialized = true;
		
		Log.i("Logger initialized:\n\tStdOut: " + Level.getChar(levelOfStandardOutput) + "\n\tFileOut: " + Level.getChar(levelOfFileLogs)
			+ "\n\tFile: " + Log.logFilePath + "\n"
		);
	}
	
	private static void showOutput(String s){
		System.out.println(s);
	}
	
	private static void writeToLogFile(String line){
		FileHandler.appendToFile(logFilePath, line);
	}
	
	
	private static void log(int level, String text, int destination){
		if( !Log.isInitialized ){
			return;
		}
		
		if( Log.levelOfOutout   >= level 
			//&& (destination == Destination.STD_OUT || destination == Destination.ALL) 
		){
			showOutput(text);
		}
		if( Log.levelOfFileLogs >= level 
			//&& (destination == Destination.FILE    || destination == Destination.ALL) 
		){
			writeToLogFile( sdf.format(new Date()) + " " + Level.getChar(level) + ": " + text);
		}
		
	}
	
	public static void d(String text){
		log(Level.DEBUG, text, Destination.ALL);
	}
	
	public static void debug(String text){
		Log.d(text);
	}
	
	public static void i(String text){
		log(Level.INFO, text, Destination.ALL);
	}
	
	public static void info(String text){
		Log.i(text);
	}
	
	public static void w(String text){
		log(Level.WARN, text, Destination.ALL);
	}
	
	public static void warn(String text){
		Log.w(text);
	}
	
	public static void e(String text){
		log(Level.ERROR, text, Destination.ALL);
	}
	
	public static void error(String text){
		Log.e(text);
	}
	
	public static void all(String text){
		Log.a(text);
	}
	
	public static void a(String text){
		showOutput(text);
		writeToLogFile( sdf.format(new Date()) + " " + Level.getChar(Level.ALL) + ": " + text);
	}

	public static void t(String text) {
		// TODO Create TRACE loglevel
		Log.d(text);
	}

	/**
	 * Write text to file independently from setted log levels.
	 * @param text
	 */
	public static void f(String text) {
		writeToLogFile( sdf.format(new Date()) + " F: " + text);
	}
	
	
	
}
