package api.game;

import sjson.*;

import java.io.Reader;
import java.util.List;

/**
 * Rappresenta il primo messaggio che viene inviato dal server quando ha trovato tutti i giocatori. D&agrave; informazioni generali e
 * immutabili sulla partita. Sfrutta il template Singleton, in modo che l'oggetto di questa classe sia referenziabile velocemente.
 * Come conseguenza, nella stessa JVM pu&ograve; esistere un Game alla volta.
 */
public class Game extends TypedJSON<JSONObject>
{
	private static Game instance = null;

	/**
	 * Costruisce un oggetto Game a partire dal JSONArray contenente i nomi dei giocatori, nell'ordine in cui devono giocare
	 * @param players
	 * @throws JSONException
	 */
	public Game(List<String> players) throws JSONException
	{
		json = new JSONObject();
		if (instance != null)
			throw new JSONException("Un oggetto Game è già esistente");
		setPlayers(players);
		checkPlayers();
		instance = this;
	}

	public Game(Reader reader) throws JSONException
	{
		json = new JSONObject(reader);
		if (instance != null)
			throw new JSONException("Game instance already exists");
		checkPlayers();
		instance = this;
	}

	/**
	 * Chiude il gioco. L'esecuzione di questo metodo consente la futura istanziazione di un nuovo oggetto Game
	 */
	public static void close()
	{
		instance = null;
	}

	/**
	 * @return il numero massimo di carte che un giocatore pu&ograve; possedere
	 */
	public int getNumberOfCardsPerPlayer()
	{
		return getPlayers().length>3?4:5;
	}

	/**
	 * @param index il turno del giocatore di cui si vuole conoscere il nome
	 * @return il nome del giocatore che gioca nel turno indicato
	 */
	public String getPlayer(int index)
	{
		return getPlayers()[index];
	}

	/**
	 * @return un JSONArray contenenti i nomi di tutti i giocatori che partecipano alla partita, nell'ordine in cui tocca giocare
	 */
	public String[] getPlayers()
	{
		JSONArray a = json.getArray("players");
		String[] r = new String[a.size()];
		for(int i=0; i<a.size(); i++)
		{
			r[i] = a.getString(i);
		}
		return r;
	}

	/**
	 * @param player il nome del giocatore di cui si vuole conoscere il turno
	 * @return il turno del giocatore di nome indicato
	 */
	public int getPlayerTurn(String player)
	{
		int i = 0;
		for(String ad: getPlayers())
		{
			if (ad.equals(player))
				return i;
			i++;
		}
		return -1;
	}

	/**
	 * @param player un possibile nome di un giocatore
	 * @return true se esiste un giocatore di nome indicato, false altrimenti
	 */
	public boolean isPlaying(String player)
	{
		return (getPlayerTurn(player)!=-1);
	}

	/**
	 * Utilizzato dai costruttori e dal corrispondente metodo set, verifica l'integrit&agrave; dell'attributo "players"
	 * @throws JSONException se l'attributo "players" &egrave; mancante, se il numero di giocatori non &egrave; compreso tra 2 e 5 inclusi o se almeno un nome non &egrave; una stringa
	 */
	private void checkPlayers() throws JSONException
	{
		JSONArray array = json.getArray("players");
		if (array == null)
			throw new JSONException("L'attributo \"player\" è mancante");
		if (array.size()<2 || array.size()>5)
			throw new JSONException("Il numero di giocatori deve essere compreso tra 2 e 5 inclusi");
		for(JSONData d:array)
		{
			if (d.getJSONType()!=Type.STRING)
				throw new JSONException("Il nome di un giocatore deve essere una stringa");
		}
	}

	/**
	 * Consente di impostare l'attributo "players" di questo Game
	 * @param players List che contiene i nomi dei giocatori, nell'ordine in cui giocano
	 * @return questo Game modificato
	 * @throws JSONException in caso di errore nell'impostazione
	 */
	public Game setPlayers(List<String> players) throws JSONException
	{
		JSONArray array = new JSONArray();
		for (String p:players)
			array.add(p);
		json.set("players",array);
		return this;
	}

	/**
	 * @return l'istanza della classe Game caricata per questa JVM, pu&ograve; essere null
	 */
	public static Game getInstance()
	{
		return instance;
	}
}
