package faang.school.postservice.service.resource;

import faang.school.postservice.exception.FileException;
import faang.school.postservice.model.ResourceType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MimeConverter {

    private final Map <String, ResourceType> typeByMime = new HashMap<>();

    public MimeConverter(List<String> image, List<String> audio, List<String> video) {
        image.forEach(mime -> typeByMime.put(mime, ResourceType.IMAGE));
        audio.forEach(mime -> typeByMime.put(mime, ResourceType.AUDIO));
        video.forEach(mime -> typeByMime.put(mime, ResourceType.VIDEO));
    }

    public ResourceType getType(String mime) {
        if (!typeByMime.containsKey(mime)) {
            throw new FileException("Unsupported file format");
        }
        return typeByMime.get(mime);
    }
}
