package io.github.lionheartlattice.util;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Random;

public class CaptchaImageUtil {

    private static final int SLIDER_WIDTH = 50;
    private static final int SLIDER_HEIGHT = 50;
    private static final int CIRCLE_R = 5;

    @Data
    @Accessors(chain = true)
    public static class CaptchaImage {
        private String backgroundImage;
        private String sliderImage;
        private int x;
        private int y;
    }

    public static CaptchaImage generate(InputStream imageStream) throws IOException {
        BufferedImage originalImage = ImageIO.read(imageStream);
        // Resize if too big? Or assume reasonable size.
        // Let's resize to a standard width if needed, e.g., 300px width.
        // But for now, let's use original.

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // Random position
        Random random = new Random();
        int x = random.nextInt(width - SLIDER_WIDTH - 20) + 10; // Margin
        int y = random.nextInt(height - SLIDER_HEIGHT - 20) + 10;

        // Create slider image (transparent)
        BufferedImage sliderImage = new BufferedImage(SLIDER_WIDTH, SLIDER_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D sliderG = sliderImage.createGraphics();
        sliderG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Create background image copy to draw hole
        BufferedImage backgroundImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D backgroundG = backgroundImage.createGraphics();
        backgroundG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        backgroundG.drawImage(originalImage, 0, 0, null);

        // Define the jigsaw shape
        GeneralPath path = getPath();

        // 1. Draw the part of original image into slider
        // We need to clip the slider graphics to the path
        sliderG.setClip(path);
        // Draw the original image at offset -x, -y
        sliderG.drawImage(originalImage, -x, -y, null);

        // Draw border on slider
        sliderG.setColor(Color.WHITE);
        sliderG.setStroke(new BasicStroke(2));
        sliderG.draw(path);

        // 2. Draw the hole on the background
        // We want to make the hole semi-transparent or blurred.
        // Simple way: fill with semi-transparent gray
        backgroundG.translate(x, y); // Move to position
        backgroundG.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.6f));
        backgroundG.setColor(Color.GRAY);
        backgroundG.fill(path);

        // Draw border on hole
        backgroundG.setColor(Color.WHITE);
        backgroundG.setStroke(new BasicStroke(2));
        backgroundG.draw(path);

        sliderG.dispose();
        backgroundG.dispose();

        return new CaptchaImage()
                .setBackgroundImage(toBase64(backgroundImage, "jpg"))
                .setSliderImage(toBase64(sliderImage, "png"))
                .setX(x)
                .setY(y);
    }

    private static GeneralPath getPath() {
        GeneralPath path = new GeneralPath();
        float w = SLIDER_WIDTH;
        float h = SLIDER_HEIGHT;
        float r = CIRCLE_R;

        path.moveTo(0, 0);

        // Top: bump out
        path.lineTo(w / 2 - r, 0);
        path.quadTo(w / 2, -r * 2, w / 2 + r, 0);
        path.lineTo(w, 0);

        // Right: bump in
        path.lineTo(w, h / 2 - r);
        path.quadTo(w - r * 2, h / 2, w, h / 2 + r);
        path.lineTo(w, h);

        // Bottom: bump out
        path.lineTo(w / 2 + r, h);
        path.quadTo(w / 2, h + r * 2, w / 2 - r, h);
        path.lineTo(0, h);

        // Left: straight (or bump in)
        path.lineTo(0, 0);

        path.closePath();
        return path;
    }

    private static String toBase64(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, format, os);
        return "data:image/" + format + ";base64," + Base64.getEncoder().encodeToString(os.toByteArray());
    }
}

