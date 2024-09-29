package faang.school.postservice.service.comment.sort;

import faang.school.postservice.dto.comment.SortingBy;
import faang.school.postservice.dto.comment.SortingOrder;
import faang.school.postservice.model.Comment;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class SortByUpdateAscending extends CommentSortingStrategy {
    public SortByUpdateAscending() {
        super(SortingBy.UPDATED_AT, SortingOrder.ASC);
    }

    @Override
    public List<Comment> apply(@NonNull List<Comment> comments) {
        return comments.stream()
                .sorted(Comparator.comparing(Comment::getUpdatedAt))
                .toList();
    }
}
