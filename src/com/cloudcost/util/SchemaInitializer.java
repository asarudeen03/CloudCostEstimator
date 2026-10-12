package com.cloudcost.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class to initialize the database schema and populate seed data
 * for CloudCostEstimator (INSTANCE_TYPES, STORAGE_TIERS, ESTIMATES).
 */
public class SchemaInitializer {

    // SQL Statements to drop existing tables
    private static final String DROP_ESTIMATES_TABLE = 
            "DROP TABLE IF EXISTS ESTIMATES CASCADE;";
    private static final String DROP_STORAGE_TIERS_TABLE = 
            "DROP TABLE IF EXISTS STORAGE_TIERS CASCADE;";
    private static final String DROP_INSTANCE_TYPES_TABLE = 
            "DROP TABLE IF EXISTS INSTANCE_TYPES CASCADE;";

    // SQL Statements to create tables
    private static final String CREATE_INSTANCE_TYPES_TABLE = 
            "CREATE TABLE IF NOT EXISTS INSTANCE_TYPES (" +
            "    id INT AUTO_INCREMENT PRIMARY KEY," +
            "    provider VARCHAR(50) NOT NULL," +
            "    instance_name VARCHAR(100) NOT NULL UNIQUE," +
            "    category VARCHAR(50) NOT NULL," +
            "    vcpu INT NOT NULL," +
            "    memory_gb DOUBLE NOT NULL," +
            "    cost_per_hour DOUBLE NOT NULL," +
            "    description VARCHAR(255)" +
            ");";

    private static final String CREATE_STORAGE_TIERS_TABLE = 
            "CREATE TABLE IF NOT EXISTS STORAGE_TIERS (" +
            "    id INT AUTO_INCREMENT PRIMARY KEY," +
            "    provider VARCHAR(50) NOT NULL," +
            "    tier_name VARCHAR(100) NOT NULL UNIQUE," +
            "    storage_type VARCHAR(50) NOT NULL," +
            "    cost_per_gb_month DOUBLE NOT NULL," +
            "    description VARCHAR(255)" +
            ");";

    private static final String CREATE_ESTIMATES_TABLE = 
            "CREATE TABLE IF NOT EXISTS ESTIMATES (" +
            "    id INT AUTO_INCREMENT PRIMARY KEY," +
            "    project_name VARCHAR(100) NOT NULL," +
            "    provider VARCHAR(50) NOT NULL," +
            "    instance_type_id INT," +
            "    instance_name VARCHAR(100) NOT NULL," +
            "    instance_count INT NOT NULL DEFAULT 1," +
            "    hours_per_month INT NOT NULL DEFAULT 730," +
            "    storage_tier_id INT," +
            "    storage_tier_name VARCHAR(100)," +
            "    storage_gb DOUBLE NOT NULL DEFAULT 0.0," +
            "    compute_cost DOUBLE NOT NULL," +
            "    storage_cost DOUBLE NOT NULL," +
            "    total_cost DOUBLE NOT NULL," +
            "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "    FOREIGN KEY (instance_type_id) REFERENCES INSTANCE_TYPES(id) ON DELETE SET NULL," +
            "    FOREIGN KEY (storage_tier_id) REFERENCES STORAGE_TIERS(id) ON DELETE SET NULL" +
            ");";

    /**
     * Drops existing tables (if any), recreates schema, and seeds initial data.
     */
    public static void initializeDatabase() {
        System.out.println("==================================================");
        System.out.println("🚀 Initializing CloudCostEstimator Database...");
        System.out.println("==================================================");

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            System.err.println("❌ ERROR: Cannot proceed without database connection.");
            return;
        }

