package faang.school.postservice.service.comment.sort;

import faang.school.postservice.dto.comment.SortingField;
import faang.school.postservice.dto.comment.SortingOrder;
import faang.school.postservice.model.Comment;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class SortByLikesCountAscending extends CommentSortingStrategy {
    public SortByLikesCountAscending() {
        super(SortingField.LIKES_COUNT, SortingOrder.ASC);
    }

    @Override
    public List<Comment> getSortedComments(List<Comment> comments) {
        return comments.stream()
                .sorted(Comparator.comparing(comment -> comment.getLikes().size()))
                .toList();
    }
}
