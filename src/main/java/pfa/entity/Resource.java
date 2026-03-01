package pfa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "resources")
public class Resource extends AuditEntity {

    @Column(nullable = false)
    private String name;
    private String description;

    @Column(nullable = false)
    private int capacity=1;
    @Column(nullable = false)
    private boolean active=true;


}
