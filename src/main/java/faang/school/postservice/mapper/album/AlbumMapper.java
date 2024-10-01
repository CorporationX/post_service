package faang.school.postservice.mapper.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.UserAlbumAccess;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {

    @Mapping(target = "postIds", source = "posts", qualifiedByName = "mapPostToLongList")
    @Mapping(target = "userAlbumAccessIds", source = "usersAlbumAccess", qualifiedByName = "mapUserAlbumAccessToUserIdList")
    AlbumDto toAlbumDto(Album album);

    @Mapping(target = "posts", source = "postIds", qualifiedByName = "mapLongToPostList")
    @Mapping(target = "usersAlbumAccess", source = "userAlbumAccessIds", qualifiedByName = "mapUserIdToUserAlbumAccessList")
    Album toAlbumEntity(AlbumDto albumDto);

    List<AlbumDto> toDtoList(List<Album> albums);

    @Named("mapLongToPostList")
    default List<Post> mapLongToPostList(List<Long> postIds) {
        if (Objects.isNull(postIds)) {
            postIds = List.of();
        }

        return postIds.stream()
                .map(postId -> Post.builder()
                        .id(postId)
                        .build())
                .toList();
    }

    @Named("mapPostToLongList")
    default List<Long> mapPostToLongList(List<Post> posts){
        return posts.stream()
                .map(Post::getId)
                .toList();
    }

    @Named("mapUserAlbumAccessToUserIdList")
    default List<Long> mapUserAlbumAccessToUserIdList(List<UserAlbumAccess> users) {
        return users.stream()
                .map(UserAlbumAccess::getUserId)
                .toList();
    }

    @Named("mapUserIdToUserAlbumAccessList")
    default List<UserAlbumAccess> mapUserIdToUserAlbumAccessList(List<Long> userIds) {
        if (Objects.isNull(userIds)) {
            userIds = List.of();
        }

        return userIds.stream()
                .map(userId -> UserAlbumAccess.builder()
                        .userId(userId)
                        .build())
                .toList();
    }
}
