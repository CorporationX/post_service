package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;

public class PostMapper {

    public static Post PostDtoToPost(PostDto postDto) {
        return Post.builder()
                .id(postDto.getPostId())
                .authorId(postDto.getAuthorId())
                .projectId(postDto.getProjectId())
                .content(postDto.getContent())
                .build();
    }

    public static PostDto PostToPostDto(Post post) {
        return PostDto.builder()
                .postId(post.getId())
                .authorId(post.getAuthorId())
                .projectId(post.getProjectId())
                .content(post.getContent())
                .build();
    }

}
