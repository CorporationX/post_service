package faang.school.postservice.service.post;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationExceptions;
import faang.school.postservice.exception.NotFoundEntityException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void validationAndPostReceived_ShouldReturnPost_WhenValidPostIdIsProvided() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        LikeDto likeDto = LikeDto.builder().postId(postId).build();
        when(postRepository.existsById(postId)).thenReturn(true);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post result = postService.validationAndPostReceived(likeDto);

        assertEquals(post, result);
    }

    @Test
    void validationAndPostReceived_ShouldThrowDataValidationExceptions_WhenPostIdIsNull() {
        LikeDto likeDto = LikeDto.builder().postId(null).build();

        assertThrows(DataValidationExceptions.class, () ->
                postService.validationAndPostReceived(likeDto));
    }

    @Test
    void validationAndPostReceived_ShouldThrowDataValidationExceptions_WhenPostDoesNotExist() {
        Long postId = 1L;
        LikeDto likeDto = LikeDto.builder().postId(postId).build();
        when(postRepository.existsById(postId)).thenReturn(false);

        assertThrows(DataValidationExceptions.class, () ->
                postService.validationAndPostReceived(likeDto));
    }

    @Test
    void validationAndPostReceived_ShouldThrowNotFoundElementException_WhenPostNotFound() {
        Long postId = 1L;
        LikeDto likeDto = LikeDto.builder().postId(postId).build();
        when(postRepository.existsById(postId)).thenReturn(true);
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(NotFoundEntityException.class, () ->
                postService.validationAndPostReceived(likeDto));
    }
}