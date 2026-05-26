# 🧠 Représentation des Connaissances et Raisonnement (RCR) 

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)
[![Python](https://img.shields.io/badge/Python-3.8%2B-3776AB?style=for-the-badge&logo=python&logoColor=white)](https://www.python.org/)
[![Maven](https://img.shields.io/badge/Maven-3.6%2B-C71A22?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![TweetyProject](https://img.shields.io/badge/Tweety-1.23-blue?style=for-the-badge)](http://tweetyproject.org/)
[![Cross-Platform](https://img.shields.io/badge/OS-Windows%20%7C%20Linux%20%7C%20macOS-success?style=for-the-badge)](#)

Ce projet est une collection d'implémentations pour la **Représentation des Connaissances et Raisonnement (RCR)**, explicitement conçu pour modéliser des domaines réels sans s'appuyer sur des exercices de TD standards/génériques. Il exploite des frameworks comme **TweetyProject** pour la manipulation logique mathématique, la preuve et le raisonnement sémantique.

---

## 📋 Prérequis

Avant d'exécuter ce projet, assurez-vous d'avoir :

- **Java JDK 17+** ([Télécharger](https://www.oracle.com/java/technologies/downloads/))
- **Python 3.8+** ([Télécharger](https://www.python.org/downloads/))
- **Maven 3.6+** ([Télécharger](https://maven.apache.org/download.cgi))
- **Git** (pour cloner le dépôt)

### Vérifier l'installation
```bash
java -version    # Doit afficher Java 17 ou supérieur
mvn -version     # Doit afficher Maven 3.6 ou supérieur
python3 --version # Doit afficher Python 3.8 ou supérieur
```

---

## 🚀 Démarrage Rapide (Pour les Enseignants/Évaluateurs)

### 1. Cloner le Dépôt
```bash
git clone <url-du-dépôt>
cd TP-Rep-Connaissance
```

### 2. Compiler Tous les Projets Java
```bash
cd ws_rcr
mvn clean install
```
Cela téléchargera toutes les dépendances et compilera les projets. La première exécution peut prendre quelques minutes.

### 3. Exécuter l'Interface Graphique Unifiée (Recommandé)
```bash
mvn -pl mytweetyapp exec:java -Dexec.mainClass="mytweetyapp.TpRunnerFx"
```
Cela lance une interface interactive présentant tous les modèles logiques.

### 4. Consulter le Rapport
Le rapport complet du projet sera fourni séparément au format PDF (compilé depuis Overleaf).

---

## 🏗️ Architecture du Projet

L'espace de travail est organisé pour séparer clairement nos **projets réels personnalisés** des exercices TD classiques :

```text
TP-Rep-Connaissance/
├── 🌟 PROJETS RÉELS PERSONNALISÉS (Soumission Finale)
│   ├── ws_rcr/
│   │   ├── pom.xml               # Projet Maven parent
│   │   ├── mytweetyapp/          # ☕ Projet Maven principal utilisant les bibliothèques Tweety
│   │   │   ├── pom.xml           
│   │   │   └── src/main/java/mytweetyapp/
│   │   │       ├── SmartCityFol.java       # Logique du Premier Ordre : Ville Intelligente
│   │   │       ├── ECommercePl.java        # Logique Propositionnelle : E-Commerce
│   │   │       ├── SubscriptionDefL.java   # Logique par Défaut : Abonnements
│   │   │       ├── HospitalSemanticNet.java # Réseaux Sémantiques : Hôpital
│   │   │       ├── TpRunnerFx.java         # 🖥️ Interface Graphique JavaFX Unifiée
│   │   │       └── ...
│   │   └── tp06/                 # Module OWL API / Logique de Description
│   │       ├── pom.xml           
│   │       └── src/tp06/descLogic.java
│   │
└── 📚 TPs CLASSIQUES (Exercices TD & Archives)
    ├── tp1/                      # 🧩 Logique Propositionnelle & Résolution SAT (C / Python)
    │   ├── ubcsat                # Binaire compilé pour la résolution SAT
    │   └── inference.py          # Scripts d'inférence Python
    ├── tp2/                      # Archives FOL TD
    ├── tp3/                      # Archives Logique Modale TD
    ├── tp4/                      # 🔄 Logique par Défaut & Raisonnement Non-Monotone (Java)
    │   └── extensioncalculator/
    └── tp5/                      # 🕸️ Réseaux Sémantiques (Python)
        └── semantic_networks/
```

---

## 📦 Contenu du Projet (Les 4 TPs Principaux)

Nos applications pratiques (TPs) modélisent des cas d'usage modernes et originaux.

### 1️⃣ TP 1 : Logique du Premier Ordre (FOL) — Transport Public Ville Intelligente
Modélise les capacités de transport d'une ville intelligente.
* **Représentation des Connaissances** : 
  * Sortes : `Vehicle`, `Station`
  * Prédicats : `StopsAt`, `Electric`, `Connected`
* **Exploitation** : Utilise un `SimpleFolReasoner` pour prouver la logique de connectivité symétrique et effectuer des requêtes existentielles sur le réseau de transport (par exemple, vérifier si un véhicule *électrique* atteint une *station* spécifique).

### 2️⃣ TP 2 : Logique Propositionnelle (PL) — Logistique E-Commerce
Modélise un pipeline automatisé de traitement des commandes.
* **Représentation des Connaissances** : Règles concernant `PaymentReceived`, `ItemInStock`, `OrderShipped`, et `CustomerNotified`.
* **Exploitation** : Le système reçoit des faits concernant le paiement et le stock, et le `SatReasoner` infère automatiquement si le client doit recevoir un email automatique.

### 3️⃣ TP 3a : Logique modale — Sécurité tramway (Smart City)
Modélise les **contraintes de sécurité** d'un tramway avec □ (nécessaire) et ◇ (possible).
* **Axiomes** : `□(DoorOpen → ¬Moving)`, `□¬◇(DoorOpen ∧ Moving)`, `◇Moving`, `◇PowerFailure`
* **Faits** : `AtStation`, `¬Moving`, `¬DoorOpen`
* **Fichier** : `SmartCityModal.java`, `ModalLogicKb.java`
* **Exécution** : `mvn -pl mytweetyapp exec:java -Dexec.mainClass="mytweetyapp.SmartCityModal"`

### 3️⃣ TP 3b : Logique par Défaut (DL) — Abonnements à Renouvellement Automatique
Modélise la logique non-monotone pour la facturation des abonnements logiciels.
* **Représentation des Connaissances** : Une `DefaultTheory` chargée avec la syntaxe de Logique par Défaut Relationnelle (RDL).
  * Règle : "Les abonnés se renouvellent automatiquement par *défaut*, SAUF si leur carte a expiré."
* **Exploitation** : Le `SimpleDefaultReasoner` évalue plusieurs utilisateurs, supposant qu'un utilisateur se renouvelle automatiquement car il n'a pas de faits contradictoires, tout en prouvant simultanément qu'un autre utilisateur *ne peut pas* se renouveler automatiquement en raison d'un fait de carte expirée qui annule la règle par défaut.

### 5️⃣ TP 5 : Logique descriptive (DL) — Transport Public Ville Intelligente
Même domaine que Smart City (TP1 FOL), exprimé en **notation DL** (`⊑`, `⊓`, `C(a)`, `R(a,b)`), sérialisé en OWL pour HermiT.
* **T-Box** : `Tram ⊑ ElectricVehicle ⊓ PublicTransport`, `ElectricVehicle ⊓ DieselVehicle ⊑ ⊥`, rôles `stopsAt`, `connects`
* **A-Box** : `Tram(tramA)`, `stopsAt(tramA, northPark)`, `DieselVehicle(bus14)`, `connects(northPark, centralStation)`
* **Fichiers** : `DescriptionLogicKb.java`, `desclogic.dl`, `desclogic.owl`
* **Exécution** : `mvn -pl mytweetyapp exec:java -Dexec.mainClass="mytweetyapp.desclogic"`

### 4️⃣ TP 4 : Réseaux Sémantiques — Topologie du Personnel Hospitalier
Modélise l'héritage profond objet-propriété dans un environnement hospitalier.
* **Représentation des Connaissances** : Graphe Orienté Programmatique comportant des `Nœuds` et des `Relations/Arêtes` (`is-a`, `works-in`).
* **Exploitation** : Un algorithme de traversée d'héritage parcourt automatiquement l'arbre hiérarchique pour inférer que "Dr. House" travaille dans un hôpital car il hérite du trait de son nœud ancêtre (`Healthcare_Professional`).

---

## 🚀 Comment Exécuter (Multi-plateforme : Windows / Linux / macOS)

Le projet repose principalement sur Java et Python, le rendant complètement multi-plateforme. Maven est fortement recommandé pour gérer les dépendances de manière transparente quel que soit votre système d'exploitation.

### Exécution des Composants Java Tweety (Fonctionne de la même manière sur Windows et Linux)
La façon la plus simple est d'utiliser **Maven** dans votre terminal ou Invite de commandes/PowerShell :

1. **Naviguer vers l'espace de travail Java**
   ```bash
   cd ws_rcr
   ```
2. **Compiler tous les modules Java** (Télécharge les dépendances si non mises en cache)
   ```bash
   mvn clean install
   ```

### 🖥️ Exécution de l'Interface Graphique Unifiée (GUI)
Pour lancer l'interface JavaFX interactive qui regroupe tous les projets et modèles logiques dans un tableau de bord visuel :
```bash
mvn -pl mytweetyapp exec:java -Dexec.mainClass="mytweetyapp.TpRunnerFx"
```

### 🖥️ Exécution des Modules CLI Individuellement

3. **Exécuter l'exemple Ville Intelligente (FOL)**
   ```bash
   mvn -pl mytweetyapp exec:java -Dexec.mainClass="mytweetyapp.SmartCityFol"
   ```
4. **Exécuter l'exemple E-commerce (PL)**
   ```bash
   mvn -pl mytweetyapp exec:java -Dexec.mainClass="mytweetyapp.ECommercePl"
   ```
5. **Exécuter l'exemple Abonnement (Logique par Défaut)**
   ```bash
   mvn -pl mytweetyapp exec:java -Dexec.mainClass="mytweetyapp.SubscriptionDefL"
   ```
6. **Exécuter l'exemple Hôpital (Réseau Sémantique)**
   ```bash
   mvn -pl mytweetyapp exec:java -Dexec.mainClass="mytweetyapp.HospitalSemanticNet"
   ```
7. **Exécuter l'exemple Logique de Description / OWL API**
   ```bash
   mvn -pl tp06 exec:java -Dexec.mainClass="tp06.descLogic"
   ```

Si vous êtes déjà dans `ws_rcr/mytweetyapp`, vous pouvez omettre `-pl mytweetyapp` :
```bash
cd ws_rcr/mytweetyapp
mvn exec:java -Dexec.mainClass="mytweetyapp.SmartCityFol"
```

Lors de l'exécution de commandes depuis `ws_rcr`, conservez l'option `-pl`. Sans elle, Maven exécute la même commande sur chaque module, donc une classe `mytweetyapp.*` s'exécutera dans `mytweetyapp` puis échouera dans `tp06`.

Commande rapide (depuis `ws_rcr`) pour les Réseaux Sémantiques :
```bash
mvn -pl mytweetyapp exec:java -Dexec.mainClass="mytweetyapp.HospitalSemanticNet"
```

### Exécution des Scripts Auxiliaires

**Scripts Python (TP5) :**
Sur Windows :
```cmd
python tp5/semantic_networks/main.py
```
Sur Linux/macOS :
```bash
python3 tp5/semantic_networks/main.py
```

**Solveur SAT basé sur C (TP1) :**
_Note : Les binaires précompilés `.exe` ont été supprimés pour l'hygiène du dépôt._
Sur Windows, vous pouvez compiler les fichiers `.c` en utilisant `gcc` (MinGW) ou simplement utiliser un IDE C comme Code::Blocks.
Sur Linux/macOS, utilisez simplement le Makefile :
```bash
cd tp1/
make
./ubcsat [arguments]
```

---

## 🐛 Dépannage

### Échec de la Compilation Maven
- **Problème** : `JAVA_HOME non défini`
  - **Solution** : Définir la variable d'environnement JAVA_HOME vers le chemin d'installation de votre JDK
  ```bash
  # Linux/macOS
  export JAVA_HOME=/chemin/vers/jdk-17
  
  # Windows (PowerShell)
  $env:JAVA_HOME="C:\Program Files\Java\jdk-17"
  ```

### L'Interface Graphique JavaFX ne se Lance pas
- **Problème** : Modules JavaFX manquants
  - **Solution** : Le projet utilise OpenJFX que Maven télécharge automatiquement. Si les problèmes persistent, assurez-vous d'utiliser JDK 17+

### Échec des Scripts Python
- **Problème** : Module non trouvé
  - **Solution** : Installer les packages requis
  ```bash
  pip install -r requirements.txt  # Si requirements.txt existe
  ```

### Permission Refusée sur Linux/macOS
- **Problème** : Impossible d'exécuter les scripts
  - **Solution** : Rendre les scripts exécutables
  ```bash
  chmod +x tp1/ubcsat
  ```

---

## 📚 Résumé de la Structure du Projet

| Répertoire | Description |
|-----------|-------------|
| `ws_rcr/mytweetyapp/` | Projet Java principal avec implémentations Tweety |
| `ws_rcr/tp06/` | Module OWL API / Logique de Description |
| `tp1/` | Résolution SAT (C/Python) |
| `tp5/` | Réseaux sémantiques (Python) |

---

## 👥 Auteurs

- YEDDOU Abdelkader Raouf (222231501902)
- FERGUENE Abdelraouf (222231361813)
- OULDGOUGAM Riad Madjid (222231472314)

**Master 1 - Ingénierie des Systèmes Informatiques Intelligents (SII)**  
**Module** : Représentation des connaissances et raisonnement  
**Année Universitaire** : 2025 / 2026

---

## 🛡️ Licence & Intégrité Académique
Ce dépôt respecte la contrainte académique de construire des modèles logiques représentatifs du monde réel sans copier les exemples de Travaux Dirigés (TD) fournis. Développé pour le module RCR à l'USTHB.
