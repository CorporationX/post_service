package faang.school.postservice.service;

import faang.school.postservice.util.TestData;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static faang.school.postservice.exception.MessagesForCommentsException.NO_POST_IN_DB;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Spy
    private TestData testData;

    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    private long postId;

    private Post post;

    @BeforeEach
    void init() {
        postId = 2L;
        post = new Post();
        post.setId(testData.returnPostDto().getId());
    }

    @Test
    void testForGetPostByIdIfNoPostInDB() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> postService.getPostById(postId));
        assertEquals(NO_POST_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    void testForGetPostByIdrReturnPostDto() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertEquals(postService.getPostById(postId), testData.returnPostDto());
    }
}
