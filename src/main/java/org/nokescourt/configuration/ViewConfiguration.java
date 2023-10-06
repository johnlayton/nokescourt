package org.nokescourt.configuration;

import org.nokescourt.view.JsonViewResolver;
import org.nokescourt.view.MyPdfViewResolver;
import org.nokescourt.view.TxtView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

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
}
