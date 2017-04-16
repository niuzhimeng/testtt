package imgfilter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 二值化
 * Created by me on 2017/4/13.
 */
public class TwoZhiHua {
    public static void main(String[] args) throws IOException {
        BufferedImage bi = ImageIO.read(new File("D:/test/nzm.png"));//通过imageio将图像载入
        int h = bi.getHeight();//获取图像的高
        int w = bi.getWidth();//获取图像的宽
        int rgb = bi.getRGB(0, 0);//获取指定坐标的ARGB的像素值
        int[][] gray = new int[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                gray[x][y] = getGray(bi.getRGB(x, y));
            }
        }

        BufferedImage nbi = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
        int SW = 160;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (getAverageColor(gray, x, y, w, h) > SW) {
                    int max = new Color(255, 255, 255).getRGB();
                    nbi.setRGB(x, y, max);
                } else {
                    int min = new Color(0, 0, 0).getRGB();
                    nbi.setRGB(x, y, min);
                }
            }
        }

        ImageIO.write(nbi, "jpg", new File("D:/test1/二值化后_无压缩.jpg"));
    }

    private static int getGray(int rgb) {
        //or 直接new个color对象
        Color c = new Color(rgb);
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        int top = (r + g + b) / 3;
        return (int) (top);
    }

    /**
     * 自己加周围8个灰度值再除以9，算出其相对灰度值
     *
     * @param gray
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     */
    public static int getAverageColor(int[][] gray, int x, int y, int w, int h) {
        int rs = gray[x][y]
                + (x == 0 ? 255 : gray[x - 1][y])
                + (x == 0 || y == 0 ? 255 : gray[x - 1][y - 1])
                + (x == 0 || y == h - 1 ? 255 : gray[x - 1][y + 1])
                + (y == 0 ? 255 : gray[x][y - 1])
                + (y == h - 1 ? 255 : gray[x][y + 1])
                + (x == w - 1 ? 255 : gray[x + 1][y])
                + (x == w - 1 || y == 0 ? 255 : gray[x + 1][y - 1])
                + (x == w - 1 || y == h - 1 ? 255 : gray[x + 1][y + 1]);
        return rs / 9;
    }

}
