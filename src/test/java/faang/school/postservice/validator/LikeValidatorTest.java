package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataLikeValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeValidatorTest {

    @InjectMocks
    private LikeValidator likeValidator;

    @Mock
    private UserServiceClient userServiceClient;

    private LikeDto likeDto;
    private Long id;

    @BeforeEach
    public void setUp() {
        likeDto = new LikeDto();
    }

    @Test
    public void testCheckIsNull_withIdNull() {
        id = null;
        assertThrows(DataLikeValidation.class, () -> likeValidator.checkIsNull(id, likeDto));
    }

    @Test
    public void testCheckIsNull_withLikeDtoNull() {
        likeDto = null;
        id = 1L;
        assertThrows(DataLikeValidation.class, () -> likeValidator.checkIsNull(id, likeDto));
    }

    @Test
    public void testCheckExistAuthor() {
        likeDto = LikeDto.builder().userId(1L).build();
        when(userServiceClient.getUser(likeDto.getUserId())).thenReturn(null);
        assertThrows(DataLikeValidation.class, () -> likeValidator.checkExistAuthor(likeDto));
    }
}