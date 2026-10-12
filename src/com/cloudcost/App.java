package com.cloudcost;

import com.cloudcost.dao.InstanceTypeDAO;
import com.cloudcost.dao.StorageTierDAO;
import com.cloudcost.dao.EstimateDAO;
import com.cloudcost.model.InstanceType;
import com.cloudcost.model.StorageTier;
import com.cloudcost.service.CostCalculationService;

import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        InstanceTypeDAO instanceDAO = new InstanceTypeDAO();
        StorageTierDAO storageDAO = new StorageTierDAO();
        EstimateDAO estimateDAO = new EstimateDAO();
        CostCalculationService costService = new CostCalculationService();

        System.out.println("==========================================");
        System.out.println("☁️  WELCOME TO CLOUD COST ESTIMATOR");
        System.out.println("==========================================");

        System.out.print("Enter Project Name: ");
        String projectName = scanner.nextLine();

        System.out.print("Select Cloud Provider (AWS / Azure / GCP): ");
        String provider = scanner.nextLine().trim().toUpperCase();

        List<InstanceType> instances = instanceDAO.getInstancesByProvider(provider);
        if (instances.isEmpty()) {
            System.out.println("❌ No compute instances found for provider: " + provider);
            return;
        }

        System.out.println("\nAvailable Compute Instances:");
        for (int i = 0; i < instances.size(); i++) {
            InstanceType inst = instances.get(i);
            System.out.printf("[%d] %-12s | vCPU: %d | RAM: %.1f GB | $%.4f/hr%n", 
                i + 1, inst.getInstanceName(), inst.getVcpu(), inst.getMemoryGb(), inst.getCostPerHour());
        }

        System.out.print("Select Instance Number: ");
        int instChoice = scanner.nextInt() - 1;
        InstanceType selectedInstance = instances.get(instChoice);

        System.out.print("Enter Instance Count: ");
        int instanceCount = scanner.nextInt();

        System.out.print("Enter Active Hours per Month (e.g., 730): ");
        int hoursPerMonth = scanner.nextInt();

        List<StorageTier> storageTiers = storageDAO.getStorageTiersByProvider(provider);
        System.out.println("\nAvailable Storage Tiers:");
        for (int i = 0; i < storageTiers.size(); i++) {
            StorageTier st = storageTiers.get(i);
            System.out.printf("[%d] %-30s | $%.4f/GB-mo%n", i + 1, st.getTierName(), st.getCostPerGbMonth());
        }

        System.out.print("Select Storage Tier Number: ");
        int storageChoice = scanner.nextInt() - 1;
        StorageTier selectedStorage = storageTiers.get(storageChoice);

        System.out.print("Enter Storage Capacity (GB): ");
        double storageGb = scanner.nextDouble();

        double computeCost = costService.calculateComputeMonthlyCost(selectedInstance, instanceCount, hoursPerMonth);
        double storageCost = costService.calculateStorageMonthlyCost(selectedStorage, storageGb);
        double totalCost = computeCost + storageCost;

        System.out.println("\n==========================================");
        System.out.println("📊 ESTIMATE SUMMARY FOR: " + projectName);
        System.out.println("==========================================");
        System.out.printf("Compute (%d x %s): $%.2f%n", instanceCount, selectedInstance.getInstanceName(), computeCost);
        System.out.printf("Storage (%.0f GB %s): $%.2f%n", storageGb, selectedStorage.getTierName(), storageCost);
        System.out.println("------------------------------------------");
        System.out.printf("Total Monthly Cost: $%.2f%n", totalCost);
        System.out.println("==========================================");

        boolean saved = estimateDAO.saveEstimate(projectName, provider, selectedInstance.getInstanceName(), 
                instanceCount, hoursPerMonth, selectedStorage.getTierName(), storageGb, computeCost, storageCost, totalCost);

        if (saved) {
            System.out.println("✅ Estimate successfully saved to H2 database!");
        }

        scanner.close();
    }
}
