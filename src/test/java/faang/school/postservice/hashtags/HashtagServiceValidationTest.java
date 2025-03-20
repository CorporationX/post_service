package faang.school.postservice.hashtags;

import faang.school.postservice.dto.hashtag.HashtagRequestDto;
import faang.school.postservice.service.hashtags.HashtagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static faang.school.postservice.utils.validationUtils.HashtagValidation.HASHTAG_REQUEST_DTO_CANT_BE_NULL;
import static faang.school.postservice.utils.validationUtils.HashtagValidation.PAGE_IN_REQUEST_DTO_CANT_BE_NEGATIVE;
import static faang.school.postservice.utils.validationUtils.HashtagValidation.SIZE_IN_REQUEST_DTO_CANT_BE_NEGATIVE;
import static faang.school.postservice.utils.validationUtils.HashtagValidation.TAG_IN_REQUEST_DTO_CANT_BE_NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class HashtagServiceValidationTest {
    @InjectMocks
    private HashtagService hashtagService;

    private final int maxCachedPosts = 100;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(hashtagService, "maxCachedPosts", maxCachedPosts);
    }

    @Test
    public void testGetPostsByHashtag_nullHashtagRequestDto() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> hashtagService.getPostsByHashtag(null)
        );
        assertEquals(HASHTAG_REQUEST_DTO_CANT_BE_NULL, exception.getMessage());
    }

    @Test
    public void testGetPostsByHashtag_invalidHashtagRequestDto() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> hashtagService.getPostsByHashtag(new HashtagRequestDto(null, -1, -3))
        );
        List<String> errors = List.of(
                TAG_IN_REQUEST_DTO_CANT_BE_NULL,
                PAGE_IN_REQUEST_DTO_CANT_BE_NEGATIVE,
                SIZE_IN_REQUEST_DTO_CANT_BE_NEGATIVE
        );
        assertEquals(String.join(", ", errors), exception.getMessage());
    }
}
