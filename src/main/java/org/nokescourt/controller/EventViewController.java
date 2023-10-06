package org.nokescourt.controller;

import org.nokescourt.event.ExamEventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collections;

@Controller
public class EventViewController {
    private final ExamEventListener examEventListener;

    public EventViewController(final ExamEventListener examEventListener) {
        this.examEventListener = examEventListener;
    }

    @RequestMapping(value = "view/events", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_PDF_VALUE,
            MediaType.TEXT_PLAIN_VALUE,
            MediaType.TEXT_HTML_VALUE
    })
    public ModelAndView exams() {
        return new ModelAndView("sse", Collections.emptyMap());
    }

    @GetMapping(value = "view/events/tmpl", produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_PDF_VALUE,
            MediaType.TEXT_PLAIN_VALUE,
            MediaType.TEXT_HTML_VALUE,
            "text/template",
            "text/mustache"
    })
    public ModelAndView tmpl() {
        return new ModelAndView("tmpl", Collections.emptyMap());
    }

    @GetMapping("view/events/sse")
    public SseEmitter sse() {
        return examEventListener.register(new SseEmitter());
    }


/*
    @GetMapping(path = "/stream-flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> events() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> "Flux - " + LocalTime.now().toString());
    }
*/

/*
    @GetMapping("view/events")
    public SseEmitter streamSseMvc() {
        return examEventListener.register(new SseEmitter());
    }
*/

}
