package net.main;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.JTextArea;

public class OPHandler extends Handler {

	private JTextArea text;

	public OPHandler(JTextArea text) {
		this.text = text;
	}
	
	public void init(JTextArea t){
		text = t;
	}

	@Override
	public void close() throws SecurityException {

	}

	@Override
	public void flush() {

	}

	@Override
	public void publish(LogRecord e) {
		text.append(format(e));
		text.setCaretPosition(text.getDocument().getLength());
	}

	private String format(LogRecord e) {
		StringBuilder sb = new StringBuilder();
		Level lvl = e.getLevel();
		if (lvl == Level.INFO) {
			sb.append("[INFO] ");
		} else if (lvl == Level.SEVERE) {
			sb.append("[SEVERE] ");
		} else {
			sb.append("[OTHER] ");
		}
		sb.append(e.getMessage());
		sb.append('\n');
		Throwable t = e.getThrown();
		if(t!= null){
			StringWriter sw = new StringWriter();
			t.printStackTrace(new PrintWriter(sw));
			sb.append(sw.toString());
		}
		return sb.toString();
	}

}
