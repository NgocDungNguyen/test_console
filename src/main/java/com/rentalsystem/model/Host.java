package com.rentalsystem.model;


import java.util.*;


/**
 * Represents a host in the rental system.
 * A host manages properties and rental agreements.
 */
public class Host extends Person {
    private final List<Property> managedProperties = new ArrayList<>();
    private final Set<Owner> cooperatingOwners = new HashSet<>();
    private final List<RentalAgreement> managedAgreements = new ArrayList<>();


    /**
     * Constructs a new Host.
     * @param id Unique identifier for the host
     * @param fullName Full name of the host
     * @param dateOfBirth Date of birth of the host
     * @param contactInformation Contact information of the host
     */
    public Host(String id, String fullName, Date dateOfBirth, String contactInformation) {
        super(id, fullName, dateOfBirth, contactInformation);
    }


    /**
     * Retrieves a list of properties managed by this host.
     * @return A new ArrayList containing the managed properties
     */
    public List<Property> getManagedProperties() {
        return new ArrayList<>(managedProperties);
    }


    /**
     * Adds a property to the list of properties managed by this host.
     * @param property The property to be added
     */
    public void addManagedProperty(Property property) {
        if (!managedProperties.contains(property)) {
            managedProperties.add(property);
            property.addHost(this);
        }
    }


    /**
     * Removes a property from the list of properties managed by this host.
     * @param property The property to be removed
     */
    public void removeManagedProperty(Property property) {
        if (managedProperties.remove(property)) {
            if (property.getHosts().contains(this)) {
                property.removeHost(this);
            }
        }
    }


    /**
     * Adds a rental agreement to the list of agreements managed by this host.
     * @param agreement The rental agreement to be added
     */
    public void addManagedAgreement(RentalAgreement agreement) {
        if (!managedAgreements.contains(agreement)) {
            managedAgreements.add(agreement);
        }
    }


    /**
     * Removes a rental agreement from the list of agreements managed by this host.
     * @param agreement The rental agreement to be removed
     */
    public void removeManagedAgreement(RentalAgreement agreement) {
        managedAgreements.remove(agreement);
    }


    /**
     * Retrieves a list of rental agreements managed by this host.
     * @return A new ArrayList containing the managed agreements
     */
    public List<RentalAgreement> getManagedAgreements() {
        return new ArrayList<>(managedAgreements);
    }


    /**
     * Retrieves a set of owners cooperating with this host.
     * @return A new HashSet containing the cooperating owners
     */
    public Set<Owner> getCooperatingOwners() {
        return new HashSet<>(cooperatingOwners);
    }


    /**
     * Adds an owner to the set of cooperating owners.
     * @param owner The owner to be added
     */
    public void addCooperatingOwner(Owner owner) {
        cooperatingOwners.add(owner);
        owner.addManagingHost(this);
    }


    /**
     * Removes an owner from the set of cooperating owners.
     * @param owner The owner to be removed
     */
    public void removeCooperatingOwner(Owner owner) {
        if (cooperatingOwners.remove(owner)) {
            owner.removeManagingHost(this);
        }
    }


    @Override
    public String toString() {
        return "Host{" +
                "id='" + getId() + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", dateOfBirth=" + getDateOfBirth() +
                ", contactInformation='" + getContactInformation() + '\'' +
                ", managedProperties=" + managedProperties.size() +
                ", cooperatingOwners=" + cooperatingOwners.size() +
                ", managedAgreements=" + managedAgreements.size() +
                '}';
    }
}
