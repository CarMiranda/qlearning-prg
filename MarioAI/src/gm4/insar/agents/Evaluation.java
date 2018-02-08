package gm4.insar.agents;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.benchmark.tasks.LearningTask;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.MarioAIOptions;

/**
 * Classe d'évaluation de l'agent.
 * @author Carlos Miranda <carlos.miranda_lopez@insa-rouen.fr>
 */
public class Evaluation {

	/**
	 * Cette énumération représente le mode d'exécution du programme.
	 */
	public static enum Mode {
		DEBUG,
		DEMO,
		EVAL;

		static Mode getMode(String mode) {
			for (Mode m : Mode.values()) {
				if (m.name().equalsIgnoreCase(mode)) {
					return m;
				}
			}
			return Mode.DEMO;
		}
	}

	/**
	 * Cette classe représente les données d'évaluation (scores).
	 */
	public static class DonneesEvaluation {
		
		/**
		 * Moyenne arithmétique des scores sur plusieurs épisodes d'évaluation
		 */
		public float scoreMoyen = 0;
		
		/**
		 * Nombre de victoires
		 */
		public float victoires = 0;
		
		/**
		 * Moyenne arithmétique du nombre d'ennemis éliminés sur plusieurs épisodes d'évaluation
		 */
		public float eliminationMoyenne = 0;
		
		/**
		 * Moyenne arithmétique des distances maximales sur plusieurs épisodes d'évaluation
		 */
		public float distanceMoyenne = 0;
		
		/**
		 * Moyenne arithmétique des temps moyens avant la mort de l'agent.
		 */
		public float tempsMoyen = 0;

		/**
		 * Calcule les scores moyens pour chacun des critères d'évaluation.
		 */
		public void calculerInfoEvalFinale() {
			scoreMoyen /= ParametresApprentissage.NB_ITERATIONS_EVAL;
			victoires /= ParametresApprentissage.NB_ITERATIONS_EVAL;
			eliminationMoyenne /= ParametresApprentissage.NB_ITERATIONS_EVAL;
			distanceMoyenne /= ParametresApprentissage.NB_ITERATIONS_EVAL;
			tempsMoyen /= ParametresApprentissage.NB_ITERATIONS_EVAL;
		}

		/**
		 * Ajoute les scores de l'épisode d'évaluation.
		 * @param infoEvaluation Information du dernier épisode d'évaluation
		 */
		public void accumulateEvalInfo(EvaluationInfo infoEval) {
			scoreMoyen += infoEval.computeWeightedFitness();
			victoires += infoEval.marioStatus == Mario.STATUS_WIN ? 1 : 0;
			eliminationMoyenne += 1.0 * infoEval.killsTotal / infoEval.totalNumberOfCreatures;
			distanceMoyenne += 1.0 * infoEval.distancePassedCells / infoEval.levelLength;
			tempsMoyen += infoEval.timeSpent;
		}
		
		@Override
		public String toString() {
			return String.format("%f %f %f %f %f", scoreMoyen, victoires, eliminationMoyenne, distanceMoyenne, tempsMoyen);
		}
	}

	/**
	 * Le mode d'exécution
	 */
	private Mode mode;
	
	/**
	 * Les options de l'environnement MarioAI
	 */
	private MarioAIOptions marioAIOptions;
	
	/**
	 * L'agent Q-learning
	 */
	private AgentQL agent;

	/**
	 * Une liste de données d'évaluation pour écriture en fin d'exécution.
	 */
	private List<DonneesEvaluation> evaluationResults = new ArrayList<DonneesEvaluation>();

	/**
	 * Constructeur
	 * @param mode Le mode d'exécution
	 */
	public Evaluation(Mode mode) {
		this.mode = mode;
		agent = new AgentQL();
		
		marioAIOptions = new MarioAIOptions();
		marioAIOptions.setAgent(agent);
		marioAIOptions.setVisualization(false);
		marioAIOptions.setFPS(24); // valeur recommendée
		agent.setOptions(marioAIOptions);
		agent.setLearningTask(new LearningTask(marioAIOptions));
	}

