package faang.school.postservice.controller.album;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumCreateUpdateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.album.interfaces.AlbumService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlbumController.class)
class AlbumControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlbumService albumService;

    @MockBean
    private UserContext userContext;

    @Autowired
    private ObjectMapper objectMapper;

    private static final long USER_ID = 1L;
    private static final String USER_ID_HEADER = "x-user-id";

    @Test
    void testCreateAlbum() throws Exception {
        long albumId = 1L;
        String title = "Title";
        String description = "Description";
        AlbumCreateUpdateDto createDto = AlbumCreateUpdateDto.builder()
                .title(title)
                .description(description)
                .build();
        AlbumDto responseDto = AlbumDto.builder()
                .id(albumId)
                .title(title)
                .description(description)
                .build();
        objectMapper = new ObjectMapper();
        ObjectWriter ow = objectMapper.writer();
        String request = ow.writeValueAsString(createDto);
        when(albumService.createAlbum(createDto)).thenReturn(responseDto);

        mockMvc.perform(post("/albums")
                        .header(USER_ID_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/albums/1")))
                .andExpect(jsonPath("$.id").value(albumId))
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andReturn();
        verify(albumService, times(1)).createAlbum(createDto);
    }

    @Test
    void testAddPostToAlbum() throws Exception {
        Post post = new Post();
        post.setId(2L);
        AlbumDto responseDto = AlbumDto.builder()
                .id(1L)
                .postIds(List.of(post.getId()))
                .build();
        when(albumService.addPostToAlbum(1L, 2L)).thenReturn(responseDto);

        mockMvc.perform(post("/albums/1/posts/2")
                        .header(USER_ID_HEADER, USER_ID))
                .andExpect(status().isOk())
                .andReturn();

        verify(albumService).addPostToAlbum(1L, 2L);
    }

    @Test
    void testDeletePostFromAlbum() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/albums/1/posts/2")
                        .header(USER_ID_HEADER, USER_ID))
                .andExpect(status().isNoContent());

        verify(albumService).deletePostFromAlbum(1L, 2L);
    }

    @Test
    void testAddAlbumToFavorites() throws Exception {
        mockMvc.perform(post("/albums/1/favorite")
                        .header(USER_ID_HEADER, USER_ID))
                .andExpect(status().isOk());

        verify(albumService).addAlbumToFavorites(1L);
    }

    @Test
    void testDeleteAlbumFromFavorites() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/albums/1/favorite")
                        .header(USER_ID_HEADER, USER_ID))
                .andExpect(status().isNoContent());

        verify(albumService).deleteAlbumFromFavorites(1L);
    }

    @Test
    void testGetAlbumById() throws Exception {
        long albumId = 1L;
        AlbumDto responseDto = AlbumDto.builder()
                .id(albumId)
                .build();
        when(albumService.getAlbumById(albumId)).thenReturn(responseDto);

        mockMvc.perform(get("/albums/1")
                        .header(USER_ID_HEADER, USER_ID))
                .andExpect(status().isOk())
                .andReturn();

        verify(albumService).getAlbumById(1L);
    }

    @Test
    void testGetAllAlbums() throws Exception {
        List<AlbumDto> albums = Collections.singletonList(new AlbumDto());
        when(albumService.getAllAlbums(any(AlbumFilterDto.class))).thenReturn(albums);

        AlbumFilterDto filterDto = new AlbumFilterDto();
        mockMvc.perform(post("/albums/filter")
                        .header(USER_ID_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andReturn();

        verify(albumService).getAllAlbums(any(AlbumFilterDto.class));
    }

    @Test
    void testGetUserAlbums() throws Exception {
        List<AlbumDto> albums = Collections.singletonList(new AlbumDto());
        when(albumService.getUserAlbums(eq(2L), any(AlbumFilterDto.class))).thenReturn(albums);

        AlbumFilterDto filterDto = new AlbumFilterDto();
        mockMvc.perform(post("/albums/users/2/filter")
                        .header(USER_ID_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andReturn();

        verify(albumService).getUserAlbums(eq(2L), any(AlbumFilterDto.class));
    }

    @Test
    void testGetUserFavoriteAlbums() throws Exception {
        List<AlbumDto> albums = Collections.singletonList(new AlbumDto());
        when(albumService.getUserFavoriteAlbums(eq(2L), any(AlbumFilterDto.class))).thenReturn(albums);

        AlbumFilterDto filterDto = new AlbumFilterDto();
        mockMvc.perform(post("/albums/users/2/favorite/filter")
                        .header(USER_ID_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andReturn();

        verify(albumService).getUserFavoriteAlbums(eq(2L), any(AlbumFilterDto.class));
    }

    @Test
    void testUpdateAlbum() throws Exception {
        AlbumCreateUpdateDto updateDto = AlbumCreateUpdateDto.builder()
                .title("Title")
                .description("Description")
                .build();
        AlbumDto responseDto = new AlbumDto();
        responseDto.setId(1L);
        responseDto.setTitle("New title");
        responseDto.setDescription("New description");

        when(albumService.updateAlbum(1L, updateDto)).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/albums/1")
                        .header(USER_ID_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andReturn();

        verify(albumService).updateAlbum(eq(1L), any(AlbumCreateUpdateDto.class));
    }

    @Test
    void testDeleteAlbum() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/albums/1")
                        .header(USER_ID_HEADER, USER_ID))
                .andExpect(status().isNoContent());

        verify(albumService).deleteAlbum(1L);
    }
}