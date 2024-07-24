package faang.school.postservice.service.kafkadecomposer.algo.options;

import faang.school.postservice.dto.user.UserFeedDto;

import java.util.List;

public interface AlgoOptions {
    int limit();

    void decompose(List<UserFeedDto> userDtoList, Long postId);
}
