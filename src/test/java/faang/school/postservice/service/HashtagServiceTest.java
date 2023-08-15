package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.hashtag.HashtagDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.HashtagMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HashtagServiceTest {
    @Mock
    private HashtagRepository hashtagRepository;
    @Mock
    private PostRepository postRepository;
    @Spy
    private HashtagMapper hashtagMapper = Mappers.getMapper(HashtagMapper .class);
//    @Spy
//    private PostMapper postMapper = Mappers.getMapper(PostMapper .class);;
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
    public void setUp(){
        post = Post.builder().id(POST_ID).authorId(USER_ID).build();
        hashtag = Hashtag.builder().id(HASHTAG_ID).posts(List.of(post)).authorId(USER_ID).build();
        postDto = PostDto.builder().id(POST_ID).authorId(USER_ID).build();
        hashtagDto = HashtagDto.builder().id(HASHTAG_ID).postId(POST_ID).authorId(USER_ID).build();
    }

    @Test
    public void addHashtagToPostSaveDataIntoDBTest(){
        hashtagDto.setContent("#tag");
        hashtag.setContent("#tag");
        Mockito.when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        Mockito.when(hashtagRepository.save(hashtag)).thenReturn(hashtag);

        assertEquals(hashtagDto, hashtagService.addHashtagToPost(hashtagDto, USER_ID));
    }
}