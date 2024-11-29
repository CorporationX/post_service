package faang.school.postservice.aop.aspects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.TestContainersConfig;
import faang.school.postservice.config.TestListenerConfig;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.RedisPostLikeEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.VerificationPostStatus;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.like.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//@SpringBootTest
//@ActiveProfiles("test")
//@Import(TestListenerConfig.class)
//public class PostLikeEventPublishingAspectTest extends TestContainersConfig {
//
//    @Autowired
//    LikeService likeService;
//    @Autowired
//    PostRepository postRepository;
//    @MockBean
//    UserContext userContext;
//    @MockBean
//    UserServiceClient userServiceClient;
//    @SpyBean(name = "postLikeMessageListener")
//    MessageListener postLikeMessageListener;
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Captor
//    ArgumentCaptor<Message> messageCaptor;
//
//    @BeforeEach
//    void init() {
//        Post post = Post.builder()
//                .authorId(1L)
//                .published(true)
//                .content("content")
//                .verificationStatus(VerificationPostStatus.UNVERIFIED)
//                .build();
//        postRepository.save(post);
//    }
//
//    @Test
//    void testPublishLikeEvent() throws JsonProcessingException {
//        Long userId = 1L;
//        Long postId = 1L;
//        when(userContext.getUserId()).thenReturn(userId);
//        when(userServiceClient.getUser(userId)).thenReturn(new UserDto(userId, "username", "email"));
//        Like like = likeService.createPostLike(postId);
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//        verify(postLikeMessageListener).onMessage(messageCaptor.capture(), any());
//
//        RedisPostLikeEvent redisPostLikeEvent = objectMapper.readValue(messageCaptor.getValue().toString(), RedisPostLikeEvent.class);
//
//        assertEquals(like.getUserId(), redisPostLikeEvent.getLikeAuthorId());
//        assertEquals(like.getPost().getId(), redisPostLikeEvent.getPostId());
//        assertEquals(like.getPost().getAuthorId(), redisPostLikeEvent.getPostAuthorId());
//    }
//}