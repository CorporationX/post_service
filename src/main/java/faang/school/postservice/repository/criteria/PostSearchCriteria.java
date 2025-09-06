package faang.school.postservice.repository.criteria;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class PostSearchCriteria {
    private Long authorId;
    private Long projectId;
    private Boolean published;
    private Boolean deleted;
    private SortField sortField;
    private SortDirection sortDirection;

    @Getter
    public enum SortField {
        CREATED_AT("createdAt"),
        PUBLISHED_AT("publishedAt");

        private final String field;

        SortField(String field) {
            this.field = field;
        }
    }

    public enum SortDirection {
        ASC, DESC;
    }

}
