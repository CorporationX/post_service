package faang.school.postservice.dto.comment;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SortingStrategyDtoTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    @DisplayName("Creating SortingStrategyDto")
    void sortingStrategyDtoTest_CreateSortingStrategyDto() {
        SortingStrategyDto sortingStrategyDto = initSortingStrategyDto(SortingBy.UPDATED_AT, SortingOrder.ASC);

        Set<ConstraintViolation<SortingStrategyDto>> violations = validator.validate(sortingStrategyDto);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Creating SortingStrategyDto with null sorting by field")
    void sortingStrategyDtoTest_CreateSortingStrategyDtoWithNullSortingByField() {
        SortingStrategyDto sortingStrategyDto = initSortingStrategyDto(null, SortingOrder.ASC);

        Set<ConstraintViolation<SortingStrategyDto>> violations = validator.validate(sortingStrategyDto);

        assertEquals(1, violations.size());
        assertEquals("Filed for sorting is required", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Creating SortingStrategyDto with null sorting order")
    void sortingStrategyDtoTest_CreateSortingStrategyDtoWithNullSortingOrder() {
        SortingStrategyDto sortingStrategyDto = initSortingStrategyDto(SortingBy.UPDATED_AT, null);

        Set<ConstraintViolation<SortingStrategyDto>> violations = validator.validate(sortingStrategyDto);

        assertEquals(1, violations.size());
        assertEquals("Sorting order is required", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Creating SortingStrategyDto with all violations")
    void sortingStrategyDtoTest_CreateSortingStrategyDtoWithAllViolations() {
        SortingStrategyDto sortingStrategyDto = initSortingStrategyDto(null, null);

        Set<ConstraintViolation<SortingStrategyDto>> violations = validator.validate(sortingStrategyDto);

        assertEquals(2, violations.size());
        assertTrue(violations.stream()
                .map(ConstraintViolation::getMessage)
                .toList()
                .containsAll(List.of("Filed for sorting is required", "Sorting order is required")));
    }

    private SortingStrategyDto initSortingStrategyDto(SortingBy field, SortingOrder order) {
        return new SortingStrategyDto(field, order);
    }
}
