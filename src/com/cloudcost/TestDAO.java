package com.cloudcost;

import com.cloudcost.dao.InstanceTypeDAO;
import com.cloudcost.dao.StorageTierDAO;
import com.cloudcost.model.InstanceType;
import com.cloudcost.model.StorageTier;
import com.cloudcost.service.CostCalculationService;

import java.util.List;

public class TestDAO {
    public static void main(String[] args) {
        // 1. Fetch AWS Compute Profiles
        InstanceTypeDAO instanceDAO = new InstanceTypeDAO();
        List<InstanceType> awsInstances = instanceDAO.getInstancesByProvider("AWS");
        
        System.out.println("==========================================");
        System.out.println("🚀 AWS Compute Profiles Fetched from H2:");
        System.out.println("==========================================");
        for (InstanceType instance : awsInstances) {
            System.out.printf("%-12s | vCPU: %-2d | RAM: %-4.1f GB | Hourly: $%.4f%n",
                    instance.getInstanceName(),
                    instance.getVcpu(),
                    instance.getMemoryGb(),
                    instance.getCostPerHour());
        }

        // 2. Fetch Storage Tiers and perform Monthly Cost Estimation
        StorageTierDAO storageDAO = new StorageTierDAO();
        CostCalculationService costService = new CostCalculationService();

        // Locate 2 x t3.medium
        InstanceType t3Medium = awsInstances.stream()
                .filter(i -> "t3.medium".equalsIgnoreCase(i.getInstanceName()))
                .findFirst()
                .orElse(null);

        // Locate S3 Standard storage tier
        StorageTier s3Standard = storageDAO.getStorageTierByName("S3 Standard");
        if (s3Standard == null) {
            s3Standard = storageDAO.getStorageTierByName("Amazon S3 Standard");
        }

        int instanceCount = 2;
        int hoursPerMonth = 730;
        double storageGb = 100.0;

        System.out.println("\n==========================================");
        System.out.println("💰 Monthly Cloud Cost Estimate (730 Hours):");
        System.out.println("==========================================");

        if (t3Medium != null && s3Standard != null) {
            double computeCost = costService.calculateComputeCost(t3Medium, hoursPerMonth, instanceCount);
            double storageCost = costService.calculateStorageCost(s3Standard, storageGb);
            double totalMonthlyCost = costService.calculateTotalMonthlyCost(computeCost, storageCost);

            System.out.printf("Compute Configuration: %d x %s (%.4f/hr)%n", 
                    instanceCount, t3Medium.getInstanceName(), t3Medium.getCostPerHour());
            System.out.printf("Storage Configuration: %.0f GB %s ($%.4f/GB-mo)%n", 
                    storageGb, s3Standard.getTierName(), s3Standard.getCostPerGbMonth());
            System.out.println("------------------------------------------");
            System.out.printf("Monthly Compute Cost : $%.2f%n", computeCost);
            System.out.printf("Monthly Storage Cost : $%.2f%n", storageCost);
            System.out.println("------------------------------------------");
            System.out.printf("Total Monthly Cost   : $%.2f%n", totalMonthlyCost);
        } else {
            System.err.println("❌ Error: Could not find required instance or storage tier for estimate calculation.");
        }
        System.out.println("==========================================");
    }
}
