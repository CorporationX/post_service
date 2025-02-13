package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.CacheCommentDto;
import faang.school.postservice.dto.post.RedisPostDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    @Mapping(target = "hashtags", expression = "java(toHashtags(post.getHashtags()))")
    ResponsePostDto toDto(Post post);

    @Mapping(target = "hashtags", ignore = true)
    Post toEntity(CreatePostDto createPostDto);

    @Named("toHashtags")
    default Set<String> toHashtags(Set<Hashtag> hashtags) {
        return hashtags.stream().map(Hashtag::getTag).collect(Collectors.toSet());
    }

    @Mapping(target = "likeCount", expression = "java(post.getLikes() != null ? post.getLikes().size() : 0)")
    @Mapping(target = "recentComments", expression = "java(toRecentComments(post.getComments()))")
    @Mapping(target = "hashtagIds", expression = "java(toHashtagIds(post.getHashtags()))")
    RedisPostDto toRedisPostDto (Post post);

    @Named("toRecentComments")
    default List<CacheCommentDto> toRecentComments(List<Comment> comments) {
        if (comments == null) {
            return List.of();
        }
        return comments.stream()
                .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                .limit(3)
                .map(comment -> new CacheCommentDto(
                        comment.getId(),
                        comment.getAuthorId(),
                        comment.getLikes() != null ? comment.getLikes().size() : 0,
                        comment.getContent(),
                        comment.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Named("toHashtagIds")
    default Set<Long> toHashtagIds(Set<Hashtag> hashtags) {
        if (hashtags == null) {
            return Set.of();
        }
        return hashtags.stream()
                .map(Hashtag::getId)
                .collect(Collectors.toSet());
    }
}
