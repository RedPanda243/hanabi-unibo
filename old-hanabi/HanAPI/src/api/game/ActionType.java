package api.game;

/**
 * Definisce i possibili tipi di Action
 * @see Action
 */
public enum ActionType
{
	PLAY,DISCARD,HINT_COLOR,HINT_VALUE;

	public static ActionType fromString(String s)
	{
		switch (s.toLowerCase())
		{
			case "play": return PLAY;
			case "discard": return DISCARD;
			case "hint value": return HINT_VALUE;
			case "hint color": return HINT_COLOR;
			default: return null;
		}
	}

	public String toString()
	{
		if (this == PLAY)
			return "play";
		if (this == DISCARD)
			return "discard";
		if (this == HINT_COLOR)
			return "hint color";
		if (this == HINT_VALUE)
			return "hint value";
		return null;
	}

}
