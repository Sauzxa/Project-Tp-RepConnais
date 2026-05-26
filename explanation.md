# TP-RCR: Explication complète (CNF, SAT, UBCSAT, inférence, résultats attendus)

Ce document explique ce que fait le projet, surtout pour `TP1`, puis résume les autres TPs et le type de résultats attendus.

## 1) Vision globale du projet

Le dépôt combine plusieurs TPs de logique et raisonnement:

- `tp1`: logique propositionnelle + SAT + UBCSAT + inférence par l’absurde sur fichiers `.cnf`.
- `ws_rcr/mytweetyapp`: applications Java (TweetyProject) pour FOL, logique propositionnelle, modale, logique par défaut, réseaux sémantiques, description logic.
- `tp3`, `tp4`, etc.: ressources complémentaires selon les exercices.

Idée générale du module RCR:

1. Modéliser une connaissance.
2. Choisir une logique adaptée.
3. Interroger la base.
4. Interpréter les réponses (`true`/`false`, satisfiable/insatisfiable, modèles, etc.).

## 2) TP1 en détail: CNF, SAT, UBCSAT

## 2.1) Qu’est-ce que la forme CNF?

CNF = Conjunctive Normal Form = conjonction (`AND`) de clauses, chaque clause étant une disjonction (`OR`) de littéraux.

Exemple:

`(a v !b v c) ^ (!a v d) ^ (!c)`

Dans un fichier DIMACS `.cnf`:

- première ligne: `p cnf <nb_variables> <nb_clauses>`
- chaque clause se termine par `0`
- un entier positif = variable, un entier négatif = négation

Exemple:

- `3` signifie `x3`
- `-3` signifie `!x3`

## 2.2) Qu’est-ce que SAT?

Un problème SAT demande:

"Existe-t-il une affectation vrai/faux des variables qui rend toutes les clauses vraies?"

- Oui: formule satisfiable.
- Non: formule insatisfiable.

## 2.3) Rôle de UBCSAT

`UBCSAT` est un solveur SAT par recherche locale stochastique.

Dans ce dépôt:

- les sources C sont dans `tp1/src/*.c`
- compilation par [tp1/Makefile](/home/sauzxaa/Documents/academic/Project-Tp-RepConnais/tp1/Makefile:1)
- binaire cible: `tp1/ubcsat`

Commande standard:

```bash
cd tp1
./ubcsat -alg saps -i test1.cnf -solve
```

## 2.3.1) A quoi servent les fichiers C dans `tp1/src` ?

Ils sont nécessaires pour construire le solveur `ubcsat`:

- `ubcsat.c`, `ubcsat-*.c/.h`: coeur du solveur (lecture CNF, exécution, reporting).
- `saps.c`, `walksat.c`, `gsat.c`, ...: implémentations d'algorithmes SAT locaux.
- `algorithms.c`, `parameters.c`, `reports.c`: registre des algorithmes, parsing des options, statistiques.

Donc:

- si vous utilisez TP1/SAT, les fichiers C sont nécessaires;
- si vous utilisez uniquement les TPs Java (`ws_rcr`), ils ne sont pas nécessaires.

## 2.4) Fichiers TP1 déjà présents

Fichiers clés:

- [tp1/test1.cnf](/home/sauzxaa/Documents/academic/Project-Tp-RepConnais/tp1/test1.cnf:1)
- [tp1/test2.cnf](/home/sauzxaa/Documents/academic/Project-Tp-RepConnais/tp1/test2.cnf:1)
- [tp1/zoo.cnf](/home/sauzxaa/Documents/academic/Project-Tp-RepConnais/tp1/zoo.cnf:1)
- [tp1/zoologie.cnf](/home/sauzxaa/Documents/academic/Project-Tp-RepConnais/tp1/zoologie.cnf:1)
- benchmarks SATLIB: dossiers `tp1/uf75-325/...` et `tp1/uuf75-325/...`

## 2.5) Correctif appliqué pour l'erreur `Invalid Literal`

Erreur observée:

```bash
Error: Invalid Literal [-3] in clause [0]
```

Cause (sur Linux 64-bit): mismatch de format `scanf` dans le parser CNF de UBCSAT.
Le littéral était stocké dans un type `long` mais lu avec `%d` au lieu de `%ld`, ce qui corrompt la lecture des valeurs négatives.

Correctif appliqué dans [ubcsat-triggers.c](/home/sauzxaa/Documents/academic/Project-Tp-RepConnais/tp1/src/ubcsat-triggers.c:1013):

- lecture de l'entête CNF/WCNF en `%lu` pour `iNumVars`, `iNumClauses`;
- lecture des littéraux en `%ld` pour `l`.

Après recompilation, comportement validé:

- `test1.cnf` -> SAT (`Solution found`, `PercentSuccess = 100.00`);
- `test2.cnf` -> UNSAT (`No Solution found`, `PercentSuccess = 0.00`).

Commandes:

