package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import lombok.experimental.UtilityClass;

import java.util.Objects;

@UtilityClass
public class PostMapper {

    public static Post toEntity(PostCreateDto postCreateDto) {
        if (postCreateDto == null) {
            return null;
        }

        Post post = new Post();
        post.setContent(postCreateDto.content());
        return post;
    }

    public static void update(Post post, PostUpdateDto postUpdateDto) {
        if (postUpdateDto == null || Objects.equals(post.getContent(), postUpdateDto.content())) {
            return;
        }

        post.setContent(postUpdateDto.content());
    }

    public static PostDto toDto(Post post) {
        if (post == null) {
            return null;
        }

        return new PostDto(
                post.getId(),
                post.getContent(),
                post.getAuthorId(),
                post.getProjectId(),
                post.getLikes().stream().map(Like::getId).toList(),
                post.getComments().stream().map(Comment::getId).toList(),
                post.getAlbums().stream().map(Album::getId).toList(),
                post.getAd() == null ? null : post.getAd().getId(),
                post.getResources().stream().map(Resource::getId).toList(),
                post.isPublished(),
                post.getPublishedAt(),
                post.getScheduledAt(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
