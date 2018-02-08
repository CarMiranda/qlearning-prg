clear all
close all
clc

nTrainEachIter = 20;
nIter = 200;

% Chargement des différents fichiers
load ../alphaactbas_gammaopt/eval.txt
eval_alphabas = eval(1:nIter, :);

load ../alphaactopt_gammabas/eval.txt
eval_gammabas = eval(1:nIter, :);

load ../alphaactopt_gammaopt/eval.txt
eval_optimale = eval(1:nIter, :);

load ../alphafixeopt_gammaopt/eval.txt
eval_alphafixe = eval(1:nIter, :);

x = 0:nTrainEachIter:nTrainEachIter*(nIter - 1);


% Comparaison des évaluations du score 
figure(1)
grid on
hold on

filterLength = 20;
f = 1/filterLength * ones(filterLength, 1);

plot(x, eval_alphabas(:,1),'--', 'Linewidth', 1, 'color', [1, 155/255, 51/255])
filtered1 = filter(f, 1, eval_alphabas(:, 1));
plot(x-filterLength/2*nTrainEachIter, filtered1, '-', 'Linewidth', 3, 'color', [1 121/255 0]);
xlim([0, nTrainEachIter * nIter])
ylim([4000, 7500])

plot(x,eval_gammabas(:,1),'c--', 'Linewidth', 1)
filtered1 = filter(f, 1, eval_gammabas(:, 1));
plot(x-filterLength/2*nTrainEachIter, filtered1, 'b-', 'Linewidth', 3);
xlim([0, nTrainEachIter * nIter])
ylim([4000, 7500])

plot(x,eval_alphafixe(:,1),'y--', 'Linewidth', 1)
filtered1 = filter(f, 1, eval_alphafixe(:, 1));
plot(x-filterLength/2*nTrainEachIter, filtered1, 'y-', 'Linewidth', 3);
xlim([0, nTrainEachIter * nIter])
ylim([4000, 7500])

plot(x,eval_optimale(:,1),'m--', 'Linewidth', 1)
filtered1 = filter(f, 1, eval_optimale(:, 1));
plot(x-filterLength/2*nTrainEachIter, filtered1, 'r-', 'Linewidth', 3);
xlim([0, nTrainEachIter * nIter])
ylim([4000, 7500])

xlabel('Episodes d''entrainement', 'FontSize', 20);
ylabel('Scores moyens', 'FontSize', 20);
set(gca, 'XTick', [0:500:5000]);
set(gca,'FontSize',10);
legend('\alpha petit', '\alpha petit, lisse', ...
       '\gamma petit', '\gamma petit, lisse', ...
       '\alpha constant', '\alpha constant, lisse', ...
       'Parametres optimaux', 'Parametres optimaux, lisse', ... 
       'Location', 'SouthEast')
FigHandle = figure(1);
set(FigHandle, 'Position', [100, 100, 900, 600]);


%%
figure(2)
grid on
hold on


filterLength = 20;
f = 1/filterLength * ones(filterLength, 1);

plot(x,eval_alphabas(:,2),'--', 'Linewidth', 1, 'color', [1, 155/255, 51/255])
filtered1 = filter(f, 1, eval_alphabas(:, 2));
plot(x-filterLength/2*nTrainEachIter, filtered1, '-', 'Linewidth', 3, 'color', [1 121/255 0]);
xlim([0, nTrainEachIter * nIter])

plot(x,eval_gammabas(:,2),'c--', 'Linewidth', 1)
filtered1 = filter(f, 1, eval_gammabas(:, 2));
plot(x-filterLength/2*nTrainEachIter, filtered1, 'b-', 'Linewidth', 3);
xlim([0, nTrainEachIter * nIter])

plot(x,eval_alphafixe(:,2),'y--', 'Linewidth', 1)
filtered1 = filter(f, 1, eval_alphafixe(:, 2));
plot(x-filterLength/2*nTrainEachIter, filtered1, 'y-', 'Linewidth', 3);
xlim([0, nTrainEachIter * nIter])

plot(x,eval_optimale(:,2),'m--', 'Linewidth', 1)
filtered1 = filter(f, 1, eval_optimale(:, 2));
plot(x-filterLength/2*nTrainEachIter, filtered1, 'r-', 'Linewidth', 3);
xlim([0, nTrainEachIter * nIter])

xlabel('Episodes d''entrainement', 'FontSize', 20);
ylabel('Frequence de victoire', 'FontSize', 20);
set(gca, 'XTick', [0:500:5000]);
set(gca,'FontSize',10);
legend('\alpha petit', '\alpha petit, lisse', ...
       '\gamma petit', '\gamma petit, lisse', ...
       '\alpha constant', '\alpha constant, lisse', ...
       'Parametres optimaux', 'Parametres optimaux, lisse', ... 
       'Location', 'SouthEast')
