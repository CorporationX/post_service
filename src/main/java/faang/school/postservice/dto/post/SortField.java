package faang.school.postservice.dto.post;


public enum SortField {

    PUBLISHED_AT("publishedAt"),
    CREATED_AT("createdAt");
    private String value;

     SortField(String value) {
        this.value = value;
     }

    public String getValue() {
        return value;
    }
}
