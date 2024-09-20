package faang.school.postservice.dto.post;

import faang.school.postservice.dto.like.LikeDto;

import java.util.List;

public record PostDto(long id, Long authorId, List<LikeDto> likes) {
}
