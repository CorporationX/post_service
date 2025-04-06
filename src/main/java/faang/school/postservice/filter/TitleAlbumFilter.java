package faang.school.postservice.filter;


import faang.school.postservice.dto.AlbumFilterDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Album_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;

import jakarta.persistence.criteria.Predicate;
import org.springframework.stereotype.Component;

@Component
public class TitleAlbumFilter implements AlbumFilter{
    @Override
    public boolean isApplicable(AlbumFilterDto dto) {
        return dto.titlePattern() != null && !dto.titlePattern().isBlank();
    }

    @Override
    public Predicate getPredicate(AlbumFilterDto dto, Root<Album> root, CriteriaBuilder cb) {
        return cb.like(root.get(Album_.TITLE), "%" + dto.titlePattern() + "%");
    }
}
