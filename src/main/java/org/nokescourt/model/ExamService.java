package org.nokescourt.model;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
public class ExamService {
    public List<Exam> listExams() {
        return Stream.of(new Course("A"), new Course("B"), new Course("C"))
                .flatMap(indexed((course, index) -> Stream.of(
                        new Exam(course, "1", OffsetDateTime.now().plusDays(1).withHour(9 + (index * 2)), Duration.ofMinutes(120)),
                        new Exam(course, "2", OffsetDateTime.now().plusDays(2).withHour(9 + (index * 2)), Duration.ofMinutes(90)),
                        new Exam(course, "3", OffsetDateTime.now().plusDays(3).withHour(9 + (index * 2)), Duration.ofMinutes(120)))))
                .toList();
    }

    public static <A, B> Function<A, B> indexed(BiFunction<A, Integer, B> func) {
        return wrapped(func, new AtomicInteger(1)::getAndIncrement);
    }

    public static <A, B, T> Function<A, B> wrapped(BiFunction<A, T, B> func, Supplier<T> supp) {
        return input -> func.apply(input, supp.get());
    }
}
