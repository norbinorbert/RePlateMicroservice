package edu.bbte.replate.listing.util;

import edu.bbte.replate.listing.client.AuthServiceClient;
import edu.bbte.replate.listing.model.Listing;
import edu.bbte.replate.listing.service.ListingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("listingSecurity")
@Slf4j
public class ListingSecurity {
    @Autowired
    private ListingService listingService;

    @Autowired
    private AuthServiceClient authServiceClient;

    public boolean isOwner(Long listingId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Authentication is null or not authenticated");
            return false;
        }

        // Get username from authentication principal
        Object principal = authentication.getPrincipal();
        String username = null;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        }

        if (username == null) {
            log.warn("Could not extract username from authentication");
            return false;
        }

        // Get user ID from auth service
        Long userId = authServiceClient.getUserIdByUsername(username);
        if (userId == null) {
            log.warn("Could not find user ID for username: {}", username);
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
