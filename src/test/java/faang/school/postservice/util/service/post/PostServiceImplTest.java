package faang.school.postservice.util.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.TextGearsClient;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.xml.bind.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {
    @InjectMocks
    private PostServiceImpl postServiceImpl;

    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @Mock
    private PostRepository postRepository;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private CommentRepository commentRepository;

    @Captor
    private ArgumentCaptor<Post> postCaptor;

    @Mock
    private TextGearsClient textGearsClient;

    @Test
    public void createPostNonexistentProject() {
        long authorId = 1;
        CreatePostDto createPostDto = createCreatePostDtoForTest();
        doThrow(new RuntimeException()).when(projectServiceClient).getProject(createPostDto.projectId());

        assertThrows(DataValidationException.class, () -> postServiceImpl.createPost(authorId, createPostDto));
    }

    @Test
    public void createPostCreatesPost() throws ValidationException {
        long authorId = 1;
        CreatePostDto createPostDto = createCreatePostDtoForTest();

        postServiceImpl.createPost(authorId, createPostDto);

        verify(postRepository, times(1)).save(postCaptor.capture());
        Post result = postCaptor.getValue();
        assertEquals(createPostDto.content(), result.getContent());
    }

    @Test
    public void publishPostNonexistentPost() {
        long requesterId = 1;
        long postId = 1;
        doThrow(new EntityNotFoundException("")).when(postRepository).findById(postId);

        assertThrows(EntityNotFoundException.class, () -> postServiceImpl.publishPost(requesterId, postId));
    }

    @Test
    public void publishPostPublishedPost() {
        long requesterId = 1;
        long postId = 1;
        Post postToPublish = new Post();
        postToPublish.setPublished(true);
        when(postRepository.findById(postId)).thenReturn(Optional.of(postToPublish));

        assertThrows(ForbiddenException.class, () -> postServiceImpl.publishPost(requesterId, postId));
    }

    @Test
    public void publishPostByNotCreator() {
        long requesterId = 1;
        long postId = 1;
        Post postToPublish = new Post();
        postToPublish.setAuthorId(2L);
        when(postRepository.findById(postId)).thenReturn(Optional.of(postToPublish));

        assertThrows(ForbiddenException.class, () -> postServiceImpl.publishPost(requesterId, postId));
    }

    @Test
    public void publishPostPublishesPost() {
        long requesterId = 1;
        long postId = 1;
        Post postToPublish = new Post();
        postToPublish.setAuthorId(1L);
        postToPublish.setId(1L);
        when(postRepository.findById(postId)).thenReturn(Optional.of(postToPublish));

        postServiceImpl.publishPost(requesterId, postId);

        verify(postRepository, times(1)).save(postCaptor.capture());
        Post publishedPost = postCaptor.getValue();
        assertEquals(postToPublish.getId(), publishedPost.getId());
    }

    @Test
    public void updatePostNonexistentPost() {
        long postId = 1;
        long requesterId = 1;
        UpdatePostDto updatePostDto = createUpdatePostDtoForTest();
        doThrow(new EntityNotFoundException("")).when(postRepository).findById(postId);

        assertThrows(EntityNotFoundException.class,
                () -> postServiceImpl.updatePost(requesterId, postId, updatePostDto));
    }

    @Test
    public void updatePostByNotCreator() {
        long postId = 1;
        long requesterId = 1;
        UpdatePostDto updatePostDto = createUpdatePostDtoForTest();
        Post postToUpdate = new Post();
        postToUpdate.setAuthorId(2L);
        when(postRepository.findById(postId)).thenReturn(Optional.of(postToUpdate));

        assertThrows(ForbiddenException.class,
                () -> postServiceImpl.updatePost(requesterId, postId, updatePostDto));
    }

    @Test
    public void updatePostUpdatesPost() {
        long postId = 1;
        long requesterId = 1;
        UpdatePostDto updatePostDto = createUpdatePostDtoForTest();
        Post postToUpdate = new Post();
        postToUpdate.setAuthorId(1L);
        postToUpdate.setId(1L);
        when(postRepository.findById(postId)).thenReturn(Optional.of(postToUpdate));

        postServiceImpl.updatePost(requesterId, postId, updatePostDto);

        verify(postRepository, times(1)).save(postCaptor.capture());
        Post updatedPost = postCaptor.getValue();
        assertEquals(postToUpdate.getId(), updatedPost.getId());
    }

    @Test
    public void deletePostNonexistentPost() {
        long requesterId = 1;
        long postId = 1;
        doThrow(new EntityNotFoundException("")).when(postRepository).findById(postId);

        assertThrows(EntityNotFoundException.class, () -> postServiceImpl.deletePost(requesterId, postId));
    }

    @Test
    public void deletePostSoftDeletedPost() {
        long requesterId = 1;
        long postId = 1;
        Post postToDelete = new Post();
        postToDelete.setDeleted(true);

        assertThrows(EntityNotFoundException.class, () -> postServiceImpl.deletePost(requesterId, postId));
    }

    @Test
    public void deletePostByNotCreator() {
        long requesterId = 1;
        long postId = 1;
        Post postToDelete = new Post();
        postToDelete.setAuthorId(2L);
        when(postRepository.findById(postId)).thenReturn(Optional.of(postToDelete));

        assertThrows(ForbiddenException.class, () -> postServiceImpl.deletePost(requesterId, postId));
    }

    @Test
    public void deletePostDeletesPost() {
        long requesterId = 1;
        long postId = 1;
        Post postToDelete = new Post();
        postToDelete.setAuthorId(1L);
        postToDelete.setId(1L);
        when(postRepository.findById(postId)).thenReturn(Optional.of(postToDelete));

        postServiceImpl.publishPost(requesterId, postId);

        verify(postRepository, times(1)).save(postCaptor.capture());
        Post publishedPost = postCaptor.getValue();
        assertEquals(postToDelete.getId(), publishedPost.getId());
    }

    @Test
    public void getPostByIdNonexistentPost() {
        long postId = 1;
        doThrow(new EntityNotFoundException("")).when(postRepository).findById(postId);

        assertThrows(EntityNotFoundException.class, () -> postServiceImpl.getPostById(postId));
    }

    @Test
    public void getPostByIdReturnsPost() {
        long postId = 1;
        Post post = new Post();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        postServiceImpl.getPostById(postId);

        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    public void getAllUnpublishedPostsByAuthor() {
        long authorId = 1;

        postServiceImpl.getAllUnpublishedPostsByAuthor(authorId);

        verify(postRepository, times(1)).findByAuthorId(authorId);
    }

    @Test
    public void getAllUnpublishedPostsByProject() {
        long projectId = 1;

        postServiceImpl.getAllUnpublishedPostsByProject(projectId);

        verify(postRepository, times(1)).findByProjectId(projectId);
    }

    @Test
    public void getAllPublishedPostsByAuthor() {
        long authorId = 1;

        postServiceImpl.getAllPublishedPostsByAuthor(authorId);

        verify(postRepository, times(1)).findByAuthorId(authorId);
    }

    @Test
    public void getAllPublishedPostsByProject() {
        long projectId = 1;

        postServiceImpl.getAllPublishedPostsByProject(projectId);

        verify(postRepository, times(1)).findByProjectId(projectId);
    }

    @Test
    public void selectUsersForBanSuccessfullySelectes() {
        postServiceImpl.selectUsersForBan();

        verify(commentRepository, times(1)).findAll();
    }

    void checkSpellingWithAISuccess() {
        Post post = new Post();
        post.setId(1L);
        post.setContent("Превет мир");
        post.setPublished(false);

        List<Post> posts = List.of(post);

        Mockito.when(postRepository.findReadyToPublish()).thenReturn(posts);
        Mockito.when(textGearsClient.correctText("Превет мир")).thenReturn(Mono.just("Привет мир"));
        Mockito.when(textGearsClient.correctText("Хэллоу ворлд")).thenReturn(Mono.just("Hello world"));

        postServiceImpl.checkSpellingWithAI();

        assertEquals("Привет мир", post.getContent());

        verify(postRepository).save(post);
    }

    @Test
    void checkSpellingWithAI_shouldHandleTextGearsErrorAndNotFail() {
        Post post = new Post();
        post.setId(1L);
        post.setContent("Превет мир");
        post.setPublished(false);

        when(postRepository.findReadyToPublish()).thenReturn(List.of(post));
        when(textGearsClient.correctText("Превет мир"))
                .thenReturn(Mono.error(new RuntimeException("TextGears API error")));

        postServiceImpl.checkSpellingWithAI();

        assertEquals("Превет мир", post.getContent());

        verify(postRepository, never()).save(post);
    }


    private CreatePostDto createCreatePostDtoForTest() {
        return new CreatePostDto(
                "2",
                2L,
                List.of(3L, 2L),
                2L,
                List.of("3", "2")
        );
    }

    private UpdatePostDto createUpdatePostDtoForTest() {
        return new UpdatePostDto(
                2L,
                "2",
                2L,
                List.of(3L, 2L),
                2L,
                List.of("3", "2")
        );
    }
}
