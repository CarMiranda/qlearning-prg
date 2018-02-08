package gm4.insar.agents;

import java.util.ArrayList;
import java.util.List;

import ch.idsia.agents.Agent;
import ch.idsia.agents.LearningAgent;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.tasks.LearningTask;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.MarioAIOptions;

/**
 * Classe représentant l'agent implémenté.
 * @author Carlos Miranda <carlos.miranda_lopez@insa-rouen.fr>
 */
public class AgentQL implements LearningAgent {
	
	private String nom;
	
	// Options d'entraînement liées à MarioAI
	private MarioAIOptions options;
	private LearningTask tacheApprentissage;
	
	/**
	 * L'état courant de l'agent.
	 */
	private EtatMario etatCourant;
	
	/**
	 * La Q-table associée à l'agent.
	 */
	private QTableActions tableActions;
	
	/**
	 * Cette énumération représente la phase d'apprentissage de l'agent
	 * INIT : initialisation (on ne fait rien)
	 * APPR : apprentissage (actualisation de la table de Q-valeurs)
	 * EVAL : évaluation (pas d'apprentissage, exploration réduite)
	 */
	private enum Phase {
		INIT, APPR, EVAL
	}
	
	/**
	 * La phase courante de l'agent
	 */
	private Phase phaseCourante = Phase.INIT;
	
	/**
	 *  Nombre de l'essai dans la phase d'apprentissage
	 */
	private int essaiAppr = 0;
	
	/**
	 *  Liste des scores pour chaque itération de l'apprentissage
	 */
	private List<Integer> scores = new ArrayList<>(ParametresApprentissage.NB_ITERATIONS_ENTRAINEMENT);

	/**
	 * Constructeur
	 */
	public AgentQL() {
		// setName("Luigi");
		setName("Agent Q-Learning GM4");
		
		etatCourant = new EtatMario();
		tableActions = new QTableActions(ActionMario.NB_ACTIONS);
		
		if (ParametresApprentissage.CHARGER_QTABLE) {
			tableActions.chargerQTable(ParametresApprentissage.NOM_DERNIERE_QTABLE);
		}
		
		Logger.println(0, "*************************************************");
	    Logger.println(0, "*                                               *");
	    Logger.println(0, "*              Agent Q-Learning GM4             *");
	    Logger.println(0, "*                     créé!                     *");
	    Logger.println(0, "*                                               *");
	    Logger.println(0, "*************************************************");
	}
	
	/**
	 * Choix d'une action à partir de la Q-table.
	 * @return Un tableau de booléens codant l'action choisie (cf. ActionMario).
	 */
	@Override
	public boolean[] getAction() {
		int idAction = tableActions.getActionSuivante(etatCourant.getNbEtat());
		Logger.println(2, "Action suivante : " + idAction + "\n");
		return ActionMario.getAction(idAction);
	}

	/**
	 * Intégration de l'observation, i.e. actualisation de l'état de l'agent 
	 * et de la Q-table.
	 * @param environnement L'environnement du jeu (cf. ch.idsia.benchmark.mario.environments.Environment)
	 */
	@Override
	public void integrateObservation(Environment environnement) {
		// Actualiser l'état courant
		etatCourant.actualiser(environnement);
		
		if (phaseCourante == Phase.INIT && environnement.isMarioOnGround()) {
			// Commencer la phase d'apprentissage dès que Mario touche le sol
			Logger.println(1, "============ Début phase d'apprentissage");
			phaseCourante = Phase.APPR;
		} else if (phaseCourante == Phase.APPR) {
			// Actualisation de la Q-table 
			tableActions.actualiserQValeur(etatCourant.calculerRecompense(), etatCourant.getNbEtat());
		}
	}

	/**
	 * Renvoie le nom de cet agent.
	 * @return Le nom de l'agent.
	 */
	@Override
	public String getName() {
		return nom;
	}

	/**
	 * Affecte le nom de l'agent.
	 * @param nom Le nom de l'agent.
	 */
	@Override
	public void setName(String nom) {
		this.nom = nom;
	}

