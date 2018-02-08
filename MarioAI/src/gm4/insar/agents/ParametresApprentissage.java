package gm4.insar.agents;

/**
 * Cette classe sert à définir les paramètres d'apprentissage par défaut.
 * @author Carlos Miranda <carlos.miranda_lopez@insa-rouen.fr>
 */
public class ParametresApprentissage {

	/**
	 * Indique le niveau de debuggage.
	 */
	public static int DEBUG = 0;

	/**
	 * Nombre d'itérations (épisodes) par entraînement
	 */
	public static int NB_ITERATIONS_ENTRAINEMENT = 20;

	/**
	 * Nombre de modes d'entraînement
	 */
	public static int NB_MODES_ENTRAINEMENT = 3;

	/**
	 * Nombre de graines à entraîner
	 */
	public static int NB_GRAINES_ENTRAINER = 1;
	
	//  Le nombre total d'épisodes d'entraînement est donc :
	//  	NB_ITERATIONS_ENTRAINEMENT * NB_MODES_ENTRAINEMENT * NB_GRAINES_ENTRAINER * NOMBRE_ENTRAINEMENTS
	// avec NOMBRE_ENTRAINEMENTS un paramètre passé en argument lors de l'exécution (flag -n)
	// indiquant le nombre de résultats attendus

	/**
	 * Nombre d'itérations (épisodes) d'évaluation
	 */
	public static int NB_ITERATIONS_EVAL = 1;
	
	// Le nombre total d'évaluations est donc :
	// NB_ITERATIONS_EVAL * NOMBRE_ENTRAINEMENTS


	/** 
	 * Probabilité d'explorer, c'est à dire de prendre une action au hasard au 
	 * lieu de prendre celle qui maximise sa Q-value.
	 */
	public static final float PROBA_EXPLORATION = 0.3f;

	/** 
	 * Probabilité d'explorer pendant l'évaluation. La probabilité est plus faible 
	 * que d'une manière générale car on veut évaluer l'agent, on veut donc éviter qu'il
	 * prenne des actions au hasard.
	 */
	public static final float PROBA_EXPLORATION_EVAL = 0.01f;

	/**
	 * Le facteur gamma détermine quelle importance on accorde aux récompenses long terme.
	 * Si on met Gamma à 0, l'agent se contentera seulement des récompenses immédiates. 
	 * Comme pour la probabilité d'exploration, la valeur de gamma n'est pas fixe mais
	 * qu'elle diminue au cours de l'entrainement.
	 */
	public static final float GAMMA = 0.6f;

	/**
	 * La valeur de alpha, aussi appelée vitesse d'apprentissage correspond à l'importance
	 * que l'on accorde à l'information que l'on receuille. Si on fixe Alpha à 1, l'information
	 * entrante prend la place de l'ancienne. Si Alpha est fixé à 0, l'agent n'apprend rien, ses
	 * Q-value ne sont pas mis à jour. La valeur de alpha diminue aussi au cours de l'entrainement.
	 */
	public static final float ALPHA =  0.8f;

	/**
	 * Nombre de mouvement que doit effectué Mario en 1 image pour qu'il recoive une récompense de 
	 * distance.
	 */
	public static final int MIN_DISTANCE_DEPLACEMENT = 2;

	/**
	 * Nombre d'images dans lequel Mario doit être bloqué pour que son état soit changé à bloqué.
	 */
	public static final int NB_IMAGES_BLOQUE = 25;

	/**
	 * Nombre de fenêtres d'observation.
	 */
	public static final int NB_NIVEAUX_OBSERVATIONS = 3;

	/**
	 * "Rayons" des fenêtres d'observation.
	 */
	public static final int[] TAILLES_OBSERVATIONS = {1, 3, 5};

	/**
	 * Valeurs pour modifier la récompense de déplacement lorsqu'il y a des
	 * ennemis proche.
	 */
	public static final float[] MODIFICATEUR_ENNEMIS_PROCHES = {0f, 0f, 0.15f};

	/**
	 * Valeurs des récompenses.
	 */
	public static final class PARAMETRES_RECOMPENSES {
		public static final int distance = 2;
		public static final int hauteur = 8;
		public static final int collision = -800;
		public static final int mortIncinere = 60;
		public static final int mortEcrase = 60;
		public static final int bloque = -20;

		/*
	    public static final int gagne = 0;
	    public static final int mode = 0;
	    public static final int pieces = 0;
	    public static final int fleurFeu = 0;
	    public static final int morts = 0;
	    public static final int mortCarapace = 0;
	    public static final int champignon = 0;
	    public static final int tempsRestant = 0;
	    public static final int blocCache = 0;
	    public static final int champignonVert = 0;
	    public static final int ecrase = 0;*/
	};

	/**
	 * Ecrire les q-tables intermediaires dans un fichier.
	 */
	public static final boolean ECRITURE_QTABLE_INTERMEDIAIRE = false;

	/**
	 * Charger la dernière q-table pour commencer avec celle-ci au lieu de
	 * l'initialisation arbitraire. On utilise la variable NOM_DERNIERE_QTABLE
	 * pour l'identification du fichier
	 */
	public static boolean CHARGER_QTABLE = false;

	/**
	 * Format du nom du fichier d'écriture des q-tables intermediaires.
	 */
	public static String FORMAT_NOM_QTABLE = "qt.%d.txt";

	/**
	 * Format du nom du fichier d'écriture de la dernière q-table.
	 */
	public static String NOM_DERNIERE_QTABLE = "qt.final.txt";

	/**
	 * Nom du fichier des scores
	 */
	public static String SCORES_NAME = "scores.txt";
}
