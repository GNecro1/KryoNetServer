package net.main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextArea;

public class InputListener implements KeyListener {

	private JTextArea input;
	private ServerGame sg;

	public InputListener(JTextArea input, ServerGame sg) {
		this.input = input;
		this.sg = sg;
	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_ENTER) {
			String s = input.getText();
			input.setText("");
			input.setRows(0);
			input.cut();
			String[] c = s.split(" ");
			if (c[0].equals("*stop")) {
				sg.n.server.stop();
			} else if (c[0].equals("*start")) {
				sg.n.server.start();
			} else if (c[0].equals("*kick")) {
				if (c[1] != null) {
					for (Player p : sg.n.players.values()) {
						if (p.name.equalsIgnoreCase(c[1])) {
							p.c.close();
						}
					}
				}
			}
		}
	}

	public void keyReleased(KeyEvent e) {

	}

	public void keyTyped(KeyEvent e) {

	}

}
