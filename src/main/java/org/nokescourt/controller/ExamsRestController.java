package org.nokescourt.controller;

import org.nokescourt.model.Exam;
import org.nokescourt.model.ExamService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ExamsRestController {

    private final ExamService examService;

    public ExamsRestController(ExamService examService) {
        this.examService = examService;
    }

    @RequestMapping(value = "rest/exams", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
    })
    public @ResponseBody List<Exam> exams() {
        return examService.listExams();
    }
}
