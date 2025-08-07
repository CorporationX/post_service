package faang.school.postservice.service.hashtag;

import faang.school.postservice.cache.service.PostCacheService;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashtagServiceImplTest {
    @Mock
    private HashtagRepository hashtagRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostCacheService cacheService;
    @InjectMocks
    private HashtagServiceImpl hashtagService;

    @Test
    void update_shouldRemoveOldHashtagsFromPostAndCache() {
        // given
        Long postId = 1L;
        PostViewDto oldPostDto = new PostViewDto(postId, "old content with #java", null, null, null, null, null, null, null, null);
        PostViewDto newPostDto = new PostViewDto(postId, "new content with #spring", null, null, null, null, null, null, null, null);

        Post post = new Post();
        post.setId(postId);

        Hashtag oldHashtag = new Hashtag();
        oldHashtag.setName("#java");
        oldHashtag.setPosts(new HashSet<>(Set.of(post)));

        Hashtag newHashtag = new Hashtag();
        newHashtag.setName("#spring");

        post.setHashtags(new HashSet<>(Set.of(oldHashtag, newHashtag)));

        when(postRepository.findByIdOrThrow(postId)).thenReturn(post);
        when(hashtagRepository.findByName(eq("#spring"))).thenReturn(Optional.of(newHashtag));

        // when
        hashtagService.update(oldPostDto, newPostDto);

        // then
        // old hashtag must not contain the post
        assertFalse(oldHashtag.getPosts().contains(post));

        // cache service must be called to delete old hashtag
        verify(cacheService).deletePost(eq("#java"), eq(oldPostDto));
    }
}
