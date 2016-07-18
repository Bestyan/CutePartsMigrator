package log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import util.Util;

public class Log {
	public enum Level{
		TRACE, DEBUG, INFO, WARN, ERROR, FATAL;
	}
	
	private static final Logger traceLog = LogManager.getLogger("log");
	private static final Logger errorLog = LogManager.getLogger("error");
	
	public static void log(String msg, Level level){
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
}
