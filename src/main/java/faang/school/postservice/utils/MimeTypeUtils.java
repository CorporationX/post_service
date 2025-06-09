package faang.school.postservice.utils;

public class MimeTypeUtils {
    public static String getExtensionByContentType(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png"  -> ".png";
            case "image/gif"  -> ".gif";
            case "image/webp" -> ".webp";
            default -> "";
        };
    }
}
