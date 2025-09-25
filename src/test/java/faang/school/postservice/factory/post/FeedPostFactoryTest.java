package faang.school.postservice.factory.post;

import faang.school.postservice.dto.feed.FeedCommentDto;
import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.dto.feed.FeedUserDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostCache;
import faang.school.postservice.model.redis.UserCache;
import faang.school.postservice.repository.CacheRepository;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.redis.user.UserCacheRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedPostFactoryTest {

    @Mock
    private PostMapper postMapper;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private UserCacheRepository userCacheRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private CacheRepository cacheRepository;

    @InjectMocks
    private FeedPostFactory feedPostFactory;

    @Test
    void fromPost_shouldMapAndEnrich() {
        long authorId = 7L;
        Post post = new Post();
        post.setId(1L);
        post.setAuthorId(authorId);

        FeedPostDto mappedDto = new FeedPostDto();
        mappedDto.setId(1L);
        mappedDto.setAuthorId(authorId);

        Comment c1 = new Comment();
        Comment c2 = new Comment();
        List<Comment> comments = List.of(c1, c2);
        FeedCommentDto feedCommentDto1 = new FeedCommentDto();
        FeedCommentDto feedCommentDto2 = new FeedCommentDto();

        UserDto userDto = new UserDto(
                authorId,
                "User",
                "email"
        );
        FeedUserDto feedUserDto = new FeedUserDto();

        when(postMapper.toFeedPostDto(post)).thenReturn(mappedDto);
        when(commentRepository.findTop3ByPostIdOrderByCreatedAtDesc(1L)).thenReturn(comments);
        when(commentMapper.toFeedCommentDto(c1)).thenReturn(feedCommentDto1);
        when(commentMapper.toFeedCommentDto(c2)).thenReturn(feedCommentDto2);
        when(userCacheRepository.findById(String.valueOf(authorId))).thenReturn(Optional.empty());
        when(cacheRepository.getUser(authorId)).thenReturn(userDto);
        when(userMapper.toFeedUserDto(userDto)).thenReturn(feedUserDto);
        when(userMapper.toUserCache(userDto)).thenReturn(new UserCache());

        FeedPostDto result = feedPostFactory.fromPost(post);

        assertThat(result.getId()).isEqualTo(1L);
        assertEquals(List.of(feedCommentDto1, feedCommentDto2), result.getLastComments());
        assertThat(result.getAuthorUser()).isEqualTo(feedUserDto);

        verify(userCacheRepository).save(any());
    }

    @Test
    void fromPost_shouldSkipUserDataIfPostWithNoAuthor() {
        long postId = 10L;
        Post post = new Post();
        post.setId(postId);
        post.setAuthorId(null);

        FeedPostDto dto = new FeedPostDto();
        dto.setId(postId);
        dto.setAuthorId(null);
        dto.setAuthorUser(null);

        when(postMapper.toFeedPostDto(post)).thenReturn(dto);
        when(commentRepository.findTop3ByPostIdOrderByCreatedAtDesc(postId)).thenReturn(List.of());

        FeedPostDto result = feedPostFactory.fromPost(post);

        verify(userCacheRepository, never()).findById(any());
    }

    @Test
    void fromPostCache_shouldMapAndUseCacheUser() {
        long authorId = 5L;
        long postId = 9L;
        PostCache cache = new PostCache();
        cache.setId(postId);
        cache.setAuthorId(authorId);

        FeedPostDto feedPostDto = new FeedPostDto();
        feedPostDto.setId(postId);
        feedPostDto.setAuthorId(authorId);

        UserCache userCache = new UserCache();
        UserDto userDto = new UserDto(
                authorId,
                "User",
                "email"
        );

        FeedUserDto feedUser = new FeedUserDto();

        when(postMapper.toFeedPostDto(cache)).thenReturn(feedPostDto);
        when(userCacheRepository.findById(String.valueOf(authorId))).thenReturn(Optional.of(userCache));
        when(userMapper.toFeedUserDto(userCache)).thenReturn(feedUser);

        FeedPostDto result = feedPostFactory.fromPostCache(cache);

        assertThat(result.getId()).isEqualTo(postId);
        assertThat(result.getAuthorUser()).isEqualTo(feedUser);

        verifyNoInteractions(commentRepository, commentMapper);
        verify(userCacheRepository, never()).save(any());
    }
}
