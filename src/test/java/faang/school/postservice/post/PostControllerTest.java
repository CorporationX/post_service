package faang.school.postservice.post;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.controller.PostController;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.WrongInputException;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {
    @Mock
    private PostService service;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private PostController controller;

    @Test
    public void testCreateDraftPost() {
        PostDto dto = PostDto.builder()
                .authorId(1L)
                .content("Test")
                .build();
        controller.createDraftPost(dto);
        verify(service, times(1)).createDraftPost(dto);
    }

    @Test
    public void testBothUserAndProject() {
        PostDto dto = PostDto.builder()
                .authorId(1L)
                .projectId(1L)
                .content("Test")
                .build();
        assertThrows(WrongInputException.class, () -> controller.createDraftPost(dto));
    }

    @Test
    public void testPublishPost() {
        Long draftId = 1L;
        controller.publishPost(draftId);
        verify(service, times(1)).publishPost(draftId);
    }

    @Test
    public void testUpdatePost() {
        Long id = 1L;
        PostDto dto = PostDto.builder()
                .authorId(1L)
                .content("Test")
                .build();
        controller.updatePost(id, dto);
        verify(service, times(1)).updatePost(1L, dto);
    }

    @Test
    public void testDeletePost() {
        Long id = 1L;
        controller.deletePost(id);
        verify(service, times(1)).deletePost(id);
    }

    @Test
    public void testGetPost() {
        Long id = 1L;
        Long userId = 1L;
        controller.getPost(id);
        verify(service, times(1)).getPost(id);
    }

    @Test
    public void testGetDraftPostsForUser() {
        Long id = 1L;
        PostDto dto = PostDto.builder()
                .content("notUsed")
                .published(false)
                .authorId(id)
                .build();
        Long userId = 1L;
        controller.getDraftPostsForUser(id);
        verify(service, times(1)).getPostsSortedByDate(dto);
    }
}
