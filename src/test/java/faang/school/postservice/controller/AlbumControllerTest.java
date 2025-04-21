package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.service.AlbumService;
import jakarta.persistence.EntityNotFoundException;
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

import java.time.Month;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AlbumControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private AlbumService albumService;

    @InjectMocks
    private AlbumController albumController;

    private AlbumDto albumDto;
    private AlbumDto albumDto1;
    private AlbumUpdateDto albumUpdateDto;
    private Long userId;
    private Long albumId;
    private Long postId;
    private PostDto postDto;
    private AlbumFilterDto albumFilterDto;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(albumController).build();
        objectMapper = new ObjectMapper();
        albumDto = mockAlbumDto();
        albumDto1 = mockAlbumDto1();
        postDto = mockPostDto();
        albumFilterDto = mockAlbumFilterDto();
        albumUpdateDto = mockAlbumUpdateDto();
        userId = 1L;
        albumId = 2L;
        postId = 3L;
    }

    @Test
    @DisplayName("Create album success")
    void testCreateAlbumSuccess() throws Exception {
        when(albumService.createAlbum(albumDto)).thenReturn(albumDto);

        mockMvc.perform(post("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(albumDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(albumDto.getId()))
                .andExpect(jsonPath("$.authorId").value(albumDto.getAuthorId()));

        verify(albumService, times(1)).createAlbum(any(AlbumDto.class));
    }

    @Test
    @DisplayName("Create album fail")
    void testCreateAlbumFail() {
        when(albumService.createAlbum(albumDto)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> albumController.createAlbum(albumDto));
    }

    @Test
    @DisplayName("Add Post To Album success")
    void testAddPostToAlbumSuccess() throws Exception {
        when(albumService.addPostToAlbum(userId, albumId, postId)).thenReturn(albumDto);

        mockMvc.perform(post("/albums/{albumId}/users/{userId}/posts/{postId}", albumId, userId, postId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(albumDto.getId()));

        verify(albumService, times(1)).addPostToAlbum(userId, albumId, postId);
    }

    @Test
    @DisplayName("Remove Post From Album success")
    void testRemovePostFromAlbumSuccess() throws Exception {
        when(albumService.removePostFromAlbum(userId, albumId, postId)).thenReturn(albumDto);

        mockMvc.perform(delete("/albums/{albumId}/users/{userId}/posts/{postId}", albumId, userId, postId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(albumDto.getId()))
                .andExpect(jsonPath("$.title").value(albumDto.getTitle()))
                .andExpect(jsonPath("$.description").value(albumDto.getDescription()));

        verify(albumService, times(1)).removePostFromAlbum(userId, albumId, postId);
    }

    @Test
    @DisplayName("Adding album to Favorites successfully")
    void testAddAlbumToFavoritesSuccess() throws Exception {
        when(albumService.addAlbumToFavorites(userId, albumId)).thenReturn(albumDto);

        mockMvc.perform(post("/albums/{albumId}/users/{userId}/favorites", albumId, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(albumDto.getId()))
                .andExpect(jsonPath("$.title").value(albumDto.getTitle()));

        verify(albumService, times(1)).addAlbumToFavorites(userId, albumId);
    }

    @Test
    @DisplayName("Delete album from Favorites successfully")
    void testDeleteAlbumFromFavoritesSuccess() throws Exception {
        doNothing().when(albumService).deleteAlbumFromFavorites(userId, albumId);

        mockMvc.perform(delete("/albums/{albumId}/users/{userId}/favorites", albumId, userId))
                .andExpect(status().isNoContent());

        verify(albumService, times(1)).deleteAlbumFromFavorites(userId, albumId);
    }

    @Test
    @DisplayName("Get album by ID success")
    void testGetAlbumByIdSuccess() throws Exception {
        when(albumService.findByAlbumId(albumId)).thenReturn(albumDto);

        mockMvc.perform(get("/albums//{albumId}", albumId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(albumDto.getId()))
                .andExpect(jsonPath("$.title").value(albumDto.getTitle()))
                .andExpect(jsonPath("$.description").value(albumDto.getDescription()));

        verify(albumService, times(1)).findByAlbumId(albumId);
    }

    @Test
    @DisplayName("Get album by ID fail")
    void testGetAlbumByIdFail() {
        when(albumService.findByAlbumId(albumId))
                .thenThrow(new EntityNotFoundException("Album not found"));

        assertThrows(EntityNotFoundException.class, () -> albumController.getAlbumById(albumId));
    }

    @Test
    @DisplayName("Get user albums with filters success")
    void testGetUserAlbumsWithFiltersSuccess() throws Exception {
        List<AlbumDto> albums = List.of(albumDto, albumDto1);
        when(albumService.getAlbumsForUserByFilter(eq(userId), any(AlbumFilterDto.class))).thenReturn(albums);

        mockMvc.perform(get("/albums/users/{userId}", userId)
                        .param("titlePattern", albumFilterDto.getTitlePattern())
                        .param("descriptionPattern", albumFilterDto.getDescriptionPattern())
                        .param("month", albumFilterDto.getMonth() != null ? albumFilterDto.getMonth().toString() : null))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(albums.size()))
                .andExpect(jsonPath("$[0].id").value(albums.get(0).getId()));

        verify(albumService, times(1)).getAlbumsForUserByFilter(eq(userId), any(AlbumFilterDto.class));
    }

    @Test
    @DisplayName("Get user albums with filters when list is empty")
    void testGetUserAlbumsWithFiltersEmptyList() throws Exception {
        when(albumService.getAlbumsForUserByFilter(eq(userId),
                any(AlbumFilterDto.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/albums/users/{userId}", userId)
                        .param("titlePattern", albumFilterDto.getTitlePattern())
                        .param("descriptionPattern", albumFilterDto.getDescriptionPattern())
                        .param("month", albumFilterDto.getMonth() != null ? albumFilterDto.getMonth().toString() : null))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(albumService, times(1)).getAlbumsForUserByFilter(eq(userId), any(AlbumFilterDto.class));
    }

    @Test
    @DisplayName("Get all albums with filters success")
    void testGetAllAlbumsWithFiltersSuccess() throws Exception {
        List<AlbumDto> albums = List.of(albumDto, albumDto1);
        when(albumService.getAllAlbumsByFilter(any(AlbumFilterDto.class))).thenReturn(albums);

        mockMvc.perform(get("/albums")
                        .param("titlePattern", albumFilterDto.getTitlePattern())
                        .param("descriptionPattern", albumFilterDto.getDescriptionPattern())
                        .param("month", albumFilterDto.getMonth() != null ? albumFilterDto.getMonth().toString() : null))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(albums.size()))
                .andExpect(jsonPath("$[0].id").value(albums.get(0).getId()));

        verify(albumService, times(1)).getAllAlbumsByFilter(any(AlbumFilterDto.class));
    }

    @Test
    @DisplayName("Get all albums with filters when list is empty")
    void testGetAllAlbumsWithFiltersEmptyList() throws Exception {
        when(albumService.getAllAlbumsByFilter(any(AlbumFilterDto.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/albums")
                        .param("titlePattern", albumFilterDto.getTitlePattern())
                        .param("descriptionPattern", albumFilterDto.getDescriptionPattern())
                        .param("month", albumFilterDto.getMonth() != null ? albumFilterDto.getMonth().toString() : null)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(albumService, times(1)).getAllAlbumsByFilter(any(AlbumFilterDto.class));
    }

    @Test
    @DisplayName("Get favorite usersAlbums with filters success")
    void testGetUserFavoriteAlbumsWithFiltersSuccess() throws Exception {
        List<AlbumDto> albums = List.of(albumDto, albumDto1);
        when(albumService.getFavoriteAlbumsForUserByFilter(userId, albumFilterDto)).thenReturn(albums);

        mockMvc.perform(get("/albums/users/{userId}/favorites", userId)
                        .param("titlePattern", albumFilterDto.getTitlePattern())
                        .param("descriptionPattern", albumFilterDto.getDescriptionPattern())
                        .param("month", albumFilterDto.getMonth() != null ? albumFilterDto.getMonth().toString() : null)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(albums.size()))
                .andExpect(jsonPath("$[0].id").value(albums.get(0).getId()));

        verify(albumService, times(1)).getFavoriteAlbumsForUserByFilter(userId, albumFilterDto);
    }

    @Test
    @DisplayName("Get favorite usersAlbums with filters when list is empty ")
    void testGetUserFavoriteAlbumsWithFiltersEmptyList() throws Exception {
        when(albumService.getFavoriteAlbumsForUserByFilter(userId, albumFilterDto)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/albums/users/{userId}/favorites", userId)
                        .param("titlePattern", albumFilterDto.getTitlePattern())
                        .param("descriptionPattern", albumFilterDto.getDescriptionPattern())
                        .param("month", albumFilterDto.getMonth() != null ? albumFilterDto.getMonth().toString() : null)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(albumService, times(1)).getFavoriteAlbumsForUserByFilter(userId, albumFilterDto);
    }

    @Test
    @DisplayName("Update album successfully")
    void testUpdateAlbum() throws Exception {
        when(albumService.updateAlbum(albumUpdateDto)).thenReturn(albumDto);

        mockMvc.perform(put("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(albumUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(albumDto.getId()))
                .andExpect(jsonPath("$.title").value(albumDto.getTitle()));

        verify(albumService, times(1)).updateAlbum(albumUpdateDto);
    }

    @Test
    @DisplayName("Delete album successfully")
    void testDeleteAlbumSuccess() throws Exception {
        doNothing().when(albumService).deleteAlbum(userId, albumId);

        mockMvc.perform(delete("/albums/{albumId}/users/{userId}", albumId, userId))
                .andExpect(status().isNoContent());

        verify(albumService, times(1)).deleteAlbum(userId, albumId);
    }

    private AlbumDto mockAlbumDto() {
        return AlbumDto.builder()
                .id(1L)
                .title("Album1")
                .description("album about spring")
                .authorId(1)
                .postsId(List.of(1L, 2L, 3L))
                .build();
    }

    private AlbumDto mockAlbumDto1() {
        return AlbumDto.builder()
                .id(3L)
                .title("Album3")
                .description("album about summer")
                .authorId(2)
                .postsId(List.of(1L, 2L, 3L))
                .build();
    }

    private AlbumUpdateDto mockAlbumUpdateDto() {
        return AlbumUpdateDto.builder()
                .id(1L)
                .title("Album1")
                .description("album about spring")
                .postsId(List.of(1L, 2L, 3L))
                .build();
    }

    private PostDto mockPostDto() {
        postDto = new PostDto();
        postDto.setId(1L);
        return postDto;
    }

    private AlbumFilterDto mockAlbumFilterDto() {
        return AlbumFilterDto.builder()
                .titlePattern("Filter")
                .descriptionPattern("Java the best")
                .month(Month.MARCH)
                .build();
    }
}
