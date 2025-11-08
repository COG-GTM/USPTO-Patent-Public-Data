package gov.uspto.auth.authenticator;

import java.util.function.Predicate;

/**
 * Validator interface for authenticators.
 * 
 * This interface follows the same pattern as the PatentDocument Validator
 * to maintain consistency across the codebase.
 * 
 * NIST 800-53 Controls: IA-5 (Authenticator Management)
 */
public interface AuthenticatorValidator<T> extends Predicate<T> {
    
    /**
     * Gets the name of this validator.
     * 
     * @return the validator name
     */
    String getName();
    
    /**
     * Gets the validation message (typically used for failures).
     * 
     * @return the validation message
     */
    String getMessage();
}
