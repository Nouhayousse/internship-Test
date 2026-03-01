package pfa.service.ImplService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pfa.dto.request.CreateResourceRequest;
import pfa.dto.response.ResourceResponse;
import pfa.entity.Resource;
import pfa.exception.NotFoundException;
import pfa.mapper.ResourceMapper;
import pfa.repository.ReservationRepository;
import pfa.repository.ResourceRepository;
import pfa.service.IService.IResourceService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements IResourceService {

    private final ResourceMapper resourceMapper;
    private final ResourceRepository resourceRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public ResourceResponse createResource(CreateResourceRequest request) {
        Resource resource= resourceMapper.toEntity(request);
        return resourceMapper.toResponse(resourceRepository.save(resource));
    }

    @Override
    public ResourceResponse getResourceById(UUID id) {
        return resourceRepository.findById(id)
                .map(resourceMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Resource not found"));
    }




    public List<ResourceResponse> getAll() {
        return resourceRepository.findAll()
                .stream()
                .map(resourceMapper::toResponse)
                .toList();
    }


    public ResourceResponse deactivate(UUID id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Resource not found"));
        resource.setActive(false);
        return resourceMapper.toResponse(resourceRepository.save(resource));
    }

    @Override
    public ResourceResponse getById(UUID id) {
        return resourceRepository.findById(id)
                .map(resourceMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Resource not found"));
    }
}

