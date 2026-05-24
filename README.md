# 🧠 Knowledge Representation & Reasoning (RCR) 

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)
[![Maven](https://img.shields.io/badge/Maven-3.6%2B-C71A22?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![TweetyProject](https://img.shields.io/badge/Tweety-1.23-blue?style=for-the-badge)](http://tweetyproject.org/)
[![Cross-Platform](https://img.shields.io/badge/OS-Windows%20%7C%20Linux%20%7C%20macOS-success?style=for-the-badge)](#)

This project is a collection of implementations for **Knowledge Representation and Reasoning (RCR)**, explicitly designed to model real-world domains without relying on standard/generic textbook exercises (TDs). It leverages frameworks like **TweetyProject** for mathematical logic manipulation, proving, and semantic reasoning.

---

## 🏗️ Project Architecture

The workspace is organized into separate modules targeting different logic ecosystems:

```text
TP-Rep-Connaissance/
├── ws_rcr/
│   ├── pom.xml               # Parent Maven project for all Java modules
│   ├── mytweetyapp/          # ☕ Main Maven Java project using the Tweety Libs
│       ├── pom.xml           # Dependencies (tweety-full v1.23)
│       └── src/main/java/mytweetyapp/
│           ├── SmartCityFol.java       # First-Order Logic Implementation 
│           ├── ECommercePl.java        # Propositional Logic Implementation
│           ├── SubscriptionDefL.java   # Default Logic Implementation
│           └── HospitalSemanticNet.java # Semantic Networks Implementation
│   └── tp06/                 # OWL API / Description Logic Maven module
│       ├── pom.xml           # OWL API + HermiT dependencies
│       └── src/tp06/descLogic.java
├── tp1/                      # 🧩 Propositional Logic & SAT Solving (C / Python)
│   ├── ubcsat                # Compiled binary for SAT solving (Requires rebuild on Windows)
│   └── inference.py          # Python inference scripts
├── tp4/                      # 🔄 Default Logic & Non-Monotonic Reasoning (Java)
│   └── extensioncalculator/
└── tp5/                      # 🕸️ Semantic Networks
    └── semantic_networks/    # Python graph implementation
```

---

## 📦 What's Inside? (The 4 Core TPs)

Our practical applications (TPs) model modern, original use cases. 

### 1️⃣ TP 1: First-Order Logic (FOL) — Smart City Public Transport
Models a smart city's transportation capabilities. 
* **Knowledge Representation**: 
  * Sorts: `Vehicle`, `Station`
  * Predicates: `StopsAt`, `Electric`, `Connected`
* **Exploitation**: Utilizes a `SimpleFolReasoner` to prove symmetrical connectivity logic and perform existential queries on the transport web (e.g., verifying if an *electric* vehicle reaches a specific *station*).

### 2️⃣ TP 2: Propositional Logic (PL) — E-Commerce Logistics
Models an automated order processing pipeline.
* **Knowledge Representation**: Rules regarding `PaymentReceived`, `ItemInStock`, `OrderShipped`, and `CustomerNotified`.
* **Exploitation**: The system receives facts regarding payment & stock, and the `SatReasoner` autonomously infers if the client should receive an automated email.

### 3️⃣ TP 3: Default Logic (DL) — Auto-Renewing Subscriptions
Models non-monotonic logic for software subscription billing.
* **Knowledge Representation**: A `DefaultTheory` loaded with Relational Default Logic syntax (RDL).
  * Rule: "Subscribers auto-renew by *default*, UNLESS their card has expired."
* **Exploitation**: The `SimpleDefaultReasoner` evaluates multiple users, assuming one auto-renews because it has no conflicting facts, while simultaneously proving another user *cannot* auto-renew because of an expired card fact overriding the default rule.

### 4️⃣ TP 4: Semantic Networks — Hospital Staff Topology 
Models deep object-property inheritance within a hospital environment.
* **Knowledge Representation**: Programmatic Directed Graph featuring `Nodes` and `Relations/Edges` (`is-a`, `works-in`).
* **Exploitation**: An inheritance traversal algorithm automatically traverses the hierarchy tree to infer that "Dr. House" works in a hospital because he inherits the trait from his ancestor node (`Healthcare_Professional`).

---

## 🚀 How to Run (Cross-Platform: Windows / Linux / macOS)

The project relies heavily on Java and Python, making it completely cross-platform. Maven is highly recommended to handle dependencies seamlessly regardless of your OS.

### Running the Java Tweety Components (Works the same on Windows & Linux)
The easiest way is to use **Maven** in your terminal or Command Prompt/PowerShell:

1. **Navigate to the Java workspace**
   ```bash
   cd ws_rcr
   ```
2. **Compile all Java modules** (Downloads dependencies if not cached)
   ```bash
   mvn clean install
   ```
3. **Run the Smart City (FOL) Example**
   ```bash
   mvn -pl mytweetyapp exec:java -Dexec.mainClass="mytweetyapp.SmartCityFol"
   ```
4. **Run the E-commerce (PL) Example**
   ```bash
   mvn -pl mytweetyapp exec:java -Dexec.mainClass="mytweetyapp.ECommercePl"
   ```
5. **Run the Subscription (Default Logic) Example**
   ```bash
   mvn -pl mytweetyapp exec:java -Dexec.mainClass="mytweetyapp.SubscriptionDefL"
   ```
6. **Run the Hospital (Semantic Network) Example**
   ```bash
   mvn -pl mytweetyapp exec:java -Dexec.mainClass="mytweetyapp.HospitalSemanticNet"
   ```
7. **Run the Description Logic / OWL API Example**
   ```bash
   mvn -pl tp06 exec:java -Dexec.mainClass="tp06.descLogic"
   ```

If you are already inside `ws_rcr/mytweetyapp`, you can omit `-pl mytweetyapp`:
```bash
cd ws_rcr/mytweetyapp
mvn exec:java -Dexec.mainClass="mytweetyapp.SmartCityFol"
```

When running commands from `ws_rcr`, keep the `-pl` option. Without it, Maven runs the same command on every module, so a `mytweetyapp.*` class will run in `mytweetyapp` and then fail in `tp06`.

### Running the Auxiliary Scripts

**Python Scripts (TP5):**
On Windows:
```cmd
python tp5/semantic_networks/main.py
```
On Linux/macOS:
```bash
python3 tp5/semantic_networks/main.py
```

**C-Based SAT solver (TP1):**
_Note: Pre-compiled `.exe` binaries were removed for repository hygiene._
On Windows, you can compile the `.c` files using `gcc` (MinGW) or simply use a C IDE like Code::Blocks.
On Linux/macOS, simply use the Makefile:
```bash
cd tp1/
make
./ubcsat [arguments]
```

---

## 🛡️ License & Academic Integrity
This repository adheres to the academic constraint of building real-world representative logic models without copying provided Tutorial (TD) examples. Developed for the RCR module.
