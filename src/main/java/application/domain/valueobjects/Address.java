package application.domain.valueobjects;

import java.util.Objects;

public class Address {

    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    public Address() {
        this.street = "Default Street";
        this.city = "Default City";
        this.state = "";
        this.postalCode = "";
        this.country = "USA";
    }

    public Address(String street, String city, String state, String postalCode, String country) {
        setStreet(street);
        setCity(city);
        setState(state);
        setPostalCode(postalCode);
        setCountry(country);
    }

    public Address(String fullAddress) {
        if (fullAddress == null || fullAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Address text cannot be null or empty.");
        }
        this.street = fullAddress.trim();
        this.city = "N/A";
        this.state = "";
        this.postalCode = "";
        this.country = "USA";
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        if (street == null || street.trim().isEmpty()) {
            throw new IllegalArgumentException("Street address cannot be null or empty.");
        }
        this.street = street.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be null or empty.");
        }
        this.city = city.trim();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state != null ? state.trim() : "";
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode != null ? postalCode.trim() : "";
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country != null ? country.trim() : "USA";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street) &&
                Objects.equals(city, address.city) &&
                Objects.equals(state, address.state) &&
                Objects.equals(postalCode, address.postalCode) &&
                Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, state, postalCode, country);
    }

    @Override
    public String toString() {
        return street + ", " + city + (state.isEmpty() ? "" : ", " + state) + " " + postalCode + ", " + country;
    }
}
