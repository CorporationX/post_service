package faang.school.postservice.filter;

import faang.school.postservice.dto.AlbumFilterDto;
import faang.school.postservice.model.Album;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class AlbumSpecifications {
    private final List<AlbumFilter> filters;

    public Specification<Album> getSpecification(AlbumFilterDto dto) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (AlbumFilter filter : filters) {
                if (filter.isApplicable(dto)) {
                    predicates.add(filter.getPredicate(dto, root, cb));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}