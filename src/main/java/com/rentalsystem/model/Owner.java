package com.rentalsystem.model;

import java.util.*;

/**
 * Represents a property owner in the rental system.
 */
public class Owner extends Person {
    private final List<Property> ownedProperties;
    private final List<Host> managingHosts;
    private final List<RentalAgreement> rentalAgreements;

    /**
     * Constructs a new Owner.
     * @param id Unique identifier for the owner
     * @param fullName Full name of the owner
     * @param dateOfBirth Date of birth of the owner
     * @param contactInformation Contact information of the owner
     */
    public Owner(String id, String fullName, Date dateOfBirth, String contactInformation) {
        super(id, fullName, dateOfBirth, contactInformation);
        this.ownedProperties = new ArrayList<>();
        this.managingHosts = new ArrayList<>();
        this.rentalAgreements = new ArrayList<>();
    }

    /**
     * Retrieves a list of properties owned by this owner.
     * @return A new ArrayList containing the owned properties
     */
    public List<Property> getOwnedProperties() {
        return new ArrayList<>(ownedProperties);
    }

    /**
     * Adds a property to the list of properties owned by this owner.
     * @param property The property to be added
     */
    public void addOwnedProperty(Property property) {
        if (!ownedProperties.contains(property)) {
            ownedProperties.add(property);
        }
    }

    /**
     * Removes a property from the list of properties owned by this owner.
     * @param property The property to be removed
     */
    public void removeOwnedProperty(Property property) {
        ownedProperties.remove(property);
    }

    /**
     * Retrieves a list of hosts managing this owner's properties.
     * @return A new ArrayList containing the managing hosts
     */
    public List<Host> getManagingHosts() {
        return new ArrayList<>(managingHosts);
    }

    /**
     * Adds a host to the list of hosts managing this owner's properties.
     * @param host The host to be added
     */
    public void addManagingHost(Host host) {
        if (!managingHosts.contains(host)) {
            managingHosts.add(host);
            host.addCooperatingOwner(this);
        }
    }

    /**
     * Removes a host from the list of hosts managing this owner's properties.
     * @param host The host to be removed
     */
    public void removeManagingHost(Host host) {
        managingHosts.remove(host);
    }

    /**
     * Retrieves a list of rental agreements associated with this owner.
     * @return A new ArrayList containing the rental agreements
     */
    public List<RentalAgreement> getRentalAgreements() {
        return new ArrayList<>(rentalAgreements);
    }

    @Override
    public void addManagedAgreement(RentalAgreement agreement) {
        if (!rentalAgreements.contains(agreement)) {
            rentalAgreements.add(agreement);
        }
    }

    /**
     * Removes a managed agreement from this owner.
     * @param agreement The agreement to be removed
     */
    public void removeManagedAgreement(RentalAgreement agreement) {
        rentalAgreements.remove(agreement);
    }

    /**
     * Adds a rental agreement to this owner.
     * @param agreement The agreement to be added
     */
    public void addRentalAgreement(RentalAgreement agreement) {
        if (!rentalAgreements.contains(agreement)) {
            rentalAgreements.add(agreement);
        }
    }

    /**
     * Removes a rental agreement from this owner.
     * @param agreement The agreement to be removed
     */
    public void removeRentalAgreement(RentalAgreement agreement) {
        rentalAgreements.remove(agreement);
    }

    @Override
    public String toString() {
        return "Owner{" +
                "id='" + getId() + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", dateOfBirth=" + getDateOfBirth() +
                ", contactInformation='" + getContactInformation() + '\'' +
                ", ownedProperties=" + ownedProperties.size() +
                ", managingHosts=" + managingHosts.size() +
                ", rentalAgreements=" + rentalAgreements.size() +
                '}';
    }
}