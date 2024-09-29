package faang.school.postservice.service.comment.sort;

import faang.school.postservice.dto.comment.SortingBy;
import faang.school.postservice.dto.comment.SortingOrder;
import faang.school.postservice.model.Comment;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class SortByUpdateDescending extends CommentSortingStrategy {
    public SortByUpdateDescending() {
        super(SortingBy.UPDATED_AT, SortingOrder.DESC);
    }

    @Override
    public List<Comment> getSortedComments(@NonNull List<Comment> comments) {
        return comments.stream()
                .sorted(Comparator.comparing(Comment::getUpdatedAt, Comparator.reverseOrder()))
                .toList();
    }
}
