package faang.school.postservice.service;

import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;

    @Value("${commenter-banner.comments-count-for-ban}")
    private int unverifiedCommentsCountForBan;

    @Transactional(readOnly = true)
    public List<Long> findAuthorIdsForBan() {
        return commentRepository
                .findAuthorsForBanWithUnverifiedCommentsCount(unverifiedCommentsCountForBan);
    }
}
