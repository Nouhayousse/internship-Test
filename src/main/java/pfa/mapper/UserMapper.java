package pfa.mapper;

import org.mapstruct.Mapper;
import pfa.dto.request.CreateUserRequest;
import pfa.dto.response.UserResponse;
import pfa.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
    User toEntity(CreateUserRequest user);

}
