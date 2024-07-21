package faang.school.postservice.model;

public enum VisibilityType {
    All_USER("AllUser"),
    ONLY_SUBSCRIBERS("OnlySubscribers"),
    ONLY_USERS_SELECTED("OnlyUsersSelected"),
    ONLY_AUTHOR("OnlyAuthor");
    private final String visibility;

    VisibilityType(String visibility) {
        this.visibility = visibility;
    }

    public String getMessage() {
        return visibility;
    }
}