package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.hashtag.HashtagService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Spy
    private PostMapperImpl postMapper;

    @Mock
    private HashtagService hashtagService;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    void getPostsByHashtagOkSorted(){
        Post post1 = Post.builder().publishedAt(LocalDateTime.now().minusDays(3)).build();
        Post post2 = Post.builder().publishedAt(LocalDateTime.now()).build();
        when(hashtagService.findPostsByHashtag(anyString())).thenReturn(List.of(post1, post2));

        List<PostDto> posts = postService.getPostsByHashtag("a");

        assertEquals(2, posts.size());
        assertTrue(posts.get(0).publishedAt().isAfter(posts.get(1).publishedAt()));
    }
}
