package faang.school.postservice.service;

import faang.school.postservice.model.dto.hashtag.HashtagDto;
import faang.school.postservice.mapper.HashtagMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HashtagServiceTest {

    @Mock
    private HashtagRepository hashtagRepository;

    @Mock
    private HashtagMapper hashtagMapper;

    @Mock
    private PostService postService;

    @InjectMocks
    private HashtagService hashtagService;

    private Set<HashtagDto> hashtagDtos;
    private Post post;
    private Hashtag hashtag;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        HashtagDto hashtagDto = HashtagDto.builder()
                .id(1L)
                .postId(100L)
                .content("#example")
                .build();

        hashtagDtos = new HashSet<>();
        hashtagDtos.add(hashtagDto);

        post = new Post();
        post.setId(1L);
        post.setHashtags(new HashSet<>());

        hashtag = new Hashtag();
        hashtag.setId(1L);
    }

    @Test
    @Transactional
    void shouldSaveHashtagsSuccessfully() {
        when(hashtagMapper.dtoToEntity(any(HashtagDto.class))).thenReturn(hashtag);
        when(hashtagRepository.save(any(Hashtag.class))).thenReturn(hashtag);
        when(postService.getPostByIdInternal(anyLong())).thenReturn(post);

        hashtagService.saveHashtags(hashtagDtos);

        verify(hashtagRepository, times(1)).save(any(Hashtag.class));
        verify(postService, times(1)).getPostByIdInternal(anyLong());
        verify(postService, times(1)).updatePostInternal(any(Post.class));

        assertEquals(1, post.getHashtags().size());
        assertTrue(post.getHashtags().contains(hashtag));
    }
}