        try {
            dropTables(conn);
            createTables(conn);
            seedInstanceTypes(conn);
            seedStorageTiers(conn);
            seedEstimates(conn);
            printVerificationSummary(conn);
            System.out.println("==================================================");
            System.out.println("✅ SUCCESS: Database schema and seed data initialized!");
            System.out.println("==================================================");
        } catch (SQLException e) {
            System.err.println("❌ ERROR: Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void dropTables(Connection conn) throws SQLException {
        System.out.println("\n🔄 Step 1: Cleaning up existing tables...");
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(DROP_ESTIMATES_TABLE);
            stmt.execute(DROP_STORAGE_TIERS_TABLE);
            stmt.execute(DROP_INSTANCE_TYPES_TABLE);
            System.out.println("   ✓ Old tables dropped (if existed).");
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        System.out.println("\n🏗️ Step 2: Creating database tables...");
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_INSTANCE_TYPES_TABLE);
            System.out.println("   ✓ Table created: INSTANCE_TYPES");

            stmt.execute(CREATE_STORAGE_TIERS_TABLE);
            System.out.println("   ✓ Table created: STORAGE_TIERS");

            stmt.execute(CREATE_ESTIMATES_TABLE);
            System.out.println("   ✓ Table created: ESTIMATES");
        }
    }

    private static void seedInstanceTypes(Connection conn) throws SQLException {
        System.out.println("\n🌱 Step 3: Seeding INSTANCE_TYPES table...");
        String insertSql = "INSERT INTO INSTANCE_TYPES (provider, instance_name, category, vcpu, memory_gb, cost_per_hour, description) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?);";

        Object[][] instanceData = {
            // AWS Instances
            {"AWS", "t3.nano", "General Purpose", 2, 0.5, 0.0052, "AWS burstable entry-level instance"},
            {"AWS", "t3.micro", "General Purpose", 2, 1.0, 0.0104, "AWS burstable micro instance"},
            {"AWS", "t3.small", "General Purpose", 2, 2.0, 0.0208, "AWS burstable small instance"},
            {"AWS", "t3.medium", "General Purpose", 2, 4.0, 0.0416, "AWS burstable medium instance"},
            {"AWS", "t3.large", "General Purpose", 2, 8.0, 0.0832, "AWS burstable large instance"},
            {"AWS", "t3.xlarge", "General Purpose", 4, 16.0, 0.1664, "AWS burstable extra-large instance"},
            {"AWS", "m5.large", "General Purpose", 2, 8.0, 0.0960, "AWS general purpose standard compute"},
            {"AWS", "m5.xlarge", "General Purpose", 4, 16.0, 0.1920, "AWS general purpose extra-large"},
            {"AWS", "c5.large", "Compute Optimized", 2, 4.0, 0.0850, "AWS compute optimized instance"},
            {"AWS", "c5.xlarge", "Compute Optimized", 4, 8.0, 0.1700, "AWS compute optimized extra-large"},
            {"AWS", "r5.large", "Memory Optimized", 2, 16.0, 0.1260, "AWS memory optimized instance"},
            {"AWS", "r5.xlarge", "Memory Optimized", 4, 32.0, 0.2520, "AWS memory optimized extra-large"},

            // Azure Instances
            {"Azure", "Standard_B1s", "General Purpose", 1, 1.0, 0.0104, "Azure B-series burstable VM"},
            {"Azure", "Standard_B2s", "General Purpose", 2, 4.0, 0.0416, "Azure B-series burstable VM"},
            {"Azure", "Standard_D2s_v5", "General Purpose", 2, 8.0, 0.0960, "Azure D-series general purpose VM"},
            {"Azure", "Standard_D4s_v5", "General Purpose", 4, 16.0, 0.1920, "Azure D-series standard VM"},
            {"Azure", "Standard_F2s_v2", "Compute Optimized", 2, 4.0, 0.0850, "Azure F-series compute optimized VM"},
            {"Azure", "Standard_F4s_v2", "Compute Optimized", 4, 8.0, 0.1690, "Azure F-series compute optimized VM"},
            {"Azure", "Standard_E2s_v5", "Memory Optimized", 2, 16.0, 0.1260, "Azure E-series memory optimized VM"},
            {"Azure", "Standard_E4s_v5", "Memory Optimized", 4, 32.0, 0.2520, "Azure E-series memory optimized VM"},

            // GCP Instances
            {"GCP", "e2-micro", "General Purpose", 2, 1.0, 0.0084, "Google Cloud cost-effective micro VM"},
            {"GCP", "e2-small", "General Purpose", 2, 2.0, 0.0168, "Google Cloud cost-effective small VM"},
            {"GCP", "e2-medium", "General Purpose", 2, 4.0, 0.0336, "Google Cloud cost-effective medium VM"},
            {"GCP", "e2-standard-2", "General Purpose", 2, 8.0, 0.0670, "Google Cloud standard 2 vCPU VM"},
            {"GCP", "e2-standard-4", "General Purpose", 4, 16.0, 0.1340, "Google Cloud standard 4 vCPU VM"},
            {"GCP", "c2-standard-4", "Compute Optimized", 4, 16.0, 0.2088, "Google Cloud compute-optimized VM"},
            {"GCP", "n2-highmem-2", "Memory Optimized", 2, 16.0, 0.1310, "Google Cloud high-memory VM"},
            {"GCP", "n2-highmem-4", "Memory Optimized", 4, 32.0, 0.2620, "Google Cloud high-memory VM"}
        };

        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            for (Object[] row : instanceData) {
                pstmt.setString(1, (String) row[0]);
                pstmt.setString(2, (String) row[1]);
                pstmt.setString(3, (String) row[2]);
                pstmt.setInt(4, (Integer) row[3]);
                pstmt.setDouble(5, ((Number) row[4]).doubleValue());
                pstmt.setDouble(6, ((Number) row[5]).doubleValue());
                pstmt.setString(7, (String) row[6]);
                pstmt.addBatch();
            }
            int[] results = pstmt.executeBatch();
            System.out.println("   ✓ Seeded " + results.length + " rows into INSTANCE_TYPES.");
        }
    }

    private static void seedStorageTiers(Connection conn) throws SQLException {
        System.out.println("\n🌱 Step 4: Seeding STORAGE_TIERS table...");
        String insertSql = "INSERT INTO STORAGE_TIERS (provider, tier_name, storage_type, cost_per_gb_month, description) " +
                           "VALUES (?, ?, ?, ?, ?);";

        Object[][] storageData = {
            // AWS Storage
            {"AWS", "General Purpose SSD (gp3)", "Block Storage", 0.080, "AWS EBS gp3 baseline solid state drive"},
            {"AWS", "Provisioned IOPS SSD (io2)", "Block Storage", 0.125, "AWS EBS io2 high performance SSD"},
            {"AWS", "Throughput Optimized HDD (st1)", "Block Storage", 0.045, "AWS EBS st1 frequent throughput HDD"},
            {"AWS", "Cold HDD (sc1)", "Block Storage", 0.015, "AWS EBS sc1 lowest-cost block storage"},
            {"AWS", "Amazon S3 Standard", "Object Storage", 0.023, "AWS standard object storage"},
            {"AWS", "Amazon S3 Glacier Flexible", "Archive Storage", 0.004, "AWS long-term archive storage"},

            // Azure Storage
            {"Azure", "Standard HDD Managed Disks", "Block Storage", 0.040, "Azure standard magnetic HDD"},
            {"Azure", "Standard SSD Managed Disks", "Block Storage", 0.075, "Azure standard SSD disk"},
            {"Azure", "Premium SSD Managed Disks", "Block Storage", 0.135, "Azure high-performance SSD disk"},
            {"Azure", "Blob Storage (Hot)", "Object Storage", 0.018, "Azure hot tier blob storage"},
            {"Azure", "Blob Storage (Cool)", "Object Storage", 0.010, "Azure cool tier blob storage"},
            {"Azure", "Blob Storage (Archive)", "Archive Storage", 0.002, "Azure archive tier blob storage"},

            // GCP Storage
            {"GCP", "Standard Persistent Disk", "Block Storage", 0.040, "Google standard HDD persistent disk"},
            {"GCP", "Balanced Persistent Disk", "Block Storage", 0.100, "Google balanced SSD persistent disk"},
            {"GCP", "SSD Persistent Disk", "Block Storage", 0.170, "Google extreme performance SSD"},
            {"GCP", "Cloud Storage Standard", "Object Storage", 0.020, "Google standard object storage"},
            {"GCP", "Cloud Storage Nearline", "Object Storage", 0.010, "Google nearline infrequently accessed storage"},
            {"GCP", "Cloud Storage Coldline", "Archive Storage", 0.004, "Google cold archival storage"}
        };

        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            for (Object[] row : storageData) {
                pstmt.setString(1, (String) row[0]);
                pstmt.setString(2, (String) row[1]);
                pstmt.setString(3, (String) row[2]);
                pstmt.setDouble(4, ((Number) row[3]).doubleValue());
                pstmt.setString(5, (String) row[4]);
                pstmt.addBatch();
            }
            int[] results = pstmt.executeBatch();
            System.out.println("   ✓ Seeded " + results.length + " rows into STORAGE_TIERS.");
        }
    }

    private static void seedEstimates(Connection conn) throws SQLException {
        System.out.println("\n🌱 Step 5: Seeding ESTIMATES table...");

        String insertSql = "INSERT INTO ESTIMATES (" +
                "project_name, provider, instance_type_id, instance_name, instance_count, " +
                "hours_per_month, storage_tier_id, storage_tier_name, storage_gb, " +
                "compute_cost, storage_cost, total_cost) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        // Sample initial estimates
        Object[][] sampleEstimates = {
            {"Production Web Application", "AWS", "t3.medium", 2, 730, "General Purpose SSD (gp3)", 100.0},
            {"Dev/Test Microservices", "Azure", "Standard_B2s", 2, 400, "Standard SSD Managed Disks", 150.0},
            {"Big Data Analytics Pipeline", "GCP", "e2-standard-4", 3, 500, "Balanced Persistent Disk", 500.0},
            {"Enterprise Database Server", "AWS", "r5.xlarge", 1, 730, "Provisioned IOPS SSD (io2)", 1000.0},
            {"Media Archival & Web Backup", "AWS", "t3.micro", 1, 730, "Amazon S3 Standard", 2000.0}
        };

        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            for (Object[] est : sampleEstimates) {
                String projectName = (String) est[0];
                String provider = (String) est[1];
                String instanceName = (String) est[2];
                int instanceCount = (Integer) est[3];
                int hoursPerMonth = (Integer) est[4];
                String storageTierName = (String) est[5];
                double storageGb = ((Number) est[6]).doubleValue();

                // Lookup instance type id and hourly rate
                int instanceId = -1;
                double hourlyRate = 0.0;
                try (PreparedStatement q = conn.prepareStatement("SELECT id, cost_per_hour FROM INSTANCE_TYPES WHERE instance_name = ?")) {
                    q.setString(1, instanceName);
                    try (ResultSet rs = q.executeQuery()) {
                        if (rs.next()) {
                            instanceId = rs.getInt("id");
                            hourlyRate = rs.getDouble("cost_per_hour");
                        }
                    }
                }

                // Lookup storage tier id and rate
                int storageTierId = -1;
                double storageRate = 0.0;
                try (PreparedStatement q = conn.prepareStatement("SELECT id, cost_per_gb_month FROM STORAGE_TIERS WHERE tier_name = ?")) {
                    q.setString(1, storageTierName);
                    try (ResultSet rs = q.executeQuery()) {
                        if (rs.next()) {
                            storageTierId = rs.getInt("id");
                            storageRate = rs.getDouble("cost_per_gb_month");
                        }
                    }
                }

                double computeCost = Math.round(instanceCount * hoursPerMonth * hourlyRate * 100.0) / 100.0;
                double storageCost = Math.round(storageGb * storageRate * 100.0) / 100.0;
                double totalCost = Math.round((computeCost + storageCost) * 100.0) / 100.0;

                pstmt.setString(1, projectName);
                pstmt.setString(2, provider);
                if (instanceId != -1) {
                    pstmt.setInt(3, instanceId);
                } else {
                    pstmt.setNull(3, java.sql.Types.INTEGER);
                }
                pstmt.setString(4, instanceName);
                pstmt.setInt(5, instanceCount);
                pstmt.setInt(6, hoursPerMonth);
                if (storageTierId != -1) {
                    pstmt.setInt(7, storageTierId);
                } else {
                    pstmt.setNull(7, java.sql.Types.INTEGER);
                }
                pstmt.setString(8, storageTierName);
                pstmt.setDouble(9, storageGb);
                pstmt.setDouble(10, computeCost);
                pstmt.setDouble(11, storageCost);
                pstmt.setDouble(12, totalCost);
                pstmt.addBatch();
            }
            int[] results = pstmt.executeBatch();
            System.out.println("   ✓ Seeded " + results.length + " sample rows into ESTIMATES.");
        }
    }

    private static void printVerificationSummary(Connection conn) throws SQLException {
        System.out.println("\n📊 Step 6: Verifying Seeded Data...");
        try (Statement stmt = conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM INSTANCE_TYPES")) {
                if (rs.next()) {
                    System.out.println("   - INSTANCE_TYPES total rows : " + rs.getInt(1));
                }
            }
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM STORAGE_TIERS")) {
                if (rs.next()) {
                    System.out.println("   - STORAGE_TIERS total rows  : " + rs.getInt(1));
                }
            }
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM ESTIMATES")) {
                if (rs.next()) {
                    System.out.println("   - ESTIMATES total rows      : " + rs.getInt(1));
                }
            }

            System.out.println("\n🔎 Sample Estimates Summary:");
            System.out.println(String.format("   %-30s | %-6s | %-16s | %-12s | %-12s | %-12s", 
                    "Project", "Cloud", "Instance", "Compute($)", "Storage($)", "Total($)"));
            System.out.println("   " + "-".repeat(95));
            try (ResultSet rs = stmt.executeQuery(
                    "SELECT project_name, provider, instance_name, compute_cost, storage_cost, total_cost FROM ESTIMATES")) {
                while (rs.next()) {
                    System.out.println(String.format("   %-30s | %-6s | %-16s | $%-11.2f | $%-11.2f | $%-11.2f",
                            rs.getString("project_name"),
                            rs.getString("provider"),
                            rs.getString("instance_name"),
                            rs.getDouble("compute_cost"),
                            rs.getDouble("storage_cost"),
                            rs.getDouble("total_cost")));
                }
            }
        }
    }

    /**
     * Entry point to compile and run SchemaInitializer directly.
     */
    public static void main(String[] args) {
        initializeDatabase();
    }
}
