package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashtagServiceTest {
    @Mock
    private HashtagRepository hashtagRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private HashtagService hashtagService;
    Hashtag hashtag;
    Post post;
    PostDto postDto;

    @BeforeEach
    public void setUp() {
        post = Post.builder().id(1).createdAt(LocalDateTime.now()).build();
        hashtag = Hashtag.builder().id(1).posts(List.of(post)).build();
        postDto = PostDto.builder().id(1L).build();
    }

    @Test
    public void testGetPostByHashtag() {
        String tag = "exampleHashtag";
        List<Hashtag> hashtagEntities = List.of(hashtag);

        when(hashtagRepository.findByHashtags(tag.toLowerCase())).thenReturn(hashtagEntities);
        when(postMapper.toDto(post)).thenReturn(postDto);

        Set<PostDto> result = hashtagService.getPostByHashtag(tag);

        assertEquals(result, Set.of(postDto));
        verify(hashtagRepository).findByHashtags(tag.toLowerCase());
        verify(postMapper).toDto(post);
    }
}
