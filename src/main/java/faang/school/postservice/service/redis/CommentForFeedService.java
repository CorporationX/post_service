package faang.school.postservice.service.redis;

import faang.school.postservice.dto.comment.CommentFeedDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentForFeedService {

    private final CommentService commentService;
    private final UserForFeedService userForFeedService;
    private final CommentMapper commentMapper;

    public List<CommentFeedDto> getLastCommentFeedDtos(List<Long> lastCommentIds){
        List<Comment> lastComments = commentService.getAllByIds(lastCommentIds);
        List <CommentFeedDto> commentDtosForFeed = new ArrayList<>();
                lastComments.forEach(
                comment -> {
                    CommentFeedDto commentDtoForFeed = commentMapper.toCommentFeedDto(comment);
                    Long authorId = comment.getAuthorId();
                    String authorName = userForFeedService.getUserName(authorId);
                    commentDtoForFeed.setAuthorName(authorName);
                    commentDtosForFeed.add(commentDtoForFeed);
        });
        return commentDtosForFeed;
    }
}
