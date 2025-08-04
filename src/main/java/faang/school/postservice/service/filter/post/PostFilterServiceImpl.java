package faang.school.postservice.service.filter.post;

import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.filter.Filter;
import faang.school.postservice.service.filter.FilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class PostFilterServiceImpl implements FilterService<Post, PostFilterDto> {
    private final List<Filter<Post, PostFilterDto>> filters;

    @Override
    public List<Post> getFilteredList(List<Post> entities, PostFilterDto dto) {
        return applyFilters(filters, entities, dto);
    }
}
