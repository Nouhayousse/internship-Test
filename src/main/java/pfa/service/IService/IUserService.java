package pfa.service.IService;


import pfa.dto.request.CreateUserRequest;
import pfa.dto.response.UserResponse;

import java.util.UUID;

public interface IUserService {


    public UserResponse createUser(CreateUserRequest request);
    public UserResponse getUserById(UUID id);

}