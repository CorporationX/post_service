package faang.school.postservice.Mapper;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.UserVisibility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {

//    @Mapping(source = "postsId", target = "posts.id", qualifiedByName = "mapPosts")
    Album toEntity(AlbumDto albumDto);

    @Mapping(source = "posts", target = "postsId", qualifiedByName = "mapPost")
    @Mapping(source = "visibilityUsers", target = "visibilityUsersId", qualifiedByName = "mapVisibilityUsers")
    AlbumDto toDto(Album album);

    @Named("mapPost")
    default List<Long> mapPosts(List<Post> posts) {
        return posts.stream().map(Post::getId).toList();
    }

    @Named("mapVisibilityUsers")
    default List<Long> mapVisibilityUsers(List<UserVisibility> userVisibilities) {
        return userVisibilities.stream().map(UserVisibility::getId).toList();
    }
}
