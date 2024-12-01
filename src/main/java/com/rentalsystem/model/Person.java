/**
 * @author <Nguyen Ngoc Dung - s3978535>
 */

package com.rentalsystem.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


/**
 * Abstract base class for all person-related entities in the rental system.
 */
public abstract class Person implements Comparable<Person> {
    private String id;
    private String fullName;
    private Date dateOfBirth;
    private String contactInformation;

    /**
     * Constructs a new Person.
     * @param id Unique identifier for the person
     * @param fullName Full name of the person
     * @param dateOfBirth Date of birth of the person
     * @param contactInformation Contact information of the person
     */
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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDateOfBirthString() {
        return dateOfBirth != null ? new SimpleDateFormat("yyyy-MM-dd").format(dateOfBirth) : "";
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


    /**
     * Abstract method to add a managed agreement.
     * To be implemented by subclasses.
     * @param agreement The agreement to be added
     */
    public abstract void addManagedAgreement(RentalAgreement agreement);


    @Override
    public int compareTo(Person o) {
        return this.getId().compareTo(o.getId());
    }


    /**
     * Converts the person's information to a CSV format.
     * @return An array of strings representing the person's data in CSV format
     */
    public String[] toCSV() {
        return new String[] {
                this.id,
                this.fullName,
                this.getDateOfBirthString(), // Use getDateOfBirthString() instead of getDateOfBirth()
                this.contactInformation
        };
    }
}
