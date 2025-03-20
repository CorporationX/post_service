package faang.school.postservice.util.hashtags;

import faang.school.postservice.dto.hashtag.HashtagRequestDto;
import faang.school.postservice.dto.hashtag.PostResponseDto;
import faang.school.postservice.mapper.PostMapper;
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
import java.util.List;

import static faang.school.postservice.utils.validation.HashtagValidation.HASHTAG_REQUEST_DTO_CANT_BE_NULL;
import static faang.school.postservice.utils.validation.HashtagValidation.PAGE_IN_REQUEST_DTO_CANT_BE_NEGATIVE;
import static faang.school.postservice.utils.validation.HashtagValidation.SIZE_IN_REQUEST_DTO_CANT_BE_NEGATIVE;
import static faang.school.postservice.utils.validation.HashtagValidation.TAG_IN_REQUEST_DTO_CANT_BE_NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private PostMapper postMapper;

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
    public void testGetPostsByHashtag_nullHashtagRequestDto() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> hashtagService.getPostsByHashtag(null)
        );
        assertEquals(HASHTAG_REQUEST_DTO_CANT_BE_NULL, exception.getMessage());
    }

    @Test
    public void testGetPostsByHashtag_invalidHashtagRequestDto() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> hashtagService.getPostsByHashtag(new HashtagRequestDto(null, -1, -3))
        );
        List<String> errors = List.of(
                TAG_IN_REQUEST_DTO_CANT_BE_NULL,
                PAGE_IN_REQUEST_DTO_CANT_BE_NEGATIVE,
                SIZE_IN_REQUEST_DTO_CANT_BE_NEGATIVE
        );
        assertEquals(String.join(", ", errors), exception.getMessage());
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
    public void testExtractHashtagsFromContent_extractHashtags() {
        post.setContent("Content #hashtag_1 #h2 #who? ");
        when(hashtagRepository.findByTag(anyString())).thenReturn(null);
        when(hashtagRepository.save(any())).thenReturn(new Hashtag());

        hashtagService.extractHashtagsFromContent(post);

        verify(hashtagRedisService, times(1))
                .saveHashtag("hashtag_1", post);
        verify(hashtagRedisService, times(1))
                .saveHashtag("h2", post);
        verify(hashtagRedisService, times(1))
                .saveHashtag("who?", post);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    public void testExtractHashtagsFromContent_noHashtags() {
        post.setContent("Just text without hashtags");

        hashtagService.extractHashtagsFromContent(post);

        verify(hashtagRedisService, never())
                .saveHashtag(anyString(), any());
        verify(postRepository, times(1)).save(post);
    }
}
