package api.main;

import api.game.*;
import sjson.JSONException;
import sjson.JSONObject;
import sjson.JSONString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

@Deprecated
public class HanabiClient
{
	private static HanabiClient instance = null;
	private Socket socket;
	private String name;
	private PrintStream out;
	private BufferedReader in;
	private State currentState;

	private HanabiClient(String host, int port, String playerName) throws IOException
	{
		socket = new Socket(host,port);
		out = new PrintStream(socket.getOutputStream());
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out.println(playerName);
		out.flush();
		name = in.readLine();
		try
		{
			new Game(in);
		}
		catch(JSONException e)
		{
			throw new IOException(e);
		}
	}

	public static HanabiClient connect(String host, int port, String playerName) throws IOException
	{
		if (instance!=null)
			throw new IllegalStateException("Client is already connected");
		instance = new HanabiClient(host,port,playerName);
		return instance;
	}

	public static HanabiClient getInstance()
	{
		if (instance == null)
			throw new IllegalStateException("Client is not connected");
		return instance;
	}

	public void close() throws IOException
	{
		socket.close();
		Game.getInstance().close();
		instance = null;
	}

	public State getCurrentState()
	{
		return currentState;
	}

	public State waitForNewState() throws JSONException
	{
		currentState = new State(in);
		return currentState;
	}

	public State sendAction(Action a) throws JSONException
	{
		if (currentState.getCurrentPlayer().equals(name))
		{
			out.print(a.toString(0));
			out.flush();
			return waitForNewState();
		}
		else
			throw new IllegalStateException("Not your turn!");
	}
}
