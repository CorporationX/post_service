package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.IncorrectIdException;
import faang.school.postservice.exception.SamePostAuthorException;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Spy
    private PostMapperImpl postMapper;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserServiceClient userService;
    @Mock
    private ProjectServiceClient projectService;
    @InjectMocks
    private PostService postService;
    private PostDto incorrectPostDto;
    private PostDto correctPostDto;
    private final Long CORRECT_ID = 1L;

    @BeforeEach
    void initData() {
        incorrectPostDto = PostDto.builder()
                .content("content")
                .projectId(CORRECT_ID)
                .authorId(CORRECT_ID)
                .build();
        correctPostDto = PostDto.builder()
                .content("content")
                .authorId(CORRECT_ID)
                .build();
    }

    @Test
    void testCreateDaftPostWithSameAuthor() {
        assertThrows(SamePostAuthorException.class, () -> postService.crateDraftPost(incorrectPostDto));
    }

    @Test
    void testCreateDaftPostWithNonExistentUser() {
        incorrectPostDto.setProjectId(null);

        when(userService.getUser(CORRECT_ID)).thenThrow(FeignException.class);
        assertThrows(IncorrectIdException.class, () -> postService.crateDraftPost(incorrectPostDto));
    }

    @Test
    void testCreateDaftPostWithNonExistentProject() {
        incorrectPostDto.setAuthorId(null);

        when(projectService.getProject(CORRECT_ID)).thenThrow(FeignException.class);
        assertThrows(IncorrectIdException.class, () -> postService.crateDraftPost(incorrectPostDto));
    }

    @Test
    void testCreateDaftPost() {
        Post post = postMapper.toPost(correctPostDto);

        when(postRepository.save(post)).thenReturn(post);
        PostDto actualPostDto = postService.crateDraftPost(correctPostDto);
        assertEquals(correctPostDto, actualPostDto);
    }
}