package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.post.PostException;
import faang.school.postservice.exception.post.PostValidatorException;
import faang.school.postservice.filter.post.PostFilter;
import faang.school.postservice.dto.filter.PostFilterDto;
import faang.school.postservice.filter.post.filterImpl.PostFilterProjectDraftNonDeleted;
import faang.school.postservice.filter.post.filterImpl.PostFilterProjectPostNonDeleted;
import faang.school.postservice.filter.post.filterImpl.PostFilterUserDraftNonDeleted;
import faang.school.postservice.filter.post.filterImpl.PostFilterUserPostNonDeleted;
import faang.school.postservice.mapper.post.PostMapperImpl;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.service.postController.PostService;
import faang.school.postservice.validator.post.PostValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    private PostService postService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private AdRepository adRepository;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @Mock
    private PostValidator validator;
    @Spy
    private PostMapperImpl mapper;
    @Spy
    private PostFilterUserDraftNonDeleted userDraftNonDeleted;
    @Spy
    private PostFilterUserPostNonDeleted userPostNonDeleted;
    @Spy
    private PostFilterProjectDraftNonDeleted projectDraftNonDeleted;
    @Spy
    private PostFilterProjectPostNonDeleted projectPostNonDeleted;
    @Captor
    private ArgumentCaptor<Post> captorPost;
    private Post entity;
    private PostDto dto;

    @BeforeEach
    void setUp() {
        List<Optional> optionals = getOptionals();
        entity = (Post) optionals.get(0).get();
        dto = (PostDto) optionals.get(1).get();
        /*
          Нашёл такое решение для инжектирования списка компонентов.
          Делаю моки компонентов, создаю список из них и присваиваю тестируемому объекту список.
          Но у меня финальное поле и внедрение через конструктор.
          List<PostFilter> postFilters = List.of(userDraftNonDeleted, userPostNonDeleted, projectDraftNonDeleted,
                     projectPostNonDeleted);
          postService.setPostFilters(postFilters);
         */

        List<PostFilter> postFilters = List.of(userDraftNonDeleted, userPostNonDeleted, projectDraftNonDeleted,
                projectPostNonDeleted);
        postService = new PostService(postRepository, likeRepository, commentRepository, albumRepository, adRepository,
                resourceRepository, userServiceClient, projectServiceClient, validator, mapper, postFilters);
    }

    @Test
    void testCreateWithInvalidAuthor() {
        doThrow(PostValidatorException.class)
                .when(validator).validateAuthor(dto);

        Assertions.assertThrows(PostValidatorException.class, () -> postService.create(dto));
    }

    @Test
    void testCreateWithIsNotExistAuthor() {
        PostDto dtoWithOuAuthor = new PostDto();
        PostDto dtoWithOutProject = new PostDto();
        dtoWithOuAuthor.setAuthorId(1L);
        dtoWithOutProject.setProjectId(2L);
        when(userServiceClient.getUser(dtoWithOuAuthor.getAuthorId())).thenThrow(PostException.class);
        when(projectServiceClient.getProject(dtoWithOutProject.getProjectId())).thenThrow(PostException.class);

        Assertions.assertThrows(PostException.class, () -> postService.create(dtoWithOuAuthor));
        Assertions.assertThrows(PostException.class, () -> postService.create(dtoWithOutProject));
    }

    @Test
    void testCreateSuccessfully() {
        dto.setId(null);
        dto.setProjectId(null);
        entity.setId(null);
        entity.setProjectId(null);
        UserDto author = new UserDto(dto.getAuthorId(), "name", "email");
        when(userServiceClient.getUser(dto.getAuthorId())).thenReturn(author);
        List<Like> likes = new ArrayList<>(entity.getLikes());
        when(likeRepository.findAllById(dto.getLikeIds())).thenReturn(likes);
        List<Comment> comments = new ArrayList<>(entity.getComments());
        when(commentRepository.findAllById(dto.getCommentIds())).thenReturn(comments);
        List<Album> albums = new ArrayList<>(entity.getAlbums());
        when(albumRepository.findAllById(dto.getAlbumIds())).thenReturn(albums);
        Ad ad = entity.getAd();
        when(adRepository.findById(dto.getAdId())).thenReturn(Optional.of(ad));
        List<Resource> resources = new ArrayList<>(entity.getResources());
        when(resourceRepository.findAllById(dto.getResourceIds())).thenReturn(resources);

        postService.create(dto);

        verify(postRepository, times(1)).save(captorPost.capture());
        Post actualEntity = captorPost.getValue();
        entity.setCreatedAt(actualEntity.getCreatedAt());
        entity.setUpdatedAt(actualEntity.getUpdatedAt());
        assertEquals(entity, actualEntity);
    }

    @Test
    void testPublishTwice() {
        Long postPublished = entity.getId();
        when(postRepository.findById(postPublished)).thenReturn(Optional.of(entity));
        doThrow(PostValidatorException.class).when(validator).validatePublished(entity);

        Assertions.assertThrows(PostValidatorException.class, () -> postService.publish(postPublished));
    }

    @Test
    void testPublish() {
        Long postPublished = dto.getId();
        dto.setPublished(true);
        when(postRepository.findById(postPublished)).thenReturn(Optional.of(entity));

        postService.publish(dto.getId());

        verify(postRepository, times(1)).save(captorPost.capture());
        PostDto actualDto = mapper.toDto(captorPost.getValue());
        dto.setPublishedAt(actualDto.getPublishedAt());
        assertEquals(dto, actualDto);
    }

    @Test
    void testUpdateWithChangedAuthor() {
        when(postRepository.findById(dto.getId())).thenReturn(Optional.of(entity));
        doThrow(PostValidatorException.class).when(validator).checkImmutableData(dto, entity);

        Assertions.assertThrows(PostValidatorException.class, () -> postService.update(dto));
    }

    @Test
    void testUpdateSuccessfully() {
        when(postRepository.findById(dto.getId())).thenReturn(Optional.of(entity));
        when(adRepository.findById(dto.getAdId())).thenReturn(Optional.of(entity.getAd()));
        dto.setContent("newContent");

        postService.update(dto);

        verify(postRepository, times(1)).save(captorPost.capture());
        PostDto actualDto = mapper.toDto(captorPost.getValue());
        assertEquals(dto, actualDto);
    }

    @Test
    void testDelete() {
        when(postRepository.findById(dto.getId())).thenReturn(Optional.of(entity));
        dto.setDeleted(true);

        postService.delete(dto.getId());

        verify(postRepository, times(1)).save(captorPost.capture());
        Post actualEntity = captorPost.getValue();
        assertTrue(actualEntity.isDeleted());
    }

    @Test
    void testGetPost() {
        Long postId = dto.getId();
        when(postRepository.findById(postId)).thenReturn(Optional.of(entity));

        PostDto actualDto = postService.getPost(postId);

        assertEquals(dto, actualDto);
    }

    @Test
    void testGetFilteredPosts() {
        List<Post> posts = createPosts();
        // Неудалённые черновики пользователя с id:2
        PostFilterDto filters = new PostFilterDto(2L, null, false, false);
        when(postRepository.findAll()).thenReturn(posts);
        int expSize = 1;
        PostDto expDto = mapper.toDto(posts.get(4));

        List<PostDto> filteredPosts = postService.getFilteredPosts(filters);

        assertEquals(expSize, filteredPosts.size());
        assertEquals(expDto, filteredPosts.get(0));

    }

    private List<Post> createPosts() {
        Long userId = 1L;
        Long projectId = 1L;
        boolean isDeleted = false;
        boolean isPublished = false;
        Post postFirst = createPost(userId, null, isDeleted, isPublished);
        Post postSecond = createPost(userId, null, isDeleted, !isPublished);
        Post postThird = createPost(userId, null, !isDeleted, isPublished);
        Post postForth = createPost(userId++, null, !isDeleted, !isPublished);
        Post postFifth = createPost(userId, null, isDeleted, isPublished);
        Post postSixth = createPost(null, projectId++, isDeleted, isPublished);
        Post postSeventh = createPost(null, projectId, isDeleted, isPublished);
        return List.of(postFirst, postSecond, postThird, postForth, postFifth, postSixth, postSeventh);
    }

    private Post createPost(Long userId, Long projectId, boolean isDeleted, boolean isPublished) {
        Post post = new Post();
        post.setAuthorId(userId);
        post.setProjectId(projectId);
        post.setDeleted(isDeleted);
        post.setPublished(isPublished);
        return post;
    }

    private List<Optional> getOptionals() {
        Long postId = 1L;
        String content = "content";
        Long authorId = ++postId;
        Long projectId = ++postId;
        Like firstLike = new Like();
        Like secondLike = new Like();
        firstLike.setId(++postId);
        secondLike.setId(++postId);
        List<Long> likeIds = List.of(firstLike.getId(), secondLike.getId());
        List<Like> likes = List.of(firstLike, secondLike);
        Comment firstComment = new Comment();
        Comment secondComment = new Comment();
        firstComment.setId(++postId);
        secondComment.setId(++postId);
        List<Long> commentIds = List.of(firstComment.getId(), secondComment.getId());
        List<Comment> comments = List.of(firstComment, secondComment);
        Album firstAlbum = new Album();
        Album secondAlbum = new Album();
        firstAlbum.setId(++postId);
        secondAlbum.setId(++postId);
        List<Long> albumIds = List.of(firstAlbum.getId(), secondAlbum.getId());
        List<Album> albums = List.of(firstAlbum, secondAlbum);
        Ad ad = new Ad();
        ad.setId(++postId);
        Resource firstResource = new Resource();
        Resource secondResource = new Resource();
        firstResource.setId(++postId);
        secondResource.setId(++postId);
        List<Long> resourceIds = List.of(firstResource.getId(), secondResource.getId());
        List<Resource> resources = List.of(firstResource, secondResource);
        boolean published = false;
        LocalDateTime publishedAt = LocalDateTime.now();
        LocalDateTime scheduledAt = publishedAt.plusDays(1);
        boolean deleted = false;
        LocalDateTime createdAt = publishedAt.minusDays(1);
        LocalDateTime updatedAt = publishedAt.plusMinutes(5);

        Optional<Post> postOptional = Optional.of(new Post(postId, content, authorId, projectId, likes, comments,
                albums, ad, resources, published, publishedAt, scheduledAt, deleted, createdAt, updatedAt));
        Optional<PostDto> dtoOptional = Optional.of(new PostDto(postId, content, authorId, projectId, likeIds, commentIds,
                albumIds, ad.getId(), resourceIds, published, publishedAt, scheduledAt, deleted));

        return List.of(postOptional, dtoOptional);
    }
}
