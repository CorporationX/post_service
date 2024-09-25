//package faang.school.postservice.service.post;
//
//import faang.school.postservice.dto.PostDto;
//import faang.school.postservice.dto.filter.PostFilterDto;
//import faang.school.postservice.exception.post.PostAlreadyDeletedException;
//import faang.school.postservice.exception.post.PostAlreadyPublishedException;
//import faang.school.postservice.exception.post.PostWithTwoAuthorsException;
//import faang.school.postservice.exception.post.PostWithoutAuthorException;
//import faang.school.postservice.filter.post.PostFilter;
//import faang.school.postservice.filter.post.filterImpl.PostFilterProjectDraftNonDeleted;
//import faang.school.postservice.filter.post.filterImpl.PostFilterProjectPostNonDeleted;
//import faang.school.postservice.filter.post.filterImpl.PostFilterUserDraftNonDeleted;
//import faang.school.postservice.filter.post.filterImpl.PostFilterUserPostNonDeleted;
//import faang.school.postservice.mapper.PostMapperImpl;
//import faang.school.postservice.model.Like;
//import faang.school.postservice.model.Post;
//import faang.school.postservice.repository.PostRepository;
//import faang.school.postservice.util.container.PostContainer;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class PostServiceTest {
//    private PostService postService;
//    @Mock
//    private PostRepository postRepository;
//    @Mock
//    private PostValidator validator;
//    @Spy
//    private PostMapperImpl mapper;
//    @Spy
//    private PostFilterUserDraftNonDeleted userDraftNonDeleted;
//    @Spy
//    private PostFilterUserPostNonDeleted userPostNonDeleted;
//    @Spy
//    private PostFilterProjectDraftNonDeleted projectDraftNonDeleted;
//    @Spy
//    private PostFilterProjectPostNonDeleted projectPostNonDeleted;
//    @Captor
//    private ArgumentCaptor<Post> captorPost;
//    private PostContainer container = new PostContainer();
//    private Post entity;
//    private PostDto dto;
//
//    @BeforeEach
//    void setUp() {
//        entity = container.entity();
//        dto = container.dto();
//        List<PostFilter> postFilters = List.of(userDraftNonDeleted, userPostNonDeleted, projectDraftNonDeleted,
//                projectPostNonDeleted);
//
//        postService = new PostService(postRepository, validator, mapper, postFilters);
//    }
//
//    @Test
//    void testCreateWithInvalidAuthor() {
//        // given
//        PostDto dtoWOAuthors = PostDto.builder()
//                .id(container.postId())
//                .build();
//
//        PostDto dtoWithTwoAuthors = PostDto.builder()
//                .id(container.postId() + 1)
//                .build();
//
//        // when
//        doThrow(PostWithoutAuthorException.class).when(validator).validateBeforeCreate(dtoWOAuthors);
//        doThrow(PostWithTwoAuthorsException.class).when(validator).validateBeforeCreate(dtoWithTwoAuthors);
//
//        // then
//        Assertions.assertThrows(PostWithoutAuthorException.class, () -> postService.create(dtoWOAuthors));
//        Assertions.assertThrows(PostWithTwoAuthorsException.class, () -> postService.create(dtoWithTwoAuthors));
//    }
//
//    @Test
//    void testCreateSuccessfully() {
//        PostDto inputDto = PostDto.builder()
//                .authorId(container.authorId())
//                .content(container.content())
//                .build();
//        Post entity = Post.builder()
//                .authorId(container.authorId())
//                .content(container.content())
//                .build();
//
//        when(mapper.toEntity(inputDto)).thenReturn(entity);
//        when(postRepository.save(entity)).thenReturn(entity);
//        when(mapper.toDto(entity)).thenReturn(inputDto);
//
//        PostDto actual = postService.create(inputDto);
//
//        verify(validator, times(1)).validateBeforeCreate(inputDto);
//        verify(postRepository, times(1)).save(entity);
//        assertEquals(inputDto, actual);
//    }
//
//    @Test
//    void testPublishTwice() {
//        // given
//        Long postId = container.postId();
//        when(postRepository.findById(postId)).thenReturn(Optional.of(entity));
//        doThrow(PostAlreadyPublishedException.class).when(validator).validateBeforePublishing(entity);
//
//        // then
//        Assertions.assertThrows(PostAlreadyPublishedException.class, () -> postService.publish(postId));
//    }
//
//    @Test
//    void testPublish() {
//        // given
//        Long postId = entity.getId();
//        boolean isNotPublished = container.published();
//        LocalDateTime currentTime = LocalDateTime.now();
//
//        Post entity = Post.builder()
//                .id(postId)
//                .published(isNotPublished)
//                .build();
//        when(postRepository.findById(postId)).thenReturn(Optional.of(entity));
//
//        // when
//        postService.publish(postId);
//
//        // then
//        verify(postRepository, times(1)).save(captorPost.capture());
//        Post capturedPost = captorPost.getValue();
//        LocalDateTime publishedAt = capturedPost.getPublishedAt();
//        assertEquals(entity.getId(), capturedPost.getId());
//        assertTrue(capturedPost.isPublished());
//        assertTrue(Duration.between(currentTime, publishedAt).toSeconds() < 1);
//    }
//
//    @Test
//    void testUpdateWithInvalidData() {
//        // given
//        when(postRepository.findById(dto.getId())).thenReturn(Optional.of(entity));
//        doThrow(PostWithTwoAuthorsException.class).when(validator).validateBeforeUpdate(dto, entity);
//
//        // then
//        Assertions.assertThrows(PostWithTwoAuthorsException.class, () -> postService.update(dto));
//    }
//
//    @Test
//    void testUpdateSuccessfully() {
//        PostDto dto = PostDto.builder()
//                .id(1L)
//                .content("new content")
//                .build();
//        Post entity = Post.builder()
//                .id(1L)
//                .content("content")
//                .build();
//        Post updatedEntity = Post.builder()
//                .id(1L)
//                .content("new content")
//                .build();
//        when(postRepository.findById(dto.getId())).thenReturn(Optional.of(entity));
//        when(mapper.toDto(updatedEntity)).thenReturn(dto);
//        when(postRepository.save(updatedEntity)).thenReturn(updatedEntity);
//
//        PostDto actual = postService.update(dto);
//
//        verify(postRepository, times(1)).save(updatedEntity);
//        verify(validator, times(1)).validateBeforeUpdate(dto, updatedEntity);
//        assertEquals(dto, actual);
//    }
//
//    @Test
//    void testAlreadyDeleted() {
//        // given
//        Long postId = container.postId();
//        when(postRepository.findById(postId)).thenReturn(Optional.of(entity));
//        doThrow(PostAlreadyDeletedException.class).when(validator).validateBeforeDeleting(entity);
//
//        // then
//        Assertions.assertThrows(PostAlreadyDeletedException.class, () -> postService.delete(postId));
//    }
//
//    @Test
//    void testDelete() {
//        // given
//        Long postId = container.postId();
//        boolean isNotDeleted = container.deleted();
//
//        Post entity = Post.builder()
//                .id(postId)
//                .deleted(isNotDeleted)
//                .build();
//        when(postRepository.findById(postId)).thenReturn(Optional.of(entity));
//
//        Post deletedEntityExp = Post.builder()
//                .id(postId)
//                .deleted(!isNotDeleted)
//                .build();
//
//        // when
//        postService.delete(postId);
//
//        // then
//        verify(postRepository, times(1)).save(captorPost.capture());
//        Post deletedEntity = captorPost.getValue();
//        deletedEntityExp.setUpdatedAt(deletedEntity.getUpdatedAt());
//        assertEquals(deletedEntityExp, deletedEntity);
//    }
//
//    @Test
//    void testGetPost() {
//        // given
//        Long postId = dto.getId();
//        when(postRepository.findById(postId)).thenReturn(Optional.of(entity));
//
//        // when
//        PostDto actualDto = postService.getPost(postId);
//
//        // then
//        assertEquals(dto, actualDto);
//    }
//
//    @Test
//    void testGetFilteredPosts() {
//        // given
//        List<Post> posts = createPosts();
//        PostFilterDto filters = new PostFilterDto(3L, null, false, false);
//        when(postRepository.findAll()).thenReturn(posts);
//        int expSize = 1;
//        PostDto expDto = mapper.toDto(posts.get(4));
//
//        // when
//        List<PostDto> filteredPosts = postService.getFilteredPosts(filters);
//
//        // then
//        assertEquals(expSize, filteredPosts.size());
//        assertEquals(expDto, filteredPosts.get(0));
//
//    }
//
//    private List<Post> createPosts() {
//        Long userId = container.authorId();
//        Long projectId = container.projectId();
//        boolean isNotDeleted = container.deleted();
//        boolean isNotPublished = container.published();
//        Long postId = container.postId();
//        List<Like> likes = container.likes();
//
//        Post postFirst = createPost(postId++, userId, likes, null, isNotDeleted, isNotPublished, null);
//        Post postSecond = createPost(postId++, userId, likes, null, isNotDeleted, !isNotPublished, LocalDateTime.now());
//        Post postThird = createPost(postId++, userId, likes, null, !isNotDeleted, isNotPublished, null);
//        Post postForth = createPost(postId++, userId++, likes, null, !isNotDeleted, !isNotPublished, LocalDateTime.now());
//        Post postFifth = createPost(postId++, userId, likes, null, isNotDeleted, isNotPublished, null);
//        Post postSixth = createPost(postId++, null, likes, projectId++, isNotDeleted, isNotPublished, null);
//        Post postSeventh = createPost(postId, null, likes, projectId, isNotDeleted, isNotPublished, null);
//        return List.of(postFirst, postSecond, postThird, postForth, postFifth, postSixth, postSeventh);
//    }
//
//    private Post createPost(Long postId, Long authorId, List<Like> likes, Long projectId, boolean deleted, boolean published, LocalDateTime publishedAt) {
//        return Post.builder()
//                .id(postId)
//                .authorId(authorId)
//                .projectId(projectId)
//                .deleted(deleted)
//                .published(published)
//                .publishedAt(publishedAt)
//                .likes(likes)
//                .build();
//    }
//
//}
