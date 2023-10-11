package org.nokescourt.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.springframework.web.servlet.view.xml.MappingJackson2XmlView;

import java.util.Locale;

public class XmlViewResolver implements ViewResolver {

    @Override
    public View resolveViewName(@NonNull final String viewName,
                                @NonNull final Locale locale)
            throws Exception {
        MappingJackson2XmlView view = new MappingJackson2XmlView();
        view.setPrettyPrint(true);
        return view;
    }

//    private Marshaller marshaller;
//
//    @Autowired
//    public MarshallingXmlViewResolver(Marshaller marshaller) {
//        this.marshaller = marshaller;
//    }
//
//    /**
//     * Get the view to use.
//     *
//     * @return Always returns an instance of {@link MappingJacksonJsonView}.
//     */
//    @Override
//    public View resolveViewName(String viewName, Locale locale)
//            throws Exception {
//        MarshallingView view = new MarshallingView();
//        view.setMarshaller(marshaller);
//        return view;
//    }
}
