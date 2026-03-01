package pfa.mapper;

import org.mapstruct.Mapper;
import pfa.dto.request.CreateResourceRequest;
import pfa.dto.response.ResourceResponse;
import pfa.entity.Resource;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    ResourceResponse toResponse(Resource resource);

    Resource toEntity(CreateResourceRequest request);
}
