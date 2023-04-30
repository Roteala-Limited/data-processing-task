package uk.co.roteala.dataprocessingcommons;

import java.time.Instant;

public interface AuditedDate {
    Instant getDateAdded();

    Instant getDateUpdated();
}