```bash
cd tp1
rm -f ubcsat
make
./ubcsat -alg saps -i test1.cnf -solve
./ubcsat -alg saps -i test2.cnf -solve
```

## 2.6) Résultats attendus pour les exemples CNF

### Exemple `test1.cnf`

Le fichier est conçu comme satisfiable.

Attendu:

- SAT = satisfiable.
- Le solveur peut afficher un modèle (ou plusieurs selon runs).

### Exemple `test2.cnf`

Le fichier est conçu comme insatisfiable.

Attendu:

- UNSAT = non satisfiable.
- Aucun modèle total valide.

### Exemple `zoo.cnf` / `zoologie.cnf`

Ce sont les bases "zoologie/céphalopodes" traduites en CNF.

Attendu:

- le solveur répond SAT ou UNSAT selon la base exacte et les contraintes ajoutées.
- si vous ajoutez des faits contradictoires, on peut passer à UNSAT.

## 3) TP1 - inférence par l’absurde

Objectif: tester si `BC |= phi`.

Méthode standard:

`BC |= phi` ssi `BC U {!phi}` est insatisfiable.

Étapes:

1. Prendre `BC` en CNF.
2. Ajouter `!phi` comme nouvelle clause.
3. Lancer SAT.
4. Interpréter:
   - si UNSAT: `phi` est inférée.
   - si SAT: `phi` n’est pas inférée.

Implémentation dans le repo:

- script principal: [tp1/inference.py](/home/sauzxaa/Documents/academic/Project-Tp-RepConnais/tp1/inference.py:1)
- version C ancienne: [tp1/inference.c](/home/sauzxaa/Documents/academic/Project-Tp-RepConnais/tp1/inference.c:1)

Remarque importante:

- privilégier `inference.py` pour la logique du TP.
- `inference.c` est plus ancien et moins robuste.

## 4) Résultats attendus des autres TPs (Java/Tweety)

Les classes Java sont dans `ws_rcr/mytweetyapp/src/main/java/mytweetyapp`.

## 4.1) TP FOL (ex: `SmartCityFol`, `FolExample`)

Attendu:

- requêtes de type faits/implications/existentielles renvoient `true` ou `false`.
- un `true` signifie "conséquence logique de la base".

## 4.2) TP propositionnel (ex: `ECommercePl`)

Attendu:

- propagation des règles propositionnelles.
- conclusions comme `OrderShipped`, `CustomerNotified` selon les faits.

## 4.3) TP modal (ex: `Modal`, `MlExample2`)

Attendu:

- réponses booléennes sur des formules avec opérateurs modaux `[]` et `<>`.
- interprétation selon la base modale codée.

## 4.4) TP logique par défaut (ex: `SubscriptionDefL`, `DefL`)

Attendu:

- raisonnement non monotone: une conclusion par défaut peut être bloquée par une exception.
- requêtes `true/false` selon cohérence des hypothèses par défaut.

## 4.5) TP réseaux sémantiques (ex: `HospitalSemanticNet`)

Attendu:

- héritage de propriétés via relation `is-a`.
- requêtes type "X hérite-t-il de la propriété Y?" -> `true/false`.

## 4.6) TP Description Logic (ex: `desclogic`)

Attendu:

- chargement ontologie OWL.
- affichage des concepts (TBox), rôles, individus (ABox).

## 5) Comment lire les résultats correctement

Deux axes d’évaluation:

1. Satisfiabilité globale (SAT/UNSAT).
2. Entailment d’une requête (`BC |= phi`) via la méthode par l’absurde.

Résumé interprétation:

- `SAT(BC)` vrai: la base n’est pas contradictoire.
- `SAT(BC)` faux: base contradictoire.
- `SAT(BC U {!phi})` faux: `phi` est inférée.
- `SAT(BC U {!phi})` vrai: `phi` n’est pas inférée.

## 6) Checklist de validation pour le rendu TP1

1. Vérifier l’entête CNF:
   - `p cnf nb_vars nb_clauses`
   - nombre de clauses exact.
2. Vérifier que chaque clause finit par `0`.
3. Compiler/recompiler UBCSAT localement (`make`).
4. Tester au moins:
   - un exemple SAT,
   - un exemple UNSAT,
   - un test d’inférence `BC |= phi`.
5. Expliquer clairement la conclusion logique (pas seulement copier la sortie terminale).

## 7) Commandes utiles

Compilation solveur:

```bash
cd tp1
make
```

Test SAT:

```bash
./ubcsat -alg saps -i test1.cnf -solve
```

Test inférence (script Python):

```bash
python3 tp1/inference.py tp1/zoo.cnf
```

UI JavaFX (résultats des TPs Java):

```bash
cd ws_rcr
mvn -pl mytweetyapp -DskipTests javafx:run
```

---

Si besoin, on peut générer ensuite une version "rapport académique" plus formelle (avec sections: Introduction, Méthodologie, Résultats, Discussion, Conclusion) directement à partir de ce fichier.
