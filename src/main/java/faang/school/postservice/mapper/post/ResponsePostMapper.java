package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResponsePostMapper {
    ResponsePostMapper INSTANCE = Mappers.getMapper(ResponsePostMapper.class);

    @Mapping(target = "adId", source = "ad.id")
    @Mapping(target = "likesIds", source = "likes", qualifiedByName = "likesToLikesIds")
    @Mapping(target = "commentsIds", source = "comments", qualifiedByName = "commentsToCommentsIds")
    @Mapping(target = "albumsIds", source = "albums", qualifiedByName = "albumsToAlbumsIds")
    @Mapping(source = "hashtags", target = "hashtags", qualifiedByName ="hashtagToString")
    ResponsePostDto toDto(Post entity);

    List<ResponsePostDto> toDtoList(List<Post> entities);


    @Named("likesToLikesIds")
    default List<Long> likesToLikesIds(List<Like> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list.stream()
                .map(Like::getId)
                .collect(Collectors.toList());
    }

    @Named("commentsToCommentsIds")
    default List<Long> commentsToCommentsIds(List<Comment> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list.stream()
                .map(Comment::getId)
                .collect(Collectors.toList());
    }

    @Named("albumsToAlbumsIds")
    default List<Long> albumsToAlbumsIds(List<Album> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list.stream()
                .map(Album::getId)
                .collect(Collectors.toList());
    }

    @Named("hashtagToString")
    default List<String> hashtagToString(List<Hashtag> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list.stream()
                .map(Hashtag::getHashtag)
                .toList();
    }
}
