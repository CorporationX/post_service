package faang.school.postservice.service.media;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.tika.Tika;

import javax.imageio.ImageIO;

public class ImageProcessor {
    public static final long MAX_SIZE_BYTES = 5L * 1024 * 1024; // 5 MB
    private static final int MAX_W_HORIZONTAL = 1080, MAX_H_HORIZONTAL = 566;
    private static final int MAX_W_SQUARE = 1080, MAX_H_SQUARE = 1080;
    private static final Tika TIKA = new Tika();

    public record Processed(byte[] bytes, String contentType, long size, String format) {}

    public static Processed validateAndResize(byte[] input) throws IOException {
        if (input.length > MAX_SIZE_BYTES) {
            throw new IllegalArgumentException("File too large (>5MB)");
        }

        String mime = TIKA.detect(input);
        if (!"image/jpeg".equalsIgnoreCase(mime) && !"image/png".equalsIgnoreCase(mime)) {
            throw new IllegalArgumentException("Only JPEG/PNG allowed");
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(input)) {
            BufferedImage img = ImageIO.read(bais);
            if (img == null) {
                throw new IllegalArgumentException("Invalid image");
            }

            int w = img.getWidth(), h = img.getHeight();
            boolean isSquare = Math.abs(w - h) / (double) Math.max(w, h) <= 0.05;

            int maxW = isSquare ? MAX_W_SQUARE : MAX_W_HORIZONTAL;
            int maxH = isSquare ? MAX_H_SQUARE : MAX_H_HORIZONTAL;

            boolean needsResize = w > maxW || h > maxH;

            String format = "image/png".equals(mime) ? "png" : "jpg";
            byte[] out;

            if (needsResize) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Thumbnails.of(img)
                        .size(maxW, maxH)
                        .outputFormat(format)
                        .outputQuality(0.9f)
                        .useExifOrientation(true)
                        .toOutputStream(baos);
                out = baos.toByteArray();
            } else {
                out = input;
            }
            return new Processed(out, mime, out.length, format);
        }
    }
}

