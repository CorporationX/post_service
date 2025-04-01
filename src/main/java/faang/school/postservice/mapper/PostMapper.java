package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    @Mapping(target = "likesCount", ignore = true)
    PostResponseDto toPostResponseDto(Post post);

    default PostResponseDto toDtoWithLikes(Post post, LikeRepository likerepository) {
        PostResponseDto responseDto = toPostResponseDto(post);
        responseDto.setLikesCount(likerepository.countByPostId(post.getId()));
        return responseDto;
    }

    Post toPost(PostRequestDto postRequestDto);

    default List<PostResponseDto> toPostResponseDtoList(List<Post> posts, LikeRepository likerepository) {
        if (posts == null) {
            return null;
        }

        return posts.stream()
                .map(post -> toDtoWithLikes(post, likerepository))
                .collect(Collectors.toList());
    }
}
