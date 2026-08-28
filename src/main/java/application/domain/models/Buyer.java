package application.domain.models;

import application.domain.enums.UserRole;
import application.domain.enums.UserStatus;
import application.domain.valueobjects.Address;
import application.domain.valueobjects.Email;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Buyer extends User {

    private Address mainAddress;
    private List<Address> additionalAddresses = new ArrayList<>();
    private String commercialStatus;

    public Buyer() {
        super();
        setRole(UserRole.BUYER);
        setStatus(UserStatus.ACTIVE);
    }

    public Buyer(String identifier, String fullName, Email email, UserRole role, UserStatus status,
                 Address mainAddress, List<Address> additionalAddresses, String commercialStatus) {
        super(identifier, fullName, email, role, status);
        setMainAddress(mainAddress);
        setAdditionalAddresses(additionalAddresses);
        setCommercialStatus(commercialStatus);
    }

    public Buyer(String identifier, String fullName, String emailStr, String mainAddressStr, String commercialStatus) {
        this(identifier, fullName, new Email(emailStr), UserRole.BUYER, UserStatus.ACTIVE,
                new Address(mainAddressStr), new ArrayList<>(), commercialStatus);
    }

    public Address getMainAddress() {
        return mainAddress;
    }

    public void setMainAddress(Address mainAddress) {
        if (mainAddress == null) {
            throw new IllegalArgumentException("Main address cannot be null.");
        }
        this.mainAddress = mainAddress;
    }

    public List<Address> getAdditionalAddresses() {
        return Collections.unmodifiableList(additionalAddresses);
    }

    public void setAdditionalAddresses(List<Address> additionalAddresses) {
        this.additionalAddresses = new ArrayList<>();
        if (additionalAddresses != null) {
            for (Address addr : additionalAddresses) {
                if (addr != null && !this.additionalAddresses.contains(addr)) {
                    this.additionalAddresses.add(addr);
                }
            }
        }
    }

    public String getCommercialStatus() {
        return commercialStatus;
    }

    public void setCommercialStatus(String commercialStatus) {
        if (commercialStatus == null || commercialStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("Commercial status cannot be null or empty.");
        }
        this.commercialStatus = commercialStatus.trim();
    }

    public void updateMainAddress(Address address) {
        setMainAddress(address);
    }

    public void addSecondaryAddress(Address address) {
        if (address == null) {
            throw new IllegalArgumentException("Secondary address cannot be null.");
        }
        if (address.equals(this.mainAddress)) {
            throw new IllegalArgumentException("Secondary address cannot be identical to the main address.");
        }
        if (!this.additionalAddresses.contains(address)) {
            this.additionalAddresses.add(address);
        }
    }

    public void validateOperation(User target) {
        if (target == null) {
            throw new IllegalArgumentException("Target user cannot be null.");
        }
        if (target.getRole() == UserRole.BUYER || target instanceof Buyer || !target.getIdentifier().equals(this.getIdentifier())) {
            throw new IllegalStateException("Authorization Failure: Buyer '" + getIdentifier() +
                    "' is not authorized to perform administrative operations on target user '" + target.getIdentifier() + "'.");
        }
    }
}
