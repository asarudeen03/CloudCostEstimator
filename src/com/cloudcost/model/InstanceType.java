package com.cloudcost.model;

import java.util.Objects;

/**
 * Model class representing a Cloud Compute Instance Type
 * corresponding to the INSTANCE_TYPES database table.
 */
public class InstanceType {

    private int id;
    private String provider;
    private String instanceName;
    private String category;
    private int vcpu;
    private double memoryGb;
    private double costPerHour;
    private String description;

    /**
     * Default no-argument constructor.
     */
    public InstanceType() {
    }

    /**
     * Constructor without id (useful for creating new records before database persistence).
     */
    public InstanceType(String provider, String instanceName, String category, 
                        int vcpu, double memoryGb, double costPerHour, String description) {
        this.provider = provider;
        this.instanceName = instanceName;
        this.category = category;
        this.vcpu = vcpu;
        this.memoryGb = memoryGb;
        this.costPerHour = costPerHour;
        this.description = description;
    }

    /**
     * Full constructor including id.
     */
    public InstanceType(int id, String provider, String instanceName, String category, 
                        int vcpu, double memoryGb, double costPerHour, String description) {
        this.id = id;
        this.provider = provider;
        this.instanceName = instanceName;
        this.category = category;
        this.vcpu = vcpu;
        this.memoryGb = memoryGb;
        this.costPerHour = costPerHour;
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

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    // Alias for instanceName
    public String getName() {
        return instanceName;
    }

    public void setName(String name) {
        this.instanceName = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getVcpu() {
        return vcpu;
    }

    public void setVcpu(int vcpu) {
        this.vcpu = vcpu;
    }

    // Alias for vcpu
    public int getVcpus() {
        return vcpu;
    }

    public void setVcpus(int vcpus) {
        this.vcpu = vcpus;
    }

    public double getMemoryGb() {
        return memoryGb;
    }

    public void setMemoryGb(double memoryGb) {
        this.memoryGb = memoryGb;
    }

    // Alias for memoryGb
    public double getRamGb() {
        return memoryGb;
    }

    public void setRamGb(double ramGb) {
        this.memoryGb = ramGb;
    }

    public double getCostPerHour() {
        return costPerHour;
    }

    public void setCostPerHour(double costPerHour) {
        this.costPerHour = costPerHour;
    }

    // Alias for costPerHour
    public double getHourlyRate() {
        return costPerHour;
    }

    public void setHourlyRate(double hourlyRate) {
        this.costPerHour = hourlyRate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Calculates the estimated monthly compute cost for a given number of hours and quantity.
     *
     * @param hoursPerMonth Operating hours per month (typically ~730 for 24/7).
     * @param quantity      Number of instances.
     * @return Total compute cost rounded to two decimal places.
     */
    public double calculateMonthlyCost(int hoursPerMonth, int quantity) {
        return Math.round(this.costPerHour * hoursPerMonth * quantity * 100.0) / 100.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstanceType that = (InstanceType) o;
        return id == that.id &&
                vcpu == that.vcpu &&
                Double.compare(that.memoryGb, memoryGb) == 0 &&
                Double.compare(that.costPerHour, costPerHour) == 0 &&
                Objects.equals(provider, that.provider) &&
                Objects.equals(instanceName, that.instanceName) &&
                Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, provider, instanceName, category, vcpu, memoryGb, costPerHour);
    }

    @Override
    public String toString() {
        return String.format("%s %s [%s | %d vCPU | %.1f GB | $%.4f/hr]",
                provider, instanceName, category, vcpu, memoryGb, costPerHour);
    }
}
