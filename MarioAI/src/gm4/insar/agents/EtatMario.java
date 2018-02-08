package gm4.insar.agents;

import java.util.ArrayList;
import java.util.List;

import ch.idsia.benchmark.mario.engine.GeneralizerLevelScene;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;
import ch.idsia.benchmark.mario.environments.Environment;

/**
 * Cette classe représente l'état de Mario.
 * @author Carlos Miranda <carlos.miranda_lopez@insa-rouen.fr>
 */
public class EtatMario {
  
	private static final int MARIO_X = 9;
	private static final int MARIO_Y = 9;

	public final List<Champ> champs = new ArrayList<Champ>();

	// 0 pour petit, 1 pour grand, 2 pour mode feu.
	private Int modeMario = new Int("m", 2);

	// 0 à 8 (cf. classe Direction pour les valeurs correspondantes aux directions)
	private Int directionMario = new Int("Dir", 8);
	private float marioX = 0;
	private float marioY = 0;

	private Int bloque = new Int("!!", 1);
	private int compteurBloque = 0;

	private Int auSol = new Int("g", 1);
	private Int peutSauter = new Int("j", 1);

	private Int collisionsAvecCreatures = new Int("C", 1);
	private int dernieresCollisionsAvecCreatures = 0;

	private BitArray[] ennemis = new BitArray[ParametresApprentissage.NB_NIVEAUX_OBSERVATIONS];

	// Pour garder trace des ennemis dans la scène observée
	private int[] compteurEnnemis = new int[ParametresApprentissage.NB_NIVEAUX_OBSERVATIONS];

	private int compteurTotalEnnemis = 0;
	private int dernierCompteurTotalEnnemis = 0;

	private Int ennemisEcrases = new Int("ks", 1);
	private Int ennemisIncineres = new Int("kf", 1);
	private int mortsIncineres = 0;
	private int mortsEcrases = 0;

	/**
	 * [4 bits] S'il y a des obstacles devant Mario.
	 */
	//   | 3
	//   | 2 
	// M | 1
	//   | 0
	private BitArray obstacles = new BitArray("o", 3);

	/**
	 * Identifiant de l'état.
	 */
	private long nbEtat = 0;
	private Environment environnement;
	private byte[][] scene;

	/**
	 * Différence en distance (horizontale)
	 */
	private int dDistance = 0;
	
	/**
	 * Différence en hauteur
	 */
	private int dHauteur = 0;
	
	private int derniereDistance = 0;
	private int derniereHauteur = 0;

	public EtatMario() {
		for (int i = 0; i < ParametresApprentissage.NB_NIVEAUX_OBSERVATIONS; i++) {
			// Direction des ennemis: 0~8.
			ennemis[i] = new BitArray("e" + i, 8);
		}
	}

	/**
	 * Actualise l'état à partir de l'environnement donné
	 * @param environnement L'environnement de jeu (cf. ch.idsia.benchmarm.mario.environments.Environment)
	 */
	public void actualiser(Environment environnement) {

		this.environnement = environnement;
		this.scene = environnement.getMergedObservationZZ(1, 1);

		// Actualisation de la distance et de la hauteur.
		int distance = environnement.getEvaluationInfo().distancePassedPhys;
		dDistance = distance - derniereDistance;
		if (Math.abs(dDistance) <= ParametresApprentissage.MIN_DISTANCE_DEPLACEMENT) {
			dDistance = 0;
		}
		derniereDistance = distance;

		int hauteur = Math.max(0, getDistanceAuSol(MARIO_X - 1) - getDistanceAuSol(MARIO_X));
		dHauteur = Math.max(0, hauteur - derniereHauteur);
		derniereHauteur = hauteur;

		// Actualisation des attributs de l'état
		modeMario.valeur = environnement.getMarioMode();

		float[] pos = environnement.getMarioFloatPos();
		directionMario.valeur = getDirection(pos[0] - marioX, pos[1] - marioY);
		marioX = pos[0];
		marioY = pos[1];

		if (dDistance == 0) {
			compteurBloque += 1;
		} else {
			compteurBloque = 0;
			bloque.valeur = 0;
		}
		if (compteurBloque >= ParametresApprentissage.NB_IMAGES_BLOQUE) {
			bloque.valeur = 1;
		}

		collisionsAvecCreatures.valeur = environnement.getEvaluationInfo().collisionsWithCreatures - dernieresCollisionsAvecCreatures;
		dernieresCollisionsAvecCreatures = environnement.getEvaluationInfo().collisionsWithCreatures;

		peutSauter.valeur = (!environnement.isMarioOnGround() || environnement.isMarioAbleToJump()) ? 1 : 0;
		auSol.valeur = environnement.isMarioOnGround() ? 1 : 0;

		// Remplit l'information des ennemis.
		int tailleMax = ParametresApprentissage.TAILLES_OBSERVATIONS[ennemis.length - 1];
		int debutX = MARIO_X - tailleMax;
		int finX = MARIO_X + tailleMax;
		int debutY = MARIO_Y - tailleMax - getHauteurMario() + 1;
		int finY = MARIO_Y + tailleMax;

		compteurTotalEnnemis = 0;
		for (int i = 0; i < compteurEnnemis.length; i++) {
			compteurEnnemis[i] = 0;
		}

		for (int i = 0; i < ennemis.length; i++) {
			ennemis[i].reinitialiser();
		}
		
		for (int y = debutY; y <= finY; y++) {
			for (int x = debutX; x <= finX; x++) {
				if (scene[y][x] == Sprite.KIND_GOOMBA || scene[y][x] == Sprite.KIND_SPIKY) {
					int i = getNiveauObservation(x, y);
					int d = getDirection(x - MARIO_X, y - MARIO_Y);
					if (i < 0 || d == Direction.AUCUNE) {
						continue;
					}
					ennemis[i].valeur[d] = true;
					compteurEnnemis[i]++;
					compteurTotalEnnemis++;
				}
			}
		}

		ennemisEcrases.valeur = environnement.getKillsByStomp() - mortsEcrases;

		// On compte uniquement les ennemis incinérés dans le rang d'observation.
		if (compteurTotalEnnemis < dernierCompteurTotalEnnemis) {
			ennemisIncineres.valeur = environnement.getKillsByFire() - mortsIncineres;
		} else {
			ennemisIncineres.valeur = 0;
		}

		dernierCompteurTotalEnnemis = compteurTotalEnnemis;
		mortsIncineres = environnement.getKillsByFire();
		mortsEcrases = environnement.getKillsByStomp();

		// Remplit l'information des obstacles.
		obstacles.reinitialiser();
		for (int y = 0; y < obstacles.valeur.length; y++) {
			if (estObstacle(MARIO_X + 1, MARIO_Y - y + 1)) {
				obstacles.valeur[y] = true;
			}
		}

		calculerNbEtat();

		Logger.println(2, this);
	}

	/**
	 * Calcul de la récompense.
	 * @return La valeur de la récompense suite à une action.
	 */
	public float calculerRecompense() {
		/**
		 *  Cette variable sert à modifier la valeur de la récompense attribué pour
		 *  déplacement (vertical ou horizontal) lorsque des ennemis se rapprochent.
		 */
		float modificateurRecompense = 1f;
		for (int i = 0; i < compteurEnnemis.length; i++) {
			if (compteurEnnemis[i] > 0) {
				modificateurRecompense = ParametresApprentissage.MODIFICATEUR_ENNEMIS_PROCHES[i];
				break;
			}
		}

		float recompense = 
				// Pénalité pour aider l'agent lorsqu'il est bloqué
				bloque.valeur * ParametresApprentissage.PARAMETRES_RECOMPENSES.bloque +
				// Récompense pour déplacement (vertical ou horizontal)
				modificateurRecompense * dDistance * ParametresApprentissage.PARAMETRES_RECOMPENSES.distance +
				modificateurRecompense * dHauteur * ParametresApprentissage.PARAMETRES_RECOMPENSES.hauteur +
				// Récompense pour éviter ou tuer des ennemis
				collisionsAvecCreatures.valeur * ParametresApprentissage.PARAMETRES_RECOMPENSES.collision +
				ennemisIncineres.valeur * ParametresApprentissage.PARAMETRES_RECOMPENSES.mortIncinere +
				ennemisEcrases.valeur * ParametresApprentissage.PARAMETRES_RECOMPENSES.mortEcrase;

		Logger.println(2, "D: " + dDistance);
		Logger.println(2, "H:" + dHauteur);
		Logger.println(2, "Reward = " + recompense);

		return recompense;
	}

