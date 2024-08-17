package faang.school.postservice.controller;

import faang.school.postservice.dto.AlbumDto;
import faang.school.postservice.dto.AlbumFilterDto;
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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    private long authorId;
    private long postId;
    private long albumId;
    private long userId;
    private AlbumDto albumDto;
    private AlbumFilterDto albumFilterDto;
    private String albumDtoJson;
    private String albumFilterDtoJson;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        mockMvc = MockMvcBuilders.standaloneSetup(albumController).build();
        objectMapper = new ObjectMapper();

        authorId = 1L;
        postId = 2L;
        albumId = 3L;
        userId = 4L;
        albumDto = AlbumDto.builder()
                .id(albumId)
                .authorId(authorId)
                .title("albumTitle")
                .description("albumDescription")
                .build();
        albumFilterDto = new AlbumFilterDto();

        albumDtoJson = objectMapper.writeValueAsString(albumDto);
        albumFilterDtoJson = objectMapper.writeValueAsString(albumFilterDto);
    }

    @Test
    @DisplayName("testing createAlbum method")
    public void testCreateAlbum() throws Exception {
        mockMvc.perform(post("/album/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(albumDtoJson))
                .andExpect(status().isCreated());
        verify(albumService, times(1)).createAlbum(albumDto);
    }

    @Test
    @DisplayName("testing addPostToAlbum method")
    public void testAddPostToAlbum() throws Exception {
        mockMvc.perform(put("/album/add/post")
                        .param("authorId", String.valueOf(authorId))
                        .param("postId", String.valueOf(postId))
                        .param("albumId", String.valueOf(albumId)))
                .andExpect(status().isAccepted());
        verify(albumService, times(1)).addPostToAlbum(authorId, postId, albumId);
    }

    @Test
    @DisplayName("testing removePostFromAlbum method")
    public void testRemovePostFromAlbum() throws Exception {
        mockMvc.perform(put("/album/remove/post")
                        .param("authorId", String.valueOf(authorId))
                        .param("postId", String.valueOf(postId))
                        .param("albumId", String.valueOf(albumId)))
                .andExpect(status().isOk());
        verify(albumService, times(1)).removePostFromAlbum(authorId, postId, albumId);
    }

    @Test
    @DisplayName("testing addAlbumToFavorites method")
    public void testAddAlbumToFavorites() throws Exception {
        mockMvc.perform(put("/album/add/favourites/{albumId}", albumId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isAccepted());
        verify(albumService, times(1)).addAlbumToFavourites(albumId, userId);
    }

    @Test
    @DisplayName("testing removeAlbumFromFavorites method")
    public void testRemoveAlbumFromFavorites() throws Exception {
        mockMvc.perform(put("/album/remove/favourites/{albumId}", albumId)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk());
        verify(albumService, times(1)).removeAlbumFromFavourites(albumId, userId);
    }

    @Test
    @DisplayName("testing getAlbumById method")
    public void testGetAlbumById() throws Exception {
        when(albumService.getAlbumById(albumId)).thenReturn(albumDto);
        mockMvc.perform(get("/album/get/{albumId}", albumId))
                .andExpect(status().isOk());
        verify(albumService, times(1)).getAlbumById(albumId);
    }

    @Test
    @DisplayName("testing getAuthorFilteredAlbums method")
    public void testGetAuthorFilteredAlbums() throws Exception {
        when(albumService.getAuthorFilteredAlbums(authorId, albumFilterDto)).thenReturn(List.of(albumDto));
        mockMvc.perform(post("/album/get/all/{authorId}", authorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(albumFilterDtoJson))
                .andExpect(status().isOk());
        verify(albumService, times(1)).getAuthorFilteredAlbums(authorId, albumFilterDto);
    }

    @Test
    @DisplayName("testing getAllFilteredAlbums method")
    public void testGetAllFilteredAlbums() throws Exception {
        when(albumService.getAllFilteredAlbums(albumFilterDto)).thenReturn(List.of(albumDto));
        mockMvc.perform(post("/album/get/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(albumFilterDtoJson))
                .andExpect(status().isOk());
        verify(albumService, times(1)).getAllFilteredAlbums(albumFilterDto);
    }

    @Test
    @DisplayName("testing getUserFavoriteAlbums method")
    public void testGetUserFavoriteAlbums() throws Exception {
        when(albumService.getUserFavoriteAlbums(userId, albumFilterDto)).thenReturn(List.of(albumDto));
        mockMvc.perform(post("/album/get/favorites/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(albumFilterDtoJson))
                .andExpect(status().isOk());
        verify(albumService, times(1)).getUserFavoriteAlbums(userId, albumFilterDto);
    }

    @Test
    @DisplayName("testing updateAlbum method")
    public void testUpdateAlbum() throws Exception {
        when(albumService.updateAlbum(albumId, albumDto)).thenReturn(albumDto);
        mockMvc.perform(put("/album/update/{albumId}", albumId)
                        .param("authorId", String.valueOf(authorId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(albumDtoJson))
                .andExpect(status().isAccepted());
        verify(albumService, times(1)).updateAlbum(albumId, albumDto);
    }

    @Test
    @DisplayName("testing deleteAlbum method")
    public void testDeleteAlbum() throws Exception {
        doNothing().when(albumService).deleteAlbum(albumId, authorId);
        mockMvc.perform(delete("/album/delete/{albumId}", albumId)
                        .param("authorId", String.valueOf(authorId)))
                .andExpect(status().isOk());
        verify(albumService, times(1)).deleteAlbum(albumId, authorId);
    }
}