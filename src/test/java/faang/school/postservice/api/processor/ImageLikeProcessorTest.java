package faang.school.postservice.api.processor;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ImageLikeProcessorTest {
//    private ImageLikeProcessor processor;
//
//    private final int MAX_WIDTH_LANDSCAPE = 1080;
//    private final int MAX_HEIGHT_LANDSCAPE = 566;
//    private final int MAX_DIMENSION_SQUARE = 1080;
//
//    @BeforeEach
//    void setUp() {
//        processor = new ImageLikeProcessor(MAX_WIDTH_LANDSCAPE, MAX_HEIGHT_LANDSCAPE, MAX_DIMENSION_SQUARE);
//    }
//
//    private MockMultipartFile createImageFile(int width, int height, String format) throws IOException {
//        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//        Graphics2D g = image.createGraphics();
//        g.setColor(Color.RED);
//        g.fillRect(0, 0, width, height);
//        g.dispose();
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ImageIO.write(image, format, baos);
//        return new MockMultipartFile("image", "testImage." + format, "image/" + format, baos.toByteArray());
//    }
//
//    private MockMultipartFile createVideoFile(int width, int height, int numFrames, String format) throws FrameRecorder.Exception, IOException {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        try (
//                FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(baos, width, height);
//                Java2DFrameConverter converter = new Java2DFrameConverter()
//        ) {
//            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
//            recorder.setFormat(format);
//            recorder.setFrameRate(1);
//            recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
//            recorder.start();
//
//            for (int i = 0; i < numFrames; i++) {
//                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//                Graphics2D g = image.createGraphics();
//                g.setColor(new Color(255, 0, 0));
//                g.fillRect(0, 0, width, height);
//                g.setColor(Color.WHITE);
//                g.drawString("Frame " + (i + 1), width / 2 - 30, height / 2);
//                g.dispose();
//
//                Frame frame = converter.convert(image);
//                recorder.record(frame);
//            }
//        }
//        return new MockMultipartFile("video", "testVideo." + format, "video/" + format, baos.toByteArray());
//    }
//
//    private void assertImageDimensions(MultipartFile processedFile, int expectedWidth, int expectedHeight) throws IOException {
//        try (var inputStream = processedFile.getInputStream()) {
//            BufferedImage processedImage = ImageIO.read(inputStream);
//            assertEquals(expectedWidth, processedImage.getWidth());
//            assertEquals(expectedHeight, processedImage.getHeight());
//        }
//    }
//
//    private void assertVideoDimensions(MultipartFile processedFile, int expectedWidth, int expectedHeight, int numFrames) throws IOException {
//        try (
//                FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(processedFile.getInputStream());
//                Java2DFrameConverter converter = new Java2DFrameConverter()
//        ) {
//            grabber.start();
//            for (int i = 0; i < numFrames; i++) {
//                org.bytedeco.javacv.Frame frame = grabber.grabImage();
//                BufferedImage image = converter.convert(frame);
//                assertEquals(expectedWidth, image.getWidth());
//                assertEquals(expectedHeight, image.getHeight());
//            }
//            grabber.stop();
//        }
//    }
//
//    @Test
//    void testImageCompressionNeededLandscape() throws IOException {
//        MockMultipartFile image = createImageFile(2000, 1000, "jpg");
//
//        MultipartFile processedFile = processor.process(image);
//
//        // Проверяем изменение размеров
//        assertImageDimensions(processedFile, MAX_WIDTH_LANDSCAPE, MAX_HEIGHT_LANDSCAPE);
//    }
//
//    @Test
//    void testImageCompressionNotNeededLandscape() throws IOException {
//        MockMultipartFile image = createImageFile(1080, 500, "jpg");
//
//        MultipartFile processedFile = processor.process(image);
//
//        // Проверяем, что размеры не изменились
//        assertImageDimensions(processedFile, 1080, 500);
//    }
//
//    @Test
//    void testImageCompressionNeededSquare() throws IOException {
//        MockMultipartFile image = createImageFile(2000, 2000, "jpg");
//
//        MultipartFile processedFile = processor.process(image);
//
//        // Проверяем изменение размеров
//        assertImageDimensions(processedFile, MAX_DIMENSION_SQUARE, MAX_DIMENSION_SQUARE);
//    }
//
//    @Test
//    void testImageCompressionNotNeededSquare() throws IOException {
//        MockMultipartFile image = createImageFile(1000, 1000, "jpg");
//
//        MultipartFile processedFile = processor.process(image);
//
//        // Проверяем, что размеры не изменились
//        assertImageDimensions(processedFile, 1000, 1000);
//    }
//
//    @Test
//    void testVideoCompressionNeededLandscape() throws IOException, FrameGrabber.Exception {
//        MockMultipartFile video = createVideoFile(2000, 1000, 3, "mp4");
//
//        MultipartFile processedFile = processor.process(video);
//
//        // Проверяем каждый кадр видео
//        assertVideoDimensions(processedFile, MAX_WIDTH_LANDSCAPE, MAX_HEIGHT_LANDSCAPE, 3);
//    }
//
//    @Test
//    void testVideoCompressionNotNeededLandscape() throws IOException, FrameGrabber.Exception {
//        MockMultipartFile video = createVideoFile(1080, 500, 3, "mp4");
//
//        MultipartFile processedFile = processor.process(video);
//
//        // Проверяем, что размеры не изменились для каждого кадра
//        assertVideoDimensions(processedFile, 1080, 500, 3);
//    }
}

