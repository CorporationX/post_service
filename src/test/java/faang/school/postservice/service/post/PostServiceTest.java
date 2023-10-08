package faang.school.postservice.service.post;


import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.post.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostMapper postMapper;
    @Mock
    private PostValidator postValidator;
    @Spy
    private ConcurrentHashMap<LocalDateTime, Set<PostDto>> postMap;
    @InjectMocks
    private PostService postService;
    List<Post> listFindByVerifiedIsFalse;
    List<Post> draftsPosts;
    List<Post> publishedPosts;
    Post post;
    Post post2;
  
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(postService, "countOffensiveContentForBan", 1);
        ReflectionTestUtils.setField(postService, "timeForCacheUpdate", 3);

        Post post1 = Post.builder().authorId(1L).verified(false).build();
        Post post2 = Post.builder().authorId(1L).verified(false).build();
        Post post3 = Post.builder().authorId(3L).verified(true).build();
        Post post4 = Post.builder().authorId(2L).verified(false).build();
        Post post5 = Post.builder().authorId(2L).verified(false).build();

        listFindByVerifiedIsFalse = List.of(post1, post2, post3, post4, post5);
    }


    @BeforeEach
    public void init() {
        post = new Post();
        post.setAuthorId(1L);
        post.setDeleted(false);
        post.setId(1L);
        post.setCreatedAt(LocalDateTime.now());

        post2 = new Post();
        post2.setProjectId(1L);
        post2.setDeleted(false);
        post2.setId(1L);
        post2.setCreatedAt(LocalDateTime.now());

        draftsPosts = List.of(post, post2);

        Post post3 = new Post();
        post3.setPublishedAt(LocalDateTime.now());
        Post post4 = new Post();
        post4.setPublishedAt(LocalDateTime.now());
        publishedPosts = List.of(post3, post4);
    }

    @Test
    void getPostById() {
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(new Post()));
        postService.getPostById(1L);
        Mockito.verify(postRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void createPost() {
        //Mockito.when(postMapper.toEventDto(Mockito.any(PostDto.class))).thenReturn(new PostAchievementEventDto());
        PostDto postDto = PostDto.builder().content("content").authorId(1L).build();
        Mockito.when(postMapper.toEntity(postDto)).thenReturn(new Post());
        postService.createPost(postDto);
        Mockito.verify(postRepository, Mockito.times(1)).save(any(Post.class));
       // Mockito.verify(postMapper, Mockito.times(1)).toEventDto(Mockito.any(PostDto.class));
       // Mockito.verify(postAchievementPublisher, Mockito.times(1)).publish(Mockito.any(PostAchievementEventDto.class));
    }


    @Test
    void checkSchedule_Publish() {
        PostDto postDto = PostDto.builder()
                .id(1L)
                .authorId(1L)
                .build();
        Post post = new Post();

        Mockito.when(postRepository.findById(postDto.getId()))
                .thenReturn(Optional.of(post));
        postService.createPost(postDto);
        Mockito.verify(postValidator, Mockito.times(1))
                .validatePostByUser(post, postDto.getAuthorId());
        Mockito.verify(postValidator, Mockito.times(1))
                .isPublished(post);
        assertTrue(post.isPublished());
    }

    @Test
    void checkSchedule_ToCache() {
        PostDto postDto = PostDto.builder()
                .id(1L)
                .authorId(1L)
                .scheduledAt(LocalDateTime.now())
                .build();

        postService.createPost(postDto);

        Mockito.verify(postMap, Mockito.times(1))
                .putIfAbsent(any(LocalDateTime.class), any(HashSet.class));
        Mockito.verify(postMap, Mockito.times(1))
                .get(any(LocalDateTime.class));
    }

    @Test
    void findAllPostsByTimeAndStatus() {
        Mockito.when(postRepository.findAllPostsByTimeAndStatus(any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());
        postService.findAllPostsByTimeAndStatus();
        Mockito.verify(postRepository, Mockito.times(1))
                .findAllPostsByTimeAndStatus(any(LocalDateTime.class));
    }

    @Test
    void publishPost() {
        Post post = new Post();
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        postService.publishPost(1L, 1L);
        assertTrue(post.isPublished());
    }

    @Test
    void publishPostByProject() {
        Post post = new Post();
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        postService.publishPostByProject(1L, 1L);
        assertTrue(post.isPublished());
    }

    @Test
    void updatePost() {
        PostDto postDto = PostDto.builder().content("content").authorId(1L).build();
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        Mockito.when(postMapper.toEntity(postDto)).thenReturn(post);

        postService.updatePost(1L, postDto);
        Mockito.verify(postRepository, Mockito.times(1)).save(post);
    }

    @Test
    void deletePost() {
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.deletePost(1L, 1L);
        Mockito.verify(postRepository, Mockito.times(1)).save(post);
        assertTrue(post.isDeleted());
    }

    @Test
    void deletePostByProject() {
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(post2));

        postService.deletePostByProject(1L, 1L);
        Mockito.verify(postRepository, Mockito.times(1)).save(post2);
        assertTrue(post2.isDeleted());
    }

    @Test
    void getPost() {
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.getPost(1L);
        Mockito.verify(postRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void getAllUsersDrafts() {
        Mockito.when(postRepository.findAllUsersDrafts(1L)).thenReturn(draftsPosts);

        postService.getAllUsersDrafts(1L);
        Mockito.verify(postRepository, Mockito.times(1)).findAllUsersDrafts(1L);
        assertEquals(2, draftsPosts.size());
    }

    @Test
    void getAllProjectDrafts() {
        Mockito.when(postRepository.findAllProjectDrafts(1L)).thenReturn(draftsPosts);

        postService.getAllProjectDrafts(1L);
        Mockito.verify(postRepository, Mockito.times(1)).findAllProjectDrafts(1L);
        assertEquals(2, draftsPosts.size());
    }

    @Test
    void getAllUsersPublished() {
        Mockito.when(postRepository.findAllAuthorPublished(1L)).thenReturn(publishedPosts);

        postService.getAllUsersPublished(1L);
        Mockito.verify(postRepository, Mockito.times(1)).findAllAuthorPublished(1L);
        assertEquals(2, publishedPosts.size());
    }

    @Test
    void getAllProjectPublished() {
        Mockito.when(postRepository.findAllProjectPublished(1L)).thenReturn(publishedPosts);

        postService.getAllProjectPublished(1L);
        Mockito.verify(postRepository, Mockito.times(1)).findAllProjectPublished(1L);
        assertEquals(2, publishedPosts.size());
    }
    @Test
    void testGetByPostIsVerifiedFalse() {
        Mockito.when(postRepository.findByVerifiedIsFalse()).thenReturn(listFindByVerifiedIsFalse);
        List<Long> expected = List.of(1L, 2L);

        List<Long> actual = postService.getByPostIsVerifiedFalse();

        assertEquals(expected, actual);
    }

    @Test
    void testGetByPostIsVerifiedFalseWhenEmptyList() {
        Mockito.when(postRepository.findByVerifiedIsFalse()).thenReturn(List.of());
        List<Long> expected = List.of();

        List<Long> actual = postService.getByPostIsVerifiedFalse();

        assertEquals(expected, actual);
    }

    @Test
    void testGetByPostIsVerifiedFalseWhenNotVerified() {
        Mockito.when(postRepository.findByVerifiedIsFalse()).thenReturn(listFindByVerifiedIsFalse);
        List<Long> expected = List.of();
        ReflectionTestUtils.setField(postService, "countOffensiveContentForBan", 5);

        List<Long> actual = postService.getByPostIsVerifiedFalse();

        assertEquals(expected, actual);
    }
}