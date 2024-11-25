package com.rentalsystem.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Property {
    private String propertyId;
    private String address;
    private double price;
    private PropertyStatus status;
    private Owner owner;
    private Set<Host> hosts;
    private List<Tenant> tenants;
    private List<RentalAgreement> rentalHistory;



    public Property(String propertyId, String address, double price, PropertyStatus status, Owner owner) {
        this.propertyId = propertyId;
        this.address = address;
        this.price = price;
        this.status = status;
        this.owner = owner;
        this.hosts = new HashSet<>();
        this.tenants = new ArrayList<>();
        this.rentalHistory = new ArrayList<>();
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public PropertyStatus getStatus() {
        return status;
    }

    public void setStatus(PropertyStatus status) {
        this.status = status;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        if (this.owner != null) {
            this.owner.removeOwnedProperty(this);
        }
        this.owner = owner;
        if (owner != null) {
            owner.addOwnedProperty(this);
        }
    }

    public Set<Host> getHosts() {
        return hosts;
    }

    public void addHost(Host newHost) {
        this.hosts.add(newHost);
        newHost.addManagedProperty(this);
    }

    public void removeHost(Host host) {
        this.hosts.remove(host);
        host.removeManagedProperty(this);
    }

//    public void setHosts(Set<Host> newHost) {
//        if (this.hosts == newHost) {
//            return;
//        }
//
//        Set<Host> oldHost = this.hosts;
//        this.hosts = newHost;
//
//        if (oldHost != null) {
//            oldHost.forEach(host -> host.removeManagedProperty(this));
//        }
//
//        if (newHost != null) {
//            oldHost.forEach(host -> host.addManagedProperty(this));
//        }
//    }

    public List<Tenant> getTenants() {
        return new ArrayList<>(tenants);
    }

    public void addTenant(Tenant tenant) {
        if (!tenants.contains(tenant)) {
            tenants.add(tenant);
        }
    }

    public void removeTenant(Tenant tenant) {
        tenants.remove(tenant);
    }

    public void addRentalAgreement(RentalAgreement agreement) {
        rentalHistory.add(agreement);
    }

    public List<RentalAgreement> getRentalHistory() {
        return new ArrayList<>(rentalHistory);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property property = (Property) o;
        return Objects.equals(propertyId, property.propertyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyId);
    }

    @Override
    public String toString() {
        return "Property{" +
                "propertyId='" + propertyId + '\'' +
                ", address='" + address + '\'' +
                ", price=" + price +
                ", status=" + status +
                ", owner=" + (owner != null ? owner.getId() + " - " + owner.getFullName() : "N/A") +
                ", hosts=" + hosts.size() +
                ", tenants=" + tenants.size() +
                '}';
    }

//    public String toSave() {
//        StringJoiner sj = new StringJoiner(",");
//        sj.add(propertyId).add(address).add(Double.toString(price)).add(status.toString()).add(owner.getId());
//        return sj.toString();
//    }

}
