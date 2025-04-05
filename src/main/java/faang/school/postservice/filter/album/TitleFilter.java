package faang.school.postservice.filter.album;

import faang.school.postservice.dto.albums.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.stream.Stream;

public class TitleFilter implements AlbumFilter {

    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return albumFilterDto.getTitlePattern() != null && !albumFilterDto.getTitlePattern().isEmpty();
    }

    @Override
    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto albumFilterDto) {
        System.out.println("Applying TitleFilter");
//        return albums.filter(album -> album.getTitle().startsWith(albumFilterDto.getTitlePattern()));
        return albums.filter(album ->
                album.getTitle().toLowerCase().startsWith(albumFilterDto.getTitlePattern().toLowerCase()));

    }
}
