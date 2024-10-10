package faang.school.postservice.utils;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static faang.school.postservice.utils.ImageAspectRatio.HORIZONTAL;
import static faang.school.postservice.utils.ImageAspectRatio.SQUARE;
import static faang.school.postservice.utils.ImageAspectRatio.VERTICAL;
import static org.springframework.http.MediaType.IMAGE_GIF_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

public class ImageProcessingUtils {

    public static boolean isNeedResize(MultipartFile image, ImageRestrictionRule rule) throws IOException {
        BufferedImage bufferedImage = convertToBufferedImage(image);
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        ImageAspectRatio imageAspectRatio = defineAspectRatio(width, height);
        Pair<Integer, Integer> restrictions = switch (imageAspectRatio) {
            case SQUARE -> rule.getSquare();
            case HORIZONTAL -> rule.getHorizontal();
            case VERTICAL -> rule.getVertical();
        };

        return width > restrictions.getLeft() || height > restrictions.getRight();
    }

    public static BufferedImage convertToBufferedImage(MultipartFile image) throws IOException {
        return ImageIO.read(image.getInputStream());
    }

    public static BufferedImage resizeBufferedImage(BufferedImage origin, ImageRestrictionRule rule) {
        int width = origin.getWidth();
        int height = origin.getHeight();

        Pair<Integer, Integer> newWidthAndHeight = defineNewWidthAndHeight(width, height, rule);

        return resizeImage(origin, newWidthAndHeight.getLeft(), newWidthAndHeight.getRight());
    }

    public static Pair<Integer, Integer> defineNewWidthAndHeight(int widthInt, int heightInt, ImageRestrictionRule rule) {
        ImageAspectRatio imageAspectRatio = defineAspectRatio(widthInt, heightInt);
        double maxWidth = (double) defineMaxWidth(imageAspectRatio, rule);
        double maxHeight = (double) defineMaxHeight(imageAspectRatio, rule);

        Pair<Integer, Integer> newWidthAndHeight;

        if (imageAspectRatio == SQUARE) {
            newWidthAndHeight = rule.getSquare();
        } else {
            double width = (double) widthInt;
            double height = (double) heightInt;
            double imageProportion = width / height;
            double ruleProportion = maxWidth / maxHeight;

            double scaleToResize;
            if (imageProportion > ruleProportion) {
                scaleToResize = width / maxWidth;
            } else {
                scaleToResize = height / maxHeight;
            }

            int targetWidth = (int) Math.round(width / scaleToResize);
            int targetHeight = (int) Math.round(height / scaleToResize);

            newWidthAndHeight = Pair.of(targetWidth, targetHeight);
        }

        return newWidthAndHeight;
    }

    public static ImageAspectRatio defineAspectRatio(int width, int height) {
        if (width == height) {
            return SQUARE;
        } else if (width > height) {
            return HORIZONTAL;
        } else {
            return VERTICAL;
        }
    }

    private static int defineMaxWidth(ImageAspectRatio imageAspectRatio, ImageRestrictionRule rule) {
        return switch (imageAspectRatio) {
            case SQUARE -> rule.getSquare().getLeft();
            case HORIZONTAL -> rule.getHorizontal().getLeft();
            case VERTICAL -> rule.getVertical().getLeft();
        };
    }

    private static int defineMaxHeight(ImageAspectRatio imageAspectRatio, ImageRestrictionRule rule) {
        return switch (imageAspectRatio) {
            case SQUARE -> rule.getSquare().getRight();
            case HORIZONTAL -> rule.getHorizontal().getRight();
            case VERTICAL -> rule.getVertical().getRight();
        };
    }

    public static BufferedImage resizeImage(BufferedImage originalImage, int newWidth, int newHeight) {
        Image resultingImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(resultingImage, 0, 0, null);
        graphics2D.dispose();
        return resizedImage;
    }

    public static ByteArrayOutputStream getByteArrayOutputStream(BufferedImage bufferedImage, String contentType) throws IOException {
        String imageType = getImageTypeFromContentType(contentType);
        ByteArrayOutputStream temporaryStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, imageType, temporaryStream);
        return temporaryStream;
    }

    public static String getImageTypeFromContentType(String contentType) {
        return contentType.substring(contentType.lastIndexOf("/") + 1);
    }

    public static List<String> getAvailableImageTypes() {
        return List.of(IMAGE_GIF_VALUE, IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE);
    }
}
