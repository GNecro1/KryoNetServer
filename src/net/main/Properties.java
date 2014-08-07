package net.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Properties {
	public File pf;

	public int maxP = 8;
	public int port = 4321;
	public String name = "Server";

	boolean def = false;

	public Properties() {
		pf = new File("prop.txt");
		if (!pf.exists()) {
			ServerGame.logger.info("Creating prop.txt!");
			createPropFile();
		} else {
			ServerGame.logger.info("Found prop.txt!");
			loadPropFile();
		}
	}

	private void loadPropFile() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(pf));

			for (String s = ""; (s = br.readLine()) != null;) {
				String[] p = s.split(":");
				if (p[0].equals("name")) {
					name = p[1];
					ServerGame.logger.info("Name set to " + name);
				} else if (p[0].equals("port")) {
					ServerGame.logger.info("Listening to port " + port);
					port = Integer.parseInt(p[1]);
				} else if (p[0].equals("max_players")) {
					ServerGame.logger.info("Maximum players " + maxP);
					maxP = Integer.parseInt(p[1]);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			ServerGame.logger.severe("Something went wrong with loading prop.txt file!");
		}

	}

	private void createPropFile() {
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(pf));
			pw.println("name:" + name);
			ServerGame.logger.info("Name set to " + name);
			pw.println("port:" + port);
			ServerGame.logger.info("Listening to port " + port);
			pw.println("max_players:" + maxP);
			ServerGame.logger.info("Maximum players " + maxP);
			pw.close();
			def = true;
		} catch (Exception e) {
			ServerGame.logger.severe("Something went wrong with creating prop.txt file!");
			e.printStackTrace();
		}
	}
}
