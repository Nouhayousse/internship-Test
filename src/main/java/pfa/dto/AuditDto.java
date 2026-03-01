package pfa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditDto extends BaseDto{
    protected String createdBy;
    protected Timestamp createdOn;
    protected String updatedBy;
    protected Timestamp updatedOn;
}
