package faang.school.postservice.service;

import faang.school.postservice.dto.post.HashtagDto;
import faang.school.postservice.mapper.HashtagMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.service.hashtag.HashtagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HashtagServiceTest {

    @InjectMocks
    private HashtagService hashtagService;

    @Mock
    private HashtagRepository hashtagRepository;

    @Mock
    private HashtagMapper hashtagMapper;

    private Hashtag hashtag1;
    private Hashtag hashtag2;
    private List<Hashtag> hashtags;
    private Post post1;
    private Post post2;
    private List<Post> posts;
    private HashtagDto hashtagDto1;
    private HashtagDto hashtagDto2;
    private List<HashtagDto> hashtagDtos;

    @BeforeEach
    public void setUp() {
        post1 = Post.builder().id(1L).content("Post 1").published(true).build();
        post2 = Post.builder().id(2L).content("Post 2").published(true).build();
        posts = Arrays.asList(post1, post2);

        hashtag1 = Hashtag.builder().id(1L).name("#hashtag1").posts(posts).build();
        hashtag2 = Hashtag.builder().id(2L).name("#hashtag2").posts(posts).build();
        hashtags = Arrays.asList(hashtag1, hashtag2);

        hashtagDto1 = HashtagDto.builder().id(1L).name("#hashtag1").build();
        hashtagDto2 = HashtagDto.builder().id(2L).name("#hashtag2").build();
        hashtagDtos = Arrays.asList(hashtagDto1, hashtagDto2);
    }

    @Test
    public void testUpdateHashtagMap() {
        when(hashtagRepository.findAll()).thenReturn(hashtags);

        hashtagService.updateHashtagMap();

        Map<Hashtag, List<Post>> expectedMap = new HashMap<>();
        expectedMap.put(hashtag1, posts);
        expectedMap.put(hashtag2, posts);

        assertEquals(expectedMap, hashtagService.getHashtagsWithPosts());
    }

    @Test
    public void testFindTopXPopularHashtags() {
        when(hashtagRepository.findTopXPopularHashtags(PageRequest.of(0, 2))).thenReturn(hashtags);
        when(hashtagMapper.toDto(hashtags)).thenReturn(hashtagDtos);

        List<HashtagDto> result = hashtagService.findTopXPopularHashtags(2);
        assertEquals(hashtagDtos, result);
    }
}
