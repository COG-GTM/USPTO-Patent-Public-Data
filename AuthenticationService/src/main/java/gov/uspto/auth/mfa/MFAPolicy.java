package gov.uspto.auth.mfa;

import gov.uspto.auth.authenticator.AuthenticatorType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Multi-factor authentication policy.
 * 
 * Defines MFA requirements per user type:
 * - Required authenticator types
 * - Minimum number of factors required
 * - Optional vs mandatory MFA
 * 
 * NIST 800-53 Controls: IA-2(1) (Multi-factor Authentication)
 */
public class MFAPolicy {

    private final boolean mfaRequired;
    private final int minimumFactors;
    private final Set<AuthenticatorType> requiredTypes;
    private final Set<AuthenticatorType> allowedTypes;
    private final boolean privilegedAccount;

    private MFAPolicy(Builder builder) {
        this.mfaRequired = builder.mfaRequired;
        this.minimumFactors = builder.minimumFactors;
        this.requiredTypes = Collections.unmodifiableSet(new HashSet<>(builder.requiredTypes));
        this.allowedTypes = Collections.unmodifiableSet(new HashSet<>(builder.allowedTypes));
        this.privilegedAccount = builder.privilegedAccount;
    }

    /**
     * Creates a default MFA policy for regular accounts (MFA optional).
     * 
     * @return the default policy
     */
    public static MFAPolicy createDefault() {
        return new Builder()
                .mfaRequired(false)
                .minimumFactors(1)
                .allowAllTypes()
                .build();
    }

    /**
     * Creates an MFA policy for privileged accounts (MFA required).
     * 
     * @return the privileged account policy
     */
    public static MFAPolicy createPrivilegedPolicy() {
        return new Builder()
                .mfaRequired(true)
                .minimumFactors(2)
                .privilegedAccount(true)
                .allowAllTypes()
                .build();
    }

    /**
     * Checks if MFA is required.
     * 
     * @return true if MFA is required, false otherwise
     */
    public boolean isMfaRequired() {
        return mfaRequired;
    }

    /**
     * Gets the minimum number of factors required.
     * 
     * @return the minimum factor count
     */
    public int getMinimumFactors() {
        return minimumFactors;
    }

    /**
     * Gets the required authenticator types.
     * 
     * @return set of required types
     */
    public Set<AuthenticatorType> getRequiredTypes() {
        return requiredTypes;
    }

    /**
     * Gets the allowed authenticator types.
     * 
     * @return set of allowed types
     */
    public Set<AuthenticatorType> getAllowedTypes() {
        return allowedTypes;
    }

    /**
     * Checks if this is a privileged account policy.
     * 
     * @return true if privileged account, false otherwise
     */
    public boolean isPrivilegedAccount() {
        return privilegedAccount;
    }

    /**
     * Checks if the given authenticator type is allowed.
     * 
     * @param type the authenticator type
     * @return true if allowed, false otherwise
     */
    public boolean isTypeAllowed(AuthenticatorType type) {
        return allowedTypes.isEmpty() || allowedTypes.contains(type);
    }

    /**
     * Checks if the given authenticator type is required.
     * 
     * @param type the authenticator type
     * @return true if required, false otherwise
     */
    public boolean isTypeRequired(AuthenticatorType type) {
        return requiredTypes.contains(type);
    }

    /**
     * Builder for creating MFAPolicy instances.
     */
    public static class Builder {
        private boolean mfaRequired = false;
        private int minimumFactors = 1;
        private Set<AuthenticatorType> requiredTypes = new HashSet<>();
        private Set<AuthenticatorType> allowedTypes = new HashSet<>();
        private boolean privilegedAccount = false;

        public Builder mfaRequired(boolean mfaRequired) {
            this.mfaRequired = mfaRequired;
            return this;
        }

        public Builder minimumFactors(int minimumFactors) {
            if (minimumFactors < 1) {
                throw new IllegalArgumentException("Minimum factors must be at least 1");
            }
            this.minimumFactors = minimumFactors;
            return this;
        }

        public Builder requireType(AuthenticatorType type) {
            if (type == null) {
                throw new IllegalArgumentException("Authenticator type cannot be null");
            }
            this.requiredTypes.add(type);
            this.allowedTypes.add(type);
            return this;
        }

        public Builder allowType(AuthenticatorType type) {
            if (type == null) {
                throw new IllegalArgumentException("Authenticator type cannot be null");
            }
            this.allowedTypes.add(type);
            return this;
        }

        public Builder allowAllTypes() {
            this.allowedTypes.clear();
            return this;
        }

        public Builder privilegedAccount(boolean privilegedAccount) {
            this.privilegedAccount = privilegedAccount;
            return this;
        }

        public MFAPolicy build() {
            if (mfaRequired && minimumFactors < 2) {
                throw new IllegalArgumentException("MFA requires at least 2 factors");
            }
            return new MFAPolicy(this);
        }
    }

    @Override
    public String toString() {
        return "MFAPolicy{" +
                "mfaRequired=" + mfaRequired +
                ", minimumFactors=" + minimumFactors +
                ", requiredTypes=" + requiredTypes +
                ", allowedTypes=" + allowedTypes +
                ", privilegedAccount=" + privilegedAccount +
                '}';
    }
}
