package antifraud.constraints;

import antifraud.util.UserAccess;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Constraint(validatedBy = AccessValid.AccessValidValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AccessValid {

    String message() default "Invalid access";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };

    class AccessValidValidator implements ConstraintValidator<AccessValid, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            List<UserAccess> userAccesses = List.of(UserAccess.values());
            for (UserAccess userAccess : userAccesses) {
                if (userAccess.name().equals(value)) {
                    return true;
                }
            }
            return false;
        }
    }
}
