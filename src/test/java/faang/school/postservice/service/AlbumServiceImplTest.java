package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.dto.album.AlbumUsersDto;
import faang.school.postservice.exception.AlbumAccessDeniedException;
import faang.school.postservice.filter.albumvisibility.AlbumVisibilityFilter;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.repository.AlbumRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlbumServiceImpl Test")
class AlbumServiceImplTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private AlbumVisibilityFilter allUsersFilter;

    @Mock
    private AlbumVisibilityFilter followersFilter;

    private AlbumServiceImpl albumService;

    @BeforeEach
    public void init() {
        when(allUsersFilter.getAlbumVisibility()).thenReturn(AlbumVisibility.PUBLIC);
        when(followersFilter.getAlbumVisibility()).thenReturn(AlbumVisibility.FOLLOWERS);
        albumService = new AlbumServiceImpl(albumRepository, userContext, List.of(allUsersFilter, followersFilter));
    }

    @Nested
    @DisplayName("Get Album Tests")
    class GetAlbumTests {

        @Test
        @DisplayName("Should return album when found by ID")
        public void testGetAlbumById() {
            long albumId = 1L;
            when(albumRepository.findById(albumId)).thenReturn(Optional.of(createAlbum(albumId, 1L, AlbumVisibility.PUBLIC)));
            when(allUsersFilter.apply(createAlbum(albumId, 1L, AlbumVisibility.PUBLIC))).thenReturn(createAlbumResponseDto(albumId));

            AlbumResponseDto result = albumService.getAlbumById(albumId);

            assertNotNull(result);
            assertEquals(albumId, result.id());
            verify(albumRepository).findById(albumId);
            verify(allUsersFilter).apply(createAlbum(albumId, 1L, AlbumVisibility.PUBLIC));
        }

        @Test
        @DisplayName("Should throw exception when album not found")
        public void testGetAlbumByIdWhenAlbumNotFound() {
            long albumId = 1L;
            when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> albumService.getAlbumById(albumId));
            verify(albumRepository).findById(albumId);
        }

        @Test
        @DisplayName("Should return albums when found by author ID")
        public void testGetAlbumsByAuthorId() {
            long authorId = 1L;

            when(albumRepository.findByAuthorId(authorId)).thenReturn(List.of(createAlbum(1L, 1L, AlbumVisibility.PUBLIC),
                    createAlbum(2L, 1L, AlbumVisibility.FOLLOWERS)));
            when(allUsersFilter.apply(createAlbum(1L, 1L, AlbumVisibility.PUBLIC))).thenReturn(createAlbumResponseDto(1L));
            when(followersFilter.apply(createAlbum(2L, 1L, AlbumVisibility.FOLLOWERS))).thenReturn(createAlbumResponseDto(2L));

            List<AlbumResponseDto> result = albumService.getAlbumsByAuthorId(authorId);

            assertEquals(2, result.size());
            verify(albumRepository).findByAuthorId(authorId);
            verify(allUsersFilter).apply(createAlbum(1L,1L, AlbumVisibility.PUBLIC));
            verify(followersFilter).apply(createAlbum(2L, 1L, AlbumVisibility.FOLLOWERS));
        }
    }

    @Nested
    @DisplayName("Update Album Visibility Tests")
    class UpdateAlbumVisibilityTests {

        @Test
        @DisplayName("Should update album visibility if user is author")
        public void testUpdateAlbumVisibility() {
            long albumId = 1L;
            long userId = 1L;
            Album album = createAlbum(1L, 1L, AlbumVisibility.PUBLIC);
            when(albumRepository.findById(albumId))
                    .thenReturn(Optional.of(album));
            when(userContext.getUserId()).thenReturn(userId);

            Album updatedAlbum = createAlbum(1L, 1L, AlbumVisibility.FOLLOWERS);
            when(albumRepository.save(any(Album.class))).thenReturn(updatedAlbum);

            albumService.updateAlbumVisibility(albumId, AlbumVisibility.FOLLOWERS);

            assertEquals(AlbumVisibility.FOLLOWERS, updatedAlbum.getAlbumVisibility());
            verify(albumRepository).save(any(Album.class));
        }

        @Test
        @DisplayName("Should throw exception if user is not the author")
        public void testUpdateAlbumVisibilityWhenUserIsNotAuthor() {
            long albumId = 1L;
            long userId = 1L;
            long otherUserId = 2L;
            Album album = createAlbum(albumId, otherUserId, AlbumVisibility.PRIVATE);
            when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
            when(userContext.getUserId()).thenReturn(userId);

            assertThrows(AlbumAccessDeniedException.class,
                    () -> albumService.updateAlbumVisibility(albumId, AlbumVisibility.FOLLOWERS));
            verify(albumRepository, never()).save(album);
        }
    }

    @Nested
    @DisplayName("Add Users for Access Tests")
    class AddUsersForAccessTests {

        @Test
        @DisplayName("Should add users when album has correct visibility")
        public void testAddUsersForAccessAlbum() {
            long albumId = 1L;
            long userId = 1L;
            Album album = createAlbum(albumId, userId, AlbumVisibility.SELECTED);
            AlbumUsersDto albumUsersDto = new AlbumUsersDto(List.of(2L, 3L));
            when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
            when(userContext.getUserId()).thenReturn(userId);

            albumService.addUsersForAccessAlbum(albumId, albumUsersDto);

            verify(albumRepository).addUserForVisibilityAtAlbum(albumId, 2L);
            verify(albumRepository).addUserForVisibilityAtAlbum(albumId, 3L);
        }

        @Test
        @DisplayName("Should throw exception when album visibility is not SELECTED")
        public void testAddUsersForAccessAlbumWhenWrongVisibility() {
            long albumId = 1L;
            long userId = 1L;
            Album album = createAlbum(albumId, userId, AlbumVisibility.PUBLIC);
            AlbumUsersDto albumUsersDto = new AlbumUsersDto(List.of(2L, 3L));
            when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
            when(userContext.getUserId()).thenReturn(userId);

            assertThrows(IllegalArgumentException.class, () -> albumService.addUsersForAccessAlbum(albumId, albumUsersDto));
            verify(albumRepository, never()).addUserForVisibilityAtAlbum(anyLong(), anyLong());
        }
    }

    private AlbumResponseDto createAlbumResponseDto(long albumId) {
        return new AlbumResponseDto(albumId, "album", null, 1L);
    }

    private static Album createAlbum(long albumId, long authorId, AlbumVisibility albumVisibility) {
        return Album.builder()
                .id(albumId)
                .albumVisibility(albumVisibility)
                .authorId(authorId)
                .build();
    }

}