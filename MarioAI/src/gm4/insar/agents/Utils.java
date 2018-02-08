package gm4.insar.agents;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

/**
 * Classe utilitaire (debuggage, paramètres non apprentissage/modélisation, etc)
 * @author Carlos Miranda <carlos.miranda_lopez@insa-rouen.fr>
 */
public class Utils {

	/**
	 * Graines pour la génération de nombres pseudo-aléatoires.
	 */
	public static int[] graines = new int[] {
		7801, 3125, 234, 9340, 12369, 839158, 4912023, 333311, 87654, 234058
	};
	
	public static int getGraine(int i) {
		return graines[i];
	}

	public static boolean getBit(int nombre, int i) {
		return (nombre & (1 << i)) != 0;
	}

	public static String printBits(int nombre, int n) {
		String s = "";
		for (int i = 0; i < n; i++) {
			s += getBit(nombre, i) ? "1" : "0";
		}
		return s;
	}

	public static String printArray(boolean[] array) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (i > 0) {
				sb.append(" ");
			}
			sb.append(array[i] ? "1" : "0");
		}
		return sb.reverse().toString();
	}

	public static String join(List<? extends Object> elements, String separateur) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < elements.size(); i++) {
			if (i > 0) {
				sb.append(separateur);
			}
			sb.append(elements.get(i).toString());
		}
		return sb.toString();
	}

	public static String join(float[] elements, String separateur) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < elements.length; i++) {
			if (i > 0) {
				sb.append(separateur);
			}
			sb.append(String.format("%.6f", elements[i]));
		}
		return sb.toString();
	}

	public static String join(int[] elements, String separateur) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < elements.length; i++) {
			if (i > 0) {
				sb.append(separateur);
			}
			sb.append(String.format("%d", elements[i]));
		}
		return sb.toString();
	}

	public static boolean ecrire(String nomFichier, String contenu) {
		Logger.println(1, "** Ecriture dans " + nomFichier + " **");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(nomFichier));
			bw.write(contenu);
			bw.close();
			return true;
		} catch (Exception x) {
			System.err.println("Erreur lors de la sauvegarde des scores.");
		}
		return false;
	}
}
