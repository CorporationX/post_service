package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.comment.CommentServiceImpl;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeValidatorTest {
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PostService postService;
    @Mock
    private CommentServiceImpl commentService;
    @InjectMocks
    private LikeValidator likeValidator;
    private LikeDto likeDto;

    @BeforeEach
    void setUp() {
        likeDto = LikeDto.builder()
                .id(1L)
                .commentId(2L)
                .userId(3L)
                .build();
    }

    @Test
    public void validate() {
        when(userServiceClient.getUser(anyLong())).thenReturn(new UserDto());
        when(commentService.existsById(anyLong())).thenReturn(true);
        likeValidator.validate(likeDto);
        verify(userServiceClient).getUser(anyLong());
        verify(commentService).existsById(anyLong());
    }
}