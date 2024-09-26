package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class PostControllerTest {
    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    private PostDto postDto;
    private Long id = 1L;

    @BeforeEach
    void setUp() {
        postDto = new PostDto();
        postDto.setId(1L);
        postDto.setContent("content");
    }

    @Test
    void testPostCreateByUser() {
        postDto.setAuthorId(2L);
        postDto.setContent("content");

        postController.create(postDto);
        verify(postService, times(1)).createPost(postDto);
    }

    @Test
    void testPostCreateByProject() {
        postDto.setProjectId(2L);

        postController.create(postDto);
        verify(postService, times(1)).createPost(postDto);
    }

    @Test
    void testPublish() {
        postController.publish(id);
        verify(postService, times(1)).publishPost(id);
    }

    @Test
    void testDelete() {
        postController.markDeleted(id);
        verify(postService, times(1)).deletePost(id);
    }

    @Test
    void testUpdate() {
        postController.update(postDto);
        verify(postService, times(1)).updatePost(postDto);
    }

    @Test
    void testGet() {
        postController.getPost(id);
        verify(postService, times(1)).getPost(id);
    }

    @Test
    void testGetAllByAuthorId() {
        postController.getAllNonPublishedByAuthorId(id);
        verify(postService, times(1)).getAllNonPublishedByAuthorId(id);
    }

    @Test
    void testGetAllByProjectId() {
        postController.getAllNonPublishedByProjectId(id);
        verify(postService, times(1)).getAllNonPublishedByProjectId(id);
    }

    @Test
    void testGetAllPublishedByAuthorId() {
        postController.getAllPublishedByAuthorId(id);
        verify(postService, times(1)).getAllPublishedByAuthorId(id);
    }

    @Test
    void testGetAllPublishedByProjectId() {
        postController.getAllPublishedByProjectId(id);
        verify(postService, times(1)).getAllPublishedByProjectId(id);
    }
}