	/**
	 * Exécute la phase d'apprentissage, puis configure les options d'évaluation.
	 */
	@Override
	public void learn() {
		// On entraine l'agent sur plusieurs modes (petit, puis grand, puis feu)
		for (int m = 0; m < ParametresApprentissage.NB_MODES_ENTRAINEMENT; m++) {
			options.setMarioMode(m);
			// On peut entrainer l'agent avec plusieurs graines (liée au générateur de nombres pseudo-aléatoires)
			for (int j = 0; j < ParametresApprentissage.NB_GRAINES_ENTRAINER; j++) {
				if (j > 0) {
					options.setLevelRandSeed(Utils.getGraine(j - 1));
				}
				for (int i = 0; i < ParametresApprentissage.NB_ITERATIONS_ENTRAINEMENT; i++) {
					apprendreUneFois();
				}
			}
		}
		configPourEval();
	}
	
	/**
	 * Exécute un seul épisode d'apprentissage
	 */
	private void apprendreUneFois() {
		Logger.println(1, "================================================");
	    Logger.println(0, "Essai : %d", essaiAppr);

	    init();
	    tacheApprentissage.runSingleEpisode(1);

	    EvaluationInfo infoEvaluation = tacheApprentissage.getEnvironment().getEvaluationInfo();
	    
	    int score = infoEvaluation.computeWeightedFitness();
	    
	    Logger.println(1, "Score intermédiaire = " + score);
	    Logger.println(1, infoEvaluation.toStringSingleLine());
	    
	    scores.add(score);

	    // Ecriture des états les plus visités lors de cet épisode
	    if (ParametresApprentissage.ECRITURE_QTABLE_INTERMEDIAIRE) {
	      tableActions.ecrireQTable(String.format(ParametresApprentissage.FORMAT_NOM_QTABLE, essaiAppr));
	    }
	    
	    essaiAppr++;
	}
	
	/**
	 * Prépare l'agent pour l'évaluation.
	 */
	public void configPourEval() {
	    Logger.println(1, "============ Ecriture Resultats ==========");
	    // Ecriture de la dernière Q-table de la phase d'apprentissage
	    tableActions.ecrireQTable(ParametresApprentissage.NOM_DERNIERE_QTABLE);
	    // Ecriture des scores dans la phase d'apprentissage
	    ecrireScores(ParametresApprentissage.SCORES_NAME);

	    Logger.println(1, "================ Eval Phase ==============");
	    phaseCourante = Phase.EVAL;

	    // On réduit la probabilité d'exploration pour l'évaluation
	    tableActions.probaExploration = ParametresApprentissage.PROBA_EXPLORATION;
	}
	
	/**
	 * Ecrit les scores dans un fichier.
	 * @param nomFichier Le nom du fichier cible.
	 */
	private void ecrireScores(String nomFichier) {
		Utils.ecrire(nomFichier, Utils.join(scores, "\n"));
	}

	/**
	 * Setter.
	 * @param options Options de l'environnement MarioAI.
	 */
	public void setOptions(MarioAIOptions options) {
		this.options = options;
	}

	/**
	 * Setter.
	 * @param tacheApprentissage La tâche à effectuer (cf. ch.idsia.benchmark.tasks.LearningTask)
	 */
	@Override
	public void setLearningTask(LearningTask tacheApprentissage) {
		this.tacheApprentissage = tacheApprentissage;
	}

	/**
	 * Initialisation des paramètres.
	 */
	@Override
	public void init() {
		 Logger.println(1, "============== Initialisation ============");
		 phaseCourante = Phase.INIT;
		 tableActions.probaExploration = ParametresApprentissage.PROBA_EXPLORATION;
	}
	
	/**
	 * Réinitialisation de l'agent.
	 */
	@Override
	public void reset() {
		Logger.println(1, "================== Reset =================");
	    etatCourant = new EtatMario();
	}
	
	/**
	 * Fonctions qu'on n'a pas utilisées.
	 */
	@Deprecated
	public void giveReward(float reward) {}

	@Deprecated
	public void newEpisode() {}
	
	@Deprecated
	public void setEvaluationQuota(long num) {}

	@Deprecated
	public Agent getBestAgent() { return null; }
	
	@Deprecated
	public void giveIntermediateReward(float intermediateReward) {}
	
	@Deprecated
	public void setObservationDetails(int rfWidth, int rfHeight, int egoRow, int egoCol) {}
}
