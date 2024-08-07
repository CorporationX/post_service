package faang.school.postservice.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.PostServiceValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static faang.school.postservice.post.PostMock.content;
import static faang.school.postservice.post.PostMock.authorId;
import static faang.school.postservice.post.PostMock.generatePost;
import static faang.school.postservice.post.PostMock.generatePostDto;
import static faang.school.postservice.post.PostMock.generateFilteredPosts;
import static faang.school.postservice.post.PostMock.newContent;
import static faang.school.postservice.post.PostMock.postId;
import static faang.school.postservice.post.PostMock.projectId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostServiceValidator<PostDto> validator;
    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @InjectMocks
    private PostService service;


    @Test
    @DisplayName("Create post with author id")
    public void testCreatePostWithAuthor() {
        // Arrange
        PostDto expectedPostDto = generatePostDto(authorId, null, false, content);
        Post post = postMapper.toEntity(expectedPostDto);

        when(postRepository.save(post)).thenReturn(post);

        // Act
        PostDto actual = service.createPost(expectedPostDto);

        // Assert
        verify(postRepository, times(1)).save(post);
        assertNotNull(actual);
        assertEquals(expectedPostDto.getContent(), actual.getContent());
        assertEquals(expectedPostDto.getAuthorId(), actual.getAuthorId());
        assertEquals(expectedPostDto.getProjectId(), actual.getProjectId());
    }

    @Test
    @DisplayName("Create post with project id")
    public void testCreatePostWithProject() {
        // Arrange
        PostDto expectedPostDto = generatePostDto(null, projectId, false, content);
        Post post = postMapper.toEntity(expectedPostDto);

        when(postRepository.save(post)).thenReturn(post);

        // Act
        PostDto actual = service.createPost(expectedPostDto);

        // Assert
        verify(postRepository, times(1)).save(post);
        assertNotNull(actual);
        assertEquals(expectedPostDto.getContent(), actual.getContent());
        assertEquals(expectedPostDto.getAuthorId(), actual.getAuthorId());
        assertEquals(expectedPostDto.getProjectId(), actual.getProjectId());
    }

    @Test
    @DisplayName("Publish post happy path")
    public void testPublishPost() {
        // Arrange
        Post post = generatePost(authorId, null, false, content);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        // Act
        PostDto actual = service.publishPost(postId);

        // Assert
        verify(postRepository, times(1)).save(post);
        assertNotNull(actual);
        assertTrue(actual.isPublished());
    }

    @Test
    @DisplayName("Publish post should throw an error when the post is already published")
    public void testPublishPostAlreadyPublished() {
        // Arrange
        Post post = generatePost(authorId, null, true, content);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.publishPost(postId));
    }

    @Test
    @DisplayName("Publish post should throw an error when the post not found")
    public void testPublishPostNotFound() {
        // Arrange
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.publishPost(postId));
    }

    @Test
    @DisplayName("Update post happy path")
    public void testUpdatePost() {
        // Arrange
        Post expectedPost = generatePost(authorId, null, true, newContent);
        PostDto expectedPostDto = postMapper.toDto(expectedPost);

        PostDto inputPostDto = generatePostDto(authorId, null, true, newContent);
        Post existingPost = generatePost(authorId, null, true, content);

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(Mockito.any(Post.class))).thenReturn(expectedPost);

        // Act
        PostDto actual = service.updatePost(postId, inputPostDto);

        // Assert
        verify(postRepository, times(1)).save(Mockito.any(Post.class));
        assertNotNull(actual);
        assertEquals(expectedPostDto.getContent(), actual.getContent());
    }

    @Test
    @DisplayName("Update post should keep the same author id even if the input has a different one")
    public void testUpdatePostAuthorIdDifferentOne() {
        // Arrange
        long newAuthorId = 2L;
        Post expectedPost = generatePost(authorId, null, true, newContent);
        PostDto expectedPostDto = postMapper.toDto(expectedPost);

        PostDto inputPostDto = generatePostDto(newAuthorId, null, true, newContent);
        Post existingPost = generatePost(authorId, null, true, content);

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(Mockito.any(Post.class))).thenReturn(expectedPost);

        // Act
        PostDto actual = service.updatePost(postId, inputPostDto);

        // Assert
        assertEquals(expectedPostDto.getAuthorId(), actual.getAuthorId());
    }

    @Test
    @DisplayName("Update post should keep the same project id even if the input has a different one")
    public void testUpdatePostProjectIdDifferentOne() {
        // Arrange
        long newProjectId = 2L;
        Post expectedPost = generatePost(null, projectId, true, newContent);
        PostDto expectedPostDto = postMapper.toDto(expectedPost);

        PostDto inputPostDto = generatePostDto(null, newProjectId, true, newContent);
        Post existingPost = generatePost(null, projectId, true, content);

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(Mockito.any(Post.class))).thenReturn(expectedPost);

        // Act
        PostDto actual = service.updatePost(postId, inputPostDto);

        // Assert
        assertEquals(expectedPostDto.getProjectId(), actual.getProjectId());
    }

    @Test
    @DisplayName("Update post should throw an error when the post is not found")
    public void testUpdatePostNotFound() {
        // Arrange
        PostDto postDto = generatePostDto(authorId, null, true, content);

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.updatePost(postId, postDto));
    }

    @Test
    @DisplayName("Delete post happy path")
    public void testDeletePost() {
        // Arrange
        Post post = generatePost(authorId, null, true, content);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(Mockito.any(Post.class))).thenReturn(post);

        // Act
        service.deletePost(postId);

        // Assert
        verify(postRepository, times(1)).save(post);
        assertTrue(post.isDeleted());
    }

    @Test
    @DisplayName("Delete post should throw an error when the post is not found")
    public void testDeletePostNotFound() {
        // Arrange
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.deletePost(postId));
    }

    @Test
    @DisplayName("Get post happy path")
    public void testGetPost() {
        // Arrange
        Post expectedPost = generatePost(authorId, null, true, content);

        when(postRepository.findById(postId)).thenReturn(Optional.of(expectedPost));

        // Act
        PostDto actual = service.getPost(postId);

        // Assert
        assertNotNull(actual);
        assertEquals(expectedPost.getContent(), actual.getContent());
    }

    @Test
    @DisplayName("Get post should throw an error when the post is not found")
    public void testGetPostNotFound() {
        // Arrange
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.getPost(postId));
    }

    @Test
    @DisplayName("Get filtered posts returns not deleted drafts by author id and sorted in descending order")
    public void testGetFilteredPostsDraftsByAuthorId() {
        // Arrange
        boolean isPublished = false;
        when(postRepository.findByAuthorIdAndPublishedAndDeletedIsFalseOrderByPublished(authorId, isPublished))
                .thenReturn(generateFilteredPosts(authorId, null, isPublished));

        // Act
        List<PostDto> actual = service.getFilteredPosts(authorId, null, isPublished);

        // Assert
        verify(postRepository, times(1))
                .findByAuthorIdAndPublishedAndDeletedIsFalseOrderByPublished(authorId, isPublished);
        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertTrue(actual.get(0).getCreatedAt().isAfter(actual.get(1).getCreatedAt()));
    }

    @Test
    @DisplayName("Get filtered posts returns not deleted published posts by project id and sorted in descending order")
    public void testGetFilteredPostsPublishedByProjectId() {
        // Arrange
        boolean isPublished = true;
        when(postRepository.findByProjectIdAndPublishedAndDeletedIsFalseOrderByPublished(projectId, isPublished))
                .thenReturn(generateFilteredPosts(null, projectId, isPublished));

        // Act
        List<PostDto> actual = service.getFilteredPosts(null, projectId, isPublished);

        // Assert
        verify(postRepository, times(1))
                .findByProjectIdAndPublishedAndDeletedIsFalseOrderByPublished(projectId, isPublished);
        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertTrue(actual.get(0).getCreatedAt().isAfter(actual.get(1).getCreatedAt()));
    }

    @Test
    @DisplayName("Get filtered posts returns empty list if there are no filters")
    public void testGetFilteredPostsEmptyList() {
        // Arrange & Act
        List<PostDto> actual = service.getFilteredPosts(null, null, false);

        // Assert
        assertEquals(new ArrayList<>(), actual);
    }
}
