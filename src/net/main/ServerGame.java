package net.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class ServerGame extends JPanel implements Runnable {

	public static Logger logger = Logger.getLogger("Server");
	public Properties prop;
	public Network n;
	public OPHandler handler;
	public InputListener inputl;

	public ServerGame() {
		setPreferredSize(new Dimension(600, 400));
		setLayout(new BorderLayout());
		add(getLog(), "Center");
		prop = new Properties();
		n = new Network(prop);
		n.start();
		Thread t = new Thread(this);
		t.start();
	}

	private JPanel getLog() {
		JPanel jp = new JPanel(new BorderLayout());
		JTextArea text = new JTextArea();
		JScrollPane sp = new JScrollPane(text);
		text.setEditable(false);
		handler = new OPHandler(text);
		logger.addHandler(handler);
		
		JTextArea input = new JTextArea();
		inputl = new InputListener(input, this);
		input.addKeyListener(inputl);
		input.setBorder(new TitledBorder(new EtchedBorder(), "Server input"));
		jp.add(sp, "Center");
		jp.add(input, "South");
		jp.setBorder(new TitledBorder(new EtchedBorder(), "Server log"));
		return jp;
	}

	public static void main(String[] args) {
		JFrame j = new JFrame("Server");
		ServerGame g = new ServerGame();
		j.add(g);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		j.setResizable(false);
		j.setVisible(true);
		j.setLocale(null);
		j.pack();
	}

	public void run() {
		while (true) {
			n.setKeepAlive(1200000);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
