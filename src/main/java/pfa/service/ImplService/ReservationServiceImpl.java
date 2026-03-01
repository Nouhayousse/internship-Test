package pfa.service.ImplService;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pfa.dto.request.CreateReservationRequest;
import pfa.dto.response.ReservationResponse;
import pfa.entity.Reservation;
import pfa.entity.Resource;
import pfa.entity.User;
import pfa.entity.enums.ReservationStatus;
import pfa.exception.ConflictException;
import pfa.exception.NotFoundException;
import pfa.mapper.ReservationMapper;
import pfa.repository.ReservationRepository;
import pfa.repository.ResourceRepository;
import pfa.repository.UserRepository;
import pfa.service.IService.IReservationService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements IReservationService {

    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;
    private final ReservationMapper reservationMapper;


    @Override
    @Transactional
    public ReservationResponse createReservation(CreateReservationRequest request) {
        //On valide les dates
        if(!request.getEndTime().isAfter(request.getStartTime())){
            throw new ValidationException("End time must be after start time");
        }
        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Start time must be in the future");
        }

        Resource resource = resourceRepository.findByIdForUpdate(request.getResourceId())
                .orElseThrow(() -> new NotFoundException("Resource not found"));

        if (!resource.isActive()) {
            throw new ValidationException("Resource is not active");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));


        long conflicts=reservationRepository.countConflicts(request.getResourceId(), request.getStartTime(), request.getEndTime());
        if(conflicts>0) {
            throw new ConflictException("Resource is already reserved for the selected time slot");
        }


        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setResource(resource);
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());

        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }


    @Transactional
    @Override
    public ReservationResponse cancelReservation(UUID id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new ValidationException("Reservation is already canceled");
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    public ReservationResponse getById(UUID id) {
        return reservationRepository.findById(id)
                .map(reservationMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));
    }
}
