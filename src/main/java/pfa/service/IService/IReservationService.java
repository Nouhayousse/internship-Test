package pfa.service.IService;

import org.jspecify.annotations.Nullable;
import pfa.dto.request.CreateReservationRequest;
import pfa.dto.response.ReservationResponse;

import java.util.UUID;

public interface IReservationService {

    public ReservationResponse createReservation(CreateReservationRequest request);
    public ReservationResponse cancelReservation(UUID reservationId);

    public ReservationResponse getById(UUID id);
}
