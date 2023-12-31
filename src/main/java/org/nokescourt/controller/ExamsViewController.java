package org.nokescourt.controller;

import org.nokescourt.model.ExamService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
public class ExamsViewController {
    private final ExamService examService;

    public ExamsViewController(final ExamService examService) {
        this.examService = examService;
    }

    @RequestMapping(value = "view/exams", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_PDF_VALUE,
            MediaType.TEXT_PLAIN_VALUE,
            MediaType.TEXT_HTML_VALUE
    })
    public ModelAndView exams() {
        return new ModelAndView("exams", Map.of("exams", examService.listExams()));
    }
}
