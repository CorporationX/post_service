package faang.school.postservice.dto.posts;

import lombok.Builder;

@Builder
public record PostResultResponse(long id, long likeCount) {}
