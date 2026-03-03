package gov.uspto.patent.model;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Build Claim Tree by adding child claims to each claim a claim is dependent on; 
 * In other words adding the current claim as a child to the claim referenced.
 * 
 * @author Brian G. Feldman (brian.feldman@uspto.gov)
 *
 */
public class ClaimTreeBuilder {

    private final List<Claim> claims;

    public ClaimTreeBuilder(final List<Claim> claims) {
        this.claims = claims;
    }

    public void build() {
        claims.stream()
                .filter(claim -> claim.getDependentIds() != null && !claim.getDependentIds().isEmpty())
                .forEach(claim -> getClaims(claim.getDependentIds())
                        .forEach(patentClaim -> patentClaim.addChildClaim(claim)));

        claims.stream()
                .filter(claim -> ClaimType.INDEPENDENT.equals(claim.getClaimType()))
                .forEach(claim -> {
                    claim.setClaimTreeLevel(0);
                    createLevel(claim);
                });
    }

    /**
     * Recursively iterate over claims adding it's tree level or depth to each claim. 
     * 
     * @param claim
     */
    public void createLevel(Claim claim) {
        for (Claim childClaim : claim.getChildClaims()) {
            if (childClaim.getClaimTreeLevel() == -1) {
                childClaim.setClaimTreeLevel(claim.getClaimTreeLevel() + 1);
                createLevel(childClaim);
            }
        }
    }

    /**
     * Get Claim list from list of claim ids.
     * 
     * @param claimIds
     * @return
     */
    public List<Claim> getClaims(Collection<String> claimIds) {
        return this.claims.stream()
                .filter(claim -> claimIds.contains(claim.getId()))
                .collect(Collectors.toList());
    }
}
