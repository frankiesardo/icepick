package icepick;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface State {
    Class<? extends Bundler> bundler() default Bundler.class;
    Class<? extends Setuper> setuper() default null;
}
