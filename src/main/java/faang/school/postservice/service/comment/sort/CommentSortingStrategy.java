package faang.school.postservice.service.comment.sort;

import faang.school.postservice.dto.comment.SortingBy;
import faang.school.postservice.dto.comment.SortingOrder;
import faang.school.postservice.model.Comment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public abstract class CommentSortingStrategy {
    private final SortingBy field;
    private final SortingOrder order;

    public abstract List<Comment> getSortedComments(List<Comment> comments);
}
