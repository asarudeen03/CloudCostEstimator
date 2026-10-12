package com.cloudcost.dao;

import com.cloudcost.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EstimateDAO {

    public boolean saveEstimate(String projectName, String provider, String instanceName, 
                                int instanceCount, int hoursPerMonth, String storageTierName, 
                                double storageGb, double computeCost, double storageCost, double totalCost) {
        String sql = "INSERT INTO ESTIMATES (project_name, provider, instance_name, instance_count, "
                   + "hours_per_month, storage_tier_name, storage_gb, compute_cost, storage_cost, total_cost) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, projectName);
            stmt.setString(2, provider);
            stmt.setString(3, instanceName);
            stmt.setInt(4, instanceCount);
            stmt.setInt(5, hoursPerMonth);
            stmt.setString(6, storageTierName);
            stmt.setDouble(7, storageGb);
            stmt.setDouble(8, computeCost);
            stmt.setDouble(9, storageCost);
            stmt.setDouble(10, totalCost);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error saving estimate: " + e.getMessage());
            return false;
        }
    }
}
