package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.service.AlbumService;
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
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AlbumControllerTest {
    @Mock
    private AlbumService albumService;

    @InjectMocks
    private AlbumController albumController;

    private long postId;
    private long albumId;
    private long authorId;
    private AlbumDto albumDto;
    private AlbumFilterDto albumFilterDto;
    private String albumDtoJson;
    private String albumFilterDtoJson;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        mockMvc = MockMvcBuilders.standaloneSetup(albumController).build();
        ObjectMapper objectMapper = new ObjectMapper();

        postId = 1L;
        albumId = 3L;
        authorId = 4L;
        albumDto = AlbumDto.builder()
                .id(albumId)
                .title("albumTitle")
                .description("albumDescription")
                .visibility(AlbumVisibility.PUBLIC)
                .allowedUserIds(List.of(2L))
                .build();
        albumFilterDto = new AlbumFilterDto();

        albumDtoJson = objectMapper.writeValueAsString(albumDto);
        albumFilterDtoJson = objectMapper.writeValueAsString(albumFilterDto);
    }

    @Test
    @DisplayName("testing createAlbum method")
    public void testCreateAlbum() throws Exception {
        mockMvc.perform(post("/album")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(albumDtoJson))
                .andExpect(status().isCreated());
        verify(albumService, times(1)).createAlbum(albumDto);
    }

    @Test
    @DisplayName("testing addPostToAlbum method")
    public void testAddPostToAlbum() throws Exception {
        mockMvc.perform(put("/album/{albumId}/post/{postId}", albumId, postId))
                .andExpect(status().isAccepted());
        verify(albumService, times(1)).addPostToAlbum(postId, albumId);
    }

    @Test
    @DisplayName("testing removePostFromAlbum method")
    public void testRemovePostFromAlbum() throws Exception {
        mockMvc.perform(delete("/album/{albumId}/post/{postId}", albumId, postId))
                .andExpect(status().isOk());
        verify(albumService, times(1)).removePostFromAlbum(postId, albumId);
    }

    @Test
    @DisplayName("testing addAlbumToFavorites method")
    public void testAddAlbumToFavorites() throws Exception {
        mockMvc.perform(post("/album/favourites/{albumId}", albumId))
                .andExpect(status().isAccepted());
        verify(albumService, times(1)).addAlbumToFavourites(albumId);
    }

    @Test
    @DisplayName("testing removeAlbumFromFavorites method")
    public void testRemoveAlbumFromFavorites() throws Exception {
        mockMvc.perform(delete("/album/favourites/{albumId}", albumId))
                .andExpect(status().isOk());
        verify(albumService, times(1)).removeAlbumFromFavourites(albumId);
    }

    @Test
    @DisplayName("testing getAlbumById method")
    public void testGetAlbumById() throws Exception {
        mockMvc.perform(get("/album/{albumId}", albumId))
                .andExpect(status().isOk());
        verify(albumService, times(1)).getAlbumById(albumId);
    }

    @Test
    @DisplayName("testing getAuthorFilteredAlbums method")
    public void testGetAuthorFilteredAlbums() throws Exception {
        mockMvc.perform(post("/album/author/{authorId}", authorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(albumFilterDtoJson))
                .andExpect(status().isOk());
        verify(albumService, times(1)).getAuthorFilteredAlbums(authorId, albumFilterDto);
    }

    @Test
    @DisplayName("testing getAllFilteredAlbums method")
    public void testGetAllFilteredAlbums() throws Exception {
        mockMvc.perform(post("/album/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(albumFilterDtoJson))
                .andExpect(status().isOk());
        verify(albumService, times(1)).getAllFilteredAlbums(albumFilterDto);
    }

    @Test
    @DisplayName("testing getUserFavoriteAlbums method")
    public void testGetUserFavoriteAlbums() throws Exception {
        mockMvc.perform(post("/album/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(albumFilterDtoJson))
                .andExpect(status().isOk());
        verify(albumService, times(1)).getUserFavoriteAlbums(albumFilterDto);
    }

    @Test
    @DisplayName("testing updateAlbum method")
    public void testUpdateAlbum() throws Exception {
        mockMvc.perform(put("/album/{albumId}", albumId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(albumDtoJson))
                .andExpect(status().isAccepted());
        verify(albumService, times(1)).updateAlbum(albumId, albumDto);
    }

    @Test
    @DisplayName("testing deleteAlbum method")
    public void testDeleteAlbum() throws Exception {
        mockMvc.perform(delete("/album/{albumId}", albumId)
                        .param("authorId", String.valueOf(authorId)))
                .andExpect(status().isOk());
        verify(albumService, times(1)).deleteAlbum(albumId);
    }
}