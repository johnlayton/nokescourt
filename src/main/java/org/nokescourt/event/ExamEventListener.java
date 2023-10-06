package org.nokescourt.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

@Component
public class ExamEventListener {

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
