package faang.school.postservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AlbumVisibility {
    ONLY_AUTHOR("only_author"),
    ALLOWED_USERS("allowed_users"),
    SUBSCRIBERS("subscribers"),
    ALL("all");

    private String status;
}
