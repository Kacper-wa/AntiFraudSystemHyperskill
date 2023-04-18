package antifraud.constraints;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = IPv4Format.IPv4FormatValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface IPv4Format {

    String message() default "Wrong IP format";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    class IPv4FormatValidator implements ConstraintValidator<IPv4Format, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null) {
                return false;
            }
            String[] parts = value.split("\\.");
            if (parts.length != 4) {
                return false;
            }
            for (String s : parts) {
                int i = Integer.parseInt(s);
                if ((i < 0) || (i > 255)) {
                    return false;
                }
            }
            return true;
        }
    }
}
