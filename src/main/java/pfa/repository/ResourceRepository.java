package pfa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pfa.entity.Resource;

import java.util.UUID;
@Repository
public interface ResourceRepository extends JpaRepository<Resource, UUID> {

}
