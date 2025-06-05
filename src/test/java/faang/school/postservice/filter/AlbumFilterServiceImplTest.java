package faang.school.postservice.filter;

import faang.school.postservice.dto.albums.AlbumFilterDto;
import faang.school.postservice.filter.album.AlbumFilter;
import faang.school.postservice.filter.service.AlbumFilterServiceImpl;
import faang.school.postservice.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class AlbumFilterServiceImplTest {

    @InjectMocks
    AlbumFilterServiceImpl albumFilterService;

    @Mock
    AlbumFilter filter1;

    @Mock
    AlbumFilter filter2;

    private Album album1;

    @BeforeEach
    void setUp() {
        album1 = Album.builder()
                .id(1L)
                .title("Test")
                .createdAt(LocalDateTime.of(2023, 1, 1, 0, 0))
                .build();
    }

    @Test
    void applyFilters_ShouldApplyOnlyApplicableFilters() {
        AlbumFilterDto filterDto = new AlbumFilterDto();

        when(filter1.isApplicable(filterDto)).thenReturn(true);
        when(filter1.apply(any(), eq(filterDto))).thenAnswer(i -> i.getArgument(0));

        when(filter2.isApplicable(filterDto)).thenReturn(false);

        albumFilterService = new AlbumFilterServiceImpl(List.of(filter1, filter2));

        Stream<Album> inputStream = Stream.of(album1);

        List<Album> result = albumFilterService.applyFilters(inputStream, filterDto).toList();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());

        verify(filter1).apply(any(), eq(filterDto));
        verify(filter2, never()).apply(any(), any());
    }

    @Test
    void applyFilters_ShouldReturnOriginalStreamWhenDtoIsNull() {
        Stream<Album> original = Stream.of(album1);

        List<Album> result = albumFilterService.applyFilters(original, null).toList();

        assertEquals(1, result.size());
    }
}
