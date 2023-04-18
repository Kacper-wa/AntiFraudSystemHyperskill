package antifraud.constraints;

import antifraud.util.UserRoles;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Constraint(validatedBy = RoleValid.RoleValidValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RoleValid {

        String message() default "Invalid role";

        Class<?>[] groups() default { };

        Class<? extends Payload>[] payload() default { };

        class RoleValidValidator implements ConstraintValidator<RoleValid, String> {

            @Override
            public boolean isValid(String value, ConstraintValidatorContext context) {
                List<UserRoles> userRoles = List.of(UserRoles.values());
                for (UserRoles userRole : userRoles) {
                    if (userRole.name().equals(value)) {
                        return true;
                    }
                }
                return false;
            }
        }
}
