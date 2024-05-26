package faang.school.postservice.service;

import faang.school.postservice.dto.event.UserEvent;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.PublisherUsersBan;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PublisherUsersBan publisherUsersBan;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostMapper postMapper;
    @InjectMocks
    private PostService postService;

    @Test
    public void testBanUsersWithMultipleUnverifiedPostSuccessful(){
        List<Post> posts = getUnverifiedPosts();

        when(postRepository.findByVerified(false)).thenReturn(posts);

        Assertions.assertDoesNotThrow(() -> postService.banUsersWithMultipleUnverifiedPosts());
        verify(postRepository).findByVerified(false);
        verify(publisherUsersBan).publish(new UserEvent(1L));
    }

    private List<Post> getUnverifiedPosts(){
        return new ArrayList<>(List.of(
                Post.builder().verified(false).authorId(1L).build(),
                Post.builder().verified(true).authorId(2L).build()));
    }

    @Test
    public void createPostTest() {
        PostDto postDto = new PostDto();
        postDto.setAuthorId(1L);
        postDto.setContent("text");
        Post post = new Post();
        post.setAuthorId(1L);
        post.setContent("text");
        when(postMapper.toEntity(postDto)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(postDto);

        PostDto result = postService.createPost(postDto);

        verify(postMapper, times(1)).toEntity(postDto);
        verify(postRepository, times(1)).save(post);
        verify(postMapper, times(1)).toDto(post);

        Assertions.assertEquals(postDto, result);
    }

    @Test
    public void publishedPostTest() {
        Post post = new Post();
        post.setAuthorId(1L);
        post.setContent("text");
        post.setId(1L);
        post.setPublished(false);

        PostDto postDto = new PostDto();
        postDto.setAuthorId(1L);
        postDto.setContent("text");
        postDto.setId(1L);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(postDto);

        PostDto result = postService.publishedPost(1L);

        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(post);
        verify(postMapper, times(1)).toDto(post);

        Assertions.assertEquals(postDto, result);
    }

    @Test
    public void updatePostTest() {
        Post post = new Post();
        post.setAuthorId(1L);
        post.setContent("text");
        post.setId(1L);

        PostDto postDto = new PostDto();
        postDto.setAuthorId(1L);
        postDto.setContent("newNext");
        postDto.setId(1L);

        String newContent = "newNext";

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(postDto);

        PostDto result = postService.updatePost(1L, newContent);

        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(post);
        verify(postMapper, times(1)).toDto(post);

        Assertions.assertEquals(postDto, result);
    }

    @Test
    public void markDeletePost() {
        Post post = new Post();
        post.setAuthorId(1L);
        post.setContent("text");
        post.setId(1L);
        post.setDeleted(false);

        PostDto postDto = new PostDto();
        postDto.setAuthorId(1L);
        postDto.setContent("text");
        postDto.setId(1L);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(postDto);

        PostDto result = postService.markDeletePost(1L);

        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(post);
        verify(postMapper, times(1)).toDto(post);

        Assertions.assertEquals(postDto, result);
    }

    @Test
    public void getPostsNotDeleteByProjectIdTest() {
        Post post1 = new Post();
        post1.setProjectId(1L);
        post1.setContent("text");
        post1.setId(1L);
        post1.setDeleted(false);
        post1.setCreatedAt(LocalDateTime.of(2024, 10, 5, 18,0));

        Post post2 = new Post();
        post2.setProjectId(1L);
        post2.setContent("text2");
        post2.setId(2L);
        post2.setDeleted(false);
        post2.setCreatedAt(LocalDateTime.of(2024, 8, 5,18,0));

        List<Post> posts = List.of(post2, post1);

        PostDto postDto1 = new PostDto();
        postDto1.setProjectId(1L);
        postDto1.setContent("text");
        postDto1.setId(1L);
        postDto1.setCreatedAt(LocalDateTime.of(2024, 10, 5, 18,0));

        PostDto postDto2 = new PostDto();
        postDto2.setProjectId(1L);
        postDto2.setContent("text");
        postDto2.setId(2L);
        postDto2.setCreatedAt(LocalDateTime.of(2024, 8, 5,18,0));

        List<PostDto> postDtoList = List.of(postDto1, postDto2);

        when(postRepository.findByProjectId(1L)).thenReturn(posts);
        when(postMapper.toDto(post1)).thenReturn(postDto1);
        when(postMapper.toDto(post2)).thenReturn(postDto2);

        List<PostDto> result = postService.getPostsNotDeleteByProjectId(1L);

        verify(postRepository, times(1)).findByProjectId(1L);
        verify(postMapper, times(1)).toDto(post1);
        verify(postMapper, times(1)).toDto(post2);

        Assertions.assertEquals(postDtoList, result);
    }

    @Test
    public void getPostsNotDeleteByAuthorIdTest() {
        Post post1 = new Post();
        post1.setAuthorId(1L);
        post1.setContent("text");
        post1.setId(1L);
        post1.setDeleted(false);
        post1.setCreatedAt(LocalDateTime.of(2024, 10, 5, 18,0));

        Post post2 = new Post();
        post2.setAuthorId(1L);
        post2.setContent("text2");
        post2.setId(2L);
        post2.setDeleted(false);
        post2.setCreatedAt(LocalDateTime.of(2024, 8, 5,18,0));

        List<Post> posts = List.of(post2, post1);

        PostDto postDto1 = new PostDto();
        postDto1.setAuthorId(1L);
        postDto1.setContent("text");
        postDto1.setId(1L);
        postDto1.setCreatedAt(LocalDateTime.of(2024, 10, 5, 18,0));

        PostDto postDto2 = new PostDto();
        postDto2.setAuthorId(1L);
        postDto2.setContent("text");
        postDto2.setId(2L);
        postDto2.setCreatedAt(LocalDateTime.of(2024, 8, 5,18,0));

        List<PostDto> postDtoList = List.of(postDto1, postDto2);

        when(postRepository.findByAuthorId(1L)).thenReturn(posts);
        when(postMapper.toDto(post1)).thenReturn(postDto1);
        when(postMapper.toDto(post2)).thenReturn(postDto2);

        List<PostDto> result = postService.getPostsNotDeleteByAuthorId(1L);

        verify(postRepository, times(1)).findByAuthorId(1L);
        verify(postMapper, times(1)).toDto(post1);
        verify(postMapper, times(1)).toDto(post2);

        Assertions.assertEquals(postDtoList, result);
    }

    @Test
    public void getPostsPublishedByAuthorIdTest() {
        Post post1 = new Post();
        post1.setAuthorId(1L);
        post1.setContent("text");
        post1.setId(1L);
        post1.setPublished(false);
        post1.setCreatedAt(LocalDateTime.of(2024, 10, 5, 18,0));

        Post post2 = new Post();
        post2.setAuthorId(1L);
        post2.setContent("text2");
        post2.setId(2L);
        post2.setPublished(false);
        post2.setCreatedAt(LocalDateTime.of(2024, 8, 5,18,0));

        List<Post> posts = List.of(post2, post1);

        PostDto postDto1 = new PostDto();
        postDto1.setAuthorId(1L);
        postDto1.setContent("text");
        postDto1.setId(1L);
        postDto1.setCreatedAt(LocalDateTime.of(2024, 10, 5, 18,0));

        PostDto postDto2 = new PostDto();
        postDto2.setAuthorId(1L);
        postDto2.setContent("text");
        postDto2.setId(2L);
        postDto2.setCreatedAt(LocalDateTime.of(2024, 8, 5,18,0));

        List<PostDto> postDtoList = List.of(postDto1, postDto2);

        when(postRepository.findByAuthorId(1L)).thenReturn(posts);
        when(postMapper.toDto(post1)).thenReturn(postDto1);
        when(postMapper.toDto(post2)).thenReturn(postDto2);

        List<PostDto> result = postService.getPostsNotDeleteByAuthorId(1L);

        verify(postRepository, times(1)).findByAuthorId(1L);
        verify(postMapper, times(1)).toDto(post1);
        verify(postMapper, times(1)).toDto(post2);

        Assertions.assertEquals(postDtoList, result);
    }

    @Test
    public void getPostsPublishedByProjectIdTest() {
        Post post1 = new Post();
        post1.setProjectId(1L);
        post1.setContent("text");
        post1.setId(1L);
        post1.setPublished(false);
        post1.setCreatedAt(LocalDateTime.of(2024, 10, 5, 18,0));

        Post post2 = new Post();
        post2.setProjectId(1L);
        post2.setContent("text2");
        post2.setId(2L);
        post2.setPublished(false);
        post2.setCreatedAt(LocalDateTime.of(2024, 8, 5,18,0));

        List<Post> posts = List.of(post2, post1);

        PostDto postDto1 = new PostDto();
        postDto1.setProjectId(1L);
        postDto1.setContent("text");
        postDto1.setId(1L);
        postDto1.setCreatedAt(LocalDateTime.of(2024, 10, 5, 18,0));

        PostDto postDto2 = new PostDto();
        postDto2.setProjectId(1L);
        postDto2.setContent("text");
        postDto2.setId(2L);
        postDto2.setCreatedAt(LocalDateTime.of(2024, 8, 5,18,0));

        List<PostDto> postDtoList = List.of(postDto1, postDto2);

        when(postRepository.findByProjectId(1L)).thenReturn(posts);
        when(postMapper.toDto(post1)).thenReturn(postDto1);
        when(postMapper.toDto(post2)).thenReturn(postDto2);

        List<PostDto> result = postService.getPostsNotDeleteByProjectId(1L);

        verify(postRepository, times(1)).findByProjectId(1L);
        verify(postMapper, times(1)).toDto(post1);
        verify(postMapper, times(1)).toDto(post2);

        Assertions.assertEquals(postDtoList, result);
    }

    @Test
    public void getPostById() {
        Post post = new Post();
        post.setProjectId(1L);
        post.setContent("text");
        post.setId(1L);

        PostDto postDto = new PostDto();
        postDto.setProjectId(1L);
        postDto.setContent("text");
        postDto.setId(1L);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postMapper.toDto(post)).thenReturn(postDto);

        PostDto result = postService.getPostById(1L);

        verify(postRepository, times(1)).findById(1L);
        verify(postMapper, times(1)).toDto(post);

        Assertions.assertEquals(postDto, result);
    }

    @Test
    public void contentIsEmptyTest() {
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setContent("");
        Assertions.assertThrows(DataValidationException.class, () -> postService.createPost(postDto));
    }

    @Test
    public void contentIsNullTest() {
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        Assertions.assertThrows(DataValidationException.class, () -> postService.createPost(postDto));
    }

    @Test
    public void postWithoutOwnerTest() {
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setContent("rgdsg");
        Assertions.assertThrows(DataValidationException.class, () -> postService.createPost(postDto));
    }

    @Test
    public void postWithAuthorAndProjectTest() {
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setContent("rgdsg");
        postDto.setAuthorId(1L);
        postDto.setProjectId(2L);

        Assertions.assertThrows(DataValidationException.class, () -> postService.createPost(postDto));
    }
}
