package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {
    @Mock
    private PostService postService;
    @InjectMocks
    private PostController postController;
    private final PostDto postDto = PostDto.builder().id(1L).build();
    private List<MultipartFile> files;

    @BeforeEach
    void init () {
        MultipartFile multipartFileMock = mock(MultipartFile.class);
        files = new ArrayList<>(List.of(multipartFileMock, multipartFileMock, multipartFileMock));
    }

    @Test
    void testCreatePost () {
        when(postService.createPost(postDto, files)).thenReturn(postDto);

        PostDto postByController = postController.createPost(postDto, files);

        assertEquals (postDto, postByController);
        verify(postService, times(1)).createPost(postDto, files);
    }

    @Test
    void testUpdatePost () {
        Long postId = postDto.getId();

        when(postService.updatePost(postId, postDto, files)).thenReturn(postDto);

        PostDto postByController = postController.updatePost(postId, postDto, files);

        assertEquals (postDto, postByController);
        verify(postService, times(1)).updatePost(postId, postDto, files);
    }

    @Test
    void testGetPost () {
        Long postId = postDto.getId();

        when(postService.getPostDto(postId)).thenReturn(postDto);

        PostDto postByController = postController.getPost(postId);

        assertEquals (postDto, postByController);
        verify(postService, times(1)).getPostDto(postId);
    }
}