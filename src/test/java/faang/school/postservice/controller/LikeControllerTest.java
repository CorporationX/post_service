package faang.school.postservice.controller;

import faang.school.postservice.controller.like.LikeController;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.like.LikeService;
import faang.school.postservice.util.TestDataFactory;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class LikeControllerTest {

    private MockMvc mockMvc;
    @InjectMocks
    private LikeController likeController;
    @Mock
    private LikeService likeService;

    private static final Long ID = 1L;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(likeController).build();
    }

    @Test
    void givenPostIdWhenGetUsersByPostIdThenReturnUsers() throws Exception {
        // given - precondition
        List<UserDto> userDtoList = TestDataFactory.getUserDtoList();

        when(likeService.findUsersByPostId(ID))
                .thenReturn(userDtoList);
        // when - action
        var response = mockMvc.perform(get("/api/v1/likes/post/{postId}", ID));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$", Matchers.hasSize(userDtoList.size())))
                .andExpect(jsonPath("$[0].id").value(userDtoList.get(0).getId()))
                .andExpect(jsonPath("$[1].id").value(userDtoList.get(1).getId()))
                .andExpect(jsonPath("$[2].id").value(userDtoList.get(2).getId()));
    }

    @Test
    void givenCommentIdWhenGetUsersByCommentIdThenReturnUsers() throws Exception {
        // given - precondition
        List<UserDto> userDtoList = TestDataFactory.getUserDtoList();

        when(likeService.findUsersByCommentId(ID))
                .thenReturn(userDtoList);
        // when - action
        var response = mockMvc.perform(get("/api/v1/likes/comment/{commentId}", ID));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$", Matchers.hasSize(userDtoList.size())))
                .andExpect(jsonPath("$[0].id").value(userDtoList.get(0).getId()))
                .andExpect(jsonPath("$[1].id").value(userDtoList.get(1).getId()))
                .andExpect(jsonPath("$[2].id").value(userDtoList.get(2).getId()));
    }
}