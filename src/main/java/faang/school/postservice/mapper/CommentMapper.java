package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentEventDto;
import faang.school.postservice.util.exception.NotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CommentMapper {
    @Autowired
    protected PostRepository postRepository;
    @Autowired
    protected LikeRepository likeRepository;

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "likes", target = "likesIds", qualifiedByName = "mapLikesToIdList")
    public abstract CommentDto toDto(Comment comment);

    @Mapping(source = "postId", target = "post", qualifiedByName = "mapPostIdToPost")
    @Mapping(source = "likesIds", target = "likes", qualifiedByName = "mapLikesIdsToList")
    public abstract Comment toEntity(CommentDto commentDto);

    @Mapping(source = "postId", target = "post", qualifiedByName = "mapPostIdToPost")
    @Mapping(source = "likesIds", target = "likes", qualifiedByName = "mapLikesIdsToList")
    public abstract void update(CommentDto commentDto, @MappingTarget Comment comment);

    @Named("mapLikesToIdList")
    protected List<Long> mapLikesToIdList(List<Like> likes) {
        if (likes == null)
            return Collections.emptyList();
        return likes.stream().map(Like::getId).toList();
    }

    @Named("mapPostIdToPost")
    protected Post mapPostIdToPost(Long postId) {
        if (postId == null)
            throw new NotFoundException("Comment must be related to the post! But given nothing in field postId");
        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post with id " + postId + " was not found!"));
    }

    @Named("mapLikesIdsToList")
    protected List<Like> mapLikesIdsToList(List<Long> likesIds) {
        if (likesIds == null || likesIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Like> likes = new ArrayList<>();
        likesIds.forEach(likeId -> {
            Like like = likeRepository.findById(likeId)
                    .orElseThrow(() -> new NotFoundException("Like with id " + likeId + " was not found!"));
            likes.add(like);
        });
        return likes;
    }
    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "comment.id", target = "commentId")
    public abstract CommentEventDto toEvent(Comment comment);
}
