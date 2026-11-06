package application.domain.enums;

/**
 * Operational condition of a user (Domain 1: "Active, Blocked, etc.").
 * PENDING_INCORPORATION is the transient state of a seller while the Administrator incorporates it.
 */
public enum UserStatus {
    ACTIVE,
    BLOCKED,
    PENDING_INCORPORATION
}
