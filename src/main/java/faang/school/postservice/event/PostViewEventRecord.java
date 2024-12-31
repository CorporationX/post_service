package faang.school.postservice.event;

import lombok.Builder;

@Builder
public record PostViewEventRecord(Long postId) {}