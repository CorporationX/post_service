package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.AuthorDto;
import faang.school.postservice.dto.feed.FeedPostResponseDto;
import faang.school.postservice.service.FeedService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FeedController.class)
@TestPropertySource(properties = "app.feed.controller.default-limit=7")
class FeedControllerWebMvcTest {

    @Autowired MockMvc mockMvc;

    @MockBean FeedService feedService;
    @MockBean UserContext userContext;

    @Test
    void getFeed_withoutAfter_callsServiceWithNullAfter_andDefaultLimit() throws Exception {
        long userId = 10L;
        when(userContext.getUserId()).thenReturn(userId);

        var dto = new FeedPostResponseDto(
                101L, "c101", true, false,
                LocalDateTime.now(), LocalDateTime.now(),
                null, new AuthorDto(501L, "u1")
        );

        when(feedService.getFeed(eq(userId), isNull(), eq(7)))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/feed"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(101));

        verify(userContext).getUserId();
        verify(feedService).getFeed(userId, null, 7);
    }

    @Test
    void getFeed_withAfter_passesAfterParam() throws Exception {
        long userId = 10L;
        long after = 555L;

        when(userContext.getUserId()).thenReturn(userId);
        when(feedService.getFeed(eq(userId), eq(after), eq(7))).thenReturn(List.of());

        mockMvc.perform(get("/feed").param("after", String.valueOf(after)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(feedService).getFeed(userId, after, 7);
    }
}
