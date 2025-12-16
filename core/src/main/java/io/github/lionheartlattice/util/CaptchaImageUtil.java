package io.github.lionheartlattice.util;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 滑块验证码图片生成工具类
 * 基于拼图模板算法生成滑块和背景图
 */
public class CaptchaImageUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final int SLIDER_WIDTH = 50;
    private static final int SLIDER_HEIGHT = 50;
    private static final int SMALL_CIRCLE = 10;
    private static final int SMALL_CIRCLE_R_1 = 2;

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

        // Resize image to 310px width, maintaining aspect ratio
        int targetWidth = 310;
        BufferedImage bigImage = resizeImage(originalImage, targetWidth);

        int width = bigImage.getWidth();
        int height = bigImage.getHeight();

        // Generate random position with margin
        int x = generateRandomX(width, SLIDER_WIDTH);
        int y = generateRandomY(height, SLIDER_HEIGHT);

        // Generate slider template data
        int[][] slideTemplateData = getSlideTemplateData();

        // Create slider image (transparent background)
        BufferedImage sliderImage = new BufferedImage(SLIDER_WIDTH, SLIDER_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);

        // Cut by template
        cutByTemplate(bigImage, sliderImage, slideTemplateData, x, y);

        return new CaptchaImage()
                .setBackgroundImage(toBase64(bigImage, "jpg"))
                .setSliderImage(toBase64(sliderImage, "png"))
                .setX(x)
                .setY(y);
    }

    /**
     * 生成随机 X 坐标
     */
    private static int generateRandomX(int width, int sliderWidth) {
        int widthDifference = width - sliderWidth;
        if (widthDifference <= 0) {
            return 5;
        }
        return RANDOM.nextInt(widthDifference - 100) + 100 + RANDOM.nextInt(20) - 10;
    }

    /**
     * 生成随机 Y 坐标
     */
    private static int generateRandomY(int height, int sliderHeight) {
        int heightDifference = height - sliderHeight;
        if (heightDifference <= 0) {
            return 5;
        }
        return RANDOM.nextInt(heightDifference) + 5 + RANDOM.nextInt(20) - 10;
    }

    /**
     * 生成滑块模板数据（1表示需要裁剪的区域，0表示透明区域）
     */
    private static int[][] getSlideTemplateData() {
        int[][] data = new int[SLIDER_WIDTH][SLIDER_HEIGHT];

        // 计算常量
        double xBlank = (double) SLIDER_WIDTH - SMALL_CIRCLE - SMALL_CIRCLE_R_1;
        double yBlank = (double) SLIDER_HEIGHT - SMALL_CIRCLE - SMALL_CIRCLE_R_1;
        double rxa = xBlank / 2.0;
        double ryb = (double) SLIDER_HEIGHT - SMALL_CIRCLE;
        double rPow = Math.pow(SMALL_CIRCLE, 2);

        for (int i = 0; i < SLIDER_WIDTH; i++) {
            for (int j = 0; j < SLIDER_HEIGHT; j++) {
                // 计算三个圆形区域
                double topR = Math.pow(i - rxa, 2) + Math.pow(j - 2.0, 2);
                double downR = Math.pow(i - rxa, 2) + Math.pow(j - ryb, 2);
                double rightR = Math.pow(i - ryb, 2) + Math.pow(j - rxa, 2);

                // 上方凸出，下方凹入，右侧凹入
                if ((j <= yBlank && topR <= rPow) ||
                    (j >= yBlank && downR >= rPow) ||
                    (i >= xBlank && rightR >= rPow)) {
                    data[i][j] = 0; // 透明区域
                } else {
                    data[i][j] = 1; // 裁剪区域
                }
            }
        }
        return data;
    }

    /**
     * 按模板裁剪图片
     */
    private static void cutByTemplate(BufferedImage bigImage, BufferedImage smallImage,
                                     int[][] slideTemplateData, int x, int y) {
        int[][] matrix = new int[3][3];
        int[] values = new int[9];
        int yBlank = SLIDER_HEIGHT - SMALL_CIRCLE - SMALL_CIRCLE_R_1;

        Graphics2D g2dBig = bigImage.createGraphics();
        Graphics2D g2dSmall = smallImage.createGraphics();
        g2dBig.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2dSmall.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 第一遍：裁剪图片并填充背景
        for (int i = 0; i < SLIDER_WIDTH; i++) {
            for (int j = 0; j < SLIDER_HEIGHT; j++) {
                int bgX = x + i;
                int bgY = y + j;

                // 边界检查
                if (bgX >= bigImage.getWidth() || bgY >= bigImage.getHeight() || bgX < 0 || bgY < 0) {
                    continue;
                }

                int rgbOri = bigImage.getRGB(bgX, bgY);
                int rgb = slideTemplateData[i][j];

                if (rgb == 1) {
                    // 裁剪到滑块图片
                    smallImage.setRGB(i, j, rgbOri);

                    // 使用周围像素平均值填充背景（更自然的镂空效果）
                    readPixel(bigImage, bgX, bgY, values);
                    fillMatrix(matrix, values);
                    bigImage.setRGB(bgX, bgY, avgMatrix(matrix));

                    // 左边缘白色描边
                    if (j < yBlank) {
                        bigImage.setRGB(x, bgY, Color.WHITE.getRGB());
                        smallImage.setRGB(0, j, Color.WHITE.getRGB());
                    }
                } else {
                    // 透明区域
                    smallImage.setRGB(i, j, rgbOri & 0x00ffffff);
                }
            }
        }

        // 第二遍：增强轮廓（白色边框）
        for (int i = 0; i < SLIDER_WIDTH; i++) {
            for (int j = 0; j < SLIDER_HEIGHT; j++) {
                int bgX = x + i;
                int bgY = y + j;

                if (bgX >= bigImage.getWidth() || bgY >= bigImage.getHeight() || bgX < 0 || bgY < 0) {
                    continue;
                }

                if (slideTemplateData[i][j] == 0) {
                    // 检查相邻像素，如果是裁剪区域则绘制白色边框
                    if (isNearCutArea(slideTemplateData, i, j)) {
                        bigImage.setRGB(bgX, bgY, Color.WHITE.getRGB());
                        smallImage.setRGB(i, j, Color.WHITE.getRGB());
                    }
                }
            }
        }

        g2dBig.dispose();
        g2dSmall.dispose();
    }

    /**
     * 检查是否靠近裁剪区域（用于绘制边框）
     */
    private static boolean isNearCutArea(int[][] template, int i, int j) {
        // 检查上下左右及对角线8个方向
        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},  // 上下左右
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1} // 对角线
        };

        for (int[] dir : directions) {
            int ni = i + dir[0];
            int nj = j + dir[1];
            if (ni >= 0 && ni < SLIDER_WIDTH && nj >= 0 && nj < SLIDER_HEIGHT) {
                if (template[ni][nj] == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 读取像素周围3x3区域的像素值
     */
    private static void readPixel(BufferedImage img, int x, int y, int[] pixels) {
        int xStart = x - 1;
        int yStart = y - 1;
        int current = 0;

        for (int i = xStart; i < 3 + xStart; i++) {
            for (int j = yStart; j < 3 + yStart; j++) {
                int tx = i;
                if (tx < 0) {
                    tx = -tx;
                } else if (tx >= img.getWidth()) {
                    tx = x;
                }

                int ty = j;
                if (ty < 0) {
                    ty = -ty;
                } else if (ty >= img.getHeight()) {
                    ty = y;
                }

                pixels[current++] = img.getRGB(tx, ty);
            }
        }
    }

    /**
     * 填充矩阵
     */
    private static void fillMatrix(int[][] matrix, int[] values) {
        int filled = 0;
        for (int[] row : matrix) {
            for (int j = 0; j < row.length; j++) {
                row[j] = values[filled++];
            }
        }
    }

    /**
     * 计算矩阵平均值（用于生成自然的镂空效果）
     */
    private static int avgMatrix(int[][] matrix) {
        int r = 0;
        int g = 0;
        int b = 0;

        for (int i = 0; i < matrix.length; i++) {
            int[] row = matrix[i];
            for (int j = 0; j < row.length; j++) {
                if (j == 1) {
                    continue; // 跳过中心点
                }
                Color c = new Color(row[j]);
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }

        return new Color(r / 8, g / 8, b / 8).getRGB();
    }

    /**
     * 调整图片大小
     */
    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth) {
        if (originalImage.getWidth() == targetWidth) {
            return originalImage;
        }

        int targetHeight = (int) ((double) originalImage.getHeight() / originalImage.getWidth() * targetWidth);
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g.dispose();

        return resizedImage;
    }

    private static String toBase64(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, format, os);
        return "data:image/" + format + ";base64," + Base64.getEncoder().encodeToString(os.toByteArray());
    }
}

