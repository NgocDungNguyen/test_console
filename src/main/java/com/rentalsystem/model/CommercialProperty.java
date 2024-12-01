/**
 * @author <Nguyen Ngoc Dung - s3978535>
 */

package com.rentalsystem.model;

/**
 * Represents a commercial property in the rental system.
 * Extends the base Property class with additional attributes specific to commercial properties.
 */
public class CommercialProperty extends Property {
    private String businessType;
    private int parkingSpaces;
    private double squareFootage;


    /**
     * Constructs a new CommercialProperty with all attributes.
     * @param propertyId Unique identifier for the property
     * @param address Address of the property
     * @param price Rental price of the property
     * @param status Current status of the property
     * @param owner Owner of the property
     * @param businessType Type of business the property is suitable for
     * @param parkingSpaces Number of available parking spaces
     * @param squareFootage Total square footage of the property
     */
    public CommercialProperty(String propertyId, String address, double price, PropertyStatus status, Owner owner,
                              String businessType, int parkingSpaces, double squareFootage) {
        super(propertyId, address, price, status, owner);
        this.businessType = businessType;
        this.parkingSpaces = parkingSpaces;
        this.squareFootage = squareFootage;
    }


    /**
     * Constructs a CommercialProperty from an existing Property, adding commercial-specific attributes.
     * @param p Existing Property object
     * @param businessType Type of business the property is suitable for
     * @param parkingSpaces Number of available parking spaces
     * @param squareFootage Total square footage of the property
     */
    public CommercialProperty(Property p, String businessType, int parkingSpaces, double squareFootage) {
        super(p.getPropertyId(), p.getAddress(), p.getPrice(), p.getStatus(), p.getOwner());
        this.businessType = businessType;
        this.parkingSpaces = parkingSpaces;
        this.squareFootage = squareFootage;
    }


    // Getters and setters
    public String getBusinessType() {
        return businessType;
    }


    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }


    public int getParkingSpaces() {
        return parkingSpaces;
    }


    public void setParkingSpaces(int parkingSpaces) {
        this.parkingSpaces = parkingSpaces;
    }


    public double getSquareFootage() {
        return squareFootage;
    }


    public void setSquareFootage(double squareFootage) {
        this.squareFootage = squareFootage;
    }


    @Override
    public String toString() {
        return "CommercialProperty{" +
                "propertyId='" + getPropertyId() + '\'' +
                ", address='" + getAddress() + '\'' +
                ", price=" + getPrice() +
                ", status=" + getStatus() +
                ", owner=" + getOwner().getFullName() +
                ", businessType='" + businessType + '\'' +
                ", parkingSpaces=" + parkingSpaces +
                ", squareFootage=" + squareFootage +
                '}';
    }
}
