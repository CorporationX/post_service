package faang.school.postservice.exception.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Predicate;
import java.util.function.Supplier;

@AllArgsConstructor
@Getter
public class ErrorRule {
    private Predicate<Integer> condition;
    private Supplier<RuntimeException> exceptionSupplier;
}