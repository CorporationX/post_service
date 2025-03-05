package faang.school.postservice.service.album;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.album.DataValidationException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Visibility;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.album.filter.AlbumFilter;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

  @InjectMocks
  private AlbumServiceImpl albumService;
  @Mock
  private AlbumRepository albumRepository;
  @Mock
  private UserServiceClient userServiceClient;
  @Mock
  private PostRepository postRepository;
  @Spy
  private AlbumMapper albumMapper = Mappers.getMapper(AlbumMapper.class);
  private List<AlbumFilter> filters;

  private final static String ERROR_MESSAGE_NO_ACCESS = "Sorry, you have no access to this album";

  @BeforeEach
  public void init() {
    AlbumFilter filterMock = Mockito.mock(AlbumFilter.class);
    filters = List.of(filterMock);

    albumService = new AlbumServiceImpl(albumRepository, postRepository, userServiceClient,
        albumMapper, filters);
  }

  @Test
  @DisplayName("Should return filtered albums")
  void testGetAlbumsWithFilter() {
    AlbumFilterDto albumFilterDto = getTestAlbumFilterDto();
    when(filters.get(0).isApplicable(getTestAlbumFilterDto())).thenReturn(true);
    when(filters.get(0).apply(any(), any())).thenReturn(getTestAlbumStream());
    List<AlbumDto> result = albumService.getAlbumsWithFilter(any(), albumFilterDto);
    List<AlbumDto> expected = getTestAlbumStream().map(album -> albumMapper.toDto(album)).toList();
    assertEquals(result, expected);
  }

  @Test
  @DisplayName("Should return user albums")
  void testGetUserAlbums() {
    AlbumFilterDto albumFilterDto = getTestAlbumFilterDto();
    long userId = 1L;

    when(albumRepository.findByAuthorId(userId)).thenReturn(getTestAlbumStream());
    List<AlbumDto> result = albumService.getUserAlbumsWithFilters(userId, albumFilterDto);
    List<AlbumDto> expected = getTestAlbumStream().map(album -> albumMapper.toDto(album)).toList();
    verify(albumRepository, times(1)).findByAuthorId(userId);
    assertEquals(result, expected);
  }

  @Test
  @DisplayName("Should return user albums")
  void testGetUserFavoriteAlbums() {
    AlbumFilterDto albumFilterDto = getTestAlbumFilterDto();
    long userId = 1L;

    when(userServiceClient.getUser(userId)).thenReturn(new UserDto());
    when(albumRepository.findFavoriteAlbumsByUserId(userId)).thenReturn(getTestAlbumStream());
    List<AlbumDto> result = albumService.getUserFavoriteAlbumsWithFilters(userId, albumFilterDto);
    List<AlbumDto> expected = getTestAlbumStream().map(album -> albumMapper.toDto(album)).toList();

    verify(albumRepository, times(1)).findFavoriteAlbumsByUserId(userId);
    verify(userServiceClient, times(1)).getUser(userId);
    assertEquals(result, expected);
  }

  @Test
  @DisplayName("Should add post to the album")
  void testAddPostWithValidData() {
    Album album = getTestAlbum();
    Post post = getTestPost();
    long albumId = album.getId();
    long postId = post.getId();
    long userId = album.getAuthorId();

    when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
    when(postRepository.findById(postId)).thenReturn(Optional.of(post));
    when(albumRepository.save(album)).thenReturn(album);

    albumService.addPost(albumId, postId, userId);

    verify(albumRepository, times(1)).findById(albumId);
    verify(postRepository, times(1)).findById(postId);
    verify(albumRepository, times(1)).save(album);

    assertEquals(1, album.getPosts().size());
  }

  @Test
  @DisplayName("Should add album to favorites with valid data")
  void testAddAlbumToFavorites() {
    long albumId = 1;
    long userId = 1;
    when(userServiceClient.getUser(userId)).thenReturn(new UserDto());
    when(albumRepository.findById(albumId)).thenReturn(Optional.of(new Album()));
    albumService.addAlbumToFavorites(albumId, userId);

    verify(albumRepository, times(1)).addAlbumToFavorites(albumId, userId);
    verify(albumRepository, times(1)).findById(albumId);
  }

  @Test
  @DisplayName("Should throw EntityNotFoundException when user does not exist")
  void testAddAlbumToFavoritesNonExistingUser() {
    long userId = 1;
    var exception = assertThrows(EntityNotFoundException.class,
        () -> albumService.addAlbumToFavorites(1L, userId));
    assertEquals(String.format("User with ID %d not found", userId), exception.getMessage());
  }

  @Test
  @DisplayName("Should update album with valid data")
  void testDeleteAlbumWithValidData() {

  }

  @Test
  @DisplayName("Should throw EntityNotFoundException when album does not exist")
  void testDeleteNonExistingAlbum() {

  }

  @Test
  @DisplayName("Should throw DataValidationException when user is trying update not his album")
  void testDeleteAlbumByNotAuthorizedUser() {

  }


  @Test
  @DisplayName("Should update album with valid data")
  void testUpdateAlbumWithValidData() {

  }

  @Test
  @DisplayName("Should throw EntityNotFoundException when album does not exist")
  void testUpdateNonExistingAlbum() {

  }

  @Test
  @DisplayName("Should throw DataValidationException when author already has album with the title")
  void testUpdateAlbumWithTitleAlreadyExists() {

  }

  @Test
  @DisplayName("Should throw DataValidationException when user is trying update not his album")
  void testUpdateAlbumByNotAuthorizedUser() {

  }

  @Test
  @DisplayName("Should throw DataValidationException when user is not the owner of the album")
  void testNegativeAddPostByNotAuthorizedUser() {
    String errorMessage = "Only owner can add or delete post from this album";
    Album album = getTestAlbum();
    long albumId = album.getId();
    long postId = 1L;
    long userId = 11L;
    when(albumRepository.findById(albumId)).thenReturn(Optional.of(getTestAlbum()));
    when(postRepository.findById(postId)).thenReturn(Optional.of(getTestPost()));
    var exception = assertThrows(DataValidationException.class,
        () -> albumService.addPost(albumId, postId, userId));
    verify(albumRepository, times(1)).findById(albumId);
    verify(postRepository, times(1)).findById(postId);
    assertEquals(errorMessage, exception.getMessage());
  }

  @Test
  @DisplayName("Should throw EntityNotFoundException when post does not exist")
  void testNegativeAddNonExistingPostToExistingAlbum() {
    long albumId = 1L;
    long postId = 1L;
    long userId = 1L;
    when(albumRepository.findById(albumId)).thenReturn(Optional.of(getTestAlbum()));
    when(postRepository.findById(postId)).thenThrow(
        new EntityNotFoundException("Post does not exist"));
    var exception = assertThrows(EntityNotFoundException.class,
        () -> albumService.addPost(albumId, postId, userId));
    verify(albumRepository, times(1)).findById(albumId);
    verify(postRepository, times(1)).findById(postId);
    assertEquals("Post does not exist", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw EntityNotFoundException when album does not exist")
  void testNegativeAddPostToNonExistingAlbum() {
    long albumId = 1L;
    long postId = 1L;
    long userId = 1L;
    when(albumRepository.findById(albumId)).thenThrow(
        new EntityNotFoundException("Album does not exist"));
    var exception = assertThrows(EntityNotFoundException.class,
        () -> albumService.addPost(albumId, postId, userId));
    verify(albumRepository, times(1)).findById(albumId);
    assertEquals("Album does not exist", exception.getMessage());
  }

  @Test
  @DisplayName("Should add new album when valid data provided")
  void testAddAlbum() {
    AlbumCreateDto testCreateAlbumDto = getCreateAlbumDto();
    String title = testCreateAlbumDto.getTitle();
    Album album = getTestAlbum();
    long userId = 1L;

    when(albumRepository.existsByTitleAndAuthorId(title, userId)).thenReturn(false);
    when(userServiceClient.getUser(userId)).thenReturn(new UserDto());
    when(albumRepository.save(album)).thenReturn(album);
    when(albumMapper.toEntityFromCreateDto(testCreateAlbumDto)).thenReturn(album);

    AlbumDto result = albumService.add(testCreateAlbumDto, userId);

    verify(albumRepository, times(1)).save(album);

    assertEquals(album.getId(), result.getId());
  }

  @Test
  @DisplayName("Should throw DataValidationException when author already has album with the title")
  void testNegativeAddAlbumWithTitleAlreadyExists() {
    AlbumCreateDto testCreateAlbumDto = getCreateAlbumDto();
    String title = testCreateAlbumDto.getTitle();
    long userId = 1L;

    when(albumRepository.existsByTitleAndAuthorId(title, userId)).thenReturn(true);

    var exception = assertThrows(DataValidationException.class,
        () -> albumService.add(testCreateAlbumDto, userId));

    verify(albumRepository, times(1)).existsByTitleAndAuthorId(title, userId);

    assertEquals(
        String.format("User with ID %d already has album with Title %s", userId, title),
        exception.getMessage());
  }

  @Test
  @DisplayName("Should throw EntityNotFoundException when author does not exist")
  void testNegativeAddAlbumWithNonExistingAuthor() {
    AlbumCreateDto testCreateAlbumDto = getCreateAlbumDto();
    String title = testCreateAlbumDto.getTitle();
    long userId = 1L;

    when(albumRepository.existsByTitleAndAuthorId(title, userId)).thenReturn(false);
    when(userServiceClient.getUser(userId)).thenReturn(null);

    var exception = assertThrows(EntityNotFoundException.class,
        () -> albumService.add(testCreateAlbumDto, userId));

    verify(albumRepository, times(1)).existsByTitleAndAuthorId(title, userId);
    verify(userServiceClient, times(1)).getUser(userId);

    assertEquals(String.format("User with ID %d not found", userId), exception.getMessage());
  }

  @Test
  @DisplayName("Should return album, visible to all users ")
  void testGetAlbumByIdWithVisibilityALL() throws JsonProcessingException {
    Album album = getTestAlbum();
    album.setVisibility(Visibility.ALL);
    long id = album.getId();
    long userId = 11L;
    AlbumDto expected = albumMapper.toDto(album);

    when(userServiceClient.getUser(anyLong())).thenReturn(new UserDto());
    when(albumRepository.findById(id)).thenReturn(Optional.of(album));

    AlbumDto result = albumService.getAlbumById(userId, id);

    verify(albumRepository, times(1)).findById(id);
    verify(userServiceClient, times(1)).getUser(anyLong());

    assertEquals(expected.getVisibility(), result.getVisibility());
  }

  @Test
  @DisplayName("Should return album when user is subscriber")
  void testGetAlbumByIdWithVisibilitySubscribers() throws JsonProcessingException {
    Album album = getTestAlbum();
    album.setVisibility(Visibility.SUBSCRIBERS);
    album.setAuthorId(1L);
    long id = album.getId();
    long userId = 3L;
    List<UserDto> subscribers = getSubscribers();
    AlbumDto expected = albumMapper.toDto(album);

    when(userServiceClient.getUser(userId)).thenReturn(new UserDto());
    when(userServiceClient.getUserSubscribersDto(album.getAuthorId())).thenReturn(subscribers);
    when(albumRepository.findById(id)).thenReturn(Optional.of(album));

    AlbumDto result = albumService.getAlbumById(userId, id);

    verify(albumRepository, times(1)).findById(id);
    verify(userServiceClient, times(1)).getUser(userId);

    assertEquals(expected.getVisibility(), result.getVisibility());
  }

  @Test
  @DisplayName("Should return album when user is owner")
  void testGetAlbumByIdWithVisibilitySubscribersOwner() throws JsonProcessingException {
    Album album = getTestAlbum();
    album.setVisibility(Visibility.SUBSCRIBERS);
    long userId = 1L;
    album.setAuthorId(userId);
    long id = album.getId();
    List<UserDto> subscribers = getSubscribers();
    AlbumDto expected = albumMapper.toDto(album);

    when(userServiceClient.getUser(userId)).thenReturn(new UserDto());
    when(userServiceClient.getUserSubscribersDto(album.getAuthorId())).thenReturn(subscribers);
    when(albumRepository.findById(id)).thenReturn(Optional.of(album));

    AlbumDto result = albumService.getAlbumById(userId, id);

    verify(albumRepository, times(1)).findById(id);
    verify(userServiceClient, times(1)).getUser(userId);

    assertEquals(expected.getVisibility(), result.getVisibility());
  }

  @Test
  @DisplayName("Should throw exception when user is not subscriber or owner")
  void testNegativeGetAlbumByIdWithVisibilitySubscribers() {
    Album album = getTestAlbum();
    album.setVisibility(Visibility.SUBSCRIBERS);
    long userId = 11L;
    album.setAuthorId(1L);
    long id = album.getId();
    List<UserDto> subscribers = getSubscribers();

    when(userServiceClient.getUser(userId)).thenReturn(new UserDto());
    when(userServiceClient.getUserSubscribersDto(album.getAuthorId())).thenReturn(subscribers);
    when(albumRepository.findById(id)).thenReturn(Optional.of(album));

    var exception = assertThrows(DataValidationException.class,
        () -> albumService.getAlbumById(userId, id));

    verify(albumRepository, times(1)).findById(id);
    verify(userServiceClient, times(1)).getUser(userId);

    assertEquals(ERROR_MESSAGE_NO_ACCESS, exception.getMessage());
  }

  @Test
  @DisplayName("Should return album when user is in the list of favorites")
  void testGetAlbumByIdWithVisibilityFavorites() throws JsonProcessingException {
    Album album = getTestAlbum();
    album.setVisibility(Visibility.FAVORITES);
    String favoriteUsersIds = "[2, 3, 5]";
    album.setFavorites(favoriteUsersIds);
    long id = album.getId();
    long userId = 3L;
    AlbumDto expected = albumMapper.toDto(album);

    when(userServiceClient.getUser(userId)).thenReturn(new UserDto());
    when(albumRepository.findById(id)).thenReturn(Optional.of(album));

    AlbumDto result = albumService.getAlbumById(userId, id);

    verify(albumRepository, times(1)).findById(id);
    verify(userServiceClient, times(1)).getUser(userId);

    assertEquals(expected.getVisibility(), result.getVisibility());
  }

  @Test
  @DisplayName("Should return album when user is owner")
  void testGetAlbumByIdWithVisibilityFavoritesOwner() throws JsonProcessingException {
    Album album = getTestAlbum();
    album.setVisibility(Visibility.FAVORITES);
    String favoriteUsersIds = "[2, 3, 5]";
    album.setFavorites(favoriteUsersIds);
    long id = album.getId();
    long userId = album.getAuthorId();
    AlbumDto expected = albumMapper.toDto(album);

    when(userServiceClient.getUser(userId)).thenReturn(new UserDto());
    when(albumRepository.findById(id)).thenReturn(Optional.of(album));

    AlbumDto result = albumService.getAlbumById(userId, id);

    verify(albumRepository, times(1)).findById(id);
    verify(userServiceClient, times(1)).getUser(userId);

    assertEquals(expected.getVisibility(), result.getVisibility());
  }

  @Test
  @DisplayName("Should throw exception when user is not favorite or owner ")
  void testNegativeGetAlbumByIdWithVisibilityFavorites() {
    Album album = getTestAlbum();
    album.setVisibility(Visibility.FAVORITES);
    String favoriteUsersIds = "[2, 3, 5]";
    album.setFavorites(favoriteUsersIds);
    long id = album.getId();
    long userId = album.getAuthorId() + 10L;

    when(userServiceClient.getUser(userId)).thenReturn(new UserDto());
    when(albumRepository.findById(id)).thenReturn(Optional.of(album));

    var exception = assertThrows(DataValidationException.class,
        () -> albumService.getAlbumById(userId, id));

    verify(albumRepository, times(1)).findById(id);
    verify(userServiceClient, times(1)).getUser(userId);

    assertEquals(ERROR_MESSAGE_NO_ACCESS, exception.getMessage());
  }

  @Test
  @DisplayName("Should return album when user is owner")
  void testGetAlbumByIdWithVisibilityOwner() throws JsonProcessingException {
    Album album = getTestAlbum();
    album.setVisibility(Visibility.OWNER);
    long id = album.getId();
    long userId = album.getAuthorId();
    AlbumDto expected = albumMapper.toDto(album);

    when(userServiceClient.getUser(userId)).thenReturn(new UserDto());
    when(albumRepository.findById(id)).thenReturn(Optional.of(album));

    AlbumDto result = albumService.getAlbumById(userId, id);

    verify(albumRepository, times(1)).findById(id);
    verify(userServiceClient, times(1)).getUser(userId);

    assertEquals(expected.getVisibility(), result.getVisibility());
  }

  @Test
  @DisplayName("Should throw exception when user is not the owner")
  void testNegativeGetAlbumByIdWithVisibilityOwner() {
    Album album = getTestAlbum();
    album.setVisibility(Visibility.OWNER);
    long id = album.getId();
    long userId = album.getAuthorId() + 1L;

    when(userServiceClient.getUser(userId)).thenReturn(new UserDto());
    when(albumRepository.findById(id)).thenReturn(Optional.of(album));

    var exception = assertThrows(DataValidationException.class,
        () -> albumService.getAlbumById(userId, id));

    verify(albumRepository, times(1)).findById(id);
    verify(userServiceClient, times(1)).getUser(userId);

    assertEquals(ERROR_MESSAGE_NO_ACCESS, exception.getMessage());
  }

  @Test
  void TestGetAllAlbumsWithFilters() {
  }

  @Test
  void testAddFavoriteUser() {
  }

  @Test
  void testRemoveFavoriteUser() {
  }

  private List<UserDto> getSubscribers() {
    return List.of(
        UserDto.builder()
            .id(3L)
            .build(),
        UserDto.builder()
            .id(5L)
            .build(),
        UserDto.builder()
            .id(7L)
            .build()
    );
  }

  private Stream<Album> getTestAlbumStream() {
    return Stream.of(
        Album.builder()
            .id(1L)
            .title("album 1")
            .description("description 1")
            .posts(List.of(new Post(), new Post()))
            .build(),
        Album.builder()
            .id(2L)
            .title("album 2")
            .description("description 2")
            .posts(List.of(new Post(), new Post()))
            .build(),
        Album.builder()
            .id(3L)
            .title("album 3")
            .description("description 3")
            .posts(List.of(new Post(), new Post()))
            .build());
  }

  private AlbumCreateDto getCreateAlbumDto() {
    return AlbumCreateDto.builder()
        .title("Title")
        .description("Description")
        .build();
  }

  private AlbumDto getTestAlbumDto() {
    return AlbumDto.builder()
        .authorId(1L)
        .title("Title")
        .description("Description")
        .build();
  }

  private Album getTestAlbum() {
    return Album.builder()
        .id(1L)
        .authorId(1L)
        .title("Title")
        .description("Description")
        .posts(new ArrayList<>())
        .build();
  }

  private AlbumFilterDto getTestAlbumFilterDto() {
    return AlbumFilterDto.builder()
        .authorId(1L)
        .titlePattern("Title")
        .descriptionPattern("Description")
        .createdAtFrom("2024-11-22 00:21:39")
        .createdAtTo("2024-11-22 00:21:39")
        .build();
  }

  private Post getTestPost() {
    return Post.builder()
        .id(1L)
        .authorId(1L)
        .content("post content")
        .createdAt(LocalDateTime.now())
        .build();
  }
}