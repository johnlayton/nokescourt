package org.nokescourt.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ExamEventPublisher {

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
