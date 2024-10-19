package faang.school.postservice.controller;

import faang.school.postservice.cache.model.PostRedis;
import faang.school.postservice.cache.service.NewsFeedHeater;
import faang.school.postservice.cache.service.NewsFeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Set;
import java.util.TreeSet;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NewsFeedControllerTest {
    @InjectMocks
    private NewsFeedController newsFeedController;
    @Mock
    private NewsFeedService newsFeedService;
    @Mock
    private NewsFeedHeater newsFeedHeater;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(newsFeedController).build();
    }

    @Test
    void testGetPosts() throws Exception {
        Long userId = 1L;
        Long lastPostId = 150L;
        PostRedis firstPost = PostRedis.builder()
                .id(22L)
                .content("content 1")
                .likesCount(111L)
                .build();
        PostRedis secondPost = PostRedis.builder()
                .id(102L)
                .content("content 2")
                .likesCount(10L)
                .build();
        TreeSet<PostRedis> posts = new TreeSet<>(Set.of(firstPost, secondPost));

        when(newsFeedService.getNewsFeed(userId, lastPostId)).thenReturn(posts);

        mockMvc.perform(get("/feed")
                        .header("x-user-id", String.valueOf(userId))
                        .param("lastPostId", String.valueOf(lastPostId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(secondPost.getId()))
                .andExpect(jsonPath("$.[0].content").value(secondPost.getContent()))
                .andExpect(jsonPath("$.[0].likesCount").value(secondPost.getLikesCount()))
                .andExpect(jsonPath("$.[1].id").value(firstPost.getId()))
                .andExpect(jsonPath("$.[1].content").value(firstPost.getContent()))
                .andExpect(jsonPath("$.[1].likesCount").value(firstPost.getLikesCount()));

        verify(newsFeedService, times(1)).getNewsFeed(userId, lastPostId);
    }

    @Test
    void testHeat() throws Exception {
        mockMvc.perform(put("/feed/heat"))
                .andExpect(status().isOk());

        verify(newsFeedHeater, times(1)).heat();
    }
}