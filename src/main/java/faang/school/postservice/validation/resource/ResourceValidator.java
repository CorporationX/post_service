package faang.school.postservice.validation.resource;

import faang.school.postservice.exception.DataValidationException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;

@Component
public class ResourceValidator {

    private Set<String> validMediaTypes = new HashSet<>() {{
        add("mp4");
        add("mp3");
    }};

    public void validateMediaType(MultipartFile mediaFile) {
        if (!validMediaTypes.contains(mediaFile.getContentType())) {
            throw new DataValidationException("Invalid media type: " + mediaFile.getContentType());
        }
    }
}
