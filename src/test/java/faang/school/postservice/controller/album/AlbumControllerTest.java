package faang.school.postservice.controller.album;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.service.album.AlbumService;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@ContextConfiguration(classes = {AlbumController.class})
class AlbumControllerTest {

  private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @MockBean
  private AlbumService albumService;

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("Should return added album")
  void testAddAlbum() throws Exception {
    AlbumCreateDto albumDto = getAddAlbumDto();
    AlbumDto expectedResponse = getAlbumDto(1L, "New Title");

    when(albumService.add(albumDto, 1L)).thenReturn(expectedResponse);

    mockMvc.perform(post("/albums/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(OBJECT_MAPPER.writeValueAsString(albumDto))
            .header("x-user-id", 1L))
        .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(expectedResponse)))
        .andExpect(status().isOk());
    verify(albumService, times(1)).add(albumDto, 1L);
  }

  @Test
  @DisplayName("Should return album by id")
  void testGetAlbumById() throws Exception {
    AlbumDto expectedResponse = getAlbumDto(1L, "Title");
    when(albumService.getAlbumById(1L)).thenReturn(expectedResponse);
    mockMvc.perform(get("/albums/{id}", 1L))
        .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(expectedResponse)))
        .andExpect(status().isOk());
    verify(albumService, times(1)).getAlbumById(1L);
  }

  @Test
  @DisplayName("Should return updated album")
  void testUpdateAlbum() throws Exception {
    AlbumDto requestDto = getAlbumDto(1L, "Initial title");
    AlbumDto responseDto = getAlbumDto(1L, "Updated title");
    when(albumService.update(1L, requestDto)).thenReturn(responseDto);
    mockMvc.perform(put("/albums/update")
            .header("x-user-id", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(OBJECT_MAPPER.writeValueAsString(requestDto)))
        .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(responseDto)))
        .andExpect(status().isOk());
    verify(albumService, times(1)).update(1L, requestDto);
  }

  @Test
  @DisplayName("Should call removeAlbum with requested parameters")
  void testRemoveAlbum() throws Exception {
    AlbumDto requestDto = getAlbumDto(1L, "Album To Remove");
    mockMvc.perform(delete("/albums/delete")
            .header("x-user-id", 3L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(OBJECT_MAPPER.writeValueAsString(requestDto)))
        .andExpect(status().isOk());
    verify(albumService, times(1)).remove(3L, requestDto);
  }

  @Test
  @DisplayName("Should call addPostToAlbum with requested parameters")
  void testAddPostToAlbum() throws Exception {
    mockMvc.perform(post("/albums/{id}/posts/add/{postId}", 1L, 2L)
            .header("x-user-id", 3L))
        .andExpect(status().isOk());
    verify(albumService, times(1)).addPost(1L, 2L, 3L);
  }

  @Test
  @DisplayName("Should call removePostFromAlbum with requested parameters")
  void testRemovePostFromAlbum() throws Exception {
    mockMvc.perform(delete("/albums/{id}/posts/remove/{postId}", 1L, 2L)
            .header("x-user-id", 3L))
        .andExpect(status().isOk());
    verify(albumService, times(1)).removePost(1L, 2L, 3L);
  }

  @Test
  @DisplayName("Should call addAlbumToFavorites with requested parameters")
  void testAddAlbumToFavorites() throws Exception {
    mockMvc.perform(post("/albums/favorites/add/{id}", 1L)
            .header("x-user-id", 3L))
        .andExpect(status().isOk());
    verify(albumService, times(1)).addAlbumToFavorites(1L, 3L);
  }

  @Test
  @DisplayName("Should call removeAlbumFromFavorites with requested parameters")
  void testRemoveAlbumFromFavorites() throws Exception {
    mockMvc.perform(delete("/albums/favorites/remove/{id}", 1L)
            .header("x-user-id", 3L))
        .andExpect(status().isOk());
    verify(albumService, times(1)).removeAlbumFromFavorites(1L, 3L);
  }

  @Test
  @DisplayName("Should return user's filtered albums")
  void testGetUserAlbums() throws Exception {
    AlbumFilterDto albumFilterDto = AlbumFilterDto.builder()
        .titlePattern("Title")
        .build();
    List<AlbumDto> filteredAlbums = getAlbums();

    when(albumService.getUserAlbumsWithFilters(1L, albumFilterDto)).thenReturn(filteredAlbums);

    mockMvc.perform(get("/albums/filters/user")
            .header("x-user-id", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(OBJECT_MAPPER.writeValueAsString(albumFilterDto)))
        .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(filteredAlbums)))
        .andExpect(status().isOk());

    verify(albumService, times(1)).getUserAlbumsWithFilters(1L, albumFilterDto);
  }

  @Test
  @DisplayName("Should return user's favorite filtered albums")
  void testGetUserFavoritesAlbums() throws Exception {
    AlbumFilterDto albumFilterDto = getAlbumFilterDto();
    List<AlbumDto> filteredAlbums = getAlbums();

    when(albumService.getUserFavoriteAlbumsWithFilters(1L, albumFilterDto)).thenReturn(
        filteredAlbums);

    mockMvc.perform(get("/albums/filters/favorites")
            .header("x-user-id", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(OBJECT_MAPPER.writeValueAsString(albumFilterDto)))
        .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(filteredAlbums)))
        .andExpect(status().isOk());

    verify(albumService, times(1)).getUserFavoriteAlbumsWithFilters(1L, albumFilterDto);
  }

  @Test
  @DisplayName("Should return filtered albums")
  void testGetAllAlbums() throws Exception {
    AlbumFilterDto albumFilterDto = getAlbumFilterDto();
    List<AlbumDto> filteredAlbums = getAlbums();

    when(albumService.getAllAlbumsWithFilters(1L, albumFilterDto)).thenReturn(filteredAlbums);

    mockMvc.perform(get("/albums/filters/all")
            .header("x-user-id", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(OBJECT_MAPPER.writeValueAsString(albumFilterDto)))
        .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(filteredAlbums)))
        .andExpect(status().isOk());

    verify(albumService, times(1)).getAllAlbumsWithFilters(1L, albumFilterDto);
  }

  @Test
  @DisplayName("Return isBadRequest when header has no user id")
  void negativeTestAddAlbumWithNoUserId() throws Exception {
    AlbumCreateDto newAlbum = getAddAlbumDto();
    mockMvc.perform(post("/albums/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(OBJECT_MAPPER.writeValueAsString(newAlbum)))
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @MethodSource("invalidRequestDto")
  void negativeTest(AlbumDto albumDto) throws Exception {
    mockMvc.perform(post("/albums/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(OBJECT_MAPPER.writeValueAsString(albumDto))
            .header("x-user-id", 1L))
        .andExpect(status().isBadRequest());
  }

  static Stream<Object[]> invalidRequestDto() {
    return Stream.of(
        new Object[]{AlbumDto.builder()
            .authorId(1L)
            .build()},
        new Object[]{AlbumDto.builder()
            .title("title")
            .build()},
        new Object[]{AlbumDto.builder()
            .description("description")
            .build()},
        new Object[]{AlbumDto.builder()
            .build()}
    );
  }

  private AlbumDto getAlbumDto(Long id, String title) {
    return AlbumDto.builder()
        .id(id)
        .title(title)
        .description("Request Description")
        .authorId(1L)
        .build();
  }

  private AlbumCreateDto getAddAlbumDto() {
    return AlbumCreateDto.builder()
        .title("New Title")
        .description("New Description")
        .build();
  }

  private List<AlbumDto> getAlbums() {
    return List.of(
        getAlbumDto(1L, "Title 1"),
        getAlbumDto(2L, "Title 2"),
        getAlbumDto(3L, "Title 3"),
        getAlbumDto(4L, "Title 4")
    );
  }

  private AlbumFilterDto getAlbumFilterDto() {
    return AlbumFilterDto.builder()
        .titlePattern("Title")
        .build();
  }
}