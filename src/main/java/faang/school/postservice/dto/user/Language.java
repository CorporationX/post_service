package faang.school.postservice.dto.user;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Language {
    EN("en"),
    RU("ru"),
    FR("fr"),
    DE("de");

    private final String tag;
}
