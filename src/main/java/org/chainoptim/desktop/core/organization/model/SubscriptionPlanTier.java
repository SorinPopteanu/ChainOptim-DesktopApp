package org.chainoptim.desktop.core.organization.model;

public enum SubscriptionPlanTier {
    NONE,
    BASIC,
    PROFESSIONAL,
    ENTERPRISE;

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
