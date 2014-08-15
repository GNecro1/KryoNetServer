package net.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class ServerGame extends JPanel implements Runnable {

	public static Logger logger = Logger.getLogger("Server");
	public Properties prop;
	public Network n;
	public OPHandler handler;
	public InputListener inputl;
	public Map<Integer, String> pnames = new HashMap<Integer, String>();
	public JPanel jpPlayers;
	public JTextArea pl;

	public ServerGame() {
		setPreferredSize(new Dimension(800, 600));
		setLayout(new BorderLayout());
		add(getLog(), "Center");
		prop = new Properties();
		jpPlayers = getPlayers();
		add(jpPlayers, "East");
		n = new Network(prop, this);
		n.start();
		Thread t = new Thread(this);
		t.start();
	}

	private JPanel getPlayers() {
		JPanel jp = new JPanel(new BorderLayout());
		JTextArea text = new JTextArea();
		JScrollPane sp = new JScrollPane(text);
		jp.add(sp, "Center");
		text.setEditable(false);
		for (String s : pnames.values()) {
			text.append(s + "                  " + "\n");
		}
		jp.setBorder(new TitledBorder(new EtchedBorder(), "Online players"));
		pl = text;
		return jp;
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
		JScrollPane inputS = new JScrollPane(input);
		input.setBorder(new EtchedBorder());
		inputS.setBorder(new TitledBorder(new EtchedBorder(), "Server input"));
		jp.add(sp, "Center");
		jp.add(inputS, "South");
		jp.setBorder(new TitledBorder(new EtchedBorder(), "Server log"));
		return jp;
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf.graphite.GraphiteLookAndFeel");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JFrame j = new JFrame("Server");
		ServerGame g = new ServerGame();
		j.add(g);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		j.setResizable(false);
		j.setVisible(true);
		j.pack();
	}

	public void run() {
		while (true) {
			n.setKeepAlive(1200000);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
