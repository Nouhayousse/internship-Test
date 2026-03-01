package pfa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pfa.entity.Reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    @Query("""
        SELECT COUNT(r) FROM Reservation r
        WHERE r.resource.id = :resourceId
          AND r.status != 'CANCELLED'
          AND r.startTime < :endTime
          AND r.endTime > :startTime
    """)
    long countConflicts(
            @Param("resourceId") UUID resourceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );



    @Query("""
        SELECT r FROM Reservation r
        WHERE r.resource.id = :resourceId
          AND r.status != 'CANCELLED'
          AND r.startTime < :endTime
          AND r.endTime > :startTime
    """)
    List<Reservation> findConflicts(
            @Param("resourceId") UUID resourceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );


}
