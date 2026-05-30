package edu.bbte.replate.auth.mapper;

import edu.bbte.replate.auth.model.User;
import edu.bbte.replate.shared.dto.incoming.RegisterDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "joinDate", ignore = true)
    User registerDtoToUser(RegisterDto registerDto);
}
