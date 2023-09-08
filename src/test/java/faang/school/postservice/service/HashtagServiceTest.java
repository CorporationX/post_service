package faang.school.postservice.service;

import faang.school.postservice.dto.hashtag.HashtagDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.HashtagMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class HashtagServiceTest {
    @Mock
    private HashtagRepository hashtagRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private HashtagMapper hashtagMapper;
    @Mock
    private PostMapper postMapper;
    @Mock
    private PostService postService;
    @InjectMocks
    private HashtagService hashtagService;
    private Post post;
    private PostDto postDto;
    private Hashtag hashtag;
    private HashtagDto hashtagDto;
    private final Long POST_ID = 10L;
    private final Long HASHTAG_ID = 20L;
    private final Long USER_ID = 30L;

    @BeforeEach
    public void setUp() {
        post = Post.builder().id(POST_ID).authorId(USER_ID).build();
        hashtag = Hashtag.builder().id(HASHTAG_ID).posts(List.of(post)).build();
        postDto = PostDto.builder().id(POST_ID).authorId(USER_ID).build();
        hashtagDto = HashtagDto.builder().id(HASHTAG_ID).postId(POST_ID).build();
    }

    @Test
    public void saveHashtagsTest() {
        Mockito.when(hashtagRepository.save(hashtagMapper.dtoToEntity(hashtagDto)))
                .thenReturn(hashtag);
        Mockito.when(postService.getPostByIdInternal(POST_ID)).thenReturn(post);
        post.setHashtags(Set.of(hashtag));

        hashtagService.saveHashtags(Set.of(hashtagDto));

        Mockito.verify(hashtagRepository, Mockito.times(1))
                .save(hashtagMapper.dtoToEntity(hashtagDto));
        Mockito.verify(postService, Mockito.times(1))
                .getPostByIdInternal(POST_ID);
        Mockito.verify(postService, Mockito.times(1))
                .updatePostInternal(post);
    }
}