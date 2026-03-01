package pfa.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pfa.dto.AuditDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest{

    @NotNull
    private String name;
    @NotNull
    private String email;
}
