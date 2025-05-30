package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumUpdateDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AlbumMinPostsFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto filter) {
        return filter.getMaxPostsCountPattern() != null;

    }

    @Override
    public Stream<AlbumUpdateDto> apply(Stream<AlbumUpdateDto> albums, AlbumFilterDto filter) {
        Integer minCount = filter.getMaxPostsCountPattern();

        return albums.filter(album -> album.getPostsIds().size() >= minCount);
    }
}
