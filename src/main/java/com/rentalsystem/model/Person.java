package com.rentalsystem.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public abstract class Person implements Comparable<Person> {
    private String id;
    private String fullName;
    private Date dateOfBirth;
    private String contactInformation;

    public Person(String id, String fullName, Date dateOfBirth, String contactInformation) {
        this.id = id;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.contactInformation = contactInformation;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDateOfBirth() {
        return new SimpleDateFormat("yyyy-MM-dd").format(dateOfBirth);
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", contactInformation='" + contactInformation + '\'' +
                '}';
    }

    public abstract void addManagedAgreement(RentalAgreement agreement);

    @Override
    public int compareTo(Person o) {
        return this.getId().compareTo(o.getId());
    }

    public String[] toCSV() {
        return new String[] {
                this.id,
                this.fullName,
                this.getDateOfBirth(),
                this.contactInformation
        };
    }
}
