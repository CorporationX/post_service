package faang.school.postservice.util;

import faang.school.postservice.dto.resource.PostResourceDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;

public class PostResourceDtoComparator implements Comparator<PostResourceDto> {
    @Override
    public int compare(PostResourceDto dto1, PostResourceDto dto2) {
        if (!dto1.getId().equals(dto2.getId())) return dto1.getId().compareTo(dto2.getId());
        if (!dto1.getName().equals(dto2.getName())) return dto1.getName().compareTo(dto2.getName());
        if (!dto1.getType().equals(dto2.getType())) return dto1.getType().compareTo(dto2.getType());
        if (dto1.getSize() != dto2.getSize()) return Long.compare(dto1.getSize(), dto2.getSize());
        try {
            return inputStreamEquals(dto1.getResource(), dto2.getResource()) ? 0 : 1;
        } catch (IOException e) {
            throw new RuntimeException("Error comparing InputStreams", e);
        }
    }

    private boolean inputStreamEquals(InputStream is1, InputStream is2) throws IOException {
        try {
            if (is1 == is2) return true;
            if (is1 == null || is2 == null) return false;
            int ch;
            while ((ch = is1.read()) != -1) {
                if (ch != is2.read()) return false;
            }
            return is2.read() == -1;
        } finally {
            if (is1 != null) is1.close();
            if (is2 != null) is2.close();
        }
    }
}
