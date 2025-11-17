package faang.school.postservice.dto.user;

import lombok.Builder;

import java.util.List;

@Builder
public record GetUsersDto(
        List<Long> ids
) {
}