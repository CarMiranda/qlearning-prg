package gm4.insar.agents;

import ch.idsia.benchmark.mario.engine.sprites.Mario;

/**
 * Cette énumération représente une action de Mario.
 * @author Carlos Miranda <carlos.miranda_lopez@insa-rouen.fr>
 */
public enum ActionMario {
	RIEN_FAIRE(0),

	GAUCHE(1, Mario.KEY_LEFT),
	DROITE(2, Mario.KEY_RIGHT),
	SAUT(3, Mario.KEY_JUMP),
	BOULE_FEU(4, Mario.KEY_SPEED),

	SAUT_GAUCHE(5, Mario.KEY_LEFT, Mario.KEY_JUMP),
	SAUT_DROITE(6, Mario.KEY_RIGHT, Mario.KEY_JUMP),

	FEU_GAUCHE(7, Mario.KEY_LEFT, Mario.KEY_SPEED),
	FEU_DROITE(8, Mario.KEY_RIGHT, Mario.KEY_SPEED),

	FEU_SAUT(9, Mario.KEY_JUMP, Mario.KEY_SPEED),

	FEU_SAUT_GAUCHE(10, Mario.KEY_LEFT, Mario.KEY_JUMP, Mario.KEY_SPEED),
	FEU_SAUT_DROITE(11, Mario.KEY_RIGHT, Mario.KEY_JUMP, Mario.KEY_SPEED);

	/**
	 * Nombre d'actions pouvant être réalisées.
	 */
	public static final int NB_ACTIONS = 12;

	/**
	 * Nombre entier associé à l'action.
	 */
	private final int nbAction;
	
	/**
	 * Ce tableau représente l'activation d'une combinaison de touches virtuelles.
	 * En fait, on n'utilise que 6 touches virtuelles dans MarioAI (voir Mario.KEY_... ci-dessus).
	 * Chaqu'une de ces touches est codée par un entier (de 0 à 5). Ainsi, chaque valeur booléenne
	 * du tableau correspond à l'activation d'une touche.
	 * P.e. SAUT_GAUCHE correspond au tableau 
	 * Valeur  true | false | false | true | false | false
	 * Indice    0	|   1	|   2	|   3  |   4   |   5
	 */
	private final boolean[] action;

	private ActionMario(int nbAction, int... ids) {
		this.nbAction = nbAction;

		this.action = new boolean[6];
		for (int id : ids) {
			this.action[id] = true;
		}
	}

	/**
	 * 
	 * @return Le nombre entier associé à l'action.
	 */
	public int getNbAction() {
		return nbAction;
	}

	/**
	 * 
	 * @return Le tableau de booléens correspondant à l'action
	 */
	private boolean[] getAction() {
		return action;
	}

	/**
	 * 
	 * @param nbAction Le nombre entier de l'action souhaitée
	 * @return Le tableau de boléens correspondant à l'action
	 */
	public static boolean[] getAction(int nbAction) {
		return ActionMario.values()[nbAction].getAction();
	}
}
