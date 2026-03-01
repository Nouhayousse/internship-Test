package pfa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pfa.entity.User;

import java.util.UUID;
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
        User findByName(String name);
        User findByEmail(String email);
        Boolean existsByEmail(String email);
}
