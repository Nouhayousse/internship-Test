package pfa.service.IService;

import pfa.dto.request.CreateReservationRequest;
import pfa.dto.response.ReservationResponse;

public interface UserService {

    public ReservationResponse createReservation(CreateReservationRequest request);
    public ReservationResponse cancelReservation(String reservationId);


}