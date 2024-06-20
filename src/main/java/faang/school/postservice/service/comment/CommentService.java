package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.ChangeCommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.moderator.comment.logic.CommentModerator;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.threadpool.ThreadPoolForCommentModerator;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final CommentValidator commentValidator;
    private final ThreadPoolForCommentModerator threadPoolForCommentModerator;
    private final CommentModerator commentModerator;
    @Value("${postServiceThreadPool.poolComment}")
    @Setter
    private int pullNumbers;


    public void moderateComment() {
        List<Comment> comments = commentRepository.findUnVerifiedComments();
        log.debug("Received comments from DB: {}", comments);

        if (!comments.isEmpty()) {
            int stepLength = (int) Math.floor((double) comments.size() / pullNumbers);
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (int i = 0; i < pullNumbers; i++) {
                int start = i * stepLength;
                int end = (i + 1) * stepLength;

                if (i == pullNumbers - 1) {
                    end = comments.size();
                }

                int finalStart = start;
                int finalEnd = end;
                CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
                                commentModerator.moderateComment(comments.subList(finalStart, finalEnd)),
                        threadPoolForCommentModerator.taskExecutor()
                );

                futures.add(future);
            }

            try {
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(1, TimeUnit.MINUTES);
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error during comment moderation", e);
            } catch (TimeoutException e) {
                futures.forEach(future -> future.cancel(true));
                log.warn("Execution time exceeded, threads were forcibly closed.");
            }
        }
    }

    @Transactional
    public CreateCommentDto createComment(CreateCommentDto createCommentDto) {
        Comment comment = commentMapper.toEntity(createCommentDto);
        Comment commentSaved = commentRepository.save(comment);
        log.debug("Comment saved in db. Comment: {}", commentSaved);

        return commentMapper.toDto(commentSaved);
    }

    @Transactional
    public CreateCommentDto changeComment(ChangeCommentDto changeCommentDto) {
        Comment commentFromDB = commentRepository.findById(changeCommentDto.getId())
                .orElseThrow(() -> {
                    log.error("couldn't find a comment by id: {}", changeCommentDto.getId());
                    return new DataValidationException("couldn't find a comment by id: " + changeCommentDto.getId());
                });

        log.debug("Received comment for modification: {}", commentFromDB);

        commentFromDB.setContent(changeCommentDto.getContent());

        return commentMapper.toDto(commentFromDB);
    }

    @Transactional(readOnly = true)
    public List<CreateCommentDto> getAllCommentsOnPostId(long id) {
        commentValidator.getAllCommentsOnPostIdService(id);

        List<Comment> comments = commentRepository.findAllByPostIdOrderByCreatedAtDesc(id);
        log.debug("Received list of comments for post with id: {} ; CommentList: {}", id, comments);
        return comments.stream().map(commentMapper::toDto).toList();
    }

    @Transactional
    public void deleteComment(long id) {
        commentRepository.deleteById(id);
    }

    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
    }
}
