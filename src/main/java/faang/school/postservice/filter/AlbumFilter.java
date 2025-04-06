package faang.school.postservice.filter;

import faang.school.postservice.dto.AlbumFilterDto;
import faang.school.postservice.model.Album;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;

import jakarta.persistence.criteria.Predicate;

public interface AlbumFilter {
    boolean isApplicable(AlbumFilterDto dto);
    Predicate getPredicate(AlbumFilterDto dto, Root<Album> root, CriteriaBuilder cb);
}
