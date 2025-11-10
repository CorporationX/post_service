package faang.school.postservice.service.moderate;

import faang.school.postservice.job.moderator.ModerationDictionary;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ModerateComments {

    private final CommentRepository commentRepository;
    private final ModerationDictionary moderationDictionary;


    @Transactional
    public void moderateNewComments() {
        List<Comment> comments = commentRepository.findCommentByVerfiedDateNull();
        comments.forEach(moderationDictionary::verifyAndEditComment);
        commentRepository.saveAll(comments);
        log.info("comments have been checked. Size - {}", comments.size());
    }
}
