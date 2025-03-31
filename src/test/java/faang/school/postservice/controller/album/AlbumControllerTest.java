package faang.school.postservice.controller.album;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.service.album.AlbumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = AlbumController.class)
public class AlbumControllerTest {

    @MockBean
    private AlbumService albumService;
    @MockBean
    private UserContext userContext;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    long userId = 1L;
    long albumId = 2L;
    long postId = 3L;
    AlbumDto albumDto = new AlbumDto();
    AlbumFilterDto albumFilterDto = new AlbumFilterDto();

    @BeforeEach
    void setUp() {
        albumDto.setTitle("title");
        albumDto.setDescription("description");
        albumDto.setAuthorId(userId);
    }

    void prepareAlbumFilterDto() {
        albumFilterDto.setTitle("title");
        albumFilterDto.setCreatedAt(LocalDateTime.now().minusDays(1));
        albumFilterDto.setCreatedBefore(true);
    }

    @Test
    void testCreateAlbumReturnAlbum() throws Exception {
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
        when(albumService.addPostToAlbum(userId, postId, albumId)).thenReturn(albumDto);

        mockMvc.perform(post("/albums/{albumId}/{postId}", albumId, postId)
                        .header("x-user-id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(albumDto.getTitle()));
    }

    @Test
    void testDeletePostFromAlbumReturnOk() throws Exception {
        doNothing().when(albumService).deletePostFromAlbum(userId, postId, albumId);

        mockMvc.perform(delete("/albums/{albumId}/delete-post/{postId}", albumId, postId)
                        .header("x-user-id", userId))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAlbumByIdReturnAlbum() throws Exception {
        when(albumService.getAlbumById(albumId)).thenReturn(albumDto);

        mockMvc.perform(get("/albums/{albumId}", albumId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(albumDto.getTitle()));
    }

    @Test
    void testUpdateAlbumOk() throws Exception {
        when(albumService.updateAlbum(eq(userId), any(AlbumDto.class))).thenReturn(albumDto);

        mockMvc.perform(put("/albums/{albumId}", albumId)
                        .header("x-user-id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(albumDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(albumDto.getTitle()));
    }

    @Test
    void testDeleteAlbumOk() throws Exception {
        doNothing().when(albumService).deleteAlbum(userId, albumId);

        mockMvc.perform(delete("/albums/{albumId}", albumId)
                        .header("x-user-id", userId))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllAlbumsByAuthorIdWithFilters() throws Exception {
        prepareAlbumFilterDto();
        List<AlbumDto> expectedAlbums = List.of(albumDto);
        when(albumService.getAllAlbumsByAuthorIdWithFilters(eq(userId), any(AlbumFilterDto.class)))
                .thenReturn(expectedAlbums);

        mockMvc.perform(post("/albums/albums-by-author/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(albumFilterDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(albumDto.getTitle()));
    }

    @Test
    void testGetAllAlbumsWithFilters() throws Exception {
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

        mockMvc.perform(post("/albums/add-favourite/{albumId}", albumId)
                        .header("x-user-id", userId))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteFavouriteAlbum() throws Exception {
        doNothing().when(albumService).deleteFavouriteAlbum(userId, albumId);

        mockMvc.perform(delete("/albums/delete-favourite/{albumId}", albumId)
                        .header("x-user-id", userId))
                .andExpect(status().isOk());
    }

    @Test
    void testGetFavouriteAlbumsByUserId() throws Exception {
        prepareAlbumFilterDto();
        List<AlbumDto> expectedAlbums = List.of(albumDto);
        when(albumService.getFavouriteAlbumsByUserId(eq(userId), any(AlbumFilterDto.class)))
                .thenReturn(expectedAlbums);

        mockMvc.perform(post("/albums/favourite-albums-by-user")
                        .header("x-user-id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(albumFilterDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(albumDto.getTitle()));
    }

}
