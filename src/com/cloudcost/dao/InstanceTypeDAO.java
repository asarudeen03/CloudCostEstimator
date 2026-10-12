package com.cloudcost.dao;

import com.cloudcost.model.InstanceType;
import com.cloudcost.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InstanceTypeDAO {

    public List<InstanceType> getInstancesByProvider(String provider) {
        List<InstanceType> list = new ArrayList<>();
        String sql = "SELECT * FROM INSTANCE_TYPES WHERE provider = ? ORDER BY cost_per_hour ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, provider);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new InstanceType(
                    rs.getInt("id"),
                    rs.getString("provider"),
                    rs.getString("instance_name"),
                    rs.getString("category"),
                    rs.getInt("vcpu"),
                    rs.getDouble("memory_gb"),
                    rs.getDouble("cost_per_hour"),
                    rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching instances: " + e.getMessage());
        }
        return list;
    }
}
