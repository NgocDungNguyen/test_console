package com.rentalsystem.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.rentalsystem.util.FileHandler.DATE_FORMAT;

public class Tenant extends Person {
    private final List<RentalAgreement> rentalAgreements;
    private final List<Payment> paymentTransactions;
    private final List<Property> rentedProperties;
    private final List<Payment> payments;


    public Tenant(String id, String fullName, Date dateOfBirth, String contactInformation) {
        super(id, fullName, dateOfBirth, contactInformation);
        this.rentalAgreements = new ArrayList<>();
        this.payments = new ArrayList<>();
        this.paymentTransactions = new ArrayList<>();
        this.rentedProperties = new ArrayList<>();
    }

    public List<RentalAgreement> getRentalAgreements() {
        return new ArrayList<>(rentalAgreements);
    }

    public void addRentalAgreement(RentalAgreement agreement) {
        if (!rentalAgreements.contains(agreement)) {
            rentalAgreements.add(agreement);
        }
    }

    public void removeRentalAgreement(RentalAgreement agreement) {
        rentalAgreements.remove(agreement);
    }

    public List<Payment> getPaymentTransactions() {
        return new ArrayList<>(paymentTransactions);
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
    }

    public List<Payment> getPayments() {
        return new ArrayList<>(payments);
    }

    public void addPaymentTransaction(Payment payment) {
        paymentTransactions.add(payment);
    }

    public List<Property> getRentedProperties() {
        return new ArrayList<>(rentedProperties);
    }

    public void addRentedProperty(Property property) {
        if (!rentedProperties.contains(property)) {
            rentedProperties.add(property);
        }
    }

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
