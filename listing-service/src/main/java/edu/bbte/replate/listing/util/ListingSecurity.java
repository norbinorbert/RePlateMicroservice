package edu.bbte.replate.listing.util;

import edu.bbte.replate.listing.model.Listing;
import edu.bbte.replate.listing.service.ListingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("listingSecurity")
@Slf4j
public class ListingSecurity {
    @Autowired
    private ListingService listingService;

    /**
     * Check if the provided user ID is the owner of the listing.
     * This method is used for direct user ID validation (without needing SecurityContext).
     *
     * @param listingId the ID of the listing
     * @param userId    the ID of the user to check
     * @return true if the user is the owner of the listing, false otherwise
     */
    public boolean isOwnerByUserId(Long listingId, Long userId) {
        if (userId == null) {
            log.warn("User ID is null");
            return false;
        }

        // Check if listing exists and belongs to user
        Listing listing = listingService.findById(listingId);
        if (listing == null) {
            log.warn("Listing not found: {}", listingId);
            return false;
        }

        boolean isOwner = Objects.equals(listing.getOwnerId(), userId);
        log.debug("Ownership check for listing {}: {} (userId: {}, listingOwnerId: {})",
                listingId, isOwner, userId, listing.getOwnerId());

        return isOwner;
    }
}
