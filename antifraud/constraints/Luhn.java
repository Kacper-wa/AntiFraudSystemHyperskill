package antifraud.constraints;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = Luhn.LuhnValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface Luhn {
    String message() default "Card number does not pass Luhn algorithm";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    class LuhnValidator implements ConstraintValidator<Luhn, String> {

            @Override
            public boolean isValid(String value, ConstraintValidatorContext context) {
                if (value == null || value.isEmpty()) {
                    return false;
                }
                int sum = 0;
                boolean alternate = false;
                for (int i = value.length() - 1; i >= 0; i--) {
                    int n = Integer.parseInt(value.substring(i, i + 1));
                    if (alternate) {
                        n *= 2;
                        if (n > 9) {
                            n = (n % 10) + 1;
                        }
                    }
                    sum += n;
                    alternate = !alternate;
                }
                return (sum % 10 == 0);
            }
    }
}
