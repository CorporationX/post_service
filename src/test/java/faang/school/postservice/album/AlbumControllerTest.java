package faang.school.postservice.album;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.controller.album.AlbumController;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Visibility;
import faang.school.postservice.service.album.AlbumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AlbumControllerTest {
    @Mock
    AlbumService service;
    @Spy
    ObjectMapper objectMapper;
    @InjectMocks
    AlbumController controller;

    MockMvc mockMvc;
    AlbumDto albumDto;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        albumDto = AlbumDto.builder()
                .title("Title4")
                .description("Album Descri2ption 2")
                .authorId(2L)
                .postIds(null)
                .visibility(Visibility.EVERYONE)
                .allowedUsersIds(Arrays.asList(1L, 4L, 5L))
                .build();
    }

    @Test
    public void testCreateAlbum() throws Exception {
        when(service.createAlbum(Mockito.any())).thenReturn(albumDto);
        String albumDtoJson = objectMapper.writeValueAsString(albumDto);

        mockMvc.perform(post("/api/v1/albums")
                        .content(albumDtoJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
