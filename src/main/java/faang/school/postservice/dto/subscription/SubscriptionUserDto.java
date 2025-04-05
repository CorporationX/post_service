package faang.school.postservice.dto.subscription;

import lombok.Builder;

@Builder
public record SubscriptionUserDto(

        Long id,

        String username,

        String email
) {
}
