package faang.school.postservice.dto.spelling_corrector.text_gears;

public enum TextGearsLang {
    RU,
    OTHER;

    public static TextGearsLang fromString(String lang) {
        for (TextGearsLang val : TextGearsLang.values()) {
            if (val.name().equalsIgnoreCase(lang)) {
                return val;
            }
        }

        return OTHER;
    }
}
