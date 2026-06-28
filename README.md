# An-explainable-IOT-framework-for-cause-based-machine-failure-diagnosis
This project implements a **Causal Discovery Engine** designed to perform Root Cause Analysis (RCA) on industrial CNC milling machines. It moves beyond traditional correlation dependent predictive maintenance by utilizing **Granger Causality** to mathematically prove the directional link between mechanical strain (Torque) and thermal failure (Process Temperature).

By engineering an event-windowed matrix with 30-step temporal lags, the Java-based OLS (Ordinary Least Squares) engine successfully isolates high-entropy failure states from steady-state operational noise, autonomously diagnosing mechanical overstrain as the root cause of temperature spikes.

Link of the submitted dataset: https://ieee-dataport.org/documents/granger-causal-failure-vectors-extracted-thermal-mechanical-signals-ai4i-2020-dataset

## Architecture & Technologies
* **Language:** Java 20
* **Math Engine:** Apache Commons Math (v3.6.1)
* **Database:** Oracle Database (XE) via JDBC (ojdbc8)
* **Data Pipeline:** Oracle SQL*Loader

## Data Pipeline: How to Add and Process CSV Files
* Load Data into Oracle (SQL*Loader)
* Open your terminal, navigate to the directory, and execute the control file:
* sqlldr userid=system/YOUR_PASSWORD control=load_data.ctl data=../dataset.csv log=load_data.log
* Note:The Java engine relies on a pre-calculated temporal matrix. Ensure that the milling_causal_view exists in your Oracle database.

## Running the Causality Engine
* Once the data is staged in the database view, execute the Java application.
* Ensure commons-math3-3.6.1.jar and ojdbc8.jar are on your classpath.
* Compile and run CausalDiscoveryEngine.java.
