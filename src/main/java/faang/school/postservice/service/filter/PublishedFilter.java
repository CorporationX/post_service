package faang.school.postservice.service.filter;

import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class PublishedFilter implements PostFilter {

    @Override
    public boolean isApplicable(PostFilterDto postFilterDto) {
        return postFilterDto.getPublished() != null;
    }

    @Override
    public Stream<Post> apply(Stream<Post> posts, PostFilterDto postFilterDto) {
        return posts.filter(post -> postFilterDto.getPublished() == post.isPublished());
    }
}
