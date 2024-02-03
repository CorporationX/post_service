package faang.school.postservice.filter.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostFilterDto;

import java.util.stream.Stream;

public interface PostFilter {
    boolean isApplicable(PostDto postDto);

    Stream<PostDto> apply(Stream<PostDto> userStream, PostFilterDto userFilterDto);
}
