package faang.school.postservice.service.comment;

public enum CommentServiceErrors {
    COMMENT_IS_EMPTY("Комментарий не может быть пустым"),
    COMMENT_TOO_LONG("Длинна комментария не может быть больше 4096 символов"),
    USER_NOT_FOUND("Пользователь не найден в базе данных"),
    POST_NOT_FOUND("Пост не найден в системе"),
    COMMENT_NOT_FOUND("Комментарий не найден"),
    CHANGE_NOT_COMMENT("Можно изменить только текст комментария");

    final String value;

    CommentServiceErrors(String value) {
        this.value = value;
    }
}
