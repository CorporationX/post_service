package faang.school.postservice.controller;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.adapter.PostRepositoryAdapter;
import faang.school.postservice.service.redis.RedisFeedService;
import faang.school.postservice.util.BaseContextTest;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static faang.school.postservice.constant.PostEventTestConstants.POST;
import static faang.school.postservice.constant.PostEventTestConstants.POST_AUTHOR_FOLLOWERS_IDS_LIST;
import static faang.school.postservice.constant.PostEventTestConstants.POST_AUTHOR_ID;

@EmbeddedKafka(
        topics = "${spring.kafka.topic.posts.name}",
        brokerProperties = {"listeners=PLAINTEXT://${spring.kafka.bootstrap-servers}", "port=9092"})
public class PostControllerIntegrationTest extends BaseContextTest {

    @Autowired
    private RedisFeedService redisFeedService;

    @Autowired
    private PostRepositoryAdapter postRepositoryAdapter;

    @Autowired
    private PostRepository postRepository;

    @MockBean
    private UserServiceClient userServiceClient;

    @BeforeEach
    public void init() {
        postRepository.deleteAll();
    }

    @Test
    void publishPost_shouldBeCompletedSuccessfully() throws Exception {
        Post post = postRepository.save(POST);
        long postId = post.getId();

        Mockito.when(userServiceClient.getFollowersIds(POST_AUTHOR_ID)).thenReturn(POST_AUTHOR_FOLLOWERS_IDS_LIST);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/posts/{id}", postId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        Awaitility.await().atMost(20, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    Assertions.assertTrue(postRepositoryAdapter.getById(postId).isPublished());

                    Long followerId = POST_AUTHOR_FOLLOWERS_IDS_LIST.get(0);
                    Set<Long> expectedFollowerPosts = Set.of(postId);

                    Set<Long> actualFollowerPosts = redisFeedService.getPostsIdsListInReverseOrder(followerId).stream()
                            .map(obj -> Long.valueOf(obj.toString()))
                            .collect(Collectors.toSet());

                    Assertions.assertEquals(expectedFollowerPosts, actualFollowerPosts);
                });
    }

}
