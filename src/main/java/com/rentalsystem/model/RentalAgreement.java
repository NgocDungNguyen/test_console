package com.rentalsystem.model;

import java.util.*;

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

    public enum RentalPeriod {
        DAILY, WEEKLY, FORTNIGHTLY, MONTHLY
    }

    public enum Status {
        NEW, ACTIVE, COMPLETED
    }

    public boolean isCurrentlyActive() {
        Date currentDate = new Date();
        return currentDate.after(startDate) && currentDate.before(endDate) && status == Status.ACTIVE;
    }

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

        property.addTenant(mainTenant);
        property.addRentalAgreement(this);
        mainTenant.addRentalAgreement(this);
        host.addManagedAgreement(this);
        owner.addRentalAgreement(this);
    }

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

    public void addSubTenant(Tenant subTenant) {
        if (mainTenant == subTenant) {
            System.out.println("Main tenant is sub tenant, skip");
        }
        if (!subTenants.contains(subTenant)) {
            subTenants.add(subTenant);
            subTenant.addRentalAgreement(this);
            property.addTenant(subTenant);
        }
    }

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

    public void addPayment(Payment payment) {
        payments.add(payment);
    }

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
