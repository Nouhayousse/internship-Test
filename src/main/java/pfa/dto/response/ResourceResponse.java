package pfa.dto.response;

import pfa.dto.AuditDto;

public class ResourceResponse extends AuditDto {
    private String name;
    private String description;
    private int capacity;
    private boolean active;
}
