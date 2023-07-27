package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.NotFoundException;
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

    @Mapping(target = "post", ignore = true)
    @Mapping(target = "likes", ignore = true)
    public abstract Comment toEntity(CommentDto commentDto);

    public abstract void update(CommentDto commentDto, @MappingTarget Comment comment);

    @Named("mapLikesToIdList")
    protected List<Long> mapLikesToIdList(List<Like> likes) {
        if (likes == null)
            return Collections.emptyList();
        return likes.stream().map(Like::getId).toList();
    }

    public void convertDependenciesToEntity(CommentDto commentDto, Comment comment) {
        if (commentDto.getPostId() != null) {
            Post post = postRepository.findById(commentDto.getPostId())
                    .orElseThrow(() -> new NotFoundException("Post with id " + commentDto.getPostId() + " was not found!"));
            comment.setPost(post);
        }
        if (commentDto.getLikesIds() != null && !commentDto.getLikesIds().isEmpty()) {
            List<Like> likes = new ArrayList<>();
            commentDto.getLikesIds().forEach(likeId -> {
                Like like = likeRepository.findById(likeId)
                        .orElseThrow(() -> new NotFoundException("Like with id " + likeId + " was not found!"));
                likes.add(like);
            });
            comment.setLikes(likes);
        }
    }
}
