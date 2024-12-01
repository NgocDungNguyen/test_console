package com.rentalsystem.model;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import static com.rentalsystem.util.FileHandler.DATE_FORMAT;


/**
 * Represents a tenant in the rental system.
 */
public class Tenant extends Person {
    private final List<RentalAgreement> rentalAgreements;
    private final List<Payment> paymentTransactions;
    private final List<Property> rentedProperties;
    private final List<Payment> payments;


    /**
     * Constructs a new Tenant.
     * @param id Unique identifier for the tenant
     * @param fullName Full name of the tenant
     * @param dateOfBirth Date of birth of the tenant
     * @param contactInformation Contact information of the tenant
     */
    public Tenant(String id, String fullName, Date dateOfBirth, String contactInformation) {
        super(id, fullName, dateOfBirth, contactInformation);
        this.rentalAgreements = new ArrayList<>();
        this.payments = new ArrayList<>();
        this.paymentTransactions = new ArrayList<>();
        this.rentedProperties = new ArrayList<>();
    }


    /**
     * Retrieves the list of rental agreements for this tenant.
     * @return A new ArrayList containing the rental agreements
     */
    public List<RentalAgreement> getRentalAgreements() {
        return new ArrayList<>(rentalAgreements);
    }


    /**
     * Adds a rental agreement to this tenant.
     * @param agreement The rental agreement to be added
     */
    public void addRentalAgreement(RentalAgreement agreement) {
        if (!rentalAgreements.contains(agreement)) {
            rentalAgreements.add(agreement);
        }
    }


    /**
     * Removes a rental agreement from this tenant.
     * @param agreement The rental agreement to be removed
     */
    public void removeRentalAgreement(RentalAgreement agreement) {
        rentalAgreements.remove(agreement);
    }


    /**
     * Retrieves the list of payment transactions for this tenant.
     * @return A new ArrayList containing the payment transactions
     */
    public List<Payment> getPaymentTransactions() {
        return new ArrayList<>(paymentTransactions);
    }


    /**
     * Adds a payment to this tenant.
     * @param payment The payment to be added
     */
    public void addPayment(Payment payment) {
        payments.add(payment);
    }


    /**
     * Retrieves the list of payments for this tenant.
     * @return A new ArrayList containing the payments
     */
    public List<Payment> getPayments() {
        return new ArrayList<>(payments);
    }


    /**
     * Adds a payment transaction to this tenant.
     * @param payment The payment transaction to be added
     */
    public void addPaymentTransaction(Payment payment) {
        paymentTransactions.add(payment);
    }


    /**
     * Retrieves the list of rented properties for this tenant.
     * @return A new ArrayList containing the rented properties
     */
    public List<Property> getRentedProperties() {
        return new ArrayList<>(rentedProperties);
    }


    /**
     * Adds a rented property to this tenant.
     * @param property The property to be added
     */
    public void addRentedProperty(Property property) {
        if (!rentedProperties.contains(property)) {
            rentedProperties.add(property);
        }
    }


    /**
     * Removes a rented property from this tenant.
     * @param property The property to be removed
     */
    public void removeRentedProperty(Property property) {
        rentedProperties.remove(property);
    }


    @Override
    public void addManagedAgreement(RentalAgreement agreement) {
        addRentalAgreement(agreement);
    }


    @Override
    public String toString() {
        return String.join(",",
                getId(),
                getFullName(),
                getDateOfBirth(),
                getContactInformation()
        );
    }
}