	/**
	 * Prépare et lance l'entraînement, puis l'évaluation de l'agent.
	 * @return Le score final
	 */
	public float evaluate() {
		
		marioAIOptions.setVisualization(false);
		
		// Mode de débuggage
		if (mode == Mode.DEBUG) {
			marioAIOptions.setVisualization(true);
			ParametresApprentissage.DEBUG = 2;
		}

		// Entraînement de l'agent
		agent.learn();

		// Mode de démonstration
		if (mode == Mode.DEMO) {
			marioAIOptions.setVisualization(true);
		}

		BasicTask basicTask = new BasicTask(marioAIOptions);

		Logger.println(0, "*************************************************");
		Logger.println(0, "*                                               *");
		Logger.println(0, "*            Début de l'évaluation              *");
		Logger.println(0, "*                                               *");
		Logger.println(0, "*************************************************");

		System.out.println("Tâche = " + basicTask);
		System.out.println("Agent = " + agent);

		DonneesEvaluation resultats = new DonneesEvaluation();
		evaluationResults.add(resultats);

		for (int i = 0; i < ParametresApprentissage.NB_ITERATIONS_EVAL; i++) {

			Date date = new Date();
			marioAIOptions.setLevelRandSeed(Math.round(date.getTime() % 1000));

			// L'évaluation peut échouer. On essaie de faire l'évaluation 3 fois
			// avant de quitter.
			int compteurEchoue = 0;
			while (!basicTask.runSingleEpisode(1)) {
				System.err.println("MarioAI: Défaut de temps de calcul !");
				compteurEchoue++;
				if (compteurEchoue >= 3) {
					System.err.println("Quitter...");
					System.exit(0);
				}
			}

			EvaluationInfo infoEvaluation = basicTask.getEvaluationInfo();
			resultats.accumulateEvalInfo(infoEvaluation);

			System.out.println(infoEvaluation.toString());
		}

		resultats.calculerInfoEvalFinale();
		return resultats.scoreMoyen;
	}

	/**
	 * Ecriture des résultats d'évaluation dans un fichier de texte pour exploitation.
	 */
	public void ecrireResultats() {
		Utils.ecrire("eval.txt", Utils.join(evaluationResults, "\n"));
	}

	/**
	 * Utilitaire pour analyser les paramètres de la ligne de commande.
	 * @param args Les paramètres passés dans la ligne de commandes lors de l'exécution
	 * @param nom Nom du paramètre
	 * @return La valeur du paramètre sous forme d'une chaîne de caractères
	 */
	public static String getParam(String[] args, String nom) {
		for (int i = 0; i < args.length; i++) {
			String s = args[i];
			if (s.startsWith("-") && s.substring(1).equals(nom)) {
				if (i + 1 < args.length) {
					String v = args[i + 1];
					if (!v.startsWith("-")) {
						return v;
					}
				}
				return "";
			}
		}
		return null;
	}

	/**
	 * 
	 * @param v La chaîne à tester
	 * @return Si la chaîne est vide ou pas initialisée.
	 */
	public static boolean isNullOrEmpty(String v) {
		return v == null || v.isEmpty();
	}

	/**
	 * Comme getParam mais pour un paramètre entier. On peut associer une valeur par défaut.
	 * @param args Les paramètres passés dans la ligne de commandes lors de l'exécution
	 * @param nom Nom du paramètre
	 * @param defaut Valeur par défaut (si paramètre vide)
	 * @return La valeur du paramètre sous forme d'un entier
	 */
	public static int getIntParam(String[] args, String nom, int defaut) {
		String v = getParam(args, nom);
		return isNullOrEmpty(v) ? defaut : Integer.valueOf(v);
	}

	/**
	 * Comme getParam mais pour un paramètre booléen.
	 * @param args Les paramètres passés dans la ligne de commandes lors de l'exécution
	 * @param nom Nom du paramètre
	 * @return S'il existe dans les paramètres passés ou pas
	 */
	public static boolean getBooleanParam(String[] args, String nom) {
		String v = getParam(args, nom);
		return v != null;
	}

	public static void main(String[] args) {
		
		// Initialisation des paramètres
		Mode mode = Mode.getMode(getParam(args, "m"));
		int numRes = getIntParam(args, "n", 1);
		ParametresApprentissage.NB_MODES_ENTRAINEMENT =
				getIntParam(args, "nm", ParametresApprentissage.NB_MODES_ENTRAINEMENT);
		ParametresApprentissage.NB_GRAINES_ENTRAINER =
				getIntParam(args, "ng", ParametresApprentissage.NB_GRAINES_ENTRAINER);
		ParametresApprentissage.NB_ITERATIONS_ENTRAINEMENT =
				getIntParam(args, "i", ParametresApprentissage.NB_ITERATIONS_ENTRAINEMENT);
		ParametresApprentissage.NB_ITERATIONS_EVAL =
				getIntParam(args, "ei", ParametresApprentissage.NB_ITERATIONS_EVAL);
		ParametresApprentissage.CHARGER_QTABLE = getBooleanParam(args, "l");

		// Evaluation
		Evaluation eval = new Evaluation(mode);

		for (int i = 0; i < numRes; i++) {
			System.out.println("~ Itération " + i + " ~");
			float scoreFinal = eval.evaluate();
			System.out.println("Score final = " + scoreFinal + "\n");
		}
		eval.ecrireResultats();
	}
}
