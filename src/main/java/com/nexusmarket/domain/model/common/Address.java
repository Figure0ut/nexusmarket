package com.nexusmarket.domain.model.common;

import java.util.Objects;

/**
 * Immutable Value Object representing physical or billing addresses in NexusMarket.
 */
public final class Address {

    private final String street;
    private final String city;
    private final String state;
    private final String postalCode;
    private final String country;

    public Address(String street, String city, String state, String postalCode, String country) {
        if (street == null || street.trim().isEmpty()) {
            throw new IllegalArgumentException("Street address cannot be null or empty.");
        }
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be null or empty.");
        }
        this.street = street.trim();
        this.city = city.trim();
        this.state = state != null ? state.trim() : "";
        this.postalCode = postalCode != null ? postalCode.trim() : "";
        this.country = country != null ? country.trim() : "USA";
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

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
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
