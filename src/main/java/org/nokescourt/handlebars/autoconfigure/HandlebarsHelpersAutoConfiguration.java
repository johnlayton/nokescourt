package org.nokescourt.handlebars.autoconfigure;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.HumanizeHelper;
import com.github.jknack.handlebars.Jackson2Helper;
import com.github.jknack.handlebars.MarkdownHelper;
import com.github.jknack.handlebars.helper.*;
import org.nokescourt.handlebars.springmvc.HandlebarsViewResolver;
import jakarta.annotation.PostConstruct;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

@Configuration
@ConditionalOnClass(HandlebarsViewResolver.class)
@ConditionalOnWebApplication
public class HandlebarsHelpersAutoConfiguration {

    @Configuration
    @ConditionalOnClass(Jackson2Helper.class)
    static class JsonHelperAutoConfiguration {

        @Autowired
        private HandlebarsViewResolver handlebarsViewResolver;

        @PostConstruct
        public void registerHelper() {
            handlebarsViewResolver.registerHelper("json", Jackson2Helper.INSTANCE);
        }
    }

    @Configuration
    @ConditionalOnClass(AssignHelper.class)
    static class AssignHelperAutoConfiguration {

        @Autowired
        private HandlebarsViewResolver handlebarsViewResolver;

        @PostConstruct
        public void registerHelper() {
            handlebarsViewResolver.registerHelper("assign", AssignHelper.INSTANCE);
        }
    }

    @Configuration
    @ConditionalOnClass(IncludeHelper.class)
    static class IncludeHelperAutoConfiguration {

        @Autowired
        private HandlebarsViewResolver handlebarsViewResolver;

        @PostConstruct
        public void registerHelper() {
            handlebarsViewResolver.registerHelper("include", IncludeHelper.INSTANCE);
        }
    }

    @Configuration
    @ConditionalOnClass(MarkdownHelper.class)
    static class MarkdownHelperAutoConfiguration {

        @Autowired
        private HandlebarsViewResolver handlebarsViewResolver;

        @PostConstruct
        public void registerHelper() {
            handlebarsViewResolver.registerHelper("md", MarkdownHelper.INSTANCE);
        }
    }

    @Configuration
    @ConditionalOnClass(NumberHelper.class)
    static class NumberHelpersAutoConfiguration {

        @Autowired
        private HandlebarsViewResolver handlebarsViewResolver;

        @PostConstruct
        public void registerHelpers() {
            NumberHelper.register(handlebarsViewResolver.getHandlebars());
        }
    }

    @Configuration
    @ConditionalOnClass(HumanizeHelper.class)
    static class HumanizeHelpersAutoConfiguration {

        @Autowired
        private HandlebarsViewResolver handlebarsViewResolver;

        @PostConstruct
        public void registerHelpers() {
            HumanizeHelper.register(handlebarsViewResolver.getHandlebars());
        }
    }

    @Configuration
    @ConditionalOnClass({JodaHelper.class, DateTimeFormat.class})
    static class JodaHelpersAutoConfiguration {

        @Autowired
        private HandlebarsViewResolver handlebarsViewResolver;

        @PostConstruct
        public void registerHelpers() {
            handlebarsViewResolver.registerHelpers(JodaHelper.class);
        }
    }

    @Configuration
    @ConditionalOnClass(StringHelpers.class)
    static class StringHelpersAutoConfiguration {

        @Autowired
        private HandlebarsViewResolver handlebarsViewResolver;

        @PostConstruct
        public void registerHelpers() {
            StringHelpers.register(handlebarsViewResolver.getHandlebars());
        }
    }

    @Bean
    public BeanPostProcessor handlebarsBeanPostProcessor(final HandlebarsViewResolver handlebarsViewResolver) {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                HandlebarsHelper annotation = findAnnotation(bean.getClass(), HandlebarsHelper.class);
                if (annotation != null) {
                    if (bean instanceof Helper<?> helper) {
                        HelperFunction fnann = findAnnotation(bean.getClass(), HelperFunction.class);
                        handlebarsViewResolver.registerHelper(fnann.value(), helper);
                    } else {
                        handlebarsViewResolver.registerHelpers(bean);
                    }
                }
                return bean;
            }
        };
    }
}
