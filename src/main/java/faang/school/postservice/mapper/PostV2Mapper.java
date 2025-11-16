package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostV2CreateDto;
import faang.school.postservice.dto.post.PostV2Dto;
import faang.school.postservice.dto.post.PostV2UpdateDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Objects;

@UtilityClass
public class PostV2Mapper {

    public static Post toEntity(PostV2CreateDto postV2CreateDto) {
        if (postV2CreateDto == null) {
            return null;
        }

        Post post = new Post();
        post.setContent(postV2CreateDto.content());
        return post;
    }

    public static void update(Post post, PostV2UpdateDto postV2UpdateDto) {
        if (postV2UpdateDto == null || Objects.equals(post.getContent(), postV2UpdateDto.content())) {
            return;
        }

        post.setContent(postV2UpdateDto.content());
    }

    public static PostV2Dto toDto(Post post, Long likesCount, List<Long> likesIds) {
        if (post == null) {
            return null;
        }

        return PostV2Dto.builder()
                .id(post.getId())
                .content(post.getContent())
                .authorId(post.getAuthorId())
                .projectId(post.getProjectId())
                .likesIds(likesIds)
                .commentsIds(post.getComments().stream().map(Comment::getId).toList())
                .albumsIds(post.getAlbums().stream().map(Album::getId).toList())
                .adId(post.getAd() != null ? post.getAd().getId() : null)
                .resourcesIds(post.getResources().stream().map(Resource::getId).toList())
                .published(post.isPublished())
                .publishedAt(post.getPublishedAt())
                .scheduledAt(post.getScheduledAt())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .likesCount(likesCount)
                .build();
    }
}