	/**
	 * Renvoie si Mario peut sauter.
	 * @return Si Mario peut sauter.
	 */
	public boolean peutSauter() {
		return environnement.isMarioAbleToJump();
	}

	/**
	 * Renvoie le nombre identifiant cet état.
	 */
	public long getNbEtat() {
		return nbEtat;
	}

	/**
	 * Calcul du nombre de cet état par "concaténation" de bits dans une variable Long.
	 */
	private void calculerNbEtat() {
		nbEtat = 0;
		int i = 0;
		for (Champ champ : champs) {
			nbEtat += champ.getInt() << i;
			i += champ.getNBits();
		}
		if (i >= Long.SIZE) {
			System.err.println("Nombre d'état trop grand!! = " + i + "bits!!");
			System.exit(1);
		}
	}

	@Override
	public String toString() {
		return Utils.join(champs, " | ");
	}

	/**
	 * Renvoie le nombre associé à l'état sous forme d'une chaîne de caractères.
	 * @param etat Le nombre associé à l'état.
	 * @return Une représentation en chaîne de caractères du nombre unique associé à un état.
	 */
	public static String afficherNbEtat(long etat) {
		StringBuilder sb = new StringBuilder("[]");
		return sb.toString();
	}

	/**
	 * Renvoie la hauteur de Mario, i.e. sa taille.
	 * @return La taille de Mario.
	 */
	private int getHauteurMario() {
		return modeMario.valeur > 0 ? 2 : 1;
	}

	/**
	 * Renvoie le niveau d'observation (sous-division de la scène) du carreau de 
	 * coordonnées (x, y).
	 * @param x Abscisse du carreau.
	 * @param y Ordonnée du carreau.
	 * @return L'entier associé au niveau d'observation du carreau de coordonnées (x, y).
	 */
	private int getNiveauObservation(int x, int y) {
		for (int i = 0; i < ParametresApprentissage.TAILLES_OBSERVATIONS.length; i++) {
			int taille = ParametresApprentissage.TAILLES_OBSERVATIONS[i];
			int dy = y >= MARIO_Y ? (y - MARIO_Y) : (MARIO_Y - getHauteurMario() - y + 1);
			if (Math.abs(x - MARIO_X) <= taille && dy <= taille) {
				return i;
			}
		}
		System.err.println("Mauvais niveau d'observation!! " + x + " " + y);
		return -1;
	}

	/**
	 * Calcule la distance de Mario au sol.
	 * @return La distance entre Mario et le sol, -1 s'il n'y a pas de sol sous Mario.
	 */
	private int getDistanceAuSol(int x) {
		for (int y = MARIO_Y + 1; y < scene.length; y++) {
			if (estSol(x, y)) {
				return Math.min(3, y - MARIO_Y - 1);
			}
		}
		return -1;
	}

	/**
	 * Renvoie si le carreau de coordonnées (x, y) est un obstacle.
	 * @param x Abscisse
	 * @param y Ordonnée
	 * @return Si le carreau de coordonnées (x, y) est un obstacle.
	 */
	private boolean estObstacle(int x, int y) {
		switch(scene[y][x]) {
		case GeneralizerLevelScene.BRICK:
		case GeneralizerLevelScene.BORDER_CANNOT_PASS_THROUGH:
		case GeneralizerLevelScene.FLOWER_POT_OR_CANNON:
		case GeneralizerLevelScene.LADDER:
			return true;
		}
		return false;
	}

