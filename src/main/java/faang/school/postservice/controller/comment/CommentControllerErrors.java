package faang.school.postservice.controller.comment;

public enum CommentControllerErrors {
    POST_ID_NULL("ИД поста не может быть пустым"),
    POST_ID_ZERO("ИД поста не может иметь значение 0"),
    COMMENT_DTO_NULL("Дто не может быть пустым");

    public final String value;

    CommentControllerErrors(String value) {
        this.value = value;
    }
}
