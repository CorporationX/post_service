package faang.school.postservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class LikeControllerTest {

    private MockMvc mockMvc;

    private final LikeController controller = new LikeController();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void sayHello() throws Exception {
        mockMvc.perform(get("/like/hello"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getLike() {
        controller.getLike();
    }
}