package org.nokescourt.view;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.nokescourt.model.Exam;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.view.AbstractView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class TxtView extends AbstractView {
    public TxtView() {
        setContentType(MediaType.TEXT_PLAIN_VALUE);
    }

    @Override
    protected void renderMergedOutputModel(final Map<String, Object> model,
                                           final HttpServletRequest request,
                                           final HttpServletResponse response) throws Exception {

        var course = getApplicationContext().getMessage("course", null, "Course", currentLocale());
        var exam = getApplicationContext().getMessage("exam", null, "Exam", currentLocale());
        var session = getApplicationContext().getMessage("session", null, "Session", currentLocale());

        response.getWriter().println("Exams");
        response.getWriter().printf("%8s | %8s | %s%n", course, exam, session);
        List<Exam> exams = (List<Exam>) model.get("exams");
        exams.forEach(ex -> {
            try {
                response.getWriter().printf("%8s | %8s | %s - %s%n", ex.course().code(), ex.code(), ex.start(), ex.duration());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Locale currentLocale() {
        return LocaleContextHolder.getLocale();
    }
}
