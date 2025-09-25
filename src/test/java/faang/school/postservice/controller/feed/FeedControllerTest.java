package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.service.feed.FeedHeater;
import faang.school.postservice.service.feed.FeedService;
import faang.school.postservice.util.JsonMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {FeedController.class, JsonMapper.class})
class FeedControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JsonMapper jsonMapper;
    @MockBean
    private FeedService service;
    @MockBean
    private FeedHeater heater;

    @ParameterizedTest
    @CsvSource(value = {
            "null, null",
            "null, 10",
            "2, null"
    }, nullValues = "null")
    @DisplayName("200 ОК - POST /v1/feed")
    void positive_shouldCallFeed(Long lastPostId, Integer limit) throws Exception {
        List<FeedDto> feeds = List.of(createDto(1L), createDto(2L));
        String lastPostIdParam = lastPostId != null ? lastPostId.toString() : null;
        String limitParam = limit != null ? limit.toString() : null;
        limit = limit != null ? limit : 20;
        when(service.getFeed(lastPostId, limit)).thenReturn(feeds);

        mockMvc.perform(get("/v1/feed")
                        .param("lastPostId", lastPostIdParam)
                        .param("limit", limitParam))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonMapper.toJson(feeds), true));

        verify(service, times(1)).getFeed(lastPostId, limit);
    }

    @Test
    @DisplayName("200 ОК - POST /v1/feed/heat")
    void positive_shouldCallHeat() throws Exception {
        mockMvc.perform(put("/v1/feed/heat"))
                .andExpect(status().isOk());

        verify(heater, times(1)).start();
    }

    private FeedDto createDto(long postId) {
        return FeedDto.builder()
                .postId(postId)
                .build();
    }
}