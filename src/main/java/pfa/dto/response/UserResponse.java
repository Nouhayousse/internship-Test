package pfa.dto.response;


import lombok.Data;
import lombok.EqualsAndHashCode;
import pfa.dto.AuditDto;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserResponse extends AuditDto {

    private String name;
    private String email;
}
