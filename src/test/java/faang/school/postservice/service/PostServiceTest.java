package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.ScheduledTaskDto;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.mapper.ScheduledTaskMapperImpl;
import faang.school.postservice.messaging.postevent.PostEventPublisher;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.scheduled.ScheduledEntityType;
import faang.school.postservice.model.scheduled.ScheduledTask;
import faang.school.postservice.model.scheduled.ScheduledTaskType;
import faang.school.postservice.publisher.PostViewEventPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ScheduledTaskRepository;
import faang.school.postservice.util.exception.CreatePostException;
import faang.school.postservice.util.exception.DeletePostException;
import faang.school.postservice.util.exception.EntitySchedulingException;
import faang.school.postservice.util.exception.GetPostException;
import faang.school.postservice.util.exception.PostNotFoundException;
import faang.school.postservice.util.exception.PublishPostException;
import faang.school.postservice.util.exception.UpdatePostException;
import faang.school.postservice.util.validator.PostServiceValidator;
import faang.school.postservice.service.PostService;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Spy
    private PostServiceValidator validator;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ScheduledTaskRepository scheduledTaskRepository;

    @Spy
    private PostMapperImpl postMapper;

    @Spy
    private ScheduledTaskMapperImpl scheduledTaskMapper;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private PostViewEventPublisher postViewEventPublisher;

    @Mock
    private HashtagService hashtagService;

    @Mock
    private PostEventPublisher postEventPublisher;

    @InjectMocks
    private PostService postService;

    private PostDto postDto;

    @BeforeEach
    void setUp() {
        postDto = PostDto.builder()
                .id(1L)
                .authorId(1L)
                .build();
    }

    @Test
    void addPost_BothProjectAndAuthorExist_ShouldThrowException() {
        postDto.setProjectId(1L);

        CreatePostException e = Assert.assertThrows(CreatePostException.class, () -> {
            postService.addPost(postDto);
        });
        Assertions.assertEquals("Post's author can be only author or project and can't be both", e.getMessage());
    }

    @Test
    void addPost_BothProjectAndAuthorAreNull_ShouldThrowException() {
        postDto.setAuthorId(null);
        postDto.setProjectId(null);

        CreatePostException e = Assert.assertThrows(CreatePostException.class, () -> {
            postService.addPost(postDto);
        });
        Assertions.assertEquals("Post's author can be only author or project and can't be both", e.getMessage());
    }

    @Test
    void addPost_ShouldMapCorrectlyToEntity() {
        PostDto dto = buildPostDto();

        Post actual = postMapper.toEntity(dto);

        Assertions.assertEquals(buildPost(), actual);
    }

    @Test
    void addPost_ShouldMapCorrectlyToDto() {
        Post post = buildPost();

        PostDto actual = postMapper.toDto(post);

        Assertions.assertEquals(buildExpectedPostDto(), actual);
    }

    @Test
    void addPost_ByAuthor_ShouldSave() {
        doNothing().when(validator).validateToAdd(Mockito.any());
        Mockito.when(postMapper.toDto(Mockito.any())).thenReturn(postDto);
        postService.addPost(postDto);

        Mockito.verify(userServiceClient, Mockito.times(1)).getUser(postDto.getAuthorId());
    }

    @Test
    void addPost_ByProject_ShouldSave() {
        postDto.setAuthorId(null);
        postDto.setProjectId(1L);

        postService.addPost(postDto);

        Mockito.verify(projectServiceClient, Mockito.times(1)).getProject(postDto.getProjectId());
    }

    @Test
    void addPost_ShouldSave() {
        PostDto postDto = buildPostDto();

        postService.addPost(postDto);

        Mockito.verify(postRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void publishPost_PostNotFound_ShouldThrowException() {
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.empty());

        PostNotFoundException e = Assert.assertThrows(PostNotFoundException.class, () -> {
            postService.publishPost(1L);
        });
        Assertions.assertEquals("Post with id " + String.format("%d", 1L) + " not found", e.getMessage());
    }

    @Test
    void publishPost_PostIsPublished_ShouldThrowException() {
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(Post.builder().published(true).build()));

        PublishPostException e = Assert.assertThrows(PublishPostException.class, () -> {
            postService.publishPost(1L);
        });
        Assertions.assertEquals("Post is already published", e.getMessage());
    }

    @Test
    void publishPost_PostIsDeleted_ShouldThrowException() {
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(Post.builder().deleted(true).build()));

        PublishPostException e = Assert.assertThrows(PublishPostException.class, () -> {
            postService.publishPost(1L);
        });
        Assertions.assertEquals("Post is already deleted", e.getMessage());
    }

    @Test
    void publishPost_PostIsNotPublishedOrDeleted_ShouldNotThrowException() {
        Post post = Post.builder().published(false).deleted(false).build();
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        Assertions.assertDoesNotThrow(() -> postService.publishPost(1L));
    }

    @Test
    void publishPost_FieldsShouldBeSet() {
        Post post = buildPost();
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        postService.publishPost(1L);

        Assertions.assertTrue(post.isPublished());
        Assertions.assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                post.getPublishedAt());
    }

    @Test
    void publishPost_ShouldPublish() {
        Post post = buildPost();
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.publishPost(1L);

        Mockito.verify(postRepository).save(post);
    }

    @Test
    void publishPost_ShouldBeSentByPublisher() {
        Post post = buildPost();
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.publishPost(1L);

        Mockito.verify(postEventPublisher).send(post);
    }

    @Test
    void updatePost_PostNotFound_ShouldThrowException() {
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.empty());

        PostNotFoundException e = Assert.assertThrows(PostNotFoundException.class, () -> {
            postService.updatePost(1L, "content");
        });
        Assertions.assertEquals("Post with id " + String.format("%d", 1L) + " not found", e.getMessage());
    }

    @Test
    void updatePost_PostIsDeleted_ShouldThrowException() {
        Post post = Post.builder().deleted(true).build();
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        UpdatePostException e = Assert.assertThrows(UpdatePostException.class, () -> {
            postService.updatePost(1L, "content");
        });
        Assertions.assertEquals("Post is already deleted", e.getMessage());
    }

    @Test
    void updatePost_PostIsNotPublished_ShouldThrowException() {
        Post post = Post.builder().published(false).build();
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        UpdatePostException e = Assert.assertThrows(UpdatePostException.class, () -> {
            postService.updatePost(1L, "content");
        });
        Assertions.assertEquals("Post is in draft state. It can't be updated", e.getMessage());
    }

    @Test
    void updatePost_ContentIsTheSame_ShouldThrowException() {
        Post post = Post.builder().published(true).content("content").build();
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        UpdatePostException e = Assert.assertThrows(UpdatePostException.class, () -> {
            postService.updatePost(1L, "content");
        });
        Assertions.assertEquals("There is no changes to update", e.getMessage());
    }

    @Test
    void updatePost_InputsAreCorrect_ShouldNotThrowException() {
        Post post = Post.builder().published(true).content("old content").build();
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        Assertions.assertDoesNotThrow(() -> postService.updatePost(1L, "new content"));
    }

    @Test
    void updatePost_FieldsShouldBeSet() {
        Post post = buildPost();
        post.setPublished(true);
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        postService.updatePost(1L, "cont");

        Assertions.assertEquals("cont", post.getContent());
        Assertions.assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                post.getUpdatedAt());
    }

    @Test
    void updatePost_ShouldPublish() {
        Post post = buildPost();
        post.setPublished(true);
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        postService.updatePost(1L, "cont");

        Mockito.verify(postRepository, Mockito.times(1)).save(post);
    }

    @Test
    void deletePost_PostNotFound_ShouldThrowException() {
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.empty());

        PostNotFoundException e = Assert.assertThrows(PostNotFoundException.class, () -> {
            postService.deletePost(1L);
        });
        Assertions.assertEquals("Post with id " + String.format("%d", 1L) + " not found", e.getMessage());
    }

    @Test
    void deletePost_PostIsNotPublished_ShouldThrowException() {
        Post post = Post.builder().published(false).build();
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        DeletePostException e = Assert.assertThrows(DeletePostException.class, () -> {
            postService.deletePost(1L);
        });
        Assertions.assertEquals("Post is in draft state. It can't be deleted", e.getMessage());
    }

    @Test
    void deletePost_PostIsDeleted_ShouldThrowException() {
        Post post = Post.builder().deleted(true).build();
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        DeletePostException e = Assert.assertThrows(DeletePostException.class, () -> {
            postService.deletePost(1L);
        });
        Assertions.assertEquals("Post is already deleted", e.getMessage());
    }

    @Test
    void deletePost_InputsAreCorrect_ShouldNotThrowException() {
        Post post = Post.builder().published(true).deleted(false).build();
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        Assertions.assertDoesNotThrow(() -> postService.deletePost(1L));
    }

    @Test
    void deletePost_FieldsShouldBeSet() {
        Post post = Post.builder().published(true).deleted(false).build();
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));
        postService.deletePost(1L);

        Assertions.assertTrue(post.isDeleted());
        Assertions.assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                post.getUpdatedAt());
    }

    @Test
    void deletePost_ShouldDelete() {
        Post post = Post.builder().published(true).deleted(false).build();
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        postService.deletePost(1L);

        Mockito.verify(postRepository, Mockito.times(1)).save(post);
    }

    @Test
    void getPost_PostNotFound_ShouldThrowException() {
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.empty());

        PostNotFoundException e = Assert.assertThrows(PostNotFoundException.class, () -> {
            postService.getPost(1L);
        });
        Assertions.assertEquals("Post with id " + String.format("%d", 1L) + " not found", e.getMessage());
    }

    @Test
    void getPost_PostIsDeleted_ShouldThrowException() {
        Post post = Post.builder().deleted(true).build();
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        GetPostException e = Assert.assertThrows(GetPostException.class, () -> {
            postService.getPost(1L);
        });
        Assertions.assertEquals("Post is already deleted", e.getMessage());
    }

    @Test
    void getPost_PostIsNotPublished_ShouldThrowException() {
        Post post = Post.builder().published(false).build();
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        GetPostException e = Assert.assertThrows(GetPostException.class, () -> {
            postService.getPost(1L);
        });
        Assertions.assertEquals("Post is in draft state. It can't be gotten", e.getMessage());
    }

    @Test
    void getPost_InputsAreCorrect_ShouldNotThrowException() {
        Post post = Post.builder().published(true).build();
        Mockito.when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        Assertions.assertDoesNotThrow(() -> postService.getPost(1L));
        Mockito.verify(postViewEventPublisher, Mockito.times(1)).publish(post);
    }


    @Test
    void getDrafts_ShouldMapCorrectlyToDtos() {
        List<Post> posts = buildListOfPosts();

        List<PostDto> actual = postMapper.toDtos(posts);

        Assertions.assertIterableEquals(buildListOfPostDtos(), actual);
    }

    @Test
    void getDraftsByAuthorId_ShouldNotThrowException() {
        Assertions.assertDoesNotThrow(() -> postService.getDraftsByAuthorId(1L));
        Mockito.verify(postRepository, Mockito.times(1)).findReadyToPublishByAuthorId(1L);
    }

    @Test
    void getDraftsByProjectId_ShouldNotThrowException() {
        Assertions.assertDoesNotThrow(() -> postService.getDraftsByProjectId(1L));
        Mockito.verify(postRepository, Mockito.times(1)).findReadyToPublishByProjectId(1L);
    }

    @Test
    void getPostsByAuthorId_ShouldNotThrowException() {
        Post post = buildPost();
        Mockito.when(postRepository.findPublishedPostsByAuthorId(1L)).thenReturn(List.of(post));

        Assertions.assertDoesNotThrow(() -> postService.getPostsByAuthorId(1L));
        Mockito.verify(postViewEventPublisher, Mockito.times(1)).publish(post);
        Mockito.verify(postRepository, Mockito.times(1)).findPublishedPostsByAuthorId(1L);
    }

    @Test
    void getPostsByProjectId_ShouldNotThrowException() {
        Post post = buildPost();
        Mockito.when(postRepository.findPublishedPostsByProjectId(1L)).thenReturn(List.of(post));

        Assertions.assertDoesNotThrow(() -> postService.getPostsByProjectId(1L));
        Mockito.verify(postViewEventPublisher, Mockito.times(1)).publish(post);
        Mockito.verify(postRepository, Mockito.times(1)).findPublishedPostsByProjectId(1L);
    }

    @Test
    void actWithPostBySchedule_PostNotFound_ShouldThrowException() {
        ScheduledTaskDto dto = ScheduledTaskDto.builder().entityId(1L).build();
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.empty());

        PostNotFoundException e = Assertions.assertThrows(PostNotFoundException.class, () -> {
            postService.actWithScheduledPost(dto);
        });
        Assertions.assertEquals("Post with id = " + String.format("%d", 1L) + " not found", e.getMessage());
    }

    @Test
    void actWithPostBySchedule_PostIsAlreadyScheduled_ShouldThrowException() {
        ScheduledTaskDto dto = buildScheduledTaskDto();
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(buildPost()));
        Mockito.when(scheduledTaskRepository.findScheduledTaskById(1L, dto.entityType())).thenReturn(Optional.of(buildScheduledTask()));

        EntitySchedulingException e = Assertions.assertThrows(EntitySchedulingException.class, () -> {
            postService.actWithScheduledPost(dto);
        });
        Assertions.assertEquals("Post with id = " + String.format("%d", dto.entityId()) + " already scheduled", e.getMessage());
    }

    @Test
    void actWithPostBySchedule_ShouldMapCorrectly() {
        ScheduledTask actual = scheduledTaskMapper.toEntity(buildScheduledTaskDto());

        Assertions.assertEquals(buildScheduledTask(), actual);
    }

    @Test
    void actWithPostBySchedule_ShouldSave() {
        ScheduledTaskDto dto = buildScheduledTaskDto();
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(buildPost()));
        Mockito.when(scheduledTaskRepository.findScheduledTaskById(1L, dto.entityType())).thenReturn(Optional.empty());

        postService.actWithScheduledPost(dto);

        Mockito.verify(scheduledTaskRepository).save(buildScheduledTask());
    }

    private PostDto buildPostDto() {
        return PostDto.builder()
                .content("content")
                .authorId(1L)
                .adId(1L)
                .build();
    }

    private Post buildPost() {
        return Post.builder()
                .id(0)
                .content("content")
                .authorId(1L)
                .likes(new ArrayList<>())
                .comments(new ArrayList<>())
                .albums(new ArrayList<>())
                .published(false)
                .deleted(false)
                .verifiedDate(null)
                .build();
    }

    private PostDto buildExpectedPostDto() {
        return PostDto.builder()
                .id(0L)
                .content("content")
                .authorId(1L)
                .likes(new ArrayList<>())
                .comments(new ArrayList<>())
                .albums(new ArrayList<>())
                .published(false)
                .deleted(false)
                .createdAt(null)
                .build();
    }

    private List<Post> buildListOfPosts() {
        return List.of(
                buildPost(),
                buildPost(),
                buildPost()
        );
    }

    private List<PostDto> buildListOfPostDtos() {
        return List.of(
                buildExpectedPostDto(),
                buildExpectedPostDto(),
                buildExpectedPostDto()
        );
    }

    private ScheduledTask buildScheduledTask() {
        return ScheduledTask.builder()
                .entityType(ScheduledEntityType.POST)
                .taskType(ScheduledTaskType.PUBLISHING_POST)
                .entityId(1L)
                .build();
    }

    private ScheduledTaskDto buildScheduledTaskDto() {
        return ScheduledTaskDto.builder()
                .entityType(ScheduledEntityType.POST)
                .taskType(ScheduledTaskType.PUBLISHING_POST)
                .entityId(1L)
                .build();
    }
}
