package pfa.mapper;

import org.mapstruct.Mapper;
import pfa.dto.request.CreateReservationRequest;
import pfa.dto.response.ReservationResponse;
import pfa.entity.Reservation;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    ReservationResponse toResponse(Reservation reservation);
    Reservation toEntity(CreateReservationRequest request);

}
