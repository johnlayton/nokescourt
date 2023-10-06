package org.nokescourt.view;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.nokescourt.model.Exam;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.view.AbstractView;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class TxtView extends AbstractView {
    public TxtView() {
        setContentType(MediaType.TEXT_PLAIN_VALUE);
    }

    @Override
    protected void renderMergedOutputModel(final Map<String, Object> model,
                                           final HttpServletRequest request,
                                           final HttpServletResponse response) throws Exception {
        response.getWriter().println("Exams");
        response.getWriter().printf("%8s | %8s%n", "Course", "Exam");
        List<Exam> exams = (List<Exam>) model.get("exams");
        exams.forEach(exam -> {
            try {
                response.getWriter().printf("%8s | %8s%n", exam.course().code(), exam.code());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
