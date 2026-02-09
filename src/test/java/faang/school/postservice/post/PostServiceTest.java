package faang.school.postservice.post;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostServiceImpl;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PostRepository postRepository;

    @Spy
    private PostMapperImpl mapper;

    @InjectMocks
    private PostServiceImpl service;
    
    private final Long AUTHOR_ID = 1L;
    private final Long PROJECT_ID = 2L;
    private final Long POST_ID = 100L;
    private final String CONTENT = "Lorem ipsum";

    @Test
    @DisplayName("create(): should create the post with authorId successfully")
    public void create_shouldCreatePostByAuthorIdSuccessfully() {
        CreatePostDto dto = CreatePostDto.builder().authorId(AUTHOR_ID).content(CONTENT).build();

        PostDto result = service.create(dto);

        verify(postRepository).save(any(Post.class));
        assertNotNull(result);
        assertEquals(CONTENT, result.content());
        assertEquals(AUTHOR_ID, result.authorId());
    }

    @Test
    @DisplayName("create(): should create the post with postId successfully")
    public void create_shouldCreatePostByProjectIdSuccessfully() {
        CreatePostDto dto = CreatePostDto.builder().projectId(PROJECT_ID).content(CONTENT).build();

        PostDto result = service.create(dto);

        verify(postRepository).save(any(Post.class));
        assertNotNull(result);
        assertEquals(CONTENT, result.content());
        assertEquals(PROJECT_ID, result.projectId());
    }
    
    @Test
    @DisplayName("create(): should throw an exception if the DTO contains both authorId and projectId")
    public void create_shouldThrowException_WhenAuthorIdAndProjectIdBothPresent() {
        CreatePostDto dto = CreatePostDto.builder().projectId(PROJECT_ID).authorId(AUTHOR_ID).content(CONTENT).build();
        assertThrows(DataValidationException.class,
                () -> service.create(dto));
    }

    @Test
    @DisplayName("create(): should throw an exception if the DTO doesnt contain either an authorId or a projectId")
    public void create_shouldThrowException_WhenAuthorIdAndProjectIdNotPresent() {
        CreatePostDto dto = CreatePostDto.builder().content(CONTENT).build();
        assertThrows(DataValidationException.class,
                () -> service.create(dto));
    }

    @Test
    @DisplayName("publish(): should publish the post successfully")
    public void publish_shouldPublishSuccessfully() {
        Post post = Post.builder().id(POST_ID).published(false).deleted(false).build();
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));

        service.publish(POST_ID);

        verify(postRepository).save(any(Post.class));
        assertTrue(post.isPublished());
    }

    @Test
    @DisplayName("publish(): should throw an exception if the publish post is deleted")
    public void publish_shouldThrowException_whenPostIsDeleted() {
        Post post = Post.builder().id(POST_ID).published(false).deleted(true).build();
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));

        assertThrows(DataValidationException.class, () -> service.publish(POST_ID));
    }

    @Test
    @DisplayName("publish(): should throw an exception if publish post is already published")
    public void publish_shouldThrowException_whenPostIsAlreadyPublished() {
        Post post = Post.builder().id(POST_ID).published(true).deleted(false).build();
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));

        assertThrows(DataValidationException.class, () -> service.publish(POST_ID));
    }

    @Test
    @DisplayName("update(): should update the post with new content and new published status successfully")
    public void update_shouldUpdateSuccessfully() {
        Post post = Post.builder().id(POST_ID).authorId(AUTHOR_ID).published(false).content(CONTENT).build();
        String newContent = "Lorem Ipsum 2";
        UpdatePostDto dto = UpdatePostDto.builder().content(newContent).published(true).build();
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));

        PostDto result = service.update(POST_ID, dto);

        assertNotNull(result);
        assertTrue(result.published());
        assertEquals(dto.content(), result.content());
    }

    @Test
    @DisplayName("softDelete(): should flag the post as deleted")
    public void softDelete_shouldDeleteSuccessfully() {
        Post post = Post.builder().id(POST_ID).projectId(AUTHOR_ID).published(true).content(CONTENT).build();
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        
        service.softDelete(POST_ID);

        verify(postRepository).save(any(Post.class));
        assertTrue(post.isDeleted());
        assertFalse(post.isPublished());
    }

    @Test
    @DisplayName("softDelete(): should throw an exception if the post already have deleted flag")
    public void softDelete_shouldThrowException_whenPostIsAlreadyDeleted() {
        Post post = Post.builder().id(POST_ID).projectId(AUTHOR_ID).deleted(true).content(CONTENT).build();
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));

        assertThrows(DataValidationException.class, () -> service.softDelete(POST_ID));
    }

    @Test
    @DisplayName("restore(): should flag the post as not deleted")
    public void restore_shouldRestoreSuccessfully() {
        Post post = Post.builder().id(POST_ID).projectId(AUTHOR_ID).deleted(true).content(CONTENT).build();
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));

        service.restore(POST_ID);

        verify(postRepository).save(any(Post.class));
        assertFalse(post.isDeleted());
    }

    @Test
    @DisplayName("restore(): should throw an exception if the post is not deleted")
    public void restore_shouldThrowException_whenPostIsNotDeleted() {
        Post post = Post.builder().id(POST_ID).projectId(AUTHOR_ID).deleted(false).content(CONTENT).build();
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));

        assertThrows(DataValidationException.class, () -> service.restore(POST_ID));
    }
}

