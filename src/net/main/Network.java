package net.main;

import java.util.HashMap;
import java.util.Map;

import net.packets.PlayerAddPacket;
import net.packets.PlayerRemovePacket;
import net.packets.PlayerXUpdatePacket;
import net.packets.PlayerYUpdatePacket;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class Network extends Listener {

	private int port, maxP;
	public Server server;
	public Map<Integer, Player> players = new HashMap<Integer, Player>();

	public Network(Properties p) {
		port = p.port;
		maxP = p.maxP;
	}

	public void start() {
		server = new Server();
		server.addListener(this);
		ServerGame.logger.info("Server created!");
		Kryo kryo = server.getKryo();
		kryo.register(PlayerAddPacket.class);
		kryo.register(PlayerRemovePacket.class);
		kryo.register(PlayerXUpdatePacket.class);
		kryo.register(PlayerYUpdatePacket.class);
		ServerGame.logger.info("Registered classes!");
		ServerGame.logger.info("Starting server....");
		try {
			server.bind(port);
			server.start();
			ServerGame.logger.info("Done! Server is operational!");
		} catch (Exception e) {
			ServerGame.logger.severe("Something went wrong with server!");
			e.printStackTrace();
		}
	}

	public void connected(Connection c) {
		// if (server.getConnections().length < maxP) {
		// c.setKeepAliveTCP(0);
		// ServerGame.logger.info("We receved connection ID:" + c.getID() +
		// " IP:" + c.getRemoteAddressTCP());
		// ServerGame.logger.info("Disconected, full server!");
		// } else {
		ServerGame.logger.info("We recs connection ID:" + c.getID() + " IP:" + c.getRemoteAddressTCP());
		for (Player p : players.values()) {
			PlayerAddPacket pack = new PlayerAddPacket();
			pack.x = p.x;
			pack.y = p.y;
			pack.name = p.name;
			pack.id = p.id;
			c.sendTCP(pack);
		}
		c.setKeepAliveTCP(120000);
		// }
	}

	public void setKeepAlive(int time) {
		for (Connection c : server.getConnections()) {
			c.setKeepAliveTCP(time * 100);
		}
	}

	public void disconnected(Connection c) {
		ServerGame.logger.info("We were disconected from ID:" + c.getID() + " IP:" + c.getRemoteAddressTCP());
		PlayerRemovePacket p = new PlayerRemovePacket();
		p.id = players.get(c.getID()).id;
		server.sendToAllExceptTCP(c.getID(), p);
	}

	public void received(Connection c, Object o) {
		if (o instanceof PlayerAddPacket) {
			PlayerAddPacket p = (PlayerAddPacket) o;
			Player p2 = new Player();
			p2.x = p.x;
			p2.y = p.y;
			p2.id = p.id;
			p2.name = p.name;
			p2.c = c;
			players.put(c.getID(), p2);
			server.sendToAllExceptTCP(c.getID(), p);
			ServerGame.logger.info("A player joined the game: " + p2.name);
		}
		if (o instanceof PlayerXUpdatePacket) {
			PlayerXUpdatePacket p = (PlayerXUpdatePacket) o;
			players.get(c.getID()).x = p.x;
			server.sendToAllExceptTCP(c.getID(), p);
			ServerGame.logger.info("Got x packet : " + p.x);
		}
		if (o instanceof PlayerYUpdatePacket) {
			PlayerYUpdatePacket p = (PlayerYUpdatePacket) o;
			players.get(c.getID()).y = p.y;
			server.sendToAllExceptTCP(c.getID(), p);
			ServerGame.logger.info("Got y packet : " + p.y);
		}
		if (o instanceof PlayerRemovePacket) {
			PlayerRemovePacket p = (PlayerRemovePacket) o;
			players.remove(players.get(players.get(c.getID())));
			server.sendToAllExceptTCP(c.getID(), p);
		}
		c.setKeepAliveTCP(120000);
	}
}
