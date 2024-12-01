/**
 * @author <Nguyen Ngoc Dung - s3978535>
 */
package com.rentalsystem.model;

import java.util.*;

/**
 * Represents a rental agreement in the rental system.
 */
public class RentalAgreement {
    private String agreementId;
    private Property property;
    private Tenant mainTenant;
    private List<Tenant> subTenants;
    private Owner owner;
    private Host host;
    private Date startDate;
    private Date endDate;
    private double rentAmount;
    private RentalPeriod rentalPeriod;
    private Status status;
    private List<Payment> payments;

    /**
     * Enum representing the possible rental periods.
     */
    public enum RentalPeriod {
        DAILY, WEEKLY, FORTNIGHTLY, MONTHLY
    }

    /**
     * Enum representing the possible statuses of a rental agreement.
     */
    public enum Status {
        NEW, ACTIVE, COMPLETED
    }

    /**
     * Constructs a new RentalAgreement.
     * @param agreementId Unique identifier for the agreement
     * @param property The property being rented
     * @param mainTenant The main tenant of the agreement
     * @param owner The owner of the property
     * @param host The host managing the property
     * @param startDate The start date of the agreement
     * @param endDate The end date of the agreement
     * @param rentAmount The rent amount
     * @param rentalPeriod The rental period
     */
    public RentalAgreement(String agreementId, Property property, Tenant mainTenant, Owner owner, Host host,
                           Date startDate, Date endDate, double rentAmount, RentalPeriod rentalPeriod) {
        this.agreementId = agreementId;
        this.property = property;
        this.mainTenant = mainTenant;
        this.owner = owner;
        this.host = host;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rentAmount = rentAmount;
        this.rentalPeriod = rentalPeriod;
        this.subTenants = new ArrayList<>();
        this.status = Status.NEW;
        this.payments = new ArrayList<>();

        // Add main tenant and rental agreement links
        property.addTenant(mainTenant);
        property.addRentalAgreement(this);
        mainTenant.addRentalAgreement(this);
        host.addManagedAgreement(this);
        owner.addRentalAgreement(this);
    }

    /**
     * Checks if the rental agreement is currently active.
     * @return true if the agreement is active, false otherwise
     */
    public boolean isCurrentlyActive() {
        Date currentDate = new Date();
        return currentDate.after(startDate) && currentDate.before(endDate) && status == Status.ACTIVE;
    }

    // Getters and setters
    public String getAgreementId() { return agreementId; }
    public Property getProperty() { return property; }
    public Tenant getMainTenant() { return mainTenant; }
    public Owner getOwner() { return owner; }
    public Host getHost() { return host; }
    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public double getRentAmount() { return rentAmount; }
    public void setRentAmount(double rentAmount) { this.rentAmount = rentAmount; }
    public RentalPeriod getRentalPeriod() { return rentalPeriod; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public List<Tenant> getSubTenants() { return new ArrayList<>(subTenants); }

    /**
     * Gets all tenants, including the main tenant and sub-tenants.
     * @return A list of all tenants, with the main tenant as the first element.
     */
    public List<Tenant> getAllTenants() {
        List<Tenant> allTenants = new ArrayList<>();
        allTenants.add(mainTenant);
        allTenants.addAll(subTenants);
        return allTenants;
    }

    /**
     * Sets the property for this rental agreement and updates related entities.
     * @param newProperty The new property for this agreement
     */
    public void setProperty(Property newProperty) {
        if (this.property != null) {
            this.property.removeTenant(mainTenant);
            for (Tenant subTenant : subTenants) {
                this.property.removeTenant(subTenant);
            }
        }
        this.property = newProperty;
        if (newProperty != null) {
            newProperty.addTenant(mainTenant);
            for (Tenant subTenant : subTenants) {
                newProperty.addTenant(subTenant);
            }
        }
    }

    /**
     * Adds a sub-tenant to the rental agreement.
     * @param subTenant The sub-tenant to be added
     */
    public void addSubTenant(Tenant subTenant) {
        if (mainTenant == subTenant) {
            System.out.println("Main tenant cannot be added as a sub-tenant.");
            return;
        }
        if (!subTenants.contains(subTenant)) {
            subTenants.add(subTenant);
            subTenant.addRentalAgreement(this);
            property.addTenant(subTenant);
        } else {
            System.out.println("Sub-tenant already added to this agreement.");
        }
    }

    /**
     * Removes a sub-tenant from the rental agreement.
     * @param subTenantId The ID of the sub-tenant to be removed
     */
    public void removeSubTenant(String subTenantId) {
        subTenants.removeIf(tenant -> {
            if (tenant.getId().equals(subTenantId)) {
                tenant.removeRentalAgreement(this);
                property.removeTenant(tenant);
                return true;
            }
            return false;
        });
    }

    /**
     * Adds a payment to the rental agreement.
     * @param payment The payment to be added
     */
    public void addPayment(Payment payment) {
        payments.add(payment);
    }

    /**
     * Retrieves the list of payments for this rental agreement.
     * @return A new ArrayList containing the payments
     */
    public List<Payment> getPayments() {
        return new ArrayList<>(payments);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RentalAgreement that = (RentalAgreement) o;
        return Objects.equals(agreementId, that.agreementId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agreementId);
    }

    @Override
    public String toString() {
        return "RentalAgreement{" +
                "agreementId='" + agreementId + '\'' +
                ", property=" + property.getPropertyId() +
                ", mainTenant=" + mainTenant.getId() + " - " + mainTenant.getFullName() +
                ", subTenants=" + subTenants.size() +
                ", owner=" + owner.getId() + " - " + owner.getFullName() +
                ", host=" + host.getId() + " - " + host.getFullName() +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", rentAmount=" + rentAmount +
                ", rentalPeriod=" + rentalPeriod +
                ", status=" + status +
                '}';
    }
}