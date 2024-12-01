package com.rentalsystem.model;


import java.util.HashSet;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Represents a property in the rental system.
 */
public class Property {
    private String propertyId;
    private String address;
    private double price;
    private PropertyStatus status;
    private Owner owner;
    private Set<Host> hosts;
    private List<Tenant> tenants;
    private List<RentalAgreement> rentalHistory;


    /**
     * Constructs a new Property.
     * @param propertyId Unique identifier for the property
     * @param address Address of the property
     * @param price Rental price of the property
     * @param status Current status of the property
     * @param owner Owner of the property
     */
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


    // Getters and setters


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


    /**
     * Sets the owner of the property and updates the owner's owned properties.
     * @param owner The new owner of the property
     */
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


    /**
     * Adds a host to the property and updates the host's managed properties.
     * @param newHost The host to be added
     */
    public void addHost(Host newHost) {
        this.hosts.add(newHost);
        newHost.addManagedProperty(this);
    }


    /**
     * Removes a host from the property and updates the host's managed properties.
     * @param host The host to be removed
     */
    public void removeHost(Host host) {
        this.hosts.remove(host);
        host.removeManagedProperty(this);
    }


    public List<Tenant> getTenants() {
        return new ArrayList<>(tenants);
    }


    /**
     * Adds a tenant to the property.
     * @param tenant The tenant to be added
     */
    public void addTenant(Tenant tenant) {
        if (!tenants.contains(tenant)) {
            tenants.add(tenant);
        }
    }


    /**
     * Removes a tenant from the property.
     * @param tenant The tenant to be removed
     */
    public void removeTenant(Tenant tenant) {
        tenants.remove(tenant);
    }


    /**
     * Adds a rental agreement to the property's rental history.
     * @param agreement The rental agreement to be added
     */
    public void addRentalAgreement(RentalAgreement agreement) {
        rentalHistory.add(agreement);
    }


    /**
     * Retrieves the rental history of the property.
     * @return A new ArrayList containing the rental history
     */
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
}
