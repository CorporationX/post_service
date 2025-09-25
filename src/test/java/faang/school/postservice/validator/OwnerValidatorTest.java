package faang.school.postservice.validator;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.MoreOneOwnerException;
import faang.school.postservice.exception.NotResourceOwnerException;
import faang.school.postservice.exception.OwnerIdNotPresentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
class OwnerValidatorTest {
    @InjectMocks
    private OwnerValidator validator;
    @Mock
    private UserContext userContext;

    private static final long USER_ID = 2;
    private static final long PROJECT_ID = 1;
    private static final long OTHER_ID = 3;

    @ParameterizedTest
    @CsvSource(value = {
            "2, null, 2",
            "null, 1, 1"
    }, nullValues = {"null"})
    @DisplayName("Успешная валидация - если только один владелец")
    void positive_whenOneOwnerPresent_shouldValidate(Long userId, Long projectId, Long currentId) {
        when(userContext.getUserId()).thenReturn(currentId);

        validator.validateOwnerIds(userId, projectId);
    }

    @Test
    @DisplayName("Ошибка валидации - указан и user и project")
    void negative_whenUserAndProjectIdPresent_throwsError() {
        assertThrows(MoreOneOwnerException.class,
                () -> validator.validateOwnerIds(USER_ID, PROJECT_ID));
    }

    @Test
    @DisplayName("Ошибка валидации - не указан ни один владелец")
    void negative_whenUserAndProjectIdNotPresent_throwsError() {
        assertThrows(OwnerIdNotPresentException.class,
                () -> validator.validateOwnerIds(null, null));
    }

    @Test
    @DisplayName("Ошибка валидации - текущий пользователь не владелец")
    void negative_whenNotOwner_throwsError() {
        when(userContext.getUserId()).thenReturn(OTHER_ID);

        assertThrows(NotResourceOwnerException.class,
                () -> validator.validateOwner(USER_ID));
    }
}