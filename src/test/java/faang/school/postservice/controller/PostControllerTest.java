package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TestController.class)
@Import(UserContext.class)
@AutoConfigureMockMvc
public class PostControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void testValidation() throws Exception {
        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void testNotFound() throws Exception {
        mockMvc.perform(get("/test/not_found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.url").value("/test/not_found"))
                .andExpect(jsonPath("$.message").value("Post will not be found"));
    }

    @Test
    void testIllegalArgument() throws Exception {
        mockMvc.perform(get("/test/illegal/argument"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.url").value("/test/illegal/argument"))
                .andExpect(jsonPath("$.message").value("Bad input value"));
    }

    @Test
    void testRuntimeException() throws Exception {
        mockMvc.perform(get("/test/runtime/exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.url").value("/test/runtime/exception"))
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }

    @Test
    void testNull() throws Exception {
        mockMvc.perform(get("/test/null"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.url").value("/test/null"))
                .andExpect(jsonPath("$.message")
                        .value("ID of the author of the post should not be null"));
    }
}
