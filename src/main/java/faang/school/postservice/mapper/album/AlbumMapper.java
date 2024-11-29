package faang.school.postservice.mapper.album;

import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlbumMapper {

  Album toEntityFromCreateDto(AlbumCreateDto dto);

  @Mapping(target = "posts", ignore = true)
  @Mapping(target = "createdAt", source = "createdAt")
  @Mapping(target = "updatedAt", source = "updatedAt")
  Album toEntity(AlbumDto albumDto);

  @Mapping(source = "posts", target = "postIds", qualifiedByName = "mapPostsListToIdsList")
  @Mapping(source = "createdAt", target = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
  @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
  AlbumDto toDto(Album album);

  @Named("mapPostsListToIdsList")
  default List<Long> map(List<Post> posts) {
    return posts.stream().map(Post::getId).toList();
  }
}
