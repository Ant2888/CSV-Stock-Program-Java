package handlers;

import static handlers.GlobalVars.*;

public class BadEventException extends Exception{
	private static final long serialVersionUID = 1L;

	public BadEventException(Global_Enums args) {
		super("BAD EVENT AT "+args);
	}
}
