package faang.school.postservice.filters.album.impl;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.filters.album.AlbumFilter;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AlbumDateFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto filters) {
        return filters.getFromDatePattern() != null || filters.getToDatePattern() != null;
    }

    @Override
    public boolean filterEntity(Album album, AlbumFilterDto filters) {
        LocalDateTime createAt = album.getCreatedAt();
        if (filters.getFromDatePattern() != null && createAt.isBefore(filters.getFromDatePattern())) {
            return false;
        }
        return filters.getToDatePattern() == null || !createAt.isAfter(filters.getToDatePattern());
    }
}
