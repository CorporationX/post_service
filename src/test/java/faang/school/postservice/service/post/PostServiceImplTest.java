package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.filter.FilterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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
    @InjectMocks
    private PostServiceImpl service;

    @Test
    void create_success() {
        var currentUserId = 1L;
        var createDto = PostServiceTestData.buildCreateDto(currentUserId, null);
        var savedPost = PostServiceTestData.toEntity(createDto);
        var now = LocalDateTime.now();
        savedPost.setCreatedAt(now);
        savedPost.setUpdatedAt(now);
        savedPost.setId(1L);
        var post = PostServiceTestData.toEntity(createDto);
        var view = PostServiceTestData.toViewDto(savedPost);
        when(userContext.getUserId()).thenReturn(currentUserId);
        when(postMapper.toEntity(eq(createDto))).thenReturn(post);
        when(postRepository.save(eq(post))).thenReturn(savedPost);
        when(postMapper.toViewDto(eq(savedPost))).thenReturn(view);

        var actual = service.create(createDto);
        verify(userClient).getUser(currentUserId);
        assertEquals(view, actual);
    }

    @Test
    void publish_success() {
        var currentUserId = 1L;
        var postId = 1L;
        var now = LocalDateTime.now();
        var publishedPost = PostServiceTestData.buildEntity(postId, currentUserId, null, now);
        publishedPost.setPublished(true);
        var post = PostServiceTestData.buildEntity(postId, currentUserId, null, now);
        when(userContext.getUserId()).thenReturn(currentUserId);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        service.publish(postId);
        verify(postRepository).save(refEq(publishedPost, "publishedAt"));
    }

    @Test
    void update_success() {
        var currentUserId = 1L;
        var postId = 1L;
        var now = LocalDateTime.now();
        var updateDto = new PostUpdateDto("new content");
        var updatedPost = PostServiceTestData.buildEntity(postId, currentUserId, null, now);
        updatedPost.setContent(updateDto.content());
        var post = PostServiceTestData.buildEntity(postId, currentUserId, null, now);
        var view = PostServiceTestData.toViewDto(updatedPost);

        when(userContext.getUserId()).thenReturn(currentUserId);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(eq(post))).thenReturn(updatedPost);
        when(postMapper.toViewDto(eq(updatedPost))).thenReturn(view);

        var actual = service.update(postId, updateDto);
        assertEquals(view, actual);
        verify(postMapper).update(eq(updateDto), eq(post));
    }

    @Test
    void delete_success() {
        var currentUserId = 1L;
        var postId = 1L;
        var now = LocalDateTime.now();
        var deletedPost = PostServiceTestData.buildEntity(postId, currentUserId, null, now);
        deletedPost.setDeleted(true);
        var post = PostServiceTestData.buildEntity(postId, currentUserId, null, now);

        when(userContext.getUserId()).thenReturn(currentUserId);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        service.delete(postId);
        verify(postRepository).save(eq(deletedPost));
    }

    @Test
    void getById() {
        var postId = 1L;
        var authorId = 1L;
        var now = LocalDateTime.now();
        var post = PostServiceTestData.buildEntity(postId, authorId, null, now);
        var view = PostServiceTestData.toViewDto(post);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toViewDto(eq(post))).thenReturn(view);

        var actual = service.getById(postId);
        assertEquals(view, actual);
    }

    @Test
    void getList_success() {
        var currentUserId = 1L;
        var now = LocalDateTime.now();
        var post1 = PostServiceTestData.buildEntity(1L, currentUserId, null, now);
        var post2 = PostServiceTestData.buildEntity(2L, currentUserId, null, now);
        var post3 = PostServiceTestData.buildEntity(3L, currentUserId, null, now);
        var posts = List.of(post1, post2, post3);
        var views = PostServiceTestData.toViewDtoList(posts);
        var filterDto = new PostFilterDto(currentUserId, true, false);

        when(postRepository.findByAuthorId(currentUserId)).thenReturn(posts);
        when(filterService.getFilteredList(eq(posts), eq(filterDto))).thenReturn(posts);
        when(postMapper.toViewDtoList(eq(posts))).thenReturn(views);

        var actual = service.getList(filterDto);
        assertEquals(views, actual);
    }
}