package faang.school.postservice.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.controller.handler.GlobalExceptionHandler;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.dto.album.AlbumUsersDto;
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
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static faang.school.postservice.model.AlbumVisibility.PUBLIC;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Album Controller Tests")
class AlbumControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AlbumService albumService;
    @Mock
    private UserContext userContext;

    @InjectMocks
    private AlbumController albumController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    long userId = 1L;
    long albumId = 2L;
    long postId = 3L;
    AlbumDto albumDto = new AlbumDto();
    AlbumFilterDto albumFilterDto = new AlbumFilterDto();

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(albumController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    void setUpAlbumDto() {
        albumDto.setTitle("title");
        albumDto.setDescription("description");
        albumDto.setAuthorId(userId);
    }

    void prepareAlbumFilterDto() {
        albumFilterDto.setTitle("title");
        albumFilterDto.setCreatedAtBefore(LocalDateTime.now().minusDays(2));
    }

    @Test
    @DisplayName("Find album by ID should return album DTO")
    public void testFindAlbumById() throws Exception {
        when(albumService.getAlbumById(eq(1L))).thenReturn(
                new AlbumResponseDto(1L, "title", "description", 1L)
        );

        mockMvc.perform(get("/albums/{id}", 1L))
                .andDo(print())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Find non-existent album by ID should return 404")
    void testFindNonExistentAlbumById() throws Exception {
        when(albumService.getAlbumById(999L)).thenThrow(new EntityNotFoundException("Album not found"));

        mockMvc.perform(get("/albums/{id}", 999L))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Album not found"));
    }

    @Test
    @DisplayName("Find albums by author ID should return album list")
    public void testFindAlbumsByAuthorId() throws Exception {
        when(albumService.getAlbumsByAuthorId(eq(1L))).thenReturn(
                List.of(new AlbumResponseDto(1L, "title", "description", 1L))
        );

        mockMvc.perform(get("/albums/author/{authorId}", 1L))
                .andDo(print())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Update album visibility should return updated album")
    public void testUpdateAlbumVisibility() throws Exception {
        AlbumResponseDto expectedResponse = new AlbumResponseDto(1L, "Test Title", "Test Description", 100L);

        when(albumService.updateAlbumVisibility(eq(1L), eq(PUBLIC))).thenReturn(expectedResponse);

        mockMvc.perform(put("/albums/{id}/visibility/{visibility}", 1L, PUBLIC.name()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.authorId").value(100L));
    }

    @Test
    @DisplayName("Update visibility for non-existent album should return 404")
    void testUpdateVisibilityForNonExistentAlbum() throws Exception {
        doThrow(new EntityNotFoundException("Album not found"))
                .when(albumService).updateAlbumVisibility(eq(999L), eq(PUBLIC));

        mockMvc.perform(put("/albums/{id}/visibility/{visibility}", 999L, "PUBLIC"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Album not found"));
    }

    @Test
    @DisplayName("Add users for access to album should return updated user list")
    public void testAddUsersForAccessAlbum() throws Exception {
        AlbumUsersDto dto = new AlbumUsersDto(Arrays.asList(1L, 2L));
        List<Long> expectedResponse = dto.usersIds();

        when(albumService.addUsersForAccessAlbum(eq(1L),
                argThat(arg -> arg.usersIds().equals(dto.usersIds()))))
                .thenReturn(expectedResponse);

        mockMvc.perform(put("/albums/{id}/add-users-for-access", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Add users to non-existent album should return 404")
    void testAddUsersToNonExistentAlbum() throws Exception {
        AlbumUsersDto dto = new AlbumUsersDto(List.of(1L, 2L));
        doThrow(new EntityNotFoundException("Album not found"))
                .when(albumService).addUsersForAccessAlbum(eq(999L), any());

        mockMvc.perform(put("/albums/{id}/add-users-for-access", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Invalid ID format should return 400")
    void testInvalidIdFormat() throws Exception {
        mockMvc.perform(get("/albums/{id}", "invalid_id"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Invalid visibility value should return 400")
    void testInvalidVisibilityValue() throws Exception {
        mockMvc.perform(put("/albums/{id}/visibility/{visibility}", 1L, "INVALID_VISIBILITY"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateAlbumReturnAlbum() throws Exception {
        setUpAlbumDto();
        when(albumService.createAlbum(userId, albumDto)).thenReturn(albumDto);

        mockMvc.perform(post("/albums/new")
                        .header("x-user-id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(albumDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(albumDto.getTitle()));
    }

    @Test
    void testAddPostToAlbum() throws Exception {
        setUpAlbumDto();
        when(albumService.addPostToAlbum(userId, postId, albumId)).thenReturn(albumDto);

        mockMvc.perform(post("/albums/{albumId}/add-post/{postId}", albumId, postId)
                        .header("x-user-id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(albumDto.getTitle()));
    }

    @Test
    void testDeletePostFromAlbumReturnOk() throws Exception {
        doNothing().when(albumService).deletePostFromAlbum(userId, postId, albumId);

        mockMvc.perform(delete("/albums/{albumId}/delete-post/{postId}", albumId, postId)
                        .header("x-user-id", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetAlbumByIdReturnAlbum() throws Exception {
        setUpAlbumDto();
        when(albumService.getAlbumByIdReturnAlbumDto(albumId)).thenReturn(albumDto);

        mockMvc.perform(get("/albums/get/{albumId}", albumId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(albumDto.getTitle()));
    }

    @Test
    void testUpdateAlbumOk() throws Exception {
        setUpAlbumDto();
        when(albumService.updateAlbum(eq(userId), any(AlbumDto.class))).thenReturn(albumDto);

        mockMvc.perform(put("/albums/update/{albumId}", albumId)
                        .header("x-user-id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(albumDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(albumDto.getTitle()));
    }

    @Test
    void testDeleteAlbumOk() throws Exception {
        doNothing().when(albumService).deleteAlbum(userId, albumId);

        mockMvc.perform(delete("/albums/delete/{albumId}", albumId)
                        .header("x-user-id", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetAllAlbumsByAuthorIdWithFilters() throws Exception {
        setUpAlbumDto();
        prepareAlbumFilterDto();
        List<AlbumDto> expectedAlbums = List.of(albumDto);
        when(albumService.getAllAlbumsByAuthorIdWithFilters(eq(userId), any(AlbumFilterDto.class)))
                .thenReturn(expectedAlbums);

        mockMvc.perform(post("/albums/by-author/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(albumFilterDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(albumDto.getTitle()));
    }

    @Test
    void testGetAllAlbumsWithFilters() throws Exception {
        setUpAlbumDto();
        prepareAlbumFilterDto();
        List<AlbumDto> expectedAlbums = List.of(albumDto);
        when(albumService.getAllAlbumsWithFilters(any(AlbumFilterDto.class)))
                .thenReturn(expectedAlbums);

        mockMvc.perform(post("/albums/all-albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(albumFilterDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(albumDto.getTitle()));
    }

    @Test
    void testAddFavouriteAlbum() throws Exception {
        doNothing().when(albumService).addFavouriteAlbum(userId, albumId);

        mockMvc.perform(post("/albums/favourite/add/{albumId}", albumId)
                        .header("x-user-id", userId))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteFavouriteAlbum() throws Exception {
        doNothing().when(albumService).deleteFavouriteAlbum(userId, albumId);

        mockMvc.perform(delete("/albums/favourite/delete/{albumId}", albumId)
                        .header("x-user-id", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetFavouriteAlbumsByUserId() throws Exception {
        setUpAlbumDto();
        prepareAlbumFilterDto();
        List<AlbumDto> expectedAlbums = List.of(albumDto);
        when(albumService.getFavouriteAlbumsByUserId(eq(userId), any(AlbumFilterDto.class)))
                .thenReturn(expectedAlbums);

        mockMvc.perform(post("/albums/favourite")
                        .header("x-user-id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(albumFilterDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(albumDto.getTitle()));
    }

}
