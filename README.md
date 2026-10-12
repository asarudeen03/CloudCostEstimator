# ☁️ CloudResource Cost Estimator

A Java console-based application designed to calculate and model estimated monthly infrastructure costs for cloud compute instances and storage capacity. Built using core Java, JDBC, an embedded H2 SQL database, and structured with the Data Access Object (DAO) design pattern.

---

## 🛠️ Key Features
* **Dynamic Cost Calculation:** Computes total monthly cost based on active runtime hours, instance sizing (vCPU/RAM specs), and storage tier capacity.
* **Embedded Database Persistence:** Utilizes an embedded H2 SQL database (`cloudcost_db.mv.db`) so historical estimate records persist across application restarts.
* **Modular DAO Architecture:** Cleanly separates database access (`DAO`), business logic math (`Service`), and user interaction (`Console/App`).
* **Relational SQL Schema:** Stores instance pricing catalogs, storage tiers, and generated cost estimate logs.

---

## 💻 Tech Stack
* **Language:** Java (JDK 17+)
* **Database:** H2 Database Engine (Embedded SQL)
* **Connectivity:** JDBC (Java Database Connectivity)
* **Design Pattern:** Data Access Object (DAO) Pattern

---

## 🏗️ Project Architecture
```text
src/com/cloudcost/
├── app/
│   └── App.java                   # Main terminal-based user interface
├── dao/
│   ├── EstimateDAO.java           # DB operations for saving/retrieving estimates
│   ├── InstanceTypeDAO.java       # DB operations for compute pricing catalog
│   └── StorageTierDAO.java        # DB operations for storage tier pricing
├── model/
│   ├── Estimate.java              # Estimate domain entity
│   ├── InstanceType.java          # Compute instance model
│   └── StorageTier.java           # Storage tier model
├── service/
│   └── CostCalculationService.java # Business logic engine for cost math
└── util/
    └── DatabaseConnection.java     # Centralized JDBC connection management
