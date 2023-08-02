package faang.school.postservice.album;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.controller.album.AlbumController;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Visibility;
import faang.school.postservice.service.album.AlbumService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
                .id(1L)
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

    @Test
    public void testGetAlbum() throws Exception {
        when(service.getAlbum(Mockito.anyLong(), Mockito.anyLong())).thenReturn(albumDto);

        mockMvc.perform(get("/api/v1/albums/{albumId}", 1)
                        .header("x-user-id", albumDto.getAuthorId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visibility").value(albumDto.getVisibility().name()));
    }

    @Test
    public void testUpdateAlbum() throws Exception {
        when(service.update(Mockito.any(), Mockito.anyLong())).thenReturn(albumDto);
        String albumDtoJson = objectMapper.writeValueAsString(albumDto);

        mockMvc.perform(put("/api/v1/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", albumDto.getAuthorId())
                        .content(albumDtoJson))
                .andExpect(status().isOk())
        ;
    }

//    пока не понял, как написать тест на выкидывание ошибки
//    @Test
//    public void testUpdateAlbumWithoutId() throws Exception {
//        albumDto.setId(null);
//        String albumDtoJson = objectMapper.writeValueAsString(albumDto);
//        when(service.update(Mockito.any(), Mockito.anyLong())).thenReturn(albumDto);
//
//        assertThrows(ServletException.class,
//                () -> mockMvc.perform(put("/api/v1/albums")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("x-user-id", albumDto.getAuthorId())
//                        .content(albumDtoJson)));
//    }

    @Test
    public void testDeleteAlbum() throws Exception {
        long albumId = 1L;
        long userId = 2L;

        mockMvc.perform(delete("/api/v1/albums/{albumId}", albumId)
                        .header("x-user-id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(service).delete(albumId, userId);
    }
}
