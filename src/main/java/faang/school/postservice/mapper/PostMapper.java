package faang.school.postservice.mapper;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.hashtag.HashtagDto;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;

import java.util.List;

public interface PostMapper {
    Post dtoToEntity(HashtagDto hashtagDto);
    HashtagDto entityToDto(Post post);
    List<Post> listDtoToEntity(List<PostDto> postDtoList);
    List<PostDto> listEntityToDto(List<Post> posts);
}
