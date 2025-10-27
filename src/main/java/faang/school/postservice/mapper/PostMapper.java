package faang.school.postservice.mapper;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user_service.user.UserDto;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class PostMapper {

    private final UserServiceClient userServiceClient;

    public List<PostDto> toDtos(List<Post> posts) {
        return posts.stream()
                .map(post -> toDto(post, userServiceClient.getUser(post.getAuthorId())))
                .toList();
    }

    public PostDto toDto(Post post, UserDto postAuthor) {
        return PostDto.builder()
                .id(post.getId())
                .content(post.getContent())
                .author(postAuthor)
                .build();
    }
}
