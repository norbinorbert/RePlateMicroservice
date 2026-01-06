package edu.bbte.replate.mapper;

import edu.bbte.replate.dto.incoming.RegisterDto;
import edu.bbte.replate.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "joinDate", ignore = true)
    @Mapping(target = "listings", ignore = true)
    User registerDtoToUser(RegisterDto registerDto);
}
