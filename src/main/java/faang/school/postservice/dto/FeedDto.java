package faang.school.postservice.dto;

import faang.school.postservice.dto.redis.RedisPostDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedDto {

    private long requesterId;
    private List<RedisPostDto> dtos;
}
