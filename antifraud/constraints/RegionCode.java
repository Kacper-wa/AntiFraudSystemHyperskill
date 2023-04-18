package antifraud.constraints;

import antifraud.util.RegionCodes;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Constraint(validatedBy = RegionCode.RegionCodeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RegionCode {

    String message() default "Invalid region code";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    class RegionCodeValidator implements ConstraintValidator<RegionCode, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            List<RegionCodes> regionCodes = List.of(RegionCodes.values());
            for (RegionCodes regionCode : regionCodes) {
                if (regionCode.name().equals(value)) {
                    return true;
                }
            }
            return false;
        }
    }
}
