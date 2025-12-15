## Rapport de synthèse du projet

Ce document résume les informations nécessaires pour évaluer le projet (code, tests, couverture) et fournit les liens/chemins utiles.

---

### 1. Dépôt GitHub

- **Dépôt GitHub du projet**:  
  `https://github.com/vbuyakov/ocr-java-angular-project-4/tree/main`  

---

### 2. Lancement de l’application

- **Back-end (Spring Boot)**  
  - Prérequis: JDK 21, Docker, Docker Compose, Maven ≥ 3.9.3  
  - Commandes:
    - `cd back`
    - `mvn spring-boot:run`
  - Le back démarre sur le port **8080** et lance automatiquement le conteneur MySQL défini dans `back/compose.yaml`.

- **Front-end (Angular)**  
  - Prérequis: Node/npm installés  
  - Commandes:
    - `cd front`
    - `npm install`
    - `npm run start`  
  - Le front démarre en général sur `http://localhost:4200/`.

---

### 3. Tests et couverture – Front

- **Tests unitaires & d’intégration (Jest)**  
  - Commandes:
    - `cd front`
    - `npm run test`                # exécution des tests Jest
    - `npm run test:coverage`       # exécution + rapport de couverture
  - Rapports de couverture:
    - Résumé JSON: `front/coverage/coverage-summary.json`
    - Rapport HTML: `front/coverage/jest/index.html`  
      ou `front/coverage/lcov-report/index.html`
  - Niveau de couverture global actuel (issu de `coverage-summary.json`):  
    - **Statements**: **97,45 %**  
    - **Branches**: **93,33 %**  
    - **Lines**: **97,15 %**  
    - **Functions**: **97,72 %**  

- **Tests end-to-end (Cypress)**  
  - Commandes:
    - `cd front`
    - `npm run e2e`            # exécution des tests E2E
    - `npm run e2e:coverage`   # tests E2E + génération du rapport de couverture
  - Rapports de couverture:
    - Fichier LCOV: `front/coverage/lcov.info`
    - Rapport HTML: `front/coverage/lcov-report/index.html`

---

### 4. Tests et couverture – Back

- **Tests unitaires & d’intégration (JUnit / Mockito)**  
  - Commandes:
    - `cd back`
    - `mvn test`     # tests unitaires
    - `mvn verify`   # tests unitaires + intégration + JaCoCo
  - Rapports de tests:
    - Rapports Surefire (unitaires): `back/target/surefire-reports/`
    - Rapports Failsafe (intégration): `back/target/failsafe-reports/`

- **Rapports de couverture (JaCoCo)**  
  - Rapport HTML global:
    - `back/target/site/jacoco/index.html`
  - Rapport de synthèse rédigé:
    - `back/TEST_COVERAGE_REPORT.md`  
      (détail par packages: controllers, services, security, etc.)
  - Principaux niveaux de couverture (issus de `TEST_COVERAGE_REPORT.md`):  
    - **Couverture globale méthodes sur l’ensemble du code**: **48 %** (instructions), avec **100 %** pour les controllers.  
    - **Controllers** (`com.openclassrooms.starterjwt.controllers`): **100 %** (17/17 méthodes).  
    - **Services** (`com.openclassrooms.starterjwt.services`): **98 %** (29/30 méthodes).  
    - **Security JWT** (`com.openclassrooms.starterjwt.security.jwt`): **92 %** (9/11 méthodes).  
    - **Security Services** (`com.openclassrooms.starterjwt.security.services`): **92 %** (24/26 méthodes).  
    - **Total des packages testés (controllers + services + security)**: **94 %** des méthodes couvertes (79/84).

---

### 5. Correspondance avec les attentes pédagogiques

- **Maintenir le code avec le débogage**
  - Code front-end et back-end refactoré (gestion centralisée des exceptions, découpage en couches, désabonnement des Observables, typage strict, suppression des `any` en prod).
  - **Livrable**: fichier TXT/PDF pointant vers le même dépôt GitHub que ci‑dessus.

- **Écrire des tests unitaires sur le front et le back**
  - Tests unitaires & d’intégration front (Jest) dans `front/src/app/**/*.spec.ts`.
  - Tests unitaires & d’intégration back (JUnit/Mockito) dans `back/src/test/java/com/openclassrooms/starterjwt/**`.

- **Écrire des tests d’intégration et E2E**
  - Tests E2E front (Cypress) dans `front/cypress/e2e/*.cy.ts`.
  - Intégration back: `*IntegrationTest.java` dans `back/src/test/java/com/openclassrooms/starterjwt/integration/`.

- **Rapports de couverture (≥ 80 % sur chaque partie)**
  - Front Jest: `front/coverage/coverage-summary.json` & `front/coverage/jest/index.html`.
  - Front E2E: `front/coverage/lcov-report/index.html`.
  - Back: `back/target/site/jacoco/index.html` & `back/TEST_COVERAGE_REPORT.md`.

---

### 6. Rappel des chemins clés

- **Front**
  - Code: `front/src/app`
  - Tests Jest: `front/src/app/**/*.spec.ts`
  - Tests Cypress: `front/cypress/e2e/*.cy.ts`
  - Couverture Jest: `front/coverage/`
  - Couverture E2E: `front/coverage/lcov-report/index.html`

- **Back**
  - Code: `back/src/main/java/com/openclassrooms/starterjwt`
  - Tests: `back/src/test/java/com/openclassrooms/starterjwt`
  - Couverture JaCoCo: `back/target/site/jacoco/index.html`
  - Synthèse couverture: `back/TEST_COVERAGE_REPORT.md`


