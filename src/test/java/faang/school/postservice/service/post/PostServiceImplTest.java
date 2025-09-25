package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.messaging.dto.PostUpdatedEvent;
import faang.school.postservice.messaging.producer.EventProducer;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRedisRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.UserRedisRepository;
import faang.school.postservice.service.filter.FilterService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static faang.school.postservice.service.post.PostServiceTestData.buildCreateDto;
import static faang.school.postservice.service.post.PostServiceTestData.buildPostEntity;
import static faang.school.postservice.service.post.PostServiceTestData.toEntity;
import static faang.school.postservice.service.post.PostServiceTestData.toViewDto;
import static faang.school.postservice.service.post.PostServiceTestData.toViewDtoList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тест для PostServiceImpl")
class PostServiceImplTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserServiceClient userClient;
    @Mock
    private ProjectServiceClient projectClient;
    @Mock
    private UserContext userContext;
    @Mock
    private PostMapper postMapper;
    @Mock
    private FilterService<Post, PostFilterDto> filterService;
    @Mock
    private EventProducer<PostViewDto> postCreateProducer;
    @Mock
    private EventProducer<PostUpdatedEvent> postUpdatedEventProducer;
    @Mock
    private ExecutorService executor;
    @InjectMocks
    private PostServiceImpl service;

    @Test
    @DisplayName("тест успешного создания поста")
    void create_success() {
        var currentUserId = 1L;
        var createDto = buildCreateDto(currentUserId, null);
        var savedPost = toEntity(createDto);
        var now = LocalDateTime.now();
        savedPost.setCreatedAt(now);
        savedPost.setUpdatedAt(now);
        savedPost.setId(1L);
        var post = toEntity(createDto);
        var view = toViewDto(savedPost);
        when(userContext.getUserId()).thenReturn(currentUserId);
        when(postMapper.toEntity(eq(createDto))).thenReturn(post);
        when(postRepository.save(eq(post))).thenReturn(savedPost);
        when(postMapper.toViewDto(eq(savedPost))).thenReturn(view);

        var actual = service.create(createDto);
        verify(userClient).getUser(currentUserId);
        assertEquals(view, actual);
    }

    @Test
    @DisplayName("тест успешного обновления поста")
    void update_success() {
        var currentUserId = 1L;
        var postId = 1L;
        var now = LocalDateTime.now();
        var updateDto = new PostUpdateDto("new content");
        var updatedPost = buildPostEntity(postId, currentUserId, null, now);
        updatedPost.setContent(updateDto.content());
        var oldPost = buildPostEntity(postId, currentUserId, null, now);
        var oldPostView = toViewDto(oldPost);
        var updatedPostView = toViewDto(updatedPost);

        when(postMapper.toViewDto(eq(oldPost))).thenReturn(oldPostView);
        when(userContext.getUserId()).thenReturn(currentUserId);
        when(postRepository.findById(postId)).thenReturn(Optional.of(oldPost));
        when(postRepository.save(eq(oldPost))).thenReturn(updatedPost);
        when(postMapper.toViewDto(eq(updatedPost))).thenReturn(updatedPostView);

        var actual = service.update(postId, updateDto);
        assertEquals(updatedPostView, actual);
        verify(postMapper).update(eq(updateDto), eq(oldPost));
    }

    @Test
    @DisplayName("тест успешного удаления поста")
    void delete_success() {
        var currentUserId = 1L;
        var postId = 1L;
        var now = LocalDateTime.now();
        var deletedPost = buildPostEntity(postId, currentUserId, null, now);
        deletedPost.setDeleted(true);
        var post = buildPostEntity(postId, currentUserId, null, now);

        when(userContext.getUserId()).thenReturn(currentUserId);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        service.delete(postId);
        verify(postRepository).save(eq(deletedPost));
    }

    @Test
    @DisplayName("тест успешного получения поста по id")
    void getById() {
        var postId = 1L;
        var authorId = 1L;
        var now = LocalDateTime.now();
        var post = buildPostEntity(postId, authorId, null, now);
        var view = toViewDto(post);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toViewDto(eq(post))).thenReturn(view);

        var actual = service.getById(postId);
        assertEquals(view, actual);
    }

    @Test
    @DisplayName("тест успешного получения постов")
    void getList_success() {
        var currentUserId = 1L;
        var now = LocalDateTime.now();
        var post1 = buildPostEntity(1L, currentUserId, null, now);
        var post2 = buildPostEntity(2L, currentUserId, null, now);
        var post3 = buildPostEntity(3L, currentUserId, null, now);
        var posts = List.of(post1, post2, post3);
        var views = toViewDtoList(posts);
        var filterDto = new PostFilterDto(currentUserId, true, false);

        when(postRepository.findByAuthorIdWithLikes(currentUserId)).thenReturn(posts);
        when(filterService.getFilteredList(eq(posts), eq(filterDto))).thenReturn(posts);
        when(postMapper.toViewDtoList(eq(posts))).thenReturn(views);

        var actual = service.getList(filterDto);
        assertEquals(views, actual);
    }
}