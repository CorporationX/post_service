package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Spy
    private PostMapper postMapper = PostMapper.INSTANCE;
    @Mock
    private HashtagRepository hashtagRepository;
    @InjectMocks
    private PostService postService;

    @Test
    void testOrderByDate() {
        String hashtag = "#test";
        when(postRepository.findByHashtagOrderByDate(hashtag)).thenReturn(createPostList());

        List<PostDto> result = postService.getPostsByHashtagOrderByDate(hashtag);

        assertNotNull(result);
        assertEquals(createPostDtoList(), result);
    }

    @Test
    void testOrderByPopularity() {
        String hashtag = "#test";
        when(postRepository.findByHashtagOrderByPopularity(hashtag)).thenReturn(createPostList());

        List<PostDto> result = postService.getPostsByHashtagOrderByPopularity(hashtag);

        assertNotNull(result);
        assertEquals(createPostDtoList(), result);
    }

    private List<Post> createPostList() {
        List<Hashtag> hashtags = createHashtagList();
        return List.of(Post.builder().id(1).hashtags(hashtags).build(),
                Post.builder().id(2).hashtags(hashtags).build(),
                Post.builder().id(3).hashtags(hashtags).build(),
                Post.builder().id(4).hashtags(hashtags).build(),
                Post.builder().id(5).hashtags(hashtags).build());
    }

    private List<PostDto> createPostDtoList() {
        List<String> hashtags = List.of("#test");
        return List.of(PostDto.builder().id(1).hashtags(hashtags).build(),
                PostDto.builder().id(2).hashtags(hashtags).build(),
                PostDto.builder().id(3).hashtags(hashtags).build(),
                PostDto.builder().id(4).hashtags(hashtags).build(),
                PostDto.builder().id(5).hashtags(hashtags).build());
    }

    private List<Hashtag> createHashtagList() {
        return List.of(Hashtag.builder().id(1L).hashtag("#test").build());
    }
}