package htmlGenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.TYPE , ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface HtmlElement {

    
    HTMLType type() default HTMLType.DIV;
    
    
    
}
