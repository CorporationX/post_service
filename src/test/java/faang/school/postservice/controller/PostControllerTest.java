package faang.school.postservice.controller;

import faang.school.postservice.model.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {
    private static final long POST_ID = 1L;
    private static final long AUTHOR_ID = 2L;
    private static final long PROJECT_ID = 3L;
    private static final String HASHTAG = "#test";

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    private PostDto createTestPostDto(long id, String content) {
        PostDto postDto = new PostDto();
        postDto.setId(id);
        postDto.setContent(content);
        return postDto;
    }

    @Test
    void testCreatePost_Success() {
        PostDto postDto = createTestPostDto(POST_ID, "Test content");

        when(postService.createPost(any(PostDto.class))).thenReturn(postDto);

        PostDto result = postController.createPost(postDto).getBody();

        assertEquals(POST_ID, result.getId());
        assertEquals("Test content", result.getContent());
        verify(postService, times(1)).createPost(any(PostDto.class));
    }

    @Test
    void testPublishPost_Success() {
        PostDto postDto = createTestPostDto(POST_ID, "Published post");

        when(postService.publishPost(POST_ID)).thenReturn(postDto);

        PostDto result = postController.publishPost(POST_ID).getBody();

        assertEquals(POST_ID, result.getId());
        assertEquals("Published post", result.getContent());
        verify(postService, times(1)).publishPost(POST_ID);
    }

    @Test
    void testUpdatePost_Success() {
        PostDto postDto = createTestPostDto(POST_ID, "Updated post");

        when(postService.updatePost(eq(POST_ID), any(PostDto.class))).thenReturn(postDto);

        PostDto result = postController.updatePost(POST_ID, postDto).getBody();

        assertEquals(POST_ID, result.getId());
        assertEquals("Updated post", result.getContent());
        verify(postService, times(1)).updatePost(eq(POST_ID), any(PostDto.class));
    }

    @Test
    void testDeletePost_Success() {
        doNothing().when(postService).deletePost(POST_ID);

        postController.deletePost(POST_ID);

        verify(postService, times(1)).deletePost(POST_ID);
    }

    @Test
    void testGetPost_Success() {
        PostDto postDto = createTestPostDto(POST_ID, "Test post");

        when(postService.getPost(POST_ID)).thenReturn(postDto);

        PostDto result = postController.getPost(POST_ID).getBody();

        assertEquals(POST_ID, result.getId());
        assertEquals("Test post", result.getContent());
        verify(postService, times(1)).getPost(POST_ID);
    }

    @Test
    void testGetUserDrafts_Success() {
        PostDto postDto = createTestPostDto(POST_ID, "User draft");

        when(postService.getUserDrafts(AUTHOR_ID)).thenReturn(List.of(postDto));

        List<PostDto> result = postController.getUserDrafts(AUTHOR_ID).getBody();

        assertEquals(1, result.size());
        assertEquals("User draft", result.get(0).getContent());
        verify(postService, times(1)).getUserDrafts(AUTHOR_ID);
    }

    @Test
    void testGetProjectDrafts_Success() {
        PostDto postDto = createTestPostDto(POST_ID, "Project draft");

        when(postService.getProjectDrafts(PROJECT_ID)).thenReturn(List.of(postDto));

        List<PostDto> result = postController.getProjectDrafts(PROJECT_ID).getBody();

        assertEquals(1, result.size());
        assertEquals("Project draft", result.get(0).getContent());
        verify(postService, times(1)).getProjectDrafts(PROJECT_ID);
    }

    @Test
    void testGetUserPublishedPosts_Success() {
        PostDto postDto = createTestPostDto(POST_ID, "Published user post");

        when(postService.getUserPublishedPosts(AUTHOR_ID)).thenReturn(List.of(postDto));

        List<PostDto> result = postController.getUserPublishedPosts(AUTHOR_ID).getBody();

        assertEquals(1, result.size());
        assertEquals("Published user post", result.get(0).getContent());
        verify(postService, times(1)).getUserPublishedPosts(AUTHOR_ID);
    }

    @Test
    void testGetProjectPublishedPosts_Success() {
        PostDto postDto = createTestPostDto(POST_ID, "Published project post");

        when(postService.getProjectPublishedPosts(PROJECT_ID)).thenReturn(List.of(postDto));

        List<PostDto> result = postController.getProjectPublishedPosts(PROJECT_ID).getBody();

        assertEquals(1, result.size());
        assertEquals("Published project post", result.get(0).getContent());
        verify(postService, times(1)).getProjectPublishedPosts(PROJECT_ID);
    }

    @Test
    void testGetAllPostsByHashtag_Success() {
        Page<PostDto> page = new PageImpl<>(List.of(createTestPostDto(POST_ID, "Post with hashtag")));

        when(postService.getAllPostsByHashtagId(eq(HASHTAG), any(Pageable.class))).thenReturn(page);

        Page<PostDto> result = postController.getAllPostsByHashtag(HASHTAG, Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals("Post with hashtag", result.getContent().get(0).getContent());
        verify(postService, times(1)).getAllPostsByHashtagId(eq(HASHTAG), any(Pageable.class));
    }
}