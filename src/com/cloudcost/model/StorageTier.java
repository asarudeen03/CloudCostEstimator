package com.cloudcost.model;

import java.util.Objects;

/**
 * Model class representing a Cloud Storage Tier
 * corresponding to the STORAGE_TIERS database table.
 */
public class StorageTier {

    private int id;
    private String provider;
    private String tierName;
    private String storageType;
    private double costPerGbMonth;
    private String description;

    public StorageTier() {
    }

    public StorageTier(String provider, String tierName, String storageType, 
                       double costPerGbMonth, String description) {
        this.provider = provider;
        this.tierName = tierName;
        this.storageType = storageType;
        this.costPerGbMonth = costPerGbMonth;
        this.description = description;
    }

    public StorageTier(int id, String provider, String tierName, String storageType, 
                       double costPerGbMonth, String description) {
        this.id = id;
        this.provider = provider;
        this.tierName = tierName;
        this.storageType = storageType;
        this.costPerGbMonth = costPerGbMonth;
        this.description = description;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getTierName() {
        return tierName;
    }

    public void setTierName(String tierName) {
        this.tierName = tierName;
    }

    public String getName() {
        return tierName;
    }

    public void setName(String name) {
        this.tierName = name;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public String getType() {
        return storageType;
    }

    public void setType(String type) {
        this.storageType = type;
    }

    public double getCostPerGbMonth() {
        return costPerGbMonth;
    }

    public void setCostPerGbMonth(double costPerGbMonth) {
        this.costPerGbMonth = costPerGbMonth;
    }

    public double getCostPerGb() {
        return costPerGbMonth;
    }

    public void setCostPerGb(double costPerGb) {
        this.costPerGbMonth = costPerGb;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Calculates the estimated monthly storage cost for a given storage amount in GB.
     *
     * @param storageGb Storage amount in Gigabytes.
     * @return Total storage cost rounded to two decimal places.
     */
    public double calculateMonthlyCost(double storageGb) {
        return Math.round(this.costPerGbMonth * storageGb * 100.0) / 100.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StorageTier that = (StorageTier) o;
        return id == that.id &&
                Double.compare(that.costPerGbMonth, costPerGbMonth) == 0 &&
                Objects.equals(provider, that.provider) &&
                Objects.equals(tierName, that.tierName) &&
                Objects.equals(storageType, that.storageType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, provider, tierName, storageType, costPerGbMonth);
    }

    @Override
    public String toString() {
        return String.format("%s %s [%s | $%.4f/GB-month]",
                provider, tierName, storageType, costPerGbMonth);
    }
}
