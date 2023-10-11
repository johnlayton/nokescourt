package org.nokescourt.model;

import java.time.Duration;
import java.time.OffsetDateTime;

public record Exam(Course course, String code, OffsetDateTime start, Duration duration) {
}
