package faang.school.postservice.utils;

public class StringUtils {
    public static boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}