	/**
	 * Renvoie si on pet marcher sur le carreau de coordonnées (x, y).
	 * @param x Abscisse
	 * @param y Ordonnée
	 * @return Si on peut marcher sur le carreau de coordonnées (x, y).
	 */
	private boolean estSol(int x, int y) {
		return estObstacle(x, y) || scene[y][x] == GeneralizerLevelScene.BORDER_HILL;
	}

	/**
	 * Classe représentant les différentes directions de Mario
	 */
	public static class Direction {
		public static final int HAUT = 0;
		public static final int DROITE = 1;
		public static final int BAS = 2;
		public static final int GAUCHE = 3;
		public static final int HAUT_DROITE = 4;
		public static final int BAS_DROITE = 5;
		public static final int BAS_GAUCHE = 6;
		public static final int HAUT_GAUCHE = 7;
		public static final int AUCUNE = 8;
	}

	/**
	 * Contrainte liée à MarioAI
	 */
	private static final float SEUIL_DIRECTION = 0.8f;

	/**
	 * Renvoie la direction correspondante au vecteur déplacement (dx, dy).
	 * @param dx Différence en abscisse (horizontal).
	 * @param dy Différence en ordonnée (vertical).
	 * @return Un entier représentant la direction du vecteur (dx, dy).
	 */
	private int getDirection(float dx, float dy) {
		// Contrainte liée à MarioAI.
		if (Math.abs(dx) < SEUIL_DIRECTION) {
			dx = 0;
		}
		if (Math.abs(dy) < SEUIL_DIRECTION) {
			dy = 0;
		}

		if (dx == 0 && dy > 0) {
			return Direction.HAUT;
		} else if (dx > 0 && dy > 0) {
			return Direction.HAUT_DROITE;
		} else if (dx > 0 && dy == 0) {
			return Direction.DROITE;
		} else if (dx > 0 && dy < 0) {
			return Direction.BAS_DROITE;
		} else if (dx == 0 && dy < 0) {
			return Direction.BAS;
		} else if (dx < 0 && dy < 0) {
			return Direction.BAS_GAUCHE;
		} else if (dx < 0 && dy == 0) {
			return Direction.GAUCHE;
		} else if (dx < 0 && dy > 0) {
			return Direction.HAUT_GAUCHE;
		}
		return Direction.AUCUNE;
	}

	/**
	 * Cette classe est une enveloppe abstraite qui servira à identifier les critères utilisés
	 * pour caractériser l'état, notamment pour le calcul de l'identifiant.
	 */
	public abstract class Champ {
		String nom;
		public Champ(String nom) {
			this.nom = nom;
			champs.add(this);
		}

		@Override
		public String toString() {
			return String.format("%s: %s", nom, getValeurAString());
		}

		public abstract String getValeurAString();
		public abstract int getNBits();
		public abstract int getInt();
	}

	/**
	 * Enveloppe d'un tableau de booléens
	 */
	public class BitArray extends Champ {
		boolean[] valeur;

		public BitArray(String nom, int n) {
			super(nom);
			valeur = new boolean[n];
		}

		@Override
		public int getNBits() {
			return valeur.length;
		}

		@Override
		public int getInt() {
			int decInt = 0;
			for (int i = 0; i < valeur.length; i++) {
				decInt <<= 1;
				decInt += valeur[i] ? 1 : 0;
			}
			return decInt;
		}

		@Override
		public String getValeurAString() {
			return Utils.printArray(valeur);
		}

		private void reinitialiser() {
			for (int i = 0; i < valeur.length; i++) {
				valeur[i] = false;
			}
		}
	}

	/**
	 * Enveloppe d'un entier
	 */
	public class Int extends Champ {
		int valeur;
		// Valeur maximal de cet entier.
		private final int max;

		public Int(String nom, int max) {
			super(nom);
			this.max = max;
		}

		@Override
		public int getNBits() {
			return (int)Math.ceil(Math.log(max + 1) / Math.log(2));
		}

		@Override
		public int getInt() {
			valeur = Math.max(0, Math.min(max, valeur));
			return valeur;
		}

		@Override
		public String getValeurAString() {
			return String.valueOf(valeur);
		}
	}
}

