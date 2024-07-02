package faang.school.postservice.service;

import faang.school.postservice.dto.event.LikeKafkaEvent;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaLikeProducer;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validator.LikeValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long POST_ID = 1L;
    private static final long LIKE_ID = 1L;

    @Mock
    private LikeValidator likeValidator;
    @Mock
    private PostService postService;
    @Mock
    private LikeMapper likeMapper;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private KafkaLikeProducer kafkaLikeProducer;

    @InjectMocks
    private LikeService likeService;


    @Test
    public void whenLikePost() {
        LikeDto likeDto = LikeDto.builder()
                .userId(USER_ID)
                .postId(POST_ID)
                .build();
        LikeDto savedLikeDto = LikeDto.builder()
                .id(LIKE_ID)
                .userId(USER_ID)
                .postId(POST_ID)
                .build();
        Post post = Post.builder()
                .id(POST_ID)
                .build();
        Like like = Like.builder()
                .userId(USER_ID)
                .build();
        Like savedLike = Like.builder()
                .id(LIKE_ID)
                .userId(USER_ID)
                .build();
        LikeKafkaEvent likeKafkaEvent = new LikeKafkaEvent(USER_ID, POST_ID, null);

        when(postService.findById(POST_ID)).thenReturn(post);
        when(likeMapper.toEntity(likeDto)).thenReturn(like);
        when(likeRepository.save(like)).thenReturn(savedLike);
        when(likeMapper.toDto(savedLike)).thenReturn(savedLikeDto);

        LikeDto actualResult = likeService.addLikeToPost(POST_ID, likeDto);

        assertEquals(savedLikeDto, actualResult);
        verify(kafkaLikeProducer).sendEvent(likeKafkaEvent);
    }
}