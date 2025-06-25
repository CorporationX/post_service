package faang.school.postservice.service;

import faang.school.postservice.cash.NewsFeed;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.repository.NewsFeedCashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsFeedService {
    private final NewsFeedCashRepository newsFeedCashRepository;

    public NewsFeed getNewsFeed(Long userId) {
        Optional<NewsFeed> optional = newsFeedCashRepository.findById(userId);
        if(optional.isPresent()) {
            return optional.get();
        } else {
            throw new NotFoundException("User with id=" + userId + " is not found");
        }
    }
}
