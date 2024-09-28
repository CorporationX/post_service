package faang.school.postservice.service.redis;

import faang.school.postservice.comparator.CommentDtoComparator;
import faang.school.postservice.dto.comment.CommentFeedDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class CommentForFeedService {

    private final CommentService commentService;
    private final UserForFeedService userForFeedService;
    private final CommentMapper commentMapper;

    public TreeSet<CommentFeedDto> getLastCommentFeedDtos(TreeSet<Long> lastCommentIds){
        List<Comment> lastCommentsList = commentService.getAllByIds(lastCommentIds);
        TreeSet <CommentFeedDto> commentDtosForFeed = new TreeSet<>(new CommentDtoComparator());
                lastCommentsList.forEach(
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
