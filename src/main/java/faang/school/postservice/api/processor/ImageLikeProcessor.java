package faang.school.postservice.api.processor;

import faang.school.postservice.exception.resource.ResourceProcessingException;
import faang.school.postservice.exception.resource.UnsupportedResource;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

@Slf4j
//@Component  // temp unused
public class ImageLikeProcessor implements MultipartFileProcessor {
    private final int MAX_WIDTH_LANDSCAPE;
    private final int MAX_HEIGHT_LANDSCAPE;
    private final int MAX_DIMENSION_SQUARE;

    public ImageLikeProcessor(
            int maxWidthLandscape,
            int maxHeightLandscape,
            int maxDimensionSquare
    ) {
        MAX_WIDTH_LANDSCAPE = maxWidthLandscape;
        MAX_HEIGHT_LANDSCAPE = maxHeightLandscape;
        MAX_DIMENSION_SQUARE = maxDimensionSquare;
    }

    @Override
    public MultipartFile process(MultipartFile resource) {

        Optional<String> mimeType = getMimeType(resource);

        if (mimeType.isEmpty()) {
            throw new UnsupportedResource(
                    resource.getName(),
                    resource.getOriginalFilename()
            );
        }

        ByteArrayOutputStream outputStream;
        try {
            if (mimeType.get().startsWith("image")) {
                outputStream = processImage(resource);
            } else if (mimeType.get().startsWith("video")) {
                outputStream = processVideo(resource);
            } else {
                throw new IllegalArgumentException("Unsupported file type: " + mimeType);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ResourceProcessingException(
                    resource.getName(),
                    resource.getOriginalFilename()
            );
        }

        return new MockMultipartFile(
                resource.getName(),
                resource.getOriginalFilename(),
                resource.getContentType(),
                outputStream.toByteArray()
        );
    }

    private Optional<String> getMimeType(MultipartFile file) {
        String mimeType = file.getContentType();
        return mimeType != null ? Optional.of(mimeType) : Optional.empty();
    }

    @Override
    public boolean canBeProcessed(String mimeType, MultipartFile resource) {
        // mb need more complex check
        var type = mimeType;
        if (type == null) {
            var t = getMimeType(resource);
            if (t.isPresent()) {
                type = t.get();
            } else {
                return false;
            }
        }
        return type.startsWith("image") || type.startsWith("video");
    }

    private ByteArrayOutputStream processImage(MultipartFile resource) throws IOException {
        BufferedImage image = ImageIO.read(resource.getInputStream());
        BufferedImage processedImage = resizeIfNeeded(image);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(processedImage, getFormatName(resource), outputStream);
        return outputStream;
    }

    private ByteArrayOutputStream processVideo(MultipartFile resource) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (
                FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(resource.getInputStream());
                FFmpegFrameRecorder recorder = getFFmpegFrameRecorder(outputStream, grabber);
                Java2DFrameConverter converter = new Java2DFrameConverter()
        ) {
            grabber.start();
            recorder.setVideoCodec(grabber.getVideoCodec());
            recorder.setFormat(grabber.getFormat());
            recorder.setFrameRate(grabber.getFrameRate());

            Frame frame;
            while ((frame = grabber.grabFrame()) != null) {
                if (frame.image != null) {
                    BufferedImage image = converter.getBufferedImage(frame);
                    BufferedImage resizedImage = resizeIfNeeded(image);
                    recorder.record(converter.getFrame(resizedImage, Frame.DEPTH_UBYTE));
                } else {
                    recorder.record(frame);
                }
            }
        }

        return outputStream;
    }

    private FFmpegFrameRecorder getFFmpegFrameRecorder(ByteArrayOutputStream outputStream, FFmpegFrameGrabber grabber) {
        return new FFmpegFrameRecorder(
                outputStream,
                grabber.getImageWidth(),
                grabber.getImageHeight(),
                grabber.getAudioChannels()
        );
    }

    private BufferedImage resizeIfNeeded(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (isLandscape(width, height) && (isTooWideForLandscape(width) || isTooTallForLandscape(height))) {
            return resizeToLandscape(image, width, height);
        } else if (isSquareOrPortrait(width, height) && isTooLargeForSquare(width, height)) {
            return resizeToSquare(image);
        }

        return image;
    }

    private boolean isLandscape(int width, int height) {
        return width > height;
    }

    private boolean isTooWideForLandscape(int width) {
        return width > MAX_WIDTH_LANDSCAPE;
    }

    private boolean isTooTallForLandscape(int height) {
        return height > MAX_HEIGHT_LANDSCAPE;
    }

    private boolean isSquareOrPortrait(int width, int height) {
        return width <= height;
    }

    private boolean isTooLargeForSquare(int width, int height) {
        return width > MAX_DIMENSION_SQUARE || height > MAX_DIMENSION_SQUARE;
    }

    private BufferedImage resizeToLandscape(BufferedImage image, int width, int height) {
        int newWidth = MAX_WIDTH_LANDSCAPE;
        int newHeight = (MAX_WIDTH_LANDSCAPE * height) / width;

        // Ensure the new height does not exceed the maximum allowed for landscape
        if (newHeight > MAX_HEIGHT_LANDSCAPE) {
            newHeight = MAX_HEIGHT_LANDSCAPE;
            newWidth = (MAX_HEIGHT_LANDSCAPE * width) / height;
        }

        return resizeImage(image, newWidth, newHeight);
    }

    private BufferedImage resizeToSquare(BufferedImage image) {
        int newSize = MAX_DIMENSION_SQUARE;
        return resizeImage(image, newSize, newSize);
    }

    private BufferedImage resizeImage(BufferedImage image, int newWidth, int newHeight) {
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, image.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, newWidth, newHeight, null);
        g.dispose();
        return resizedImage;
    }

    private String getFormatName(MultipartFile resource) {
        String originalFilename = resource.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
        } else {
            throw new IllegalArgumentException("Invalid file name or extension: " + originalFilename);
        }
    }
}

