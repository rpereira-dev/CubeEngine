package com.grillecube.server.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.LinkedList;

import com.grillecube.server.Game;

public class Server
{
	/** game isntance */
	private Game	_game;
	
	/** a thread that execute queued packets */
	private ThreadPacketQueue			_packet_queue;
	private LinkedList<DatagramPacket>	_queue;

	
	/** UDP protocol */
	private DatagramSocket	_socket;
	private int				_port;
	
	public Server(Game game, int port)
	{
		this._game = game;
		this._packet_queue = new ThreadPacketQueue(this);
		this._port = port;
		this._socket = null;
	}

	/** initialize server protocole */
	public void	start() throws SocketException
	{
		this._socket = new DatagramSocket(this._port);
		this._packet_queue.start();
	}

	/** called to stop the server */
	public void	stop()
	{
		this._socket.close();
		try
		{
			this._packet_queue.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
			this._packet_queue.interrupt();
		}
	}
	
	/** return the next packet to be executed */
	public DatagramPacket	getNextPacket()
	{
		return (this._queue.removeFirst());
	}

	/** queue the given packet */
	public void	queuePacket(DatagramPacket packet)
	{
		this._queue.add(packet);
	}

	/** return true if the server is running */
	public boolean isRunning()
	{
		return (this._game.hasState(Game.GameState.RUNNING));
	}

	/** return server datagram socket */
	public DatagramSocket	getSocket()
	{
		return (this._socket);
	}
	
	/** return the port that this server is running on */
	public int	getPort()
	{
		return (this._port);
	}
	
}
