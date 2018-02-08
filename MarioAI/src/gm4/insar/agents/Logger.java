package gm4.insar.agents;

/**
 * Classe qui permet d'obtenir des messages d'erreurs sous différentes configurations.
 * @author Carlos Miranda <carlos.miranda_lopez@insa-rouen.fr>
 */
public class Logger {

	public static void println(int niveau, String message) {
		Logger.print(niveau, message + "\n");
	}

	public static void println(int niveau, Object message) {
		Logger.println(niveau, message.toString());
	}

	public static void println(int niveau, int nombre) {
		Logger.println(niveau, String.valueOf(nombre));
	}

	public static void println(int niveau, String format, Object... values) {
		Logger.println(niveau, String.format(format, values));
	}

	public static void print(int niveau, String message) {
		if (niveau <= ParametresApprentissage.DEBUG) {
			System.out.print(message);
		}
	}
}

