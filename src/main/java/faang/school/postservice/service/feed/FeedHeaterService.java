package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedHeaterService {
    private final UserServiceClient userServiceClient;
    private final FeedEventService feedEventService;
    private final CacheService cacheService;
    private final FeedHeaterTransactionalService feedHeaterTransactionalService;

    @Value("${feed-heater.pageSize}")
    private int pageSize;

    public void startCacheWarmup() {
        log.info("Starting cache warmup...");

        List<Long> allUserIds = userServiceClient.findAllUserIds();

        int pageNumber = 0;
        boolean hasMorePages = true;

        while (hasMorePages) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            List<PostDto> postDtos = feedHeaterTransactionalService.processPosts(allUserIds, pageable);

            if (postDtos.isEmpty()) {
                break;
            }

            Map<Long, List<CommentDto>> commentsMap = feedHeaterTransactionalService.processComments(postDtos);

            Set<Long> userIds = collectUserIds(postDtos, commentsMap);

            cacheUsers(userIds);

            sendFeedPostEvents(postDtos);

            hasMorePages = postDtos.size() == pageSize;
            pageNumber++;
        }

        log.info("Cache warmup completed.");
    }

    private Set<Long> collectUserIds(List<PostDto> postDtos, Map<Long, List<CommentDto>> commentsMap) {
        Set<Long> userIds = postDtos.stream()
                .map(PostDto::getAuthorId)
                .collect(Collectors.toSet());

        commentsMap.values().stream()
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .map(CommentDto::getAuthorId)
                .forEach(userIds::add);

        return userIds;
    }

    private void cacheUsers(Set<Long> userIds) {
        List<UserDto> userDtos = userServiceClient.getUsersByIds(new ArrayList<>(userIds));
        cacheService.saveUsers(userDtos);
    }

    private void sendFeedPostEvents(List<PostDto> postDtos) {
        postDtos.forEach(postDto -> feedEventService.createAndSendFeedPostEventForFeedHeater(
                postDto.getId(),
                postDto.getAuthorId(),
                postDto.getPublishedAt()));
    }
}
