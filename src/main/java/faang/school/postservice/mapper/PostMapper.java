package faang.school.postservice.mapper;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "amountLikes",
            expression = "java(post.getLikes() != null && !post.getLikes().isEmpty() ? post.getLikes().size() : 0)")
    @Mapping(source = "comments", target="commentIds")
    @Mapping(source = "albums", target = "albumIds")
    PostDto toDto(Post post);

    Post toEntity(PostDto postDto);
    default List<Long> extractIdsFromComments(List<Comment> comments) {
        return comments != null
                ? comments.stream().map(Comment::getId).collect(Collectors.toList())
                : Collections.emptyList();
    }

    List<PostDto> toDtoList(List<Post> postList);

    List<Post> toEntityList(List<PostDto> postDtoList);
    default List<Long> extractIdsFromAlbums(List<Album> albums) {
        return albums != null
                ? albums.stream().map(Album::getId).toList()
                : Collections.emptyList();
    }
}
