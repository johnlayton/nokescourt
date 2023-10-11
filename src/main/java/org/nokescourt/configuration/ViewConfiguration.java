package org.nokescourt.configuration;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.helper.HelperFunction;
import org.nokescourt.handlebars.autoconfigure.HandlebarsHelper;
import org.nokescourt.view.JsonViewResolver;
import org.nokescourt.view.MyPdfViewResolver;
import org.nokescourt.view.TxtView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

@Configuration
public class ViewConfiguration {
    @Bean
    public BeanNameViewResolver beanNameViewResolver() {
        BeanNameViewResolver beanNameViewResolver = new BeanNameViewResolver();
        beanNameViewResolver.setOrder(1);
        return beanNameViewResolver;
    }

    @Bean
    public View exams() {
        return new TxtView();
    }

/*
    @Bean
    public MyPdfViewResolver myViewResolver() {
        MyPdfViewResolver myViewResolver = new MyPdfViewResolver();
        myViewResolver.setOrder(1);
        return myViewResolver;
    }
*/

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

/*
    @Component
    @HandlebarsHelper
    public static class CustomHelper {
        @HelperFunction("raw")
        public CharSequence raw(Object context, String tmpl, Options options) {
            return options.fn.text();
        }
    }
*/

    @Component
    @HandlebarsHelper
    @HelperFunction("raw")
    public static class RawHelper implements Helper<String> {
        @Override
        public Object apply(String tmpl, Options options) throws IOException {
            return tmpl;
        }
    }

    @Component
    @HandlebarsHelper
    @HelperFunction("datetime")
    public static class OffsetDateTimeHelper implements Helper<OffsetDateTime> {
        @Override
        public Object apply(OffsetDateTime datetime, Options options) throws IOException {
            return DateTimeFormatter.ofPattern(options.param(0, "M d y, H:m:s"))
                    .format(datetime.atZoneSameInstant(currentTimeZone().toZoneId()));
        }

        protected TimeZone currentTimeZone() {
            return LocaleContextHolder.getTimeZone();
        }
    }
}
