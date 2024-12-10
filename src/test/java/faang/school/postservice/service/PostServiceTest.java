package faang.school.postservice.service;


import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.grammar.GrammarBot;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private GrammarBot grammarBot;

    @Mock
    private PostRepository postRepository;

    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @Mock
    private UserServiceClient userServiceClient;

    @Spy
    private PostValidator validator;

    private Post post;
    private PostDto postDto;
    private ProjectDto projectDto;
    private UserDto userDto;
    private List<Post> postList;
    long postId;
    Post post5;

    @BeforeEach
    public void setUp() {
        post = new Post();
        post.setId(1L);
        post.setContent("This is a test.");
        post.setPublished(false);
        post.setCreatedAt(LocalDateTime.MIN);
        post.setPublishedAt(LocalDateTime.now());

        userDto = UserDto.builder()
                .id(1L)
                .username("John")
                .email("john@example.com")
                .build();

        projectDto = ProjectDto.builder()
                .id(1L)
                .build();

        postDto = PostDto.builder()
                .authorId(1L)
                .build();

        Post secondPost = new Post();
        secondPost.setId(2L);
        secondPost.setAuthorId(2L);
        secondPost.setCreatedAt(LocalDateTime.now());
        secondPost.setPublishedAt(LocalDateTime.now());
        postList = List.of(post, secondPost);

        postId = 5L;
        post5 = Post.builder()
                .id(postId)
                .build();
    }

    @Test
    public void testCreatePost() {
        when(postMapper.toEntity(postDto)).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(postDto);
        when(userServiceClient.getUser(userDto.getId())).thenReturn(userDto);
        when(postRepository.save(post)).thenReturn(post);

        postService.createPost(postDto);

        verify(postMapper, times(1)).toEntity(postDto);
        verify(postMapper, times(1)).toDto(post);
        verify(postRepository, times(1)).save(post);
        verify(userServiceClient, times(1)).getUser(userDto.getId());
    }

    @Test
    public void testCreatePostWithoutAuthor() {
        postDto = PostDto.builder().build();
        assertThrows(DataValidationException.class, () -> postService.createPost(postDto));
    }

    @Test
    public void testCreatePostWithProjectAndUserAuthor() {
        postDto = PostDto.builder()
                .projectId(1L)
                .authorId(1L)
                .build();
        assertThrows(DataValidationException.class, () -> postService.createPost(postDto));
    }

    @Test
    public void testPublishPost() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        PostDto dto = postService.publishPost(1L);

        verify(postRepository, times(1)).findById(anyLong());
        assertTrue(dto.published());
    }

    @Test
    public void testPublishDeletedPost() {
        post.setDeleted(true);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        assertThrows(DataValidationException.class, () -> postService.publishPost(1L));
        verify(postRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testUpdatePost() {
        post.setContent("content");
        UpdatePostDto updatePostDto = UpdatePostDto.builder()
                .content("new content")
                .build();
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        PostDto dto = postService.updatePost(1L, updatePostDto);

        verify(postRepository, times(1)).findById(anyLong());
        assertEquals(dto.content(), updatePostDto.content());
    }

    @Test
    public void testUpdateDeletedPost() {
        UpdatePostDto updatePostDto = UpdatePostDto.builder()
                .content("new content")
                .build();
        post.setDeleted(true);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        assertThrows(DataValidationException.class, () -> postService.updatePost(1L, updatePostDto));

        verify(postRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testDeletePost() {
        post.setDeleted(false);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        PostDto dto = postService.deletePost(1L);

        verify(postRepository, times(1)).findById(anyLong());
        assertTrue(dto.deleted());
    }

    @Test
    public void testDeleteDeletedPost() {
        post.setDeleted(true);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        assertThrows(DataValidationException.class, () -> postService.deletePost(1L));
        verify(postRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testGetAllDraftNotDeletedPostsByUserId() {
        when(postRepository.findByAuthorId(anyLong())).thenReturn(postList);

        List<PostDto> dtoList = postService.getAllDraftNotDeletedPostsByUserId(1L);

        verify(postRepository, times(1)).findByAuthorId(anyLong());
        assertTrue(dtoList.stream()
                .allMatch(dto -> !dto.published() && !dto.deleted()));
    }

    @Test
    public void testGetAllPublishedNotDeletedPostsByUserId() {
        postList.forEach(p -> p.setPublished(true));
        when(postRepository.findByAuthorId(anyLong())).thenReturn(postList);

        List<PostDto> dtoList = postService.getAllPublishedNotDeletedPostsByUserId(1L);

        verify(postRepository, times(1)).findByAuthorId(anyLong());
        assertTrue(dtoList.stream()
                .allMatch(dto -> dto.published() && !dto.deleted()));
    }

    @Test
    public void testGetById() {
        // arrange
        long postId = 5L;
        Optional<Post> post = Optional.ofNullable(Post.builder()
                .id(postId)
                .build());

        when(postRepository.findById(postId)).thenReturn(post);

        // act
        Optional<Post> returnedPost = postService.findPostById(postId);

        // assert
        assertEquals(post, returnedPost);
    }

    @Test
    public void testGetPostByIdWithExistentPost(){
        when(postRepository.findById(postId))
                .thenReturn(Optional.ofNullable(post5));

        Post result = postService.getPostById(postId);

        assertNotNull(result);
        assertEquals(postId, result.getId());
    }

    @Test
    public void testGetPostByIdWhenPostNotExist(){
        when(postRepository.findById(postId))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> postService.getPostById(postId));
    }

    @Test
    public void testIsPostNotExistWithExistentPost(){
        when(postRepository.existsById(postId)).thenReturn(true);

        boolean result = postService.isPostNotExist(postId);

        assertFalse(result);
    }

    @Test
    public void testIsPostNotExistWhenPostNotExist() {
        when(postRepository.existsById(postId)).thenReturn(false);

        boolean result = postService.isPostNotExist(postId);

        assertTrue(result);
    }

    @Test
    void testCorrectUnpublishedPosts_success() {

        List<Post> unpublishedPosts = new ArrayList<>(List.of(post));

        when(postRepository.findReadyToPublish()).thenReturn(unpublishedPosts);
        when(grammarBot.checkGrammar("Ths is a tst.")).thenReturn("This is a test.");
        when(grammarBot.checkGrammar("Anothr eror.")).thenReturn("Another error.");

        postService.correctUnpublishedPosts();

        verify(postRepository).findReadyToPublish();
        verify(grammarBot).checkGrammar("Ths is a tst.");
        verify(grammarBot).checkGrammar("Anothr eror.");
        verify(postRepository, times(2)).save(any(Post.class));

        assertEquals("This is a test.", post.getContent());
        assertTrue(post.isPublished());
    }

    @Test
    void testCorrectUnpublishedPosts_partialFailure() {
        List<Post> unpublishedPosts = List.of(post);

        when(postRepository.findReadyToPublish()).thenReturn(unpublishedPosts);
        when(grammarBot.checkGrammar("Ths is a tst.")).thenReturn("This is a test.");
        when(grammarBot.checkGrammar("Anothr eror.")).thenThrow(new RuntimeException("Grammar service failure"));

        postService.correctUnpublishedPosts();

        verify(postRepository).findReadyToPublish();
        verify(grammarBot).checkGrammar("Ths is a tst.");
        verify(grammarBot).checkGrammar("Anothr eror.");
        verify(postRepository, times(1)).save(post);
        verify(postRepository, never()).save(post);

        assertEquals("This is a test.", post.getContent());
        assertTrue(post.isPublished());
    }

    @Test
    void testCorrectUnpublishedPosts_noPostsToProcess() {

        when(postRepository.findReadyToPublish()).thenReturn(Collections.emptyList());

        postService.correctUnpublishedPosts();

        verify(postRepository).findReadyToPublish();
        verify(grammarBot, never()).checkGrammar(anyString());
    }
}
