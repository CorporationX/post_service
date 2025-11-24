package faang.school.postservice.service;

import faang.school.postservice.filters.AlbumFilter;
import faang.school.postservice.filters.AlbumFilterDto;
import faang.school.postservice.model.Album;

import java.util.stream.Stream;

public class TestAlbumTitleFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        return true;
    }

    @Override
    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto albumFilterDto) {
        return albums.filter(album -> album.getTitle().equals("album1"));
    }
}
