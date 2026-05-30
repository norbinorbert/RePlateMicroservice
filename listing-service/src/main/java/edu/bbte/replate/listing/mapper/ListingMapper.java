package edu.bbte.replate.listing.mapper;

import edu.bbte.replate.listing.model.Listing;
import edu.bbte.replate.shared.dto.incoming.ListingCreateDto;
import edu.bbte.replate.shared.dto.incoming.ListingUpdateDto;
import edu.bbte.replate.shared.dto.outgoing.ListingDetailedOutDto;
import edu.bbte.replate.shared.dto.outgoing.ListingSimpleOutDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ListingMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cityId", ignore = true)
    @Mapping(target = "countyId", ignore = true)
    @Mapping(target = "countryId", ignore = true)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "datePosted", ignore = true)
    Listing createDtoToListing(ListingCreateDto dto);

    @Mapping(target = "cityId", ignore = true)
    @Mapping(target = "countyId", ignore = true)
    @Mapping(target = "countryId", ignore = true)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "datePosted", ignore = true)
    Listing updateDtoToListing(ListingUpdateDto dto);

    ListingSimpleOutDto listingToSimpleOutDto(Listing listing);

    ListingDetailedOutDto listingToDetailedOutDto(Listing listing);
}
