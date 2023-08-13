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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
        hashtag = Hashtag.builder().id(1).posts(new ArrayList<>(List.of(post))).build();
        postDto = PostDto.builder().id(1L).build();
    }

    @Test
    public void testGetPostByHashtag() {
        String tag = "exampleHashtag";

        when(hashtagRepository.findByHashtag(tag.toLowerCase())).thenReturn(Optional.of(hashtag));
        when(postMapper.toDto(post)).thenReturn(postDto);

        List<PostDto> result = hashtagService.getPostByHashtag(tag);

        assertEquals(result, List.of(postDto));
        verify(hashtagRepository).findByHashtag(tag.toLowerCase());
        verify(postMapper).toDto(post);
    }

    @Test
    public void testParseContent() {
        String content = "This is a #test #content with #hashtags";
        post.setContent(content);

        List<String> extractedHashtags = List.of("#test", "#content", "#hashtags");
        when(hashtagRepository.findByHashtag(any())).thenReturn(Optional.empty());
        when(hashtagRepository.save(any(Hashtag.class))).thenReturn(hashtag);

        hashtagService.parseContentToAdd(post);

        verify(hashtagRepository, times(extractedHashtags.size())).save(any());
    }

    @Test
    public void testParseEmptyContent() {
        String content = "   ";
        post.setContent(content);
        hashtagService.parseContentToAdd(post);

        verify(hashtagRepository, times(0)).save(any());
    }

    @Test
    public void testParseContentToUpdate() {
        String previousContent = "This is a #test #content with #hashtags";
        String newContent = "Updated #content with #newhashtags";

        when(hashtagRepository.findByHashtag(any())).thenReturn(Optional.of(hashtag));
        post.setContent(newContent);

        hashtagService.parseContentToUpdate(post, previousContent);

        verify(hashtagRepository, times(3)).findByHashtag(Mockito.anyString());
        verify(hashtagRepository, times(2)).deletePostHashtag(any(), any());
        verify(hashtagRepository, times(0)).save(any());
    }
}
