package pfa.service.IService;

import org.jspecify.annotations.Nullable;
import pfa.dto.request.CreateResourceRequest;
import pfa.dto.response.ResourceResponse;

import java.util.List;
import java.util.UUID;

public interface IResourceService {

    public ResourceResponse createResource(CreateResourceRequest request);

    public ResourceResponse getResourceById(UUID id);

    public List<ResourceResponse> getAll();


    public ResourceResponse deactivate(UUID id);

    public ResourceResponse getById(UUID id);
}