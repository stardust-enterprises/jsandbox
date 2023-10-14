package enterprises.stardust.jsandbox.rt.annotation;

import java.lang.annotation.*;

/**
 * Marks a class as a proxy for another class.
 * <p>
 * This tells the <a href="https://github.com/xtrm-en/postprocessor">postprocessor</a> Gradle plugin to generate, if
 * not defined:
 * <ul>
 *     <li>a private constructor that takes the proxied class as a parameter</li>
 *     <li>a field that holds the proxied class</li>
 *     <li>every static method of the proxied class</li>
 *     <li>every non-static method of the proxied class</li>
 * </ul>
 * <p>
 * <b>Note</b>: the proxied methods that are annotated with {@link sun.reflect.CallerSensitive} will be ignored and
 * give out a warning.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Proxy {
    Class<?> value();
}
