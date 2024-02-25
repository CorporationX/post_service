package faang.school.postservice.validator;

import faang.school.postservice.dto.AdDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdValidatorTest {
    @InjectMocks
    AdValidator adValidator;

    @Mock
    AdRepository adRepository;
    @Mock
    PostRepository postRepository;

    AdDto adDto;

    @BeforeEach
    public void setup() {
        adDto = AdDto.builder().id(1L).postId(1L).build();
    }

    @Test
    public void shouldThrowExceptionIfAdExists() {
        when(adRepository.existsById(1L)).thenReturn(true);

        assertThrows(DataValidationException.class,
                () -> adValidator.validate(adDto));
    }

    @Test
    public void shouldThrowExceptionIfPostNotFound() {
        when(postRepository.existsById(1L)).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> adValidator.validate(adDto));
    }
}
