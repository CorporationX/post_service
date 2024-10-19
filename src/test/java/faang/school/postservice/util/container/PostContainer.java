package faang.school.postservice.util.container;

import faang.school.postservice.dto.filter.PostFilterDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.Ad;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostContainer {
    private Long postId;
    private String content;
    private Long authorId;
    private Long projectId;
    private Long firstLikeId;
    private Long secondLikeId;
    private Long firstCommentId;
    private Long secondCommentId;
    private Long firstAlbumId;
    private Long secondAlbumId;
    private Long adId;
    private Long firstResourceId;
    private Long secondResourceId;
    private boolean published;
    private boolean deleted;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public List<Like> likes() {
        Like firstLike = new Like();
        Like secondLike = new Like();
        firstLike.setId(firstLikeId);
        secondLike.setId(secondLikeId);
        return new ArrayList<>(List.of(firstLike, secondLike));
    }

    public List<Long> likeIds() {
        return new ArrayList<>(List.of(firstLikeId, secondLikeId));
    }

    public List<Comment> comments() {
        Comment firstComment = new Comment();
        Comment secondComment = new Comment();
        firstComment.setId(firstCommentId);
        secondComment.setId(secondCommentId);
        return new ArrayList<>(List.of(firstComment, secondComment));
    }

    public List<Long> commentIds() {
        return new ArrayList<>(List.of(firstCommentId, secondCommentId));
    }

    public List<Album> albums() {
        Album firstAlbum = new Album();
        Album secondAlbum = new Album();
        firstAlbum.setId(firstAlbumId);
        secondAlbum.setId(secondAlbumId);
        return new ArrayList<>(List.of(firstAlbum, secondAlbum));
    }

    public List<Long> albumIds() {
        return new ArrayList<>(List.of(firstAlbumId, secondAlbumId));
    }

    public Ad ad() {
        return Ad.builder()
                .id(adId)
                .build();
    }

    public List<Resource> resources() {
        Resource firstResource = Resource.builder()
                .id(firstResourceId).build();
        Resource secondResource = Resource.builder()
                .id(secondResourceId).build();
        return new ArrayList<>(List.of(firstResource, secondResource));
    }

    public List<Long> resourceIds() {
        return new ArrayList<>(List.of(firstResourceId, secondResourceId));
    }

    public PostContainer() {
        Long id = 0L;
        postId = ++id;
        content = "content";
        authorId = ++id;
        projectId = ++id;
        firstLikeId = ++id;
        secondLikeId = ++id;
        firstAlbumId = ++id;
        secondAlbumId = ++id;
        firstCommentId = ++id;
        secondCommentId = ++id;
        firstResourceId = ++id;
        secondResourceId = ++id;
        adId = ++id;
        published = false;
        createdAt = LocalDateTime.now();
        updatedAt = createdAt.plusMinutes(30);
        publishedAt = createdAt.plusDays(1);
        scheduledAt = createdAt.plusDays(2);
        deleted = false;
    }

    public PostDto dto() {
        return PostDto.builder()
                .id(postId)
                .content(content)
                .authorId(authorId)
                .projectId(projectId)
                .published(published)
                .publishedAt(publishedAt)
                .scheduledAt(scheduledAt)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deleted(deleted)
                .likesCount((long) likes().size())
                .build();
    }

    public Post entity() {
        return Post.builder()
                .id(postId)
                .content(content)
                .authorId(authorId)
                .projectId(projectId)
                .likes(likes())
                .comments(comments())
                .albums(albums())
                .ad(ad())
                .resources(resources())
                .published(published)
                .publishedAt(publishedAt)
                .scheduledAt(scheduledAt)
                .deleted(deleted)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public Long postId() {
        return postId;
    }

    public Long authorId() {
        return authorId;
    }

    public String content() {
        return content;
    }

    public LocalDateTime publishedAt() {
        return publishedAt;
    }

    public boolean published() {
        return published;
    }

    public boolean deleted() {
        return deleted;
    }

    public Long projectId() {
        return projectId;
    }

    public Long adId() {
        return adId;
    }

    public LocalDateTime updatedAt() {
        return updatedAt;
    }

    public PostFilterDto filters() {
        return new PostFilterDto(authorId, null, false, false);
    }
}
