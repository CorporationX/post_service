package faang.school.postservice.controller;

public final class ApiPath {
    public static final String COMMENT = "/comment";

    public static final String LIKES_PATH = "/likes";
    public static final String POST_LIKES_PATH = "/post/{postId}";
    public static final String COMMENT_LIKES_PATH = "/comment/{commentId}";

    private ApiPath() {}
}
