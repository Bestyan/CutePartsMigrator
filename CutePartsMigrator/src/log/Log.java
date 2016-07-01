package log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.control.TextArea;
import util.Util;

public class Log {
	public enum Level{
		TRACE, DEBUG, INFO, WARN, ERROR, FATAL;
	}
	
	private static final Logger traceLog = LogManager.getLogger("log");
	private static final Logger errorLog = LogManager.getLogger("error");
	
	protected static TextArea guiAusgabe;
	public static void setGuiAusgabe(TextArea ta){
		guiAusgabe = ta;
	}
	
	public static void log(String msg, Level level){
		logToGuiAusgabe(msg, level);
		switch(level){
			case TRACE:
				traceLog.trace(msg);
				break;
			case DEBUG:
				traceLog.debug(msg);
				break;
			case INFO:
				traceLog.info(msg);
				break;
			case WARN:
				traceLog.warn(msg);
				break;
			case ERROR:
				traceLog.error(msg);
				errorLog.error(msg);
				break;
			case FATAL:
				traceLog.fatal(msg);
				errorLog.fatal(msg);
				break;
			default:
				//nothing
		}
	}
	
	public static void log(Exception e){
		Log.log(e, Level.ERROR);
	}
	
	public static void log(Exception e, Level level){
		String msg = Util.getStackTraceAsString(e);
		logToGuiAusgabe(msg, level);
		switch(level){
			case TRACE:
				traceLog.trace(msg);
				break;
			case DEBUG:
				traceLog.debug(msg);
				break;
			case INFO:
				traceLog.info(msg);
				break;
			case WARN:
				traceLog.warn(msg);
				break;
			case ERROR:
				traceLog.error(msg);
				errorLog.error(msg);
				break;
			case FATAL:
				traceLog.fatal(msg);
				errorLog.fatal(msg);
				break;
			default:
				//nothing
		}
	}
	
	public static void logToGuiAusgabe(String msg, Level level){
		if(level == Level.DEBUG || level == Level.TRACE){
			return;
		}
		writeToConsole(msg);
	}
	
	public static void writeToConsole(String msg){
		synchronized(guiAusgabe){
			guiAusgabe.appendText("\r\n" + msg);
		}
	}
	
	public static void clearConsole(){
		synchronized(guiAusgabe){
			guiAusgabe.setText("");
			guiAusgabe.appendText("");
		}
		Log.log("Log cleared", Log.Level.DEBUG);
	}

	protected static void logToGuiAusgabe(Exception msg, Level level){
		if(level == Level.DEBUG || level == Level.TRACE){
			return;
		}
		logToGuiAusgabe(Util.getStackTraceAsString(msg), level);
	}
}
