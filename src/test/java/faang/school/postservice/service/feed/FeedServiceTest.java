package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.FeedCommentDto;
import faang.school.postservice.dto.post.FeedPostDto;
import faang.school.postservice.mapper.CommentMapperImpl;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedServiceTest {

    @InjectMocks
    private FeedServiceImpl feedService;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private FeedRedisService feedRedisService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Spy
    private PostMapperImpl postMapper;

    @Mock
    private ExecutorService feedNextPostBatchExecutor;

    @Mock
    private ExecutorService feedNextCommentBatchExecutor;

    @Spy
    private CommentMapperImpl commentMapper;

    private final int userHeatBatchSize = 2;
    private final String heatTopic = "heatTopic";
    private final int postBatchSize = 2;
    private final int commentBatchSize = 2;
    private final Long userId = 1L;
    private final Long postId = 1L;
    private final int offset = 0;
    private final List<FeedPostDto> feedPostDtos = new ArrayList<>();
    private final List<FeedCommentDto> feedCommentDtos = new ArrayList<>();
    private final List<Comment> comments = new ArrayList<>();
    private final List<Post> posts = new ArrayList<>();
    private Page<Post> postPage;
    private Page<Comment> commentPage;

    @BeforeEach
    public void setUp() {
        feedPostDtos.add(FeedPostDto.builder()
                .id(1L)
                .authorId(1L)
                .authorName("authorName")
                .comments(0)
                .likes(1)
                .views(1)
                .content("content1")
                .build());

        feedPostDtos.add(FeedPostDto.builder()
                .id(1L)
                .projectId(1L)
                .projectName("projectName")
                .comments(2)
                .likes(5)
                .views(10)
                .content("content2")
                .build());

        posts.add(Post.builder()
                .id(1L)
                .content("content1")
                .likes(new ArrayList<>())
                .comments(new ArrayList<>())
                .authorId(1L)
                .build());

        posts.add(Post.builder()
                .id(2L)
                .likes(new ArrayList<>())
                .comments(new ArrayList<>())
                .content("content2")
                .projectId(1L)
                .build());

        feedCommentDtos.add(FeedCommentDto.builder()
                .id(1L)
                .authorId(1L)
                .likes(0)
                .postId(1L)
                .build());

        feedCommentDtos.add(FeedCommentDto.builder()
                .id(2L)
                .authorId(1L)
                .likes(10)
                .postId(1L)
                .build());

        comments.add(Comment.builder()
                .id(1L)
                .post(posts.get(0))
                .content("content1")
                .likes(new ArrayList<>())
                .authorId(1L)
                .build());

        comments.add(Comment.builder()
                .id(2L)
                .post(posts.get(1))
                .content("content2")
                .likes(new ArrayList<>())
                .authorId(1L)
                .build());

        postPage = new PageImpl<>(posts);
        commentPage = new PageImpl<>(comments);
        ReflectionTestUtils.setField(feedService, "userHeatBatchSize", userHeatBatchSize);
        ReflectionTestUtils.setField(feedService, "heatTopic", heatTopic);
        ReflectionTestUtils.setField(feedService, "postBatchSize", postBatchSize);
        ReflectionTestUtils.setField(feedService, "commentBatchSize", commentBatchSize);
    }

    @Test
    public void testGetFeedPosts_takeFromCache_commentsAvailableInCache() {
        when(feedRedisService.isPostAvailableInCache(userId, offset)).thenReturn(true);
        when(feedRedisService.loadPostsFromCache(userId, offset)).thenReturn(feedPostDtos);
        when(feedRedisService.isCommentAvailableInCache(anyLong(), eq(offset))).thenReturn(true);

        List<FeedPostDto> result = feedService.getFeedPosts(userId, offset).getContent();

        assertEquals(feedPostDtos, result);
        verify(feedRedisService, times(2)).preloadPostComments(anyLong());
        verify(feedRedisService, times(1)).loadPostsFromCache(userId, offset);
    }

    @Test
    public void testGetFeedPosts_takeFromCache_commentsNotAvailableInCache() {
        when(feedRedisService.isPostAvailableInCache(userId, offset)).thenReturn(true);
        when(feedRedisService.loadPostsFromCache(userId, offset)).thenReturn(feedPostDtos);
        when(feedRedisService.isCommentAvailableInCache(anyLong(), eq(offset))).thenReturn(false);

        List<FeedPostDto> result = feedService.getFeedPosts(userId, offset).getContent();

        assertEquals(feedPostDtos, result);
        verify(feedRedisService, never()).preloadPostComments(anyLong());
        verify(feedRedisService, times(1)).loadPostsFromCache(userId, offset);
    }

    @Test
    public void testGetFeedPosts_takeFromDataBase() {
        List<Long> followees = new ArrayList<>();
        followees.add(10L);
        when(feedRedisService.isPostAvailableInCache(userId, offset)).thenReturn(false);
        when(userServiceClient.getFollowees(userId)).thenReturn(followees);
        when(postRepository.findPublishedPostsByAuthorIds(any(Pageable.class), eq(followees)))
                .thenReturn(postPage);

        List<FeedPostDto> result = feedService.getFeedPosts(userId, offset).getContent();

        assertEquals(postMapper.toFeedPostDtoList(posts), result);
        verify(feedRedisService, never()).loadPostsFromCache(userId, offset);
        verify(postRepository, times(1))
                .findPublishedPostsByAuthorIds(any(Pageable.class), eq(followees));
    }

    @Test
    public void testGetFeedComments_takeFromCache() {
        when(feedRedisService.isCommentAvailableInCache(postId, offset)).thenReturn(true);
        when(feedRedisService.loadCommentsFromCache(postId, offset)).thenReturn(feedCommentDtos);

        List<FeedCommentDto> result = feedService.getFeedComments(postId, offset).getContent();

        assertEquals(feedCommentDtos, result);
        verify(feedRedisService, times(1)).loadCommentsFromCache(postId, offset);
    }

    @Test
    public void testGetFeedComments_takeFromDataBase() {
        when(feedRedisService.isCommentAvailableInCache(postId, offset)).thenReturn(false);
        when(commentRepository.findByPostIdOrderByCreatedAtDesc(any(Pageable.class), eq(postId)))
                .thenReturn(commentPage);

        List<FeedCommentDto> result = feedService.getFeedComments(postId, offset).getContent();

        assertEquals(commentMapper.toFeedCommentDtoList(comments), result);
        verify(commentRepository, times(1))
                .findByPostIdOrderByCreatedAtDesc(any(Pageable.class), eq(postId));
    }
}
