package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.service.album.AlbumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AlbumControllerTest {

    @Mock
    private AlbumService albumService;

    @InjectMocks
    private AlbumController albumController;

    private MockMvc mockMvc;

    private AlbumDto albumDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(albumController).build();

        albumDto = new AlbumDto(
                1L,
                "Album test",
                "Description test",
                1L,
                1L
        );
    }

    @Test
    @DisplayName("Should create an album and return the created AlbumDto")
    void createAlbum_shouldReturnCreatedAlbum() throws Exception {
        when(albumService.createAlbum(any(AlbumDto.class))).thenReturn(albumDto);

        mockMvc.perform(post("/api/v1/album")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": 1,
                                    "title": "Album test",
                                    "description": "Description test",
                                    "userId": 1,
                                    "authorId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(albumDto.getId()))
                .andExpect(jsonPath("$.title").value(albumDto.getTitle()));

        verify(albumService, times(1)).createAlbum(any(AlbumDto.class));
    }

    @Test
    @DisplayName("Should update album and return updated AlbumDto")
    void updateAlbum_shouldReturnUpdatedAlbum() throws Exception {
        when(albumService.updateAlbum(any(AlbumDto.class))).thenReturn(albumDto);

        mockMvc.perform(put("/api/v1/album")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": 1,
                                    "title": "Album test",
                                    "description": "Description test",
                                    "userId": 1,
                                    "authorId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(albumDto.getId()))
                .andExpect(jsonPath("$.title").value(albumDto.getTitle()));

        verify(albumService, times(1)).updateAlbum(any(AlbumDto.class));
    }

    @Test
    @DisplayName("Should delete album without errors")
    void deleteAlbum_shouldCallService() throws Exception {
        doNothing().when(albumService).deleteAlbum(anyLong(), anyLong());

        mockMvc.perform(delete("/api/v1/album/users/1/albums/2"))
                .andExpect(status().isOk());

        verify(albumService, times(1)).deleteAlbum(2L, 1L);
    }
}