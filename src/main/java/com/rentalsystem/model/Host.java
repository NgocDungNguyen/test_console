package com.rentalsystem.model;

import java.util.*;

public class Host extends Person {
    private final List<Property> managedProperties = new ArrayList<>();
    private final Set<Owner> cooperatingOwners = new HashSet<>();
    private final List<RentalAgreement> managedAgreements = new ArrayList<>();


    public Host(String id, String fullName, Date dateOfBirth, String contactInformation) {
        super(id, fullName, dateOfBirth, contactInformation);
    }

    public List<Property> getManagedProperties() {
        return new ArrayList<>(managedProperties);
    }

    public void addManagedProperty(Property property) {
        if (!managedProperties.contains(property)) {
            managedProperties.add(property);
            property.addHost(this);
        }
    }

    public void removeManagedProperty(Property property) {
        if (managedProperties.remove(property)) {
            if (property.getHosts().contains(this)) {
                property.removeHost(this);
            }
        }
    }

    public void addManagedAgreement(RentalAgreement agreement) {
        if (!managedAgreements.contains(agreement)) {
            managedAgreements.add(agreement);
        }
    }

    public void removeManagedAgreement(RentalAgreement agreement) {
        managedAgreements.remove(agreement);
    }

    public List<RentalAgreement> getManagedAgreements() {
        return new ArrayList<>(managedAgreements);
    }

    public Set<Owner> getCooperatingOwners() {
        return new HashSet<>(cooperatingOwners);
    }

    public void addCooperatingOwner(Owner owner) {
        cooperatingOwners.add(owner);
        owner.addManagingHost(this);
    }

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
