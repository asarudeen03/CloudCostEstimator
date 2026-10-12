package com.cloudcost.service;

import com.cloudcost.model.InstanceType;
import com.cloudcost.model.StorageTier;

/**
 * Service responsible for performing cloud compute, storage,
 * and total monthly cost estimations.
 */
public class CostCalculationService {

    /**
     * Calculates the monthly compute cost based on an InstanceType, quantity, and operating hours.
     *
     * @param instance       InstanceType compute profile.
     * @param instanceCount  Number of instances.
     * @param hoursPerMonth  Number of operating hours in the month (e.g. 730).
     * @return Calculated compute cost rounded to 2 decimal places.
     */
    public double calculateComputeMonthlyCost(InstanceType instance, int instanceCount, int hoursPerMonth) {
        if (instance == null) {
            return 0.0;
        }
        double total = instance.getCostPerHour() * hoursPerMonth * instanceCount;
        return Math.round(total * 100.0) / 100.0;
    }

    /**
     * Calculates the monthly storage cost based on a StorageTier and capacity in GB.
     *
     * @param storageTier StorageTier profile.
     * @param storageGb   Allocated storage in Gigabytes.
     * @return Calculated storage cost rounded to 2 decimal places.
     */
    public double calculateStorageMonthlyCost(StorageTier storageTier, double storageGb) {
        if (storageTier == null) {
            return 0.0;
        }
        double total = storageTier.getCostPerGbMonth() * storageGb;
        return Math.round(total * 100.0) / 100.0;
    }

    /**
     * Calculates the monthly compute cost based on an InstanceType, operating hours, and quantity.
     *
     * @param instance       InstanceType compute profile.
     * @param hoursPerMonth  Number of operating hours in the month (e.g. 730).
     * @param instanceCount  Number of instances.
     * @return Calculated compute cost rounded to 2 decimal places.
     */
    public double calculateComputeCost(InstanceType instance, int hoursPerMonth, int instanceCount) {
        return calculateComputeMonthlyCost(instance, instanceCount, hoursPerMonth);
    }

    /**
     * Calculates the monthly compute cost based on hourly rate, operating hours, and quantity.
     *
     * @param costPerHour    Hourly cost in USD.
     * @param hoursPerMonth  Number of operating hours in the month.
     * @param instanceCount  Number of instances.
     * @return Calculated compute cost rounded to 2 decimal places.
     */
    public double calculateComputeCost(double costPerHour, int hoursPerMonth, int instanceCount) {
        double total = costPerHour * hoursPerMonth * instanceCount;
        return Math.round(total * 100.0) / 100.0;
    }

    /**
     * Calculates the monthly storage cost based on a StorageTier and capacity in GB.
     *
     * @param storageTier StorageTier profile.
     * @param storageGb   Allocated storage in Gigabytes.
     * @return Calculated storage cost rounded to 2 decimal places.
     */
    public double calculateStorageCost(StorageTier storageTier, double storageGb) {
        return calculateStorageMonthlyCost(storageTier, storageGb);
    }

    /**
     * Calculates the monthly storage cost based on the GB/month rate and capacity in GB.
     *
     * @param costPerGbMonth Rate per GB-month in USD.
     * @param storageGb      Allocated storage in Gigabytes.
     * @return Calculated storage cost rounded to 2 decimal places.
     */
    public double calculateStorageCost(double costPerGbMonth, double storageGb) {
        double total = costPerGbMonth * storageGb;
        return Math.round(total * 100.0) / 100.0;
    }

    /**
     * Combines compute and storage costs into a total monthly estimate.
     *
     * @param computeCost Monthly compute cost.
     * @param storageCost Monthly storage cost.
     * @return Total monthly cost rounded to 2 decimal places.
     */
    public double calculateTotalMonthlyCost(double computeCost, double storageCost) {
        return Math.round((computeCost + storageCost) * 100.0) / 100.0;
    }

    /**
     * Full cost calculation for an instance specification and storage specification.
     *
     * @param instance      InstanceType compute profile.
     * @param hoursPerMonth Operating hours per month.
     * @param instanceCount Number of instances.
     * @param storageTier   StorageTier profile.
     * @param storageGb     Allocated storage in Gigabytes.
     * @return Total monthly cost rounded to 2 decimal places.
     */
    public double calculateTotalMonthlyCost(InstanceType instance, int hoursPerMonth, int instanceCount,
                                           StorageTier storageTier, double storageGb) {
        double compute = calculateComputeCost(instance, hoursPerMonth, instanceCount);
        double storage = calculateStorageCost(storageTier, storageGb);
        return calculateTotalMonthlyCost(compute, storage);
    }
}
