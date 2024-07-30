package faang.school.postservice.post;

import faang.school.postservice.controller.PostController;
import faang.school.postservice.dto.Post.PostDto;
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
    @InjectMocks
    private PostController controller;

    @Test
    public void testCreateDraftPost() {
        PostDto dto = new PostDto();
        dto.setAuthorId(1L);
        dto.setContent("Test");
        controller.createDraftPost(dto);
        verify(service, times(1)).createDraftPost(dto);
    }

    @Test
    public void testBothUserAndProject() {
        PostDto dto = new PostDto();
        dto.setAuthorId(1L);
        dto.setProjectId(1L);
        dto.setContent("Test");
        assertThrows(WrongInputException.class, () -> controller.createDraftPost(dto));
    }

    @Test
    public void testNullOrEmptyMessage() {
        PostDto dto = new PostDto();
        dto.setAuthorId(1L);
        dto.setContent(null);
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
        PostDto dto = new PostDto();
        dto.setAuthorId(1L);
        dto.setContent("Test");
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
        controller.getPost(id);
        verify(service, times(1)).getPost(id);
    }

    @Test
    public void testgetDraftPostsForUser() {
        Long id = 1L;
        PostDto dto = new PostDto();
        dto.setPublished(false);
        dto.setAuthorId(id);
        controller.getDraftPostsForUser(id);
        verify(service, times(1)).getSortedPosts(dto);
    }
}
