package com.cloudcost.dao;

import com.cloudcost.model.StorageTier;
import com.cloudcost.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for StorageTier.
 * Handles database operations for the STORAGE_TIERS table.
 */
public class StorageTierDAO {

    /**
     * Retrieves all storage tiers for a given cloud provider.
     *
     * @param provider Cloud provider name (e.g., 'AWS', 'Azure', 'GCP').
     * @return List of StorageTier objects.
     */
    public List<StorageTier> getStorageTiersByProvider(String provider) {
        List<StorageTier> list = new ArrayList<>();
        String sql = "SELECT id, provider, tier_name, storage_type, cost_per_gb_month, description " +
                     "FROM STORAGE_TIERS WHERE UPPER(provider) = UPPER(?) ORDER BY cost_per_gb_month ASC;";

        Connection conn = DBConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, provider);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToStorageTier(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching storage tiers: " + e.getMessage());
        }
        return list;
    }

    /**
     * Alias for getStorageTiersByProvider.
     */
    public List<StorageTier> getTiersByProvider(String provider) {
        return getStorageTiersByProvider(provider);
    }

    /**
     * Finds a storage tier by name or partial name match.
     *
     * @param tierName The tier name (e.g. 'Amazon S3 Standard' or 'S3 Standard').
     * @return StorageTier if found, null otherwise.
     */
    public StorageTier getStorageTierByName(String tierName) {
        String sql = "SELECT id, provider, tier_name, storage_type, cost_per_gb_month, description " +
                     "FROM STORAGE_TIERS WHERE UPPER(tier_name) = UPPER(?) OR UPPER(tier_name) LIKE UPPER(?) " +
                     "ORDER BY CASE WHEN UPPER(tier_name) = UPPER(?) THEN 1 ELSE 2 END LIMIT 1;";

        Connection conn = DBConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tierName);
            stmt.setString(2, "%" + tierName + "%");
            stmt.setString(3, tierName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStorageTier(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching storage tier by name: " + e.getMessage());
        }
        return null;
    }

    /**
     * Alias for getStorageTierByName.
     */
    public StorageTier getTierByName(String tierName) {
        return getStorageTierByName(tierName);
    }

    /**
     * Finds a storage tier by its ID.
     *
     * @param id The storage tier ID.
     * @return StorageTier if found, null otherwise.
     */
    public StorageTier getStorageTierById(int id) {
        String sql = "SELECT id, provider, tier_name, storage_type, cost_per_gb_month, description " +
                     "FROM STORAGE_TIERS WHERE id = ?;";

        Connection conn = DBConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStorageTier(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching storage tier by id: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all storage tiers in the database.
     *
     * @return List of all StorageTier objects.
     */
    public List<StorageTier> getAllStorageTiers() {
        List<StorageTier> list = new ArrayList<>();
        String sql = "SELECT id, provider, tier_name, storage_type, cost_per_gb_month, description " +
                     "FROM STORAGE_TIERS ORDER BY provider, tier_name;";

        Connection conn = DBConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToStorageTier(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching all storage tiers: " + e.getMessage());
        }
        return list;
    }

    /**
     * Adds a new storage tier to the database.
     */
    public boolean addStorageTier(StorageTier tier) {
        String sql = "INSERT INTO STORAGE_TIERS (provider, tier_name, storage_type, cost_per_gb_month, description) " +
                     "VALUES (?, ?, ?, ?, ?);";

        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, tier.getProvider());
            stmt.setString(2, tier.getTierName());
            stmt.setString(3, tier.getStorageType());
            stmt.setDouble(4, tier.getCostPerGbMonth());
            stmt.setString(5, tier.getDescription());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        tier.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error adding storage tier: " + e.getMessage());
        }
        return false;
    }

    private StorageTier mapResultSetToStorageTier(ResultSet rs) throws SQLException {
        return new StorageTier(
                rs.getInt("id"),
                rs.getString("provider"),
                rs.getString("tier_name"),
                rs.getString("storage_type"),
                rs.getDouble("cost_per_gb_month"),
                rs.getString("description")
        );
    }
}