FigHandle = figure(2);
set(FigHandle, 'Position', [100, 100, 900, 600]);

%%
figure(3)
grid on
hold on


filterLength = 20;
f = 1/filterLength * ones(filterLength, 1);

plot(x,eval_alphabas(:,3),'--', 'Linewidth', 1, 'color', [1, 155/255, 51/255])
filtered1 = filter(f, 1, eval_alphabas(:, 3));
plot(x-filterLength/2*nTrainEachIter, filtered1, '-', 'Linewidth', 3, 'color', [1 121/255 0]);
xlim([0, nTrainEachIter * nIter])
ylim([0.3, 0.7])

plot(x,eval_gammabas(:,3),'c--', 'Linewidth', 1)
filtered1 = filter(f, 1, eval_gammabas(:, 3));
plot(x-filterLength/2*nTrainEachIter, filtered1, 'b-', 'Linewidth', 3);
xlim([0, nTrainEachIter * nIter])
ylim([0.3, 0.7])

plot(x,eval_alphafixe(:,3),'y--', 'Linewidth', 1)
filtered1 = filter(f, 1, eval_alphafixe(:, 3));
plot(x-filterLength/2*nTrainEachIter, filtered1, 'y-', 'Linewidth', 3);
xlim([0, nTrainEachIter * nIter])
ylim([0.3, 0.7])

plot(x,eval_optimale(:,3),'m--', 'Linewidth', 1)
filtered1 = filter(f, 1, eval_optimale(:, 3));
plot(x-filterLength/2*nTrainEachIter, filtered1, 'r-', 'Linewidth', 3);
xlim([0, nTrainEachIter * nIter])
ylim([0.3, 0.7])

xlabel('Episodes d''entrainement', 'FontSize', 20);
ylabel('% de monstres tues', 'FontSize', 20);
set(gca, 'XTick', [0:500:5000]);
set(gca,'FontSize',10);
legend('\alpha petit', '\alpha petit, lisse', ...
       '\gamma petit', '\gamma petit, lisse', ...
       '\alpha constant', '\alpha constant, lisse', ...
       'Parametres optimaux', 'Parametres optimaux, lisse', ... 
       'Location', 'SouthEast')
FigHandle = figure(3);
set(FigHandle, 'Position', [100, 100, 900, 600]);

%%
figure(4)
grid on
hold on


filterLength = 20;
f = 1/filterLength * ones(filterLength, 1);

plot(x,eval_alphabas(:,5),'--', 'Linewidth', 1, 'color', [1, 155/255, 51/255])
filtered1 = filter(f, 1, eval_alphabas(:, 5));
plot(x-filterLength/2*nTrainEachIter, filtered1, '-', 'Linewidth', 3, 'color', [1 121/255 0]);
xlim([0, nTrainEachIter * nIter])

plot(x,eval_gammabas(:,5),'c--', 'Linewidth', 1)
filtered1 = filter(f, 1, eval_gammabas(:, 5));
plot(x-filterLength/2*nTrainEachIter, filtered1, 'b-', 'Linewidth', 3);
xlim([0, nTrainEachIter * nIter])

plot(x,eval_alphafixe(:,5),'y--', 'Linewidth', 1)
filtered1 = filter(f, 1, eval_alphafixe(:, 5));
plot(x-filterLength/2*nTrainEachIter, filtered1, 'y-', 'Linewidth', 3);
xlim([0, nTrainEachIter * nIter])

plot(x,eval_optimale(:,5),'m--', 'Linewidth', 1)
filtered1 = filter(f, 1, eval_optimale(:, 5));
plot(x-filterLength/2*nTrainEachIter, filtered1, 'r-', 'Linewidth', 3);
xlim([0, nTrainEachIter * nIter])

xlabel('Episodes d''entrainement', 'FontSize', 20);
ylabel('Temps de survie (en images)', 'FontSize', 20);
set(gca, 'XTick', [0:500:5000]);
set(gca,'FontSize',10);
legend('\alpha petit', '\alpha petit, lisse', ...
       '\gamma petit', '\gamma petit, lisse', ...
       '\alpha constant', '\alpha constant, lisse', ...
       'Parametres optimaux', 'Parametres optimaux, lisse', ... 
       'Location', 'SouthEast')
FigHandle = figure(4);
set(FigHandle, 'Position', [100, 100, 900, 600]);
