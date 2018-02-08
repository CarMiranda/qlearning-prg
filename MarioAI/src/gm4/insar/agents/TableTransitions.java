package gm4.insar.agents;

import java.util.Hashtable;

/**
 * Cette classe représente un table comptabilisant le nombre de fois qu'une action a été
 * choisie depuis chaque état.
 * @author Carlos Miranda <carlos.miranda_lopez@insa-rouen.fr>
 */
public class TableTransitions {

	/**
	 * Cette classe est une enveloppe pour les données associées à une action depuis un état donné.
	 */
	public static class DonneesAction {
		
		/**
		 * Nombre de fois l'action a été choisie depuis l'état.
		 */
		private int compteurActions = 0;
	    
		private Hashtable<Long, Integer> transitions = new Hashtable<Long, Integer>();
	    
	    public int getCompteurActions() { return compteurActions; }
	    
	    public void setCompteurActions(int count) { compteurActions = count; }
	    
	    public void ajouterTransition(long versEtat) {
	    	compteurActions += 1;

	    	if (!transitions.containsKey(versEtat)) {
	    		transitions.put(versEtat, 0);
	    	}
	    	transitions.put(versEtat, transitions.get(versEtat) + 1);
	    }
	}
	
	/**
	 * Table comptabilisant les couples (état, action choisie).
	 */
	Hashtable<Long, DonneesAction[]> compteurEtats = new Hashtable<Long, DonneesAction[]>();
	
	/**
	 * Le nombre d'actions disponibles.
	 */
	private final int nbActions;
	  
	public TableTransitions(int nbActions) {
		this.nbActions = nbActions;
	}

	/**
	 * 
	 * @param etat Identifiant de l'état
	 * @return 
	 */
	private DonneesAction[] getEtat(long etat) {
		DonneesAction[] donneesAction = compteurEtats.get(etat);
		if (donneesAction == null) {
			donneesAction = new DonneesAction[nbActions];
			for (int i = 0; i < nbActions; i++) {
				donneesAction[i] = new DonneesAction();
			}
			compteurEtats.put(etat, donneesAction);
		}
		return donneesAction;
	}

	/**
	 * 
	 * @param deEtat Identifiant de l'état courant/duquel on veut les données
	 * @param action L'action de laquelle on veut les données (action prise depuis deEtat)
	 * @return
	 */
	private DonneesAction getDonneesAction(long deEtat, int action) {
		return getEtat(deEtat)[action];
	}
	
	/**
	 * 
	 * @param deEtat Identifiant de l'état courant/duquel on veut modifier les données
	 * @param action L'action engendrant la transition
	 * @param versEtat Identifiant de l'état d'arrivée
	 */
	public void ajouterTransition(long deEtat, int action, long versEtat) {
		getDonneesAction(deEtat, action).ajouterTransition(versEtat);
	}

	/**
	 * 
	 * @param deEtat Identifiant de l'état courant/duquel on veut le compteur
	 * @param action L'action de laquelle on veut les données (action prise depuis deEtat)
	 * @return Le nombre de fois que l'action a été prise depuis l'état deEtat
	 */
	public int getCompteur(long deEtat, int action) {
		return getDonneesAction(deEtat, action).getCompteurActions();
	}

	/**
	 * 
	 * @param etat Identifiant d'un état
	 * @return Les compteurs pour chaque action depuis l'état identifié par etat
	 */
	public int[] getCompteurs(long etat) {
		int[] compteurs = new int[nbActions];
		for (int i = 0; i < nbActions; i++) {
			compteurs[i] = getDonneesAction(etat, i).getCompteurActions();
		}
		return compteurs;
	}

	/**
	 * 
	 * @param deEtat Identifiant de l'état courant/duquel on veut modifier un compteur
	 * @param action L'action (depuis deEtat) pour laquelle on veut modifier le compteur
	 * @param compteur La valeur qu'on veut affecter au compteur
	 */
	public void setCompteur(long deEtat, int action, int compteur) {
		getDonneesAction(deEtat, action).setCompteurActions(compteur);
	}
}
