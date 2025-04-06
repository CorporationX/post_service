package faang.school.postservice.hashtags;

import faang.school.postservice.dto.hashtag.HashtagRequestDto;
import faang.school.postservice.dto.hashtag.PostResponseDto;
import faang.school.postservice.mapper.HashtagsPostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.hashtags.HashtagRedisService;
import faang.school.postservice.service.hashtags.HashtagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashtagServiceTest {
    @InjectMocks
    private HashtagService hashtagService;

    @Mock
    private HashtagRepository hashtagRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private HashtagRedisService hashtagRedisService;

    @Spy
    private HashtagsPostMapper postMapper;

    private final int maxCachedPosts = 100;
    private HashtagRequestDto hashtagRequestDto;
    private PostResponseDto postResponseDto;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private final LocalDateTime publishedAt = LocalDateTime.now();
    private Post post;

    @BeforeEach
    public void setUp() {
        postResponseDto = new PostResponseDto(1L, "content", 1L,
                null, LocalDateTime.now().format(dateFormatter));
        hashtagRequestDto = new HashtagRequestDto("hashtag", 0, 100);
        ReflectionTestUtils.setField(hashtagService, "maxCachedPosts", maxCachedPosts);
        post = Post.builder().id(1L).content("content").authorId(1L).publishedAt(publishedAt).build();
    }

    @Test
    public void testGetPostsByHashtag_returnFromRedis() {
        Page<PostResponseDto> postPage = new PageImpl<>(Collections.singletonList(postResponseDto));
        when(hashtagRedisService.getPostsByHashtag(hashtagRequestDto))
                .thenReturn(postPage);

        hashtagService.getPostsByHashtag(hashtagRequestDto);

        verify(hashtagRedisService, times(1))
                .getPostsByHashtag(hashtagRequestDto);
        verify(postRepository, never()).findPostsByHashtag(any(), anyString());
    }

    @Test
    public void testGetPostsByHashtag_returnFromDB() {
        Page<Post> postPage = new PageImpl<>(Collections.singletonList(post));
        when(hashtagRedisService.getPostsByHashtag(hashtagRequestDto))
                .thenReturn(null);
        when(postRepository.findPostsByHashtag(any(), anyString())).thenReturn(postPage);

        hashtagService.getPostsByHashtag(hashtagRequestDto);

        verify(hashtagRedisService, times(1))
                .getPostsByHashtag(hashtagRequestDto);
        Pageable pageable = PageRequest.of(hashtagRequestDto.getPage(), hashtagRequestDto.getSize());
        verify(postRepository, times(1))
                .findPostsByHashtag(pageable, hashtagRequestDto.getTag());
    }

    @Test
    public void testGetPostsByHashtag_moreThanRedisCanStore() {
        hashtagRequestDto.setSize(1000);
        Page<Post> postPage = new PageImpl<>(Collections.singletonList(post));
        when(postRepository.findPostsByHashtag(any(), anyString())).thenReturn(postPage);

        hashtagService.getPostsByHashtag(hashtagRequestDto);

        verify(hashtagRedisService, never())
                .getPostsByHashtag(hashtagRequestDto);
        Pageable pageable = PageRequest.of(hashtagRequestDto.getPage(), hashtagRequestDto.getSize());
        verify(postRepository, times(1))
                .findPostsByHashtag(pageable, hashtagRequestDto.getTag());
    }

    @Test
    public void testExtractHashtagsFromPost_extractHashtags() {
        post.setContent("Content #hashtag_1 #h2 #who? ");
        when(hashtagRepository.findByTag(anyString())).thenReturn(null);
        when(hashtagRepository.save(any())).thenReturn(new Hashtag());

        hashtagService.extractHashtagsFromPost(post);

        verify(hashtagRedisService, times(1))
                .saveHashtag("hashtag_1", post);
        verify(hashtagRedisService, times(1))
                .saveHashtag("h2", post);
        verify(hashtagRedisService, times(1))
                .saveHashtag("who?", post);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    public void testExtractHashtagsFromPost_noHashtags() {
        post.setContent("Just text without hashtags");

        hashtagService.extractHashtagsFromPost(post);

        verify(hashtagRedisService, never())
                .saveHashtag(anyString(), any());
        verify(postRepository, times(1)).save(post);
    }
}
