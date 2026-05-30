package edu.bbte.replate.listing.util;

import edu.bbte.replate.listing.model.Listing;
import edu.bbte.replate.listing.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("listingSecurity")
public class ListingSecurity {
    @Autowired
    private ListingService listingService;

    public boolean isOwner(Long listingId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();

        Listing listing = listingService.findById(listingId);
        if (listing == null) {
            return false;
        }

        return principal != null && Objects.equals(listing.getOwnerId(), principal.getId());
    }
}
