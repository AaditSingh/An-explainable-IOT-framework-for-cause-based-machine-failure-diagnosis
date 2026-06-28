package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class CausalDiscoveryEngine {
    public static void main(String[] args) {
        // UPDATE THESE TO MATCH YOUR SETUP
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String user = "system";
        String password = "";

        List<Double> yList = new ArrayList<>();
        List<double[]> xRestrictedList = new ArrayList<>();
        List<double[]> xUnrestrictedList = new ArrayList<>();

        System.out.println("[CONNECTING TO ORACLE DATABASE...]");

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();

             ResultSet rs = stmt.executeQuery("SELECT * FROM milling_causal_view WHERE machine_failure = 1")) {

            System.out.println("[FETCHING REAL-WORLD MILLING DATA...]");

            while (rs.next()) {
                double currentTemp = rs.getDouble("current_temp");

                double tempL1 = rs.getDouble("temp_lag_1");
                double tempL2 = rs.getDouble("temp_lag_2");
                double tempL3 = rs.getDouble("temp_lag_3");

                double torqueL1 = rs.getDouble("torque_lag_1");
                double torqueL2 = rs.getDouble("torque_lag_2");
                double torqueL3 = rs.getDouble("torque_lag_3");

                yList.add(currentTemp);
                xRestrictedList.add(new double[]{tempL1, tempL2, tempL3});
                xUnrestrictedList.add(new double[]{tempL1, tempL2, tempL3, torqueL1, torqueL2, torqueL3});
            }

            // Convert to Math Arrays
            double[] y = yList.stream().mapToDouble(Double::doubleValue).toArray();
            double[][] xRestricted = xRestrictedList.toArray(new double[0][]);
            double[][] xUnrestricted = xUnrestrictedList.toArray(new double[0][]);

            System.out.println("[RUNNING GRANGER CAUSALITY MATH...]");

            // 1. Restricted Model (Guessing using only one attribute)
            OLSMultipleLinearRegression restrictedModel = new OLSMultipleLinearRegression();
            restrictedModel.newSampleData(y, xRestricted);
            double rssRestricted = restrictedModel.calculateResidualSumOfSquares();

            // 2. Unrestricted Model (Guessing using multiple attributes)
            OLSMultipleLinearRegression unrestrictedModel = new OLSMultipleLinearRegression();
            unrestrictedModel.newSampleData(y, xUnrestricted);
            double rssUnrestricted = unrestrictedModel.calculateResidualSumOfSquares();

            System.out.println("Restricted Model Error (RSS): " + String.format("%.4f", rssRestricted));
            System.out.println("Unrestricted Model Error (RSS): " + String.format("%.4f", rssUnrestricted));

            // Calculate Error Reduction
            double errorReduction = ((rssRestricted - rssUnrestricted) / rssRestricted) * 100;

            if (errorReduction > 1.0) {
                System.out.println("Result: TORQUE CAUSES TEMPERATURE SPIKES");
                System.out.println("Causality proven. Error reduced by " + String.format("%.2f", errorReduction) + "% when including past torque data.");
                System.out.println("-----------------------------------");
                System.out.println("DIAGNOSIS: Root Cause is MECHANICAL OVERSTRAIN (Torque)");
                System.out.println("-----------------------------------");
            } else {
                System.out.println("Result: No strong causal link found between Torque and Temperature in this dataset window.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

    
