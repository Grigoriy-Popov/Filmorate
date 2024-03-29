package ru.yandex.practicum.filmorate.exceptions.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = AfterValidator.class)
public @interface AfterCinemaBirthday {

    String message() default "{Release date must be after 28.12.1895)}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
