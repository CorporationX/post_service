package faang.school.postservice.service.s3;

import faang.school.postservice.config.context.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class S3KeyGenerator {

    private static final String ORIGINAL_IMAGE_KEY_PATTERN = "user_%s/originals/%s-%s-%s";
    private static final String PREVIEW_IMAGE_KEY_PATTERN  = "user_%s/previews/%s-%s-%s";
    private static final String DEFAULT_USER_ID = "default";

    private static final Pattern INVALID_FILENAME_CHARS = Pattern.compile("[^\\w\\dа-яА-ЯёЁ._\\- ]");

    private final UserContext userContext;

    public String generateImageKey(String originalFileName) {
        return generateKey(originalFileName, ORIGINAL_IMAGE_KEY_PATTERN);
    }

    public String generatePreviewKey(String originalFileName) {
        return generateKey(originalFileName, PREVIEW_IMAGE_KEY_PATTERN);
    }

    private String generateKey(String originalFileName, String pattern) {
        String userPart = resolveUserIdPath();
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String uniqueId = UUID.randomUUID().toString();
        String sanitizedFileName = sanitize(originalFileName);

        return String.format(pattern, userPart, timeStamp, uniqueId, sanitizedFileName);
    }

    private String resolveUserIdPath() {
        return userContext != null && userContext.getUserId() > 0
                ? String.valueOf(userContext.getUserId())
                : DEFAULT_USER_ID;
    }

    private String sanitize(String fileName) {
        return INVALID_FILENAME_CHARS.matcher(fileName).replaceAll("_");
    }
}
