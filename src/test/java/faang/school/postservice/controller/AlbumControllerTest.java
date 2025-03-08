package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDTO;
import faang.school.postservice.service.AlbumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AlbumControllerTest {
    private MockMvc mockMvc;

    @Mock
    private AlbumService albumService;

    @InjectMocks
    private AlbumController albumController;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(albumController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createAlbum_ShouldReturnAlbum() throws Exception {
        AlbumDTO albumDTO = new AlbumDTO();
        albumDTO.setTitle("title");
        albumDTO.setDescription("desc");
        albumDTO.setAuthorId(1L);
        when(albumService.save(Mockito.any(AlbumDTO.class))).thenReturn(albumDTO);

        mockMvc.perform(post("/api/v1/album")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(albumDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void getById_ShouldReturnAlbum() throws Exception {
        AlbumDTO albumDTO = new AlbumDTO();
        albumDTO.setTitle("title");
        albumDTO.setDescription("desc");
        albumDTO.setAuthorId(1L);

        when(albumService.getById(1L)).thenReturn(albumDTO);

        mockMvc.perform(get("/api/v1/album/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("title"));
    }

    @Test
    void getAll_ShouldReturnListOfAlbums() throws Exception {
        AlbumDTO albumDTO = new AlbumDTO();
        albumDTO.setTitle("title");
        albumDTO.setDescription("desc");
        albumDTO.setAuthorId(1L);
        List<AlbumDTO> albums = Collections.singletonList(albumDTO);

        when(albumService.getAll(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(albums);

        mockMvc.perform(get("/api/v1/album"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("title"));
    }

    @Test
    void update_ShouldReturnUpdatedAlbum() throws Exception {
        AlbumDTO albumDTO = new AlbumDTO();
        albumDTO.setTitle("updated title");
        albumDTO.setDescription("updated desc");
        albumDTO.setAuthorId(1L);

        when(albumService.update(Mockito.any(AlbumDTO.class), Mockito.anyLong())).thenReturn(albumDTO);

        mockMvc.perform(put("/api/v1/album/1")
                        .header("x-user-id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(albumDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("updated title"));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(albumService).delete(Mockito.anyLong());

        mockMvc.perform(delete("/api/v1/album/1").header("x-user-id", 1L))
                .andExpect(status().isOk());
    }
}