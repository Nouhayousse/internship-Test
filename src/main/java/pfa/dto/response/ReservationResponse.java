package pfa.dto.response;

import pfa.dto.AuditDto;
import pfa.entity.enums.ReservationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReservationResponse extends AuditDto {
    UUID userId;
    String userName;
    UUID resourceId;
    String resourceName;
    LocalDateTime startTime;
    LocalDateTime endTime;
    ReservationStatus status;
}
