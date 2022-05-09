package ua.com.cruises.model.tags;

import jakarta.servlet.jsp.jstl.fmt.LocalizationContext;
import jakarta.servlet.jsp.tagext.TagSupport;
import org.apache.taglibs.standard.tag.common.fmt.BundleSupport;

import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;

/*
* Tag designed to provide all subclasses a simple way to be internationalized
* */
public class LocalizedTag extends TagSupport {

    //Method receives a predefined key from language resources properties
    protected Optional<String> localeMessage(String key) {
        LocalizationContext locCtxt = BundleSupport.getLocalizationContext(this.pageContext);
        if (locCtxt != null) {
            ResourceBundle bundle = locCtxt.getResourceBundle();
            if (bundle != null) {
                try {
                    String message = bundle.getString(key);
                    return Optional.of(message);
                } catch (MissingResourceException var10) {
                    return Optional.empty();
                }
            }
        }
        return Optional.empty();
    }
}
