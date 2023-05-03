package uk.co.roteala.dataprocessingcommons;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.Instant;

@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public class AuditedDateEntity implements AuditedDate{
    @CreatedDate
    @Field(name = "date_added")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Instant dateAdded;

    @LastModifiedDate
    @Field(name = "date_updated")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Instant dateUpdated;

    public AuditedDateEntity() {}

    public Instant getDateAdded() {
        return this.dateAdded;
    }

    public void setDateAdded(Instant dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Instant getDateUpdated() {
        return this.dateUpdated;
    }

    public void setDateUpdated(Instant dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

}
