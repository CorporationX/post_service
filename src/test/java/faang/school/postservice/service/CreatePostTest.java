package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.model.dto.ProjectDto;
import faang.school.postservice.model.dto.UserDto;
import faang.school.postservice.enums.AuthorType;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.publisher.NewPostPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreatePostTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private NewPostPublisher newPostPublisher;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostServiceImpl postService;

    private PostDto validUserPostDto;
    private PostDto validProjectPostDto;

    @BeforeEach
    void setUp() {
        validUserPostDto = PostDto.builder()
                .authorId(1L)
                .authorType(AuthorType.USER)
                .content("A post with a user as the author")
                .build();

        validProjectPostDto = PostDto.builder()
                .authorId(1L)
                .authorType(AuthorType.PROJECT)
                .content("A post with a project as the author")
                .build();
    }

    @Test
    void shouldCreatePostWithUserAsAuthor() {
        UserDto userDto = new UserDto();
        when(userServiceClient.getUser(1L)).thenReturn(userDto);

        Post post = new Post();
        when(postMapper.toPost(validUserPostDto)).thenReturn(post);

        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.toPostDto(post)).thenReturn(validUserPostDto);

        PostDto createdPost = postService.createPost(validUserPostDto);

        assertNotNull(createdPost, "Created post should not be null");
        assertFalse(createdPost.isPublished(), "Post should not be published");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userServiceClient.getUser(1L)).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> postService.createPost(validUserPostDto));
    }

    @Test
    void shouldCreatePostWithProjectAsAuthor() {
        ProjectDto projectDto = new ProjectDto();
        when(projectServiceClient.getProject(1L)).thenReturn(projectDto);

        Post post = new Post();
        when(postMapper.toPost(validProjectPostDto)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toPostDto(post)).thenReturn(validProjectPostDto);

        PostDto createdPost = postService.createPost(validProjectPostDto);

        assertNotNull(createdPost);
        assertFalse(createdPost.isPublished());
    }
}