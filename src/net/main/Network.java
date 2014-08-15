package net.main;

import java.util.HashMap;
import java.util.Map;

import net.packets.DiedCountPacket;
import net.packets.MessagePacket;
import net.packets.PlayerAddPacket;
import net.packets.PlayerHealthPacket;
import net.packets.PlayerRemovePacket;
import net.packets.PlayerXUpdatePacket;
import net.packets.PlayerYUpdatePacket;
import net.packets.ProjectilePacket;
import net.packets.ProjectileRemovePacket;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class Network extends Listener {

	private int port, maxP;
	public Server server;
	public Map<Integer, Player> players = new HashMap<Integer, Player>();
	public Map<Integer, Projectile> proj = new HashMap<Integer, Projectile>();
	private ServerGame sg;
	private Properties p;

	public Network(Properties p, ServerGame sg) {
		this.p = p;
		this.sg = sg;
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
		kryo.register(MessagePacket.class);
		kryo.register(ProjectilePacket.class);
		kryo.register(ProjectileRemovePacket.class);
		kryo.register(PlayerHealthPacket.class);
		kryo.register(DiedCountPacket.class);
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
		sg.pl.append("Server : " + p.name + "                  " + "\n");
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
			c.setTimeout(120000);
		}
	}

	public void disconnected(Connection c) {
		ServerGame.logger.info("We were disconected from ID:" + c.getID() + " IP:" + c.getRemoteAddressTCP());
		PlayerRemovePacket p = new PlayerRemovePacket();
		p.id = players.get(c.getID()).id;
		server.sendToAllExceptTCP(c.getID(), p);
		MessagePacket p3 = new MessagePacket();
		p3.id = -1;
		p3.message = players.get(c.getID()).name + " closed connection!";
		server.sendToAllExceptTCP(c.getID(), p3);
		players.remove(c.getID());
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
			p2.health = 100;
			players.put(c.getID(), p2);
			server.sendToAllExceptTCP(c.getID(), p);
			ServerGame.logger.info("A player joined the game: " + p2.name);
			MessagePacket p3 = new MessagePacket();
			p3.id = -1;
			p3.message = "We have an a connection from : " + p2.name;
			server.sendToAllExceptTCP(c.getID(), p3);
			c.sendTCP(p3);
			sg.pl.append(p.name + "                  " + "\n");
		} else if (o instanceof PlayerXUpdatePacket) {
			PlayerXUpdatePacket p = (PlayerXUpdatePacket) o;
			players.get(c.getID()).x = p.x;
			server.sendToAllExceptTCP(c.getID(), p);
		} else if (o instanceof PlayerYUpdatePacket) {
			PlayerYUpdatePacket p = (PlayerYUpdatePacket) o;
			players.get(c.getID()).y = p.y;
			server.sendToAllExceptTCP(c.getID(), p);
		} else if (o instanceof PlayerRemovePacket) {
			PlayerRemovePacket p = (PlayerRemovePacket) o;
			players.remove(players.get(players.get(c.getID())));
			server.sendToAllExceptTCP(c.getID(), p);
		} else if (o instanceof ProjectilePacket) {
			ProjectilePacket pack = (ProjectilePacket) o;
			Projectile p = new Projectile();
			p.id = pack.id;
			p.c = c;
			p.x = pack.x;
			p.y = pack.y;
			p.xVel = pack.xVel;
			p.yVel = pack.yVel;
			proj.put(p.id, p);
			server.sendToAllExceptTCP(c.getID(), pack);

		} else if (o instanceof ProjectileRemovePacket) {
			ProjectileRemovePacket p = (ProjectileRemovePacket) o;
			proj.remove(p.id);
			server.sendToAllExceptTCP(c.getID(), p);
		}else if(o instanceof PlayerHealthPacket){
			PlayerHealthPacket p = (PlayerHealthPacket) o;
			players.get(c.getID()).health = p.health;
			players.get(c.getID()).died = p.died;
			server.sendToAllExceptTCP(c.getID(), p);
		}else if(o instanceof DiedCountPacket){
			DiedCountPacket d = (DiedCountPacket) o;
			players.get(c.getID()).died = d.died;
			server.sendToAllExceptTCP(c.getID(), d);
		}
		c.setKeepAliveTCP(120000);
		sg.pl.setRows(0);
		sg.pl.setText("");
		sg.pl.cut();
		for (Player p : players.values()) {
			sg.pl.append(p.name + "                  " + "\n");
		}
	}
}
