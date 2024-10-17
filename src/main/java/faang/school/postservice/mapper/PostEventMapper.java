package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostEventDto;
import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;


@Component
public class PostEventMapper {
    public PostEventDto toPostEventDto(Post post) {
        return PostEventDto.builder()
                .postId(post.getId())
                .authorId(post.getAuthorId())
                .build();
    }
}
