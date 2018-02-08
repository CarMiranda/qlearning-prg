package gm4.insar.agents;

import java.util.Hashtable;
import java.util.Random;

/**
 * Cette classe abstraite représente une Q-table implémentée par une table de hashage
 * @author Carlos Miranda <carlos.miranda_lopez@insa-rouen.fr>
 */
public abstract class QTable {
	
	protected Random generateur;
	
	private Hashtable<Long, float[]> table;
	
	/**
	 * Nombre d'actions disponibles à l'agent dans un état particulier.
	 * Ceci permet de savoir le nombre de Q-valeurs à avoir pour chaque état.
	 */
	protected int nbActions;
	
	protected float probaExploration = ParametresApprentissage.PROBA_EXPLORATION;
	
	protected float gamma = ParametresApprentissage.GAMMA;
	
	/**
	 * Paramètre alpha initial.
	 */
	protected float alpha_0 = ParametresApprentissage.ALPHA;
	
	protected long etatPrecedent;
	
	protected int actionPrecedente;

	/**
	 * Constructeur
	 * @param nbActions Nombre maximal d'actions disponibles pour chaque état.
	 */
	protected QTable(int nbActions) {
		generateur = new Random();
	    table = new Hashtable<Long, float[]>();
	    this.nbActions = nbActions;
	}
	
	/**
	 * Choisit l'action suivante (stratégie de décision e-greedy).
	 * @param nbEtat L'identifiant de l'état actuel.
	 * @return L'identifiant de l'action choisie.
	 */
	public int getActionSuivante(long nbEtat) {
	    etatPrecedent = nbEtat;
	    if (generateur.nextFloat() < probaExploration) {
	      actionPrecedente = explorer();
	    } else {
	      actionPrecedente = getActionOptimale(nbEtat);
	    }
	    return actionPrecedente;
	}
	
	/**
	 * Choisit une action au hasard.
	 * @return L'identifiant de l'action choisie.
	 */
	private int explorer() {
		return generateur.nextInt(nbActions);
	}
	
	/**
	 * Retourne un tableau des Q-valeurs des actions disponibles à l'état indiqué.
	 * Si l'état n'a jamais été visité auparavant, on appelle une fonction d'initialisation
	 * de la Q-valeur.
	 * @param nbEtat L'identifiant de l'état actuel.
	 * @return Un tableau contenant les Q-valeurs correspondantes à chaque action.
	 */
	public float[] getQValeurs(long nbEtat) {
		if (!table.containsKey(nbEtat)) {
			float[] initialQvalues = getQValeursInitiales(nbEtat);
			table.put(nbEtat, initialQvalues);
			return initialQvalues;
		}
		return table.get(nbEtat);
	}
	  	  
	/**
	 * Renvoie la Q-table.
	 * @return La Q-table.
	 */
	public Hashtable<Long, float[]> getTable() {
		return table;
	}
	
	/**
	 * Met à jour la Q-valeurs associée à la transition etatPrecedente -> actionPrecedente -> nbEtatCourant.
	 * @param recompense Valeur de la récompense attribuée après avoir effectué l'action choisie.
	 * @param nbEtatCourant Le nouvel état suite à l'action.
	 */
	public abstract void actualiserQValeur(float recompense, long nbEtatCourant);
	
	/**
	 * Renvoie l'action optimale d'après la Q-table pour l'état indiqué.
	 * @param nbEtat L'identifiant de l'état.
	 * @return L'identifiant de l'action choisie.
	 */
	public abstract int getActionOptimale(long nbEtat);
	
	/**
	 * Renvoie une valeur d'initialisation des Q-valeurs associées à un état qui n'avait
	 * jamais été visité.
	 * @param nbEtat L'identifiant de l'état.
	 * @return Un tableau contenant des valeurs d'initialisation pour l'état.
	 */
	public abstract float[] getQValeursInitiales(long nbEtat);
}
