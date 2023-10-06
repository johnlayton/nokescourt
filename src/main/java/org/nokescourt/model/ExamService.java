package org.nokescourt.model;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class ExamService {
    public List<Exam> listExams() {
        return Stream.of(new Course("A"), new Course("B"), new Course("C"))
                .flatMap(course -> Stream.of(new Exam(course, "1"), new Exam(course, "2"), new Exam(course, "3")))
                .toList();
    }
}
