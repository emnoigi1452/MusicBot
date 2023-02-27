package me.stella.Bot;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class BotLogger extends Logger {
	
	protected BotLogger(String name) {
		super(name, null);
		try {
			ConsoleLogger consoleLog = new ConsoleLogger();
			consoleLog.setEncoding("utf-8");
			consoleLog.setLevel(Level.ALL);
			consoleLog.setFormatter(new BotLogFormat());
			addHandler(consoleLog);
		} catch(Throwable t) { t.printStackTrace(); }
	}
	
	protected class BotLogFormat extends Formatter {
		private final SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
		@Override
		public String format(LogRecord record) {
			StringBuilder logBuilder = new StringBuilder();
			String timeDay = this.time.format(new Date(Long.valueOf(record.getMillis())));
			logBuilder.append("[MusicBot - " + timeDay + "] ");
			logBuilder.append(formatMessage(record).concat("\n"));
			if(record.getThrown() != null) {
				StringWriter writer = new StringWriter();
				record.getThrown().printStackTrace(new PrintWriter(writer));
				logBuilder.append(writer);
			}
			return logBuilder.toString();
		}
	}
	
	protected class ConsoleLogger extends Handler {
		@Override
		public void publish(LogRecord record) {
			if(isLoggable(record))
				System.out.print(getFormatter().format(record));
		}
		@Override
		public void flush() {}
		
		@Override
		public void close() throws SecurityException {}
		
	}
}
