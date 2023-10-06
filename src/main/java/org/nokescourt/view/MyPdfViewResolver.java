package org.nokescourt.view;

import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import java.util.Locale;

public class MyPdfViewResolver extends WebApplicationObjectSupport implements ViewResolver, Ordered {
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
