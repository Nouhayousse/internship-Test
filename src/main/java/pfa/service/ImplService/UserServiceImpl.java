package pfa.service.ImplService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pfa.dto.request.CreateUserRequest;
import pfa.dto.response.UserResponse;
import pfa.entity.User;
import pfa.exception.ConflictException;
import pfa.exception.NotFoundException;
import pfa.mapper.UserMapper;
import pfa.repository.UserRepository;
import pfa.service.IService.IUserService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new ConflictException("Email already in use");
        }
        User user = userMapper.toEntity(request);
        return userMapper.toResponse(userRepository.save(user));

    }

    @Override
    public UserResponse getUserById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

}
