/**
 * @author <Nguyen Ngoc Dung - s3978535>
 */

package com.rentalsystem.model;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents a residential property in the rental system.
 * Extends the base Property class with additional attributes specific to residential properties.
 */
public class ResidentialProperty extends Property {
    private int numberOfBedrooms;
    private boolean hasGarden;
    private boolean isPetFriendly;


    /**
     * Constructs a new ResidentialProperty.
     * @param propertyId Unique identifier for the property
     * @param address Address of the property
     * @param price Rental price of the property
     * @param status Current status of the property
     * @param owner Owner of the property
     * @param numberOfBedrooms Number of bedrooms in the property
     * @param hasGarden Whether the property has a garden
     * @param isPetFriendly Whether the property is pet-friendly
     */
    public ResidentialProperty(String propertyId,
                               String address,
                               double price,
                               PropertyStatus status,
                               Owner owner,
                               int numberOfBedrooms,
                               boolean hasGarden,
                               boolean isPetFriendly) {
        super(propertyId, address, price, status, owner);
        this.numberOfBedrooms = numberOfBedrooms;
        this.hasGarden = hasGarden;
        this.isPetFriendly = isPetFriendly;
    }


    /**
     * Constructs a ResidentialProperty from an existing Property, adding residential-specific attributes.
     * @param p Existing Property object
     * @param numberOfBedrooms Number of bedrooms in the property
     * @param hasGarden Whether the property has a garden
     * @param isPetFriendly Whether the property is pet-friendly
     */
    public ResidentialProperty(Property p, int numberOfBedrooms, boolean hasGarden, boolean isPetFriendly) {
        super(p.getPropertyId(), p.getAddress(), p.getPrice(), p.getStatus(), p.getOwner());
        this.numberOfBedrooms = numberOfBedrooms;
        this.hasGarden = hasGarden;
        this.isPetFriendly = isPetFriendly;
    }


    /**
     * Retrieves the list of sub-tenants for this residential property.
     * @return A new ArrayList of sub-tenants (currently not implemented)
     */
    public List<Tenant> getSubTenants() {
        return new ArrayList<>(); // Implement this method properly
    }


    // Getters and setters


    public int getNumberOfBedrooms() {
        return numberOfBedrooms;
    }


    public void setNumberOfBedrooms(int numberOfBedrooms) {
        this.numberOfBedrooms = numberOfBedrooms;
    }


    public boolean hasGarden() {
        return hasGarden;
    }


    public void setHasGarden(boolean hasGarden) {
        this.hasGarden = hasGarden;
    }


    public boolean isPetFriendly() {
        return isPetFriendly;
    }


    public void setPetFriendly(boolean petFriendly) {
        isPetFriendly = petFriendly;
    }


    @Override
    public String toString() {
        return "ResidentialProperty{" +
                "propertyId='" + getPropertyId() + '\'' +
                ", address='" + getAddress() + '\'' +
                ", price=" + getPrice() +
                ", status=" + getStatus() +
                ", owner=" + getOwner().getFullName() +
                ", numberOfBedrooms=" + numberOfBedrooms +
                ", hasGarden=" + hasGarden +
                ", isPetFriendly=" + isPetFriendly +
                '}';
    }
}
