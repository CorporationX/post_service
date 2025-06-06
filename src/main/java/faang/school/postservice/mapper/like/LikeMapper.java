package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Like;

import java.util.List;
import java.util.stream.Collectors;

public class LikeMapper {

    public static LikeDto likeToResponseLikeDto (Like like) {
        return LikeDto.builder()
                .id(like.getId())
                .userId(like.getUserId())
                .build();
    }

    public static List<LikeDto> likeListToResponseLikeDto(List<Like> likes) {
        return likes.stream()
                .map(LikeMapper::likeToResponseLikeDto)
                .collect(Collectors.toList());
    }

}
