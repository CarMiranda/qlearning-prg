package gm4.insar.agents;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Cette table représente la Q-table et possède aussi une table de 
 * comptabilisation des transitions de l'agent au travers les états.
 * @author Carlos Miranda <carlos.miranda_lopez@insa-rouen.fr>
 */
public class QTableActions extends QTable {

	/**
	 * La table de transitions qui sert à comptabiliser le nombre de fois qu'un état
	 * a été visité (pour l'actualisation de la vitesse d'apprentissage alpha).
	 */
	TableTransitions transitions;

	/**
	 * Constructeur
	 * @param nbActions Nombre maximal d'actions disponibles.
	 */
	public QTableActions(int nbActions) {
		super(nbActions);
		transitions = new TableTransitions(nbActions);
	}

	/**
	 * Choisit l'action optimale d'après la Q-table.
	 */
	@Override
	public int getActionOptimale(long nbEtat) {
		float[] qVals = getQValeurs(nbEtat);
		if (qVals == null) {
			System.err.println("Pas de Q-valeurs définies pour cet état.");
			return 0;
		} else {
			float qValMax = Float.NEGATIVE_INFINITY;
			int indiceQValMax = 0;

			for (int i = 0; i < qVals.length; i++) {
				if (qValMax < qVals[i]) {
					qValMax = qVals[i];
					indiceQValMax = i;
				}
			}

			Logger.println(4, "Q-valeurs: " + Utils.join(qVals, ", "));
			Logger.println(4, "Action optimale: " + indiceQValMax);

			return indiceQValMax;
		}
	}

	/**
	 * Ajout de la transition dans la table de transitions et actualisation de la Q-valeur
	 * associée au dernier changement d'état.
	 */
	@Override
	public void actualiserQValeur(float recompense, long nbEtatCourant) {
		transitions.ajouterTransition(etatPrecedent, actionPrecedente, nbEtatCourant);

		float[] qPrecs = getQValeurs(etatPrecedent);
		float qPrec = qPrecs[actionPrecedente];

		int actionOptimale = getActionOptimale(nbEtatCourant);
		float maxQ = getQValeurs(nbEtatCourant)[actionOptimale];

		// On utilise la formule :
		//		alpha = alpha_0 / (Nb de fois couple etatPrecedent/actionPrecedente a été choisi)
		float alpha = super.alpha_0 / transitions.getCompteur(etatPrecedent, actionPrecedente);

		// On utilise la formule:
		// Q(prevState, prevAction) =
		//     (1 - alpha) * Qprev + alpha * (reward + gamma * maxQ)
		float nouveauQ = (1 - alpha) * qPrec +  alpha * (recompense + gamma * maxQ);

		qPrecs[actionPrecedente] = nouveauQ;
	}

	/**
	 * L'ensemble des Q-valeurs seront initialisées pseudo-aléatoirement 
	 * avec une distribution uniforme [-0.1, 0.1].
	 * 
	 */
	@Override
	public float[] getQValeursInitiales(long nbEtat) {
		float[] qValeursInitiales = new float[nbActions];
		for (int i = 0; i < nbActions; i++) {
			qValeursInitiales[i] = (float) (generateur.nextFloat() * 0.2 - 0.1);
		}
		return qValeursInitiales;
	}

	/**
	 * Ecrit la Q-table dans le fichier spécifié.
	 * @param logfile Le nom du fichier.
	 */
	public void ecrireQTable(String logfile) {
		Logger.println(1, "** Ecriture de la Q-table dans " + logfile + " **");

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(logfile));
			bw.write("");
			for (long key : getTable().keySet()){
				bw.append(printState(key) + "\n");
			}
			bw.close();
		} catch (IOException x) {
			System.err.println("Erreur lors de l'écriture de la Q-table dans : " + logfile);
		}
	}

	/**
	 * Renvoie une représentation de l'état, les Q-valeurs et transitions associées.
	 * @param id L'état à représenter.
	 * @return La représentation de l'état sous forme de chaîne de caractères.
	 */
	public String printState(long id) {
		return String.format("%d:%s:%s", id, Utils.join(getTable().get(id), " "), Utils.join(transitions.getCompteurs(id), " "));
	}

	/**
	 * Lit un état, ses Q-valeurs et transition associées. Opération inverse à printState.
	 * @param line La ligne représentant l'état.
	 */
	private void parseState(String represEtat) {
		String[] tokens = represEtat.split(":");
		long etat = Long.valueOf(tokens[0]);
		String[] qvaleursStrings = tokens[1].split(" ");
		String[] compteurStrings = tokens[2].split(" ");
		float[] qvaleurs = getQValeurs(etat);
		for (int i = 0; i < nbActions; i++) {
			qvaleurs[i] = Float.valueOf(qvaleursStrings[i]);
			transitions.setCompteur(etat, i, Integer.valueOf(compteurStrings[i]));
		}
	}

	/**
	 * Charge une Q-table et créé une instance correspondate.
	 * @param logfile Le nom du fichier contenant la Q-table.
	 */
	public void chargerQTable(String logfile) {
		Logger.println(1, "** Chargement de la Q-table depuis " + logfile + " **");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(logfile));
			String line;
			while ((line = reader.readLine()) != null) {
				parseState(line);
			}
			reader.close();
		} catch (Exception e) {
			System.err.println("Erreur lors du chargement de la Q-table depuis : " + logfile);
		}
	}

}
