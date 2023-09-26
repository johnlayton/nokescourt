package org.nokescourt;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

@SpringBootApplication
public class NokesCourtApplication {

    public static void main(String[] args) {
        SpringApplication.run(NokesCourtApplication.class, args);
    }

    public record Course(String code) {
    }

    public record Exam(Course course, String code) {
    }

    @RestController
    public static class ExamsRestController {

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

    @Controller
    public static class ExamsViewController {
        private final ExamService examService;

        public ExamsViewController(ExamService examService) {
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

    @Service
    public static class ExamService {
        public List<Exam> listExams() {
            return Stream.of(new Course("A"), new Course("B"), new Course("C"))
                    .flatMap(course -> Stream.of(new Exam(course, "1"), new Exam(course, "2"), new Exam(course, "3")))
                    .toList();
        }
    }

    @Configuration
    @EnableWebMvc
    public static class MvcConfiguration implements WebMvcConfigurer {
        @Override
        public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
            configurer
                    .favorParameter(true)
                    .parameterName("format")
                    .ignoreAcceptHeader(false)
                    .useRegisteredExtensionsOnly(false)
                    .defaultContentType(MediaType.TEXT_HTML)
                    .mediaType("xml", MediaType.APPLICATION_XML)
                    .mediaType("json", MediaType.APPLICATION_JSON)
                    .mediaType("pdf", MediaType.APPLICATION_PDF)
                    .mediaType("html", MediaType.TEXT_HTML)
                    .mediaType("txt", MediaType.TEXT_PLAIN);
        }

    }

    @Configuration
    public static class ViewConfiguration {
        @Bean
        public BeanNameViewResolver beanNameViewResolver() {
            BeanNameViewResolver beanNameViewResolver = new BeanNameViewResolver();
            beanNameViewResolver.setOrder(1);
            return beanNameViewResolver;
        }

        @Bean
        public MyPdfViewResolver myViewResolver() {
            MyPdfViewResolver myViewResolver = new MyPdfViewResolver();
            myViewResolver.setOrder(1);
            return myViewResolver;
        }

        @Bean
        public JsonViewResolver jsonViewResolver() {
            return new JsonViewResolver();
        }

        @Bean
        public ViewResolver contentNegotiatingViewResolver(final ContentNegotiationManager manager) {
            ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
            resolver.setContentNegotiationManager(manager);
            return resolver;
        }

        @Bean
        public View exams() {
            return new TxtView();
        }
    }

    public static final class TxtView extends AbstractView {
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

    public static class JsonViewResolver implements ViewResolver {
        @Override
        public View resolveViewName(@NonNull final String viewName,
                                    @NonNull final Locale locale)
                throws Exception {
            MappingJackson2JsonView view = new MappingJackson2JsonView();
            view.setPrettyPrint(true);
            return view;
        }
    }

    public static class MyPdfViewResolver extends WebApplicationObjectSupport implements ViewResolver, Ordered {
        private int order = Ordered.LOWEST_PRECEDENCE;

        @Override
        public int getOrder() {
            return this.order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        @Override
        public View resolveViewName(@NonNull final String viewName,
                                    @NonNull final Locale locale) {
            return new PdfView();
        }
    }

    public static final class PdfView extends AbstractView {
        public PdfView() {
            setContentType(MediaType.APPLICATION_PDF_VALUE);
        }

        @Override
        protected void renderMergedOutputModel(final Map<String, Object> model,
                                               final HttpServletRequest request,
                                               final HttpServletResponse response) throws Exception {

            try (final Document document = new Document(new PdfDocument(new PdfWriter(response.getOutputStream())), PageSize.A4)) {
                PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
                document.add(new Paragraph("Exams"));
                document.setFont(font);
                List<Exam> exams = (List<Exam>) model.get("exams");
                Table table = new Table(2);
                table.addCell(new Cell()
                        .add(new Paragraph("Course")
                                .setFont(font)
                                .setFontColor(ColorConstants.DARK_GRAY))
                        .setBackgroundColor(ColorConstants.CYAN, 0.2f)
                        .setBorder(Border.NO_BORDER)
                        .setBorderRight(new SolidBorder(1))
                        .setBorderBottom(new SolidBorder(1))
                        .setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell()
                        .add(new Paragraph("Exam")
                                .setFont(font)
                                .setFontColor(ColorConstants.DARK_GRAY))
                        .setBackgroundColor(ColorConstants.PINK, 0.2f)
                        .setBorder(Border.NO_BORDER)
                        .setBorderBottom(new SolidBorder(1))
                        .setTextAlignment(TextAlignment.CENTER));
                for (Exam exam : exams) {
                    table.addCell(new Cell()
                            .add(new Paragraph(exam.course().code())
                                    .setFont(font)
                                    .setFontColor(ColorConstants.DARK_GRAY))
                            .setBackgroundColor(ColorConstants.CYAN, 0.2f)
                            .setBorder(Border.NO_BORDER)
                            .setBorderRight(new SolidBorder(1))
                            .setTextAlignment(TextAlignment.CENTER));
                    table.addCell(new Cell()
                            .add(new Paragraph(exam.code())
                                    .setFont(font)
                                    .setFontColor(ColorConstants.DARK_GRAY))
                            .setBackgroundColor(ColorConstants.PINK, 0.2f)
                            .setBorder(Border.NO_BORDER)
                            .setTextAlignment(TextAlignment.CENTER));
                }
                document.add(table);
            }
        }
    }
}
