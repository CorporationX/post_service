package faang.school.postservice.specification;

import faang.school.postservice.dto.album.filter.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.springframework.data.jpa.domain.Specification;

public class AlbumSpecificationBuilder {

    public static Specification<Album> buildSpecification(AlbumFilterDto filterDto) {
        Specification<Album> spec = Specification.where(null);

        if (filterDto.getTitle() != null) {
            spec = spec.and(AlbumSpecifications.hasTitle(filterDto.getTitle()));
        }

        if (filterDto.getDescription() != null) {
            spec = spec.and(AlbumSpecifications.hasDescription(filterDto.getDescription()));
        }

        if (filterDto.getAuthorId() != null) {
            spec = spec.and(AlbumSpecifications.hasAuthorId(filterDto.getAuthorId()));
        }

        if (filterDto.getCreatedAfter() != null) {
            spec = spec.and(AlbumSpecifications.createdAfter(filterDto.getCreatedAfter()));
        }

        if (filterDto.getCreatedBefore() != null) {
            spec = spec.and(AlbumSpecifications.createdBefore(filterDto.getCreatedBefore()));
        }

        return spec;
    }
}

