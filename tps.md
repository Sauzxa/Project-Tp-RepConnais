# Projet : Représentation des Connaissances et Raisonnement (RCR)
### Résumé des Travaux Pratiques (TPs)

Ce document résume les différentes sessions de travaux pratiques (TPs) réalisées dans ce dépôt. Chaque TP explore un paradigme spécifique de la Représentation des Connaissances et du Raisonnement, illustrant comment les modèles théoriques se traduisent en outils informatiques pratiques.

---

## TP 1 : Logique Propositionnelle & Solveurs SAT (UBCSAT)
**Emplacement :** `/tp1/`

* **Ce que cela représente :** La logique propositionnelle et la satisfaisabilité booléenne (SAT). Le but est de déterminer s'il existe une interprétation qui satisfait une formule booléenne donnée.
* **Comment ça fonctionne :** Nous représentons les problèmes logiques sous **Forme Normale Conjonctive (CNF)** (ex: `test1.cnf`, `zoo.cnf`). Un solveur appelé **UBCSAT** est utilisé avec des algorithmes comme SAPS (Scaling and Probabilistic Smoothing) pour traiter le fichier CNF.
* **Exemple :** L'exécution de `ubcsat -alg saps -i test1.cnf -solve` évalue les conditions. La sortie fournit le sous-ensemble de variables (ex: `1 -2 -3 -4 -5` représentant `v1=Vrai, v2=Faux...`) qui modélise avec succès un état où chaque clause est satisfaite (Vraie).

---

## TP 3 : Logique Modale
**Emplacement :** `/tp3/`

* **Ce que cela représente :** La logique modale ajoute des modalités à la logique classique pour exprimer des nécessités et des possibilités.
* **Comment ça fonctionne :** En utilisant une syntaxe spécifique à la Logique Modale (ex: format `.mlogic`), on définit nativement des domaines comme `Animal` ou `Plant` ainsi que des propriétés/relations.
* **Exemple (`Example.mlogic`) :** Nous utilisons des opérateurs comme `[]` (Boîte, représentant la *Nécessité / toujours vrai*) et `<>` (Losange, représentant la *Possibilité / parfois vrai*). Par exemple, `[](forall X:(([](Flies(X))) || (<>(!HasWings(X)))))` exprime des assertions complexes pour savoir si les animaux et des entités spécifiques (comme les aigles ou les pingouins) peuvent voler ou ont des ailes dans divers mondes possibles.

---

## TP 4 : Logique des Défauts (Argumentation Abstraite & Extensions)
**Emplacement :** `/tp4/extensioncalculator-master/`

* **Ce que cela représente :** Une logique non-monotone où des conclusions peuvent être tirées par défaut à moins d'être contredites par de nouvelles preuves (gestion d'informations incomplètes). 
* **Comment ça fonctionne :** Utilisation d'un **Calculateur d'Extensions** (Extension Calculator) automatisé développé en Java (utilisant SAT4J et ANTLR). En logique des défauts, les théories génèrent des *extensions* (ensembles de croyances plausibles) par le biais d'arbres de processus.
* **Exemple :** Les utilisateurs saisissent des formules propositionnelles avec des assertions logiques (`~a`, `a & b`, `a | b`). Le calculateur analyse les règles par défaut pour évaluer quelles extensions logiques sont mathématiquement viables pour la théorie des défauts donnée.

---

## TP 5 : Réseaux Sémantiques
**Emplacement :** `/tp5/semantic_networks/`

* **Ce que cela représente :** La représentation des connaissances via des graphes orientés, où les nœuds représentent des concepts et les arêtes des relations sémantiques (comme "est-un" ou "a-un").
* **Comment ça fonctionne :** Implémenté en Python, le code analyse des bases sémantiques au format JSON. Le projet est séparé en algorithmes traitant trois défis majeurs des réseaux sémantiques :
  1. **Propagation de marqueurs (`propagation.py`) :** Parcourt le réseau pour trouver des points d'intersection répondant aux requêtes.
  2. **Héritage (`heritage.py`) :** Gère les chaînes d'héritage multiple entre les concepts.
  3. **Exceptions (`exceptions.py`) :** Gère les contradictions logiques dans les héritages hiérarchiques (ex: "Les oiseaux volent, mais les pingouins sont des oiseaux et ne volent pas").

---

## TP 6 : Logiques de Description (OWL API)
**Emplacement :** `/ws_rcr/tp06/` et `desclogic.owl`

* **Ce que cela représente :** Les Logiques de Description (DL) sont des langages formels utilisés pour construire des Ontologies (le fondement du Web Sémantique). Elles séparent les connaissances en une Boîte Terminologique (T-Box) et une Boîte Assertionnelle (A-Box).
* **Comment ça fonctionne :** Un projet Java utilisant l'**API OWL** (`org.semanticweb.owlapi`). Il charge un fichier `.owl` XML/RDF standardisé.
* **Exemple :** Le fichier `desclogic.owl` indique qu'une `Sedan` (Berline) est une sous-classe de `Car` (Voiture). En Java, le script `descLogic.java` itère sur l'ontologie pour extraire dynamiquement :
  * **Concepts (T-Box) :** `Vehicle`, `Car`, `SUV`, etc.
  * **Rôles (Propriétés) :** Propriétés d'objet comme `Manufacturer`.
  * **Individus (A-Box) :** Instances spécifiques mappées aux concepts, comme `BMW` ou `RedSedan`.