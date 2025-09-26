package faang.school.postservice.factory.post;

import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.CommentCache;
import faang.school.postservice.model.redis.PostCache;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostCacheFactoryTest {

    @Mock
    private PostMapper postMapper;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private PostCacheFactory postCacheFactory;

    private final int ttlOneDay = 86400;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(postCacheFactory, "ttl", ttlOneDay); // 1 day
    }

    @Test
    void fromPost_shouldMapPostAndCommentsCorrectly() {
        Post post = new Post();
        long testPostId = 123L;
        post.setId(testPostId);

        PostCache mappedCache = new PostCache();
        List<Comment> lastComments = List.of(new Comment(), new Comment());
        List<CommentCache> mappedComments = List.of(new CommentCache(), new CommentCache());

        when(postMapper.toPostCache(post)).thenReturn(mappedCache);
        when(commentRepository.findTop3ByPostIdOrderByCreatedAtDesc(testPostId)).thenReturn(lastComments);
        when(commentMapper.toCommentCacheList(lastComments)).thenReturn(mappedComments);

        PostCache result = postCacheFactory.fromPost(post);

        assertThat(result).isSameAs(mappedCache);
        assertThat(result.getTtl()).isEqualTo(ttlOneDay);
        assertThat(result.getLastComments()).isEqualTo(mappedComments);

        verify(postMapper).toPostCache(post);
        verify(commentRepository).findTop3ByPostIdOrderByCreatedAtDesc(testPostId);
        verify(commentMapper).toCommentCacheList(lastComments);
    }
}
