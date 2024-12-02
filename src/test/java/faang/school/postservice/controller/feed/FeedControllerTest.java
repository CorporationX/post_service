package faang.school.postservice.controller.feed;


import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.dto.UserDto;
import faang.school.postservice.util.SharedTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;
import redis.clients.jedis.Jedis;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class FeedControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private UserServiceClient userServiceClient;

    @DynamicPropertySource
    static void overrideSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", SharedTestContainers.POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", SharedTestContainers.POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", SharedTestContainers.POSTGRES_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", SharedTestContainers.POSTGRES_CONTAINER::getDriverClassName);
        registry.add("spring.liquibase.enabled", () -> false);
        registry.add("spring.data.redis.host", SharedTestContainers.REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> SharedTestContainers.REDIS_CONTAINER.getMappedPort(6379));
        registry.add("feed-posts-per-request.size", () -> 2);
    }

    private final String redisHost = SharedTestContainers.REDIS_CONTAINER.getHost();
    private final Integer redisPort = SharedTestContainers.REDIS_CONTAINER.getMappedPort(6379);

    @BeforeEach
    void setUpTestRedisData() {
        try (Jedis jedis = new Jedis(redisHost, redisPort)) {
            jedis.set("feeds", "9");
            jedis.set("author", "2");
            jedis.set("author", "3");
            jedis.set("posts", "47");
            jedis.set("posts", "46");
            jedis.set("posts", "45");

            jedis.hset("author:2", "_class", "faang.school.postservice.redis.model.entity.AuthorCache");
            jedis.hset("author:2", "email", "janesmith@example.com");
            jedis.hset("author:2", "id", "2");
            jedis.hset("author:2", "username", "JaneSmith");

            jedis.hset("author:3", "_class", "faang.school.postservice.redis.model.entity.AuthorCache");
            jedis.hset("author:3", "email", "jonemith@example.com");
            jedis.hset("author:3", "id", "3");
            jedis.hset("author:3", "username", "JohnSmith");

            jedis.hset("feeds:9", "_class", "faang.school.postservice.redis.model.entity.FeedCache");
            jedis.hset("feeds:9", "id", "9");
            jedis.hset("feeds:9", "postIds.[0]", "47");
            jedis.hset("feeds:9", "postIds.[1]", "46");
            jedis.hset("feeds:9", "postIds.[2]", "45");

            jedis.hset("posts:45", "_class", "faang.school.postservice.redis.model.entity.PostCache");
            jedis.hset("posts:45", "content", "content of post 45");
            jedis.hset("posts:45", "id", "45");
            jedis.hset("posts:45", "numberOfLikes", "0");
            jedis.hset("posts:45", "numberOfViews", "0");
            jedis.hset("posts:45", "publishedAt", "2024-11-27T10:50:00.889178600");
            jedis.hset("posts:45", "authorId", "2");

            jedis.hset("posts:46", "_class", "faang.school.postservice.redis.model.entity.PostCache");
            jedis.hset("posts:46", "content", "content of post 46");
            jedis.hset("posts:46", "id", "46");
            jedis.hset("posts:46", "numberOfLikes", "0");
            jedis.hset("posts:46", "numberOfViews", "0");
            jedis.hset("posts:46", "publishedAt", "2024-11-27T10:50:01.889178600");
            jedis.hset("posts:46", "authorId", "2");

            jedis.hset("posts:47", "_class", "faang.school.postservice.redis.model.entity.PostCache");
            jedis.hset("posts:47", "content", "content of post 47");
            jedis.hset("posts:47", "id", "47");
            jedis.hset("posts:47", "numberOfLikes", "0");
            jedis.hset("posts:47", "numberOfViews", "0");
            jedis.hset("posts:47", "publishedAt", "2024-11-27T10:50:03.889178600");
            jedis.hset("posts:47", "authorId", "3");
        }
    }

    @Test
    public void getFeedFromCache_shouldReturnJsonWithTwoPosts47and46() throws Exception {
        Long feedId = 9L;
        mockMvc.perform(MockMvcRequestBuilders.get("/{feedId}", feedId)
                        .header("x-user-id", feedId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(feedId))
                .andExpect(jsonPath("$.postRedisDtos.size()").value(2))
                .andExpect(jsonPath("$.postRedisDtos[0].id").value("47"))
                .andExpect(jsonPath("$.postRedisDtos[1].id").value("46"))
                .andExpect(jsonPath("$.postRedisDtos[0].content").value("content of post 47"))
                .andExpect(jsonPath("$.postRedisDtos[1].content").value("content of post 46"))
                .andExpect(jsonPath("$.postRedisDtos[0].author.id").value("3"))
                .andExpect(jsonPath("$.postRedisDtos[1].author.id").value("2"))
                .andExpect(jsonPath("$.postRedisDtos[0].author.email").value("jonemith@example.com"))
                .andExpect(jsonPath("$.postRedisDtos[1].author.email").value("janesmith@example.com"));
    }

    @Test
    public void getFeedFromCache_shouldReturnJsonWithTwoPosts46and45_paginating() throws Exception {
        Long feedId = 9L;
        mockMvc.perform(MockMvcRequestBuilders.get("/{feedId}", feedId)
                        .header("x-user-id", feedId)
                        .param("startPostId","1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(feedId))
                .andExpect(jsonPath("$.postRedisDtos.size()").value(2))
                .andExpect(jsonPath("$.postRedisDtos[0].id").value("46"))
                .andExpect(jsonPath("$.postRedisDtos[1].id").value("45"))
                .andExpect(jsonPath("$.postRedisDtos[0].content").value("content of post 46"))
                .andExpect(jsonPath("$.postRedisDtos[1].content").value("content of post 45"))
                .andExpect(jsonPath("$.postRedisDtos[0].author.id").value("2"))
                .andExpect(jsonPath("$.postRedisDtos[1].author.id").value("2"))
                .andExpect(jsonPath("$.postRedisDtos[0].author.email").value("janesmith@example.com"))
                .andExpect(jsonPath("$.postRedisDtos[1].author.email").value("janesmith@example.com"));
    }

    @Test
    public void getFeedFromCacheAndPostgres_shouldReturnJsonWithTwoPosts47and46() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setEmail("janesmith@example.com");
        userDto.setUsername("JaneSmith");
        when(userServiceClient.getUsersByIds(any())).thenReturn(List.of(userDto));

        try (Jedis jedis = new Jedis(redisHost, redisPort)) {
            jedis.del("posts:46");
        }
        Long feedId = 9L;
        mockMvc.perform(MockMvcRequestBuilders.get("/{feedId}", feedId)
                        .header("x-user-id", feedId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(feedId))
                .andExpect(jsonPath("$.postRedisDtos.size()").value(2))
                .andExpect(jsonPath("$.postRedisDtos[0].id").value("47"))
                .andExpect(jsonPath("$.postRedisDtos[1].id").value("46"))
                .andExpect(jsonPath("$.postRedisDtos[0].content").value("content of post 47"))
                .andExpect(jsonPath("$.postRedisDtos[1].content").value("content of post 46"))
                .andExpect(jsonPath("$.postRedisDtos[0].author.id").value("3"))
                .andExpect(jsonPath("$.postRedisDtos[1].author.id").value("2"))
                .andExpect(jsonPath("$.postRedisDtos[0].author.email").value("jonemith@example.com"))
                .andExpect(jsonPath("$.postRedisDtos[1].author.email").value("janesmith@example.com"));
    }

    @Test
    public void getFeedOnlyFromPostgres_shouldReturnJsonWithTwoPosts47and46() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(2L);
        userDto.setEmail("janesmith@example.com");
        userDto.setUsername("JaneSmith");

        UserDto userDto2 = new UserDto();
        userDto2.setId(3L);
        userDto2.setEmail("jonemith@example.com");
        userDto2.setUsername("JohnSmith");

        when(userServiceClient.getUsersByIds(any())).thenReturn(List.of(userDto,userDto2));

        try (Jedis jedis = new Jedis(redisHost, redisPort)) {
            jedis.del("posts:47");
            jedis.del("posts:46");
            jedis.del("posts:45");
        }
        Long feedId = 9L;
        mockMvc.perform(MockMvcRequestBuilders.get("/{feedId}", feedId)
                        .header("x-user-id", feedId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(feedId))
                .andExpect(jsonPath("$.postRedisDtos.size()").value(2))
                .andExpect(jsonPath("$.postRedisDtos[0].id").value("47"))
                .andExpect(jsonPath("$.postRedisDtos[1].id").value("46"))
                .andExpect(jsonPath("$.postRedisDtos[0].content").value("content of post 47"))
                .andExpect(jsonPath("$.postRedisDtos[1].content").value("content of post 46"))
                .andExpect(jsonPath("$.postRedisDtos[0].author.id").value("3"))
                .andExpect(jsonPath("$.postRedisDtos[1].author.id").value("2"))
                .andExpect(jsonPath("$.postRedisDtos[0].author.email").value("jonemith@example.com"))
                .andExpect(jsonPath("$.postRedisDtos[1].author.email").value("janesmith@example.com"));
    }
}

