package pfa.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import pfa.dto.AuditDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter@Setter
public class CreateReservationRequest{

    @NotNull
    UUID userId;
    @NotNull
    UUID resourceId;
    @NotNull @Future
    LocalDateTime startTime;
    @NotNull
    LocalDateTime endTime;

}
