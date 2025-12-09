package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.common.PageResponse;
import faang.school.postservice.dto.post.PostV2CreateDto;
import faang.school.postservice.dto.post.PostV2Dto;
import faang.school.postservice.dto.post.PostV2UpdateDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.helpers.TestUtils;
import faang.school.postservice.mapper.PostV2Mapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostV2ServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private PostV2Service postV2Service;
    @Captor
    private ArgumentCaptor<Post> postCaptor;

    private final Long userId = 1L;
    private final Long postId = 3L;
    private final UserDto user = new UserDto(userId, "name", "email", Boolean.TRUE);
    private final String content = "Post content";
    private final Post post = Post.builder().id(postId).build();
    private final Pageable pageableDefault = PageRequest.of(0, 5);

    @Test
    void createPostAsDraft_shouldCreatePostByAuthor() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(postRepository.save(postCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        PostV2CreateDto postV2CreateDto = new PostV2CreateDto(content);

        PostV2Dto createdDraft;
        createdDraft = postV2Service.createPostAsDraft(postV2CreateDto);

        Post capturedPost = postCaptor.getValue();
        assertEquals(capturedPost.getAuthorId(), userId);
        assertNull(capturedPost.getProjectId());
        assertEquals(content, capturedPost.getContent());
        assertEquals(createdDraft.authorId(), userId);
        assertEquals(content, createdDraft.content());
        assertNull(createdDraft.projectId());
    }

    @Test
    void publishPost_shouldThrowException_whenPostAlreadyPublished() {
        post.setPublished(true);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(postRepository.getByIdOrThrow(postId)).thenReturn(post);

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "Post id=%s is already published".formatted(postId),
                () -> postV2Service.publishPost(postId)
        );
    }

    @Test
    void publishPost_shouldThrowException_whenValidationUpdatingPostFailsByAuthorId() {
        post.setPublished(false);
        post.setAuthorId(userId + 1);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(postRepository.getByIdOrThrow(postId)).thenReturn(post);

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "User id=%s is not author of post".formatted(userId),
                () -> postV2Service.publishPost(postId)
        );
    }

    @Test
    void publishPost_shouldPublishPost_whenAllDataValid() {
        post.setPublished(false);
        post.setContent(content);
        post.setAuthorId(userId);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(postRepository.getByIdOrThrow(postId)).thenReturn(post);
        when(postRepository.save(postCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        PostV2Dto publishedPost = postV2Service.publishPost(postId);
        Post capturedPost = postCaptor.getValue();

        assertTrue(capturedPost.isPublished());
        TestUtils.assertTimestamp(capturedPost.getPublishedAt());
        assertTrue(publishedPost.published());
        TestUtils.assertTimestamp(publishedPost.publishedAt());
    }

    @Test
    void updatePost_shouldThrowException_whenValidationUpdatingPostFailsByAuthorId() {
        post.setAuthorId(userId + 1);
        post.setContent(content);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(postRepository.findPostWithLikesAndCommentOrThrow(postId)).thenReturn(post);

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "User id=%s is not author of post".formatted(userId),
                () -> postV2Service.updatePost(postId, new PostV2UpdateDto("Updated content"))
        );
    }

    @Test
    void updatePost_shouldUpdatePost_whenAllDataValid() {
        post.setAuthorId(userId);
        post.setContent(content);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(postRepository.findPostWithLikesAndCommentOrThrow(postId)).thenReturn(post);
        when(postRepository.save(postCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        String updatedContent = "Updated content";
        PostV2Dto updated = postV2Service.updatePost(postId, new PostV2UpdateDto(updatedContent));
        Post capturedPost = postCaptor.getValue();

        assertEquals(updatedContent, capturedPost.getContent());
        assertEquals(updatedContent, updated.content());
        assertEquals(userId, updated.authorId());
    }

    @Test
    void deletePostSoftly_shouldThrowException_whenPostAlreadyDeleted() {
        post.setAuthorId(userId);
        post.setDeleted(true);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(postRepository.getByIdOrThrow(postId)).thenReturn(post);

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "Post id=%s is already deleted".formatted(postId),
                () -> postV2Service.deletePostSoftly(postId)
        );
    }

    @Test
    void deletePostSoftly_shouldThrowException_whenValidationUpdatingPostFailsByAuthorId() {
        post.setAuthorId(userId + 1);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(postRepository.getByIdOrThrow(postId)).thenReturn(post);

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "User id=%s is not author of post".formatted(userId),
                () -> postV2Service.deletePostSoftly(postId)
        );
    }

    @Test
    void deletePostSoftly_shouldDeletePost_whenAllDataValid() {
        post.setAuthorId(userId);
        post.setDeleted(false);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(postRepository.getByIdOrThrow(postId)).thenReturn(post);
        when(postRepository.save(postCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        postV2Service.deletePostSoftly(postId);
        Post capturedPost = postCaptor.getValue();

        assertTrue(capturedPost.isDeleted());
    }

    @Test
    void findById_shouldReturnPostDto_whenPostExists() {
        post.setAuthorId(userId);
        post.setContent(content);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        PostV2Dto found = postV2Service.findById(postId);

        assertEquals(postId, found.id());
        assertEquals(content, found.content());
        assertEquals(userId, found.authorId());
    }

    @Test
    void findAllDraftsByAuthor_shouldReturnPage() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);

        List<Post> posts = generateListPosts();
        Page<Post> page = new PageImpl(posts.subList(0, pageableDefault.getPageSize()), pageableDefault, posts.size());
        when(postRepository.findAll(any(Specification.class), eq(pageableDefault))).thenReturn(page);

        PageResponse<PostV2Dto> pageResponse = postV2Service.findAllDraftsByAuthor(pageableDefault);

        assertEquals(0, pageResponse.pageNumber());
        assertEquals(5, pageResponse.pageSize());
        assertEquals(2, pageResponse.totalPages());
        assertEquals(10, pageResponse.totalElements());

        List<PostV2Dto> content = pageResponse.content();
        assertEquals(5, content.size());
        assertEquals(content.get(0), PostV2Mapper.toDto(posts.get(0)));
        assertEquals(content.get(1), PostV2Mapper.toDto(posts.get(1)));
        assertEquals(content.get(2), PostV2Mapper.toDto(posts.get(2)));
        assertEquals(content.get(3), PostV2Mapper.toDto(posts.get(3)));
        assertEquals(content.get(4), PostV2Mapper.toDto(posts.get(4)));
    }

    @Test
    void findAllPublishedByFilter_shouldReturnPage_whenAuthorIdProvided() {
        List<Post> posts = generateListPosts();
        Page<Post> page = new PageImpl(posts.subList(0, pageableDefault.getPageSize()), pageableDefault, posts.size());
        when(postRepository.findAll(any(Specification.class), eq(pageableDefault))).thenReturn(page);

        PageResponse<PostV2Dto> pageResponse = postV2Service.findAllPublishedByFilter(userId, pageableDefault);

        assertEquals(0, pageResponse.pageNumber());
        assertEquals(5, pageResponse.pageSize());
        assertEquals(2, pageResponse.totalPages());
        assertEquals(10, pageResponse.totalElements());

        List<PostV2Dto> content = pageResponse.content();
        assertEquals(5, content.size());
        assertEquals(content.get(0), PostV2Mapper.toDto(posts.get(0)));
        assertEquals(content.get(1), PostV2Mapper.toDto(posts.get(1)));
        assertEquals(content.get(2), PostV2Mapper.toDto(posts.get(2)));
        assertEquals(content.get(3), PostV2Mapper.toDto(posts.get(3)));
        assertEquals(content.get(4), PostV2Mapper.toDto(posts.get(4)));
    }

    private List<Post> generateListPosts() {
        return List.of(
                Post.builder().id(1L).content("content1").build(),
                Post.builder().id(2L).content("content2").build(),
                Post.builder().id(3L).content("content3").build(),
                Post.builder().id(4L).content("content4").build(),
                Post.builder().id(5L).content("content").build(),
                Post.builder().id(6L).content("content6").build(),
                Post.builder().id(7L).content("content7").build(),
                Post.builder().id(8L).content("content8").build(),
                Post.builder().id(9L).content("content9").build(),
                Post.builder().id(10L).content("content10").build()
        );
    }

}