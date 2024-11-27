package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.comment.CommentPublishedEvent;
import faang.school.postservice.dto.post.PostForFeedDto;
import faang.school.postservice.mapper.comment.CommentPublishedEventMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.post.CacheablePost;
import faang.school.postservice.model.post.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CacheablePostMapper {
    @Value("${feed.cache.post.time-to-live-in-seconds}")
    private long timeToLive;

    @Autowired
    private CommentPublishedEventMapper commentPublishedEventMapper;

    @Mapping(target = "countOfLikes", source = "likes", qualifiedByName = "getListSizeLike")
    @Mapping(target = "countOfComments", source = "comments", qualifiedByName = "getListSizeComment")
    @Mapping(target = "comments", source = "comments", qualifiedByName = "commentsMap")
    @Mapping(target = "timeToLive", source = "comments", qualifiedByName = "timeToLive")
    public abstract CacheablePost toCacheablePost(Post post);

    public abstract PostForFeedDto toPostForFeedDto(CacheablePost cacheablePost);

    @Named("commentsMap")
    protected List<CommentPublishedEvent> toCommentPublishedEvents(List<Comment> comments) {
        return comments.stream()
                .limit(3L)
                .map(commentPublishedEventMapper::fromCommentToEvent)
                .toList();
    }

    @Named("timeToLive")
    protected long getTimeToLive(List<Comment> comments) {
        return timeToLive;
    }

    @Named("getListSizeLike")
    protected long getListSizeLike(List<Like> list) {
        return list.size();
    }

    @Named("getListSizeComment")
    protected long getListSizeComment(List<Comment> list) {
        return list.size();
    }
}
