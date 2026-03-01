package pfa.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pfa.dto.request.CreateResourceRequest;
import pfa.dto.response.ResourceResponse;
import pfa.service.IService.IResourceService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resources")
@Tag(name = "Resources", description = "Gestion des ressources")
public class ResourceController {

    private final IResourceService resourceService;


    @PostMapping
    public ResponseEntity<ResourceResponse> create(@Valid @RequestBody CreateResourceRequest request) {
        return ResponseEntity.status(201).body(resourceService.createResource(request));
    }

    @GetMapping
    public ResponseEntity<List<ResourceResponse>> getAll() {
        return ResponseEntity.ok(resourceService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(resourceService.getById(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ResourceResponse> deactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(resourceService.deactivate(id));
    }

}

