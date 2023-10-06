package org.nokescourt.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ExamEvent {
    private final UUID id;
    private final OffsetDateTime dt;

    public ExamEvent() {
        id = UUID.randomUUID();
        dt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public OffsetDateTime getDt() {
        return dt;
    }
}
