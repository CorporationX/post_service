package faang.school.postservice.PostService;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.exception.InvalidPostAuthorsException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static faang.school.postservice.utils.validationUtils.PostValidation.CANT_DELETE_POST_DURING_UPDATE;
import static faang.school.postservice.utils.validationUtils.PostValidation.CONTENT_CANT_BE_NULL;
import static faang.school.postservice.utils.validationUtils.PostValidation.POST_ALREADY_PUBLISHED;
import static faang.school.postservice.utils.validationUtils.PostValidation.POST_AUTHORS_ERROR;
import static faang.school.postservice.utils.validationUtils.PostValidation.POST_DRAFT_CANT_BE_DELETED;
import static faang.school.postservice.utils.validationUtils.PostValidation.POST_DRAFT_CANT_BE_PUBLISHED;
import static faang.school.postservice.utils.validationUtils.PostValidation.PROJECT_ID_CANT_BE_NULL;
import static faang.school.postservice.utils.validationUtils.PostValidation.USER_ID_CANT_BE_NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostValidationTest {
    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    private PostRequestDto postRequestDto;
    private Post post;
    private final Long id = 1L;

    @BeforeEach
    public void startUp() {
        postRequestDto = new PostRequestDto(1L, "content", 1L,
                null, false, false);
        post = Post.builder().id(1L).content("content").authorId(1L).build();
    }

    @Test
    public void testCreateDraftPost_bothAuthorsAbsent() {
        postRequestDto.setAuthorId(null);
        InvalidPostAuthorsException exception = assertThrows(InvalidPostAuthorsException.class,
                () -> postService.createDraftPost(postRequestDto)
        );
        assertEquals(POST_AUTHORS_ERROR, exception.getMessage());
    }

    @Test
    public void testCreateDraftPost_bothAuthorsSet() {
        postRequestDto.setProjectId(1L);
        InvalidPostAuthorsException exception = assertThrows(InvalidPostAuthorsException.class,
                () -> postService.createDraftPost(postRequestDto)
        );
        assertEquals(POST_AUTHORS_ERROR, exception.getMessage());
    }

    @Test
    public void testCreateDraftPost_nullContent() {
        postRequestDto.setContent(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.createDraftPost(postRequestDto)
        );
        assertEquals(CONTENT_CANT_BE_NULL, exception.getMessage());
    }

    @Test
    public void testCreateDraftPost_postAlreadyDeleted() {
        postRequestDto.setDeleted(true);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.createDraftPost(postRequestDto)
        );
        assertEquals(POST_DRAFT_CANT_BE_DELETED, exception.getMessage());
    }

    @Test
    public void testCreateDraftPost_postAlreadyPublished() {
        postRequestDto.setPublished(true);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.createDraftPost(postRequestDto)
        );
        assertEquals(POST_DRAFT_CANT_BE_PUBLISHED, exception.getMessage());
    }

    @Test
    public void testPublishPost_postAlreadyPublished() {
        when(postRepository.findById(postRequestDto.getId())).thenReturn(Optional.of(post));
        post.setPublished(true);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.publishPost(id)
        );
        assertEquals(String.format(POST_ALREADY_PUBLISHED, post.getId()), exception.getMessage());
    }

    @Test
    public void testUpdatePost_deletePost() {
        postRequestDto.setDeleted(true);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.updatePost(postRequestDto)
        );
        assertEquals(CANT_DELETE_POST_DURING_UPDATE, exception.getMessage());
    }

    @Test
    public void testUpdatePost_nullContent() {
        postRequestDto.setContent(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.updatePost(postRequestDto)
        );
        assertEquals(CONTENT_CANT_BE_NULL, exception.getMessage());
    }

    @Test
    public void testGetUserDraftPosts_nullUserId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.getUserDraftPosts(null)
        );
        assertEquals(USER_ID_CANT_BE_NULL, exception.getMessage());
    }

    @Test
    public void testProjectDraftPosts_nullUserId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.getProjectDraftPosts(null)
        );
        assertEquals(PROJECT_ID_CANT_BE_NULL, exception.getMessage());
    }

    @Test
    public void testUserPublishedPosts_nullUserId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.getUserPublishedPosts(null)
        );
        assertEquals(USER_ID_CANT_BE_NULL, exception.getMessage());
    }

    @Test
    public void testProjectPublishedPosts_nullUserId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.getProjectPublishedPosts(null)
        );
        assertEquals(PROJECT_ID_CANT_BE_NULL, exception.getMessage());
    }
}
