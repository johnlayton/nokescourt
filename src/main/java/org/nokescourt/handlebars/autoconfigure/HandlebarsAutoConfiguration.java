package org.nokescourt.handlebars.autoconfigure;

import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.cache.GuavaTemplateCache;
import com.github.jknack.handlebars.cache.TemplateCache;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.github.jknack.handlebars.io.TemplateSource;
import org.nokescourt.handlebars.springmvc.HandlebarsViewResolver;
import jakarta.annotation.PostConstruct;
import org.nokescourt.view.HandlebarsPdfViewResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.google.common.cache.CacheBuilder.newBuilder;

@ConditionalOnProperty(prefix = "handlebars", value = "enabled", havingValue = "true", matchIfMissing = true)
@Configuration
@EnableConfigurationProperties(HandlebarsProperties.class)
@ConditionalOnWebApplication
public class HandlebarsAutoConfiguration {

    @Configuration
    protected static class HandlebarsViewResolverConfiguration {
        @Autowired
        private HandlebarsProperties handlebars;

        @Bean
        public HandlebarsViewResolver handlebarsViewResolver() {
            HandlebarsViewResolver handlebarsViewResolver = new HandlebarsViewResolver();
            handlebars.applyToMvcViewResolver(handlebarsViewResolver);
            return handlebarsViewResolver;
        }

        @Bean
        public HandlebarsPdfViewResolver handlebarsPdfViewResolver() {
            HandlebarsPdfViewResolver handlebarsViewResolver = new HandlebarsPdfViewResolver();
            handlebars.applyToMvcViewResolver(handlebarsViewResolver);
            return handlebarsViewResolver;
        }
    }

    @Configuration
    protected static class HandlebarsCacheConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public TemplateCache templateCache() {
            return new GuavaTemplateCache(newBuilder().<TemplateSource, Template>build());
        }
    }

    @Configuration
    protected static class HandlebarsCachingStrategyConfiguration {
        @Autowired
        private HandlebarsViewResolver handlebarsViewResolver;
        @Autowired
        private HandlebarsPdfViewResolver handlebarsPdfViewResolver;

        @Autowired
        private TemplateCache templateCacheInstance;

        @PostConstruct
        public void setCachingStrategy() {
            if (handlebarsViewResolver.isCache()) {
                handlebarsViewResolver.getHandlebars().with(templateCacheInstance);
            }
            if (handlebarsPdfViewResolver.isCache()) {
                handlebarsPdfViewResolver.getHandlebars().with(templateCacheInstance);
            }
        }
    }

    @Configuration
    @ConditionalOnBean(TemplateLoader.class)
    protected static class HandlebarsTemplateLoaderConfiguration {
        @Autowired
        private HandlebarsViewResolver handlebarsViewResolver;
        @Autowired
        private HandlebarsPdfViewResolver handlebarsPdfViewResolver;

        @Autowired
        private TemplateLoader templateLoader;

        @PostConstruct
        public void setTemplateLoader() {
            handlebarsViewResolver.getHandlebars().with(templateLoader);
            handlebarsPdfViewResolver.getHandlebars().with(templateLoader);
        }
    }

    @Configuration
    @ConditionalOnProperty("handlebars.prettyPrint")
    protected static class PrettyPrintConfiguration {
        @Autowired
        private HandlebarsViewResolver handlebarsViewResolver;
        @Autowired
        private HandlebarsPdfViewResolver handlebarsPdfViewResolver;

        @PostConstruct
        public void setPrettyPrint() {
            handlebarsViewResolver.getHandlebars().prettyPrint(true);
            handlebarsPdfViewResolver.getHandlebars().prettyPrint(true);
        }
    }

    @Configuration
    @ConditionalOnProperty("handlebars.infiniteLoops")
    protected static class InfiniteLoopsConfiguration {
        @Autowired
        private HandlebarsViewResolver handlebarsViewResolver;
        @Autowired
        private HandlebarsPdfViewResolver handlebarsPdfViewResolver;

        @PostConstruct
        public void setInfiniteLoops() {
            handlebarsViewResolver.getHandlebars().infiniteLoops(true);
            handlebarsPdfViewResolver.getHandlebars().infiniteLoops(true);
        }
    }
}
