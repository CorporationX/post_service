package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeForCommentDto;
import faang.school.postservice.dto.like.LikeForPostDto;
import faang.school.postservice.model.Like;

public class LikeMapper {
    public static Like likeCreateDtoToLike(LikeForPostDto likeForPostDto) {
        return Like.builder()
                .userId(likeForPostDto.getUserId())
                .build();
    }

    public static LikeForPostDto likeCreateToLikeDto (Like like) {
        return LikeForPostDto.builder()
                .userId(like.getId())
                .build();
    }

    public static LikeDto likeToResponseLikeDto (Like like) {
        return LikeDto.builder()
                .id(like.getId())
                .userId(like.getUserId())
                .build();
    }

    public static Like likeForCommentToLike(LikeForCommentDto likeForCommentDto) {
        return Like.builder()
                .userId(likeForCommentDto.getUserId())
                .build();
    }

}
