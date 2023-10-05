package org.nokescourt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
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
import com.samskivert.mustache.Mustache.Lambda;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@SpringBootApplication
@EnableScheduling
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
        private final ExamEventListener examEventListener;

        public ExamsViewController(final ExamService examService,
                                   final ExamEventListener examEventListener) {
            this.examService = examService;
            this.examEventListener = examEventListener;
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

    @Controller
    public static class EventViewController {
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

    @Service
    public static class ExamService {
        public List<Exam> listExams() {
            return Stream.of(new Course("A"), new Course("B"), new Course("C"))
                    .flatMap(course -> Stream.of(new Exam(course, "1"), new Exam(course, "2"), new Exam(course, "3")))
                    .toList();
        }
    }

    public static class ExamEvent {
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

    @Component
    public static class ExamEventListener {

        private static final Logger LOGGER = LoggerFactory.getLogger(ExamEventListener.class);
        private final List<SseEmitter> emitters = new ArrayList<>();

        private ObjectMapper mapper;

        public ExamEventListener(ObjectMapper mapper) {
            this.mapper = mapper;
        }

//        @EventListener
//        ReturnedEvent handleUserRemovedEvent(UserRemovedEvent event) {
//            // handle UserRemovedEvent ...
//            return new ReturnedEvent();
//        }


        public record ExamEventWrapper(ExamEvent event) {
//            java record String date = """
//                            function() {
//                              return function(text, render) {
//                                 console.log(text);
//                                 return "placeholder";
//                              };
//                            }
//                            """;
//            public String getDate() {
//                return """
//                            function() {
//                              console.log("get date function");
//                              return function(text, render) {
//                                 console.log(text);
//                                 return "placeholder";
//                              };
//                            }
//                            """;
//            }
        }

//        {"event":{"id":"9e3a2a1d-731e-4c3c-9cdf-dd5fd8a4bc94","dt":1696394126.472544000},"date":"function() {\n  console.log(\"get date function\");\n  return function(text, render) {\n     console.log(text);\n     return \"placeholder\";\n  };\n}\n"}

        @EventListener
        void handleExamEvent(final ExamEvent event) {
            // handle ReturnedEvent ...
            LOGGER.info("Receive Event = {}", event.getId());
            List.copyOf(emitters).forEach(emitter -> {
                try {
                    LOGGER.info("Send Event = {} to {}", event.getId(), emitter.toString());
                    LOGGER.info("Send Event = {}", mapper.writer().writeValueAsString(new ExamEventWrapper(event)));
                    emitter.send(SseEmitter.event()
//                                    .data(mapper.writer().writeValueAsString(event), MediaType.APPLICATION_JSON)
                                    .data(new ExamEventWrapper(event), MediaType.APPLICATION_JSON)
                                    .id(event.getId().toString())
                                    .name("event")
                    );
                } catch (Exception e) {
                    LOGGER.error("Error Sending Event = {} to {}", event.getId(), emitter.toString(), e);
                    emitter.completeWithError(e);
                    emitters.remove(emitter);
                }
            });
        }

        public SseEmitter register(SseEmitter emitter) {
            emitters.add(emitter);
//            SseEmitter emitter = new SseEmitter();
//            ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();
//            sseMvcExecutor.execute(() -> {
//                try {
//                    for (int i = 0; true; i++) {
//                        SseEmitter.SseEventBuilder event = SseEmitter.event()
//                                .data("SSE MVC - " + LocalTime.now().toString())
//                                .id(String.valueOf(i))
//
//                                .name("sse event - mvc");
//                        emitter.send(event);
//                        Thread.sleep(1000);
//                    }
//                } catch (Exception ex) {
//                    emitter.completeWithError(ex);
//                }
//            });
            return emitter;
        }
//  ...
    }

//    @Component
//    public static class CustomEventEmitter {
//
//    }
//
//    public static class CustomEvent extends ApplicationEvent {
//
//    }

    @Component
    public static class ExamEventPublisher {

        private static final Logger LOGGER = LoggerFactory.getLogger(ExamEventListener.class);
        private final ApplicationEventPublisher publisher;

        public ExamEventPublisher(ApplicationEventPublisher publisher) {
            this.publisher = publisher;
        }

        @Scheduled(fixedRate = 10L, timeUnit = TimeUnit.SECONDS)
        public void publishEvent() {
            final ExamEvent event = new ExamEvent();
            LOGGER.info("Publish Event = {}", event.getId());
            publisher.publishEvent(event);
        }
    }

    @ControllerAdvice
    public static class LayoutAdvice {

//        private Mustache.Compiler compiler;
//
//        public LayoutAdvice(Mustache.Compiler compiler) {
//            this.compiler = compiler;
//        }

        @ModelAttribute("raw")
        public Lambda raw() {
            return (frag, out) -> {
                out.write(frag.decompile());
            };
        }

//        @ModelAttribute("date")
//        public Lambda date() {
//            return (frag, out) -> {
//                out.write(frag.decompile());
//            };
//        }

    }

//    public static class Layout implements Lambda {
//        private Mustache.Compiler compiler;
//
//        public Layout(Mustache.Compiler compiler) {
//            this.compiler = compiler;
//        }
//
//        String body;
//        @Override
//        public void execute(Fragment frag, Writer out) throws IOException {
////            body = frag.execute();
//            frag.decompile()
//            compiler.compile(frag.execute()).execute(frag.context(), out);
//        }
//    }

    @Configuration
    @EnableWebMvc
    public static class MvcConfiguration implements WebMvcConfigurer {

//        @Bean
//        public Mustache.Compiler mustacheCompiler(
//                Mustache.TemplateLoader templateLoader,
//                Environment environment) {
//
////            MustacheEnvironmentCollector collector
////                    = new MustacheEnvironmentCollector();
////            collector.setEnvironment(environment);
//
//            return Mustache.compiler()
//                    .
//                    .defaultValue("Some Default Value")
//                    .withLoader(templateLoader)
//                    .withCollector(collector);
//        }


        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
                    .indentOutput(true)
                    .dateFormat(new SimpleDateFormat("yyyy-MM-dd"))
                    .modulesToInstall(new ParameterNamesModule(), new JavaTimeModule());
            converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
            converters.add(new MappingJackson2XmlHttpMessageConverter(builder.createXmlMapper(true).build()));
        }

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
                                               @NonNull final HttpServletRequest request,
                                               @NonNull final HttpServletResponse response) throws Exception {

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
