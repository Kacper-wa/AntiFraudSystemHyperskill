package antifraud.constraints;

import antifraud.util.Feedback;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Constraint(validatedBy = FeedbackValid.FeedbackValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface FeedbackValid {

    String message() default "Wrong feedback format";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };


    class FeedbackValidator implements ConstraintValidator<FeedbackValid, String> {
        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            List<Feedback> feedbacks = List.of(Feedback.values());
            for (Feedback feedback : feedbacks) {
                if (feedback.name().equals(value)) {
                    return true;
                }
            }
            return false;
        }
    }
}
