# Projet Q-learning INSA de Rouen Normandie GM4

## Sources disponibles
- Rapport : le rapport du projet (.zip contenant les sources .tex et .pdf)
- MarioAI : dossier contenant les sources du projet (source de la plateforme MarioAI et de l'implémentation de l'agent Q-learning)
- alphaXXX\_gammaXXX : dossiers contenant les résultats des différentes expériences. Par exemple, alphaactbas\_gammaopt contient les résultats de l'expérience avec alpha actualisé bas (valeur 0,3) et gamma optimal (0,6).

### Dossier MarioAI
Le dossier MarioAI contient un dossier _src_ avec les sources de la plateforme MarioAI (paquet ch) et de l'agent implémenté (paquet gm4).
Ce dossier contient aussi un dossier _lib_ contenant les bibliothèques nécessaires pour le fonctionnement de la plateforme MarioAI.
Finalement, on peut trouver un script Matlab/Octave _plotAll.m_ permettant de produire les différents graphiques trouvés dans le rapport.


## Utilisation du projet (gm4.insar.agents)
La classe exécutable est Evaluation. On peut passer des paramètres via la ligne de commande. Par défaut, le programme s'exécutera en mode DEMO (démonstration) avec les paramètres d'apprentissage par défaut, i.e. ceux définis dans la classe ParametresApprentissage.

### Paramètres de la ligne de commande
- m : mode d'exécution, e.g. java Evaluation -m <DEMO|EVAL|DEBUG>
- n : nombre de résultats d'évaluation attendus, e.g. java Evaluation -m EVAL -n 200
- nm : nombre de modes d'entraînement (petit, grand, feu), e.g. java Evaluation -nm 2 (uniquement mode petit et grand)
- ng : nombre de graines (pour génération pseudo-aléatoire) à entraîner, e.g. java Evaluation -m DEMO -ng 3
- i : nombre d'itérations d'entraînement par mode, e.g. java Evaluation -i 20 (l'agent s'entraîne 20 fois par mode)
- ei : nombre d'itérations d'évaluation par q-table, e.g. java Evaluation -m EVAL -nm 3 -i 20 -ei 100 (l'agent effectue 100 épisodes d'évaluation pour la q-table issue des 20 * 3 épisodes d'entraînement)
- l : charger ou pas la dernière q-table en mémoire en début d'exécution, e.g. java Evaluation -m DEMO -l 1 (charge la dernière q-table produite, cf. ParametresApprentissage.NOM\_DERNIERE\_QTABLE pour le nom du fichier enregistré/chargé)
