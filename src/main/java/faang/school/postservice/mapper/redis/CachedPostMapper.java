package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.comment.CommentNewsFeedDto;
import faang.school.postservice.dto.post.PostNewsFeedDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.CachedPost;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CachedPostMapper {
    @Mapping(target = "likes", source = "likes", qualifiedByName = "longToLikes")
    @Mapping(target = "comments", source = "comments", qualifiedByName = "queueToComments")
    Post toDto(CachedPost cachedPost);

    @Mapping(target = "likes", source = "likes", qualifiedByName = "likesToLong")
    @Mapping(target = "comments", source = "comments", qualifiedByName = "commentsToQueue")
    CachedPost toCachedPost(Post post);

    List<PostNewsFeedDto> toPostsNewsFeedDto(List<CachedPost> cachedPosts);

    @Named("likesToLong")
    default Long likesToLong(List<Like> likes) {
        return likes != null ? (long) likes.size() : 0L;
    }

    @Named("longToLikes")
    default List<Like> longToLikes(Long likesCount) {
        return Collections.emptyList();
    }

    @Named("commentsToQueue")
    default ConcurrentLinkedQueue<CommentNewsFeedDto> commentsToQueue(List<Comment> comments) {
        if (comments == null) {
            return new ConcurrentLinkedQueue<>();
        }
        return comments.stream()
                .map(comment -> new CommentNewsFeedDto(
                        comment.getId(),
                        comment.getAuthorId(),
                        comment.getPost().getId(),
                        comment.getContent(),
                        comment.getLikes() != null
                                ? comment.getLikes().stream().map(Like::getId).toList()
                                : Collections.emptyList(),
                        comment.getCreatedAt()))
                .collect(Collectors.toCollection(ConcurrentLinkedQueue::new));
    }

    @Named("queueToComments")
    default List<Comment> queueToComments(ConcurrentLinkedQueue<CommentNewsFeedDto> commentDtos) {
        return commentDtos.stream()
                .map(dto -> {
                    Comment comment = new Comment();
                    comment.setId(dto.getId());
                    comment.setAuthorId(dto.getAuthorId());
                    comment.setContent(dto.getContent());
                    comment.setCreatedAt(dto.getCreatedAt());
                    return comment;
                })
                .toList();
    }
}
