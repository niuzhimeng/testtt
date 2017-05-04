package shibie;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 图像识别
 * Created by me on 2017/4/26.
 */
public class newc {
    private String ImageContent = "E://image";
    private static BufferedImage imageDate;
    // 图像高度属性
    private static int width = 0;
    private static int height = 0;

    @Test
    public void test() throws IOException {

        File mImgDir = new File(ImageContent);//ImageContent是图像文件夹目录
        if (mImgDir.isDirectory()) {//判断file是否是文件目录 若是返回TRUE
            String fileNames[] = mImgDir.list(); //fileNames存储file文件夹中的文件名
            assert fileNames != null;
            for (String fileName : fileNames) {
                File file = new File(ImageContent, fileName);//读取文件夹中的文件
                try {
                    imageDate = ImageIO.read(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                width = imageDate.getWidth();
                height = imageDate.getHeight();
                //*****图像转为灰度图******//
                int[][] gray = new int[height][width];
                for (int row = 0; row < height; row++) {
                    for (int col = 0; col < width; col++) {
                        Object data = imageDate.getRaster().getDataElements(col, row, null);//获取该点像素，并以object类型表示
                        int red = imageDate.getColorModel().getRed(data);
                        int green = imageDate.getColorModel().getBlue(data);
                        int blue = imageDate.getColorModel().getGreen(data);
                        gray[row][col] = (int) Math.round((double) red * 0.3 + (double) green * 0.59 + (double) blue * 0.11);

                    }
                }

                for (int j = 0; j < height; j++) {
                    for (int i = 2; i < width; i++) {
                        if (gray[j][i] > 90) {
                            gray[j][i] = 255;
                        }
                    }
                }

                //********找出图中第一个圆圈的中心点，记为（x0,y0）
                int x0, x1 = 0;
                int XX;
                XX = (width / 4 + width / 3) / 2;
                for (int j = 4; j < height - 5; j++) {//找出第一个圆的中心点x0,y0
                    for (int i = 4; i < XX - 5; i++) {
                        int sum0 = 0, sum1 = 0, sum2 = 0;
                        for (int m = i - 4; m < i + 5; m++) {
                            sum0 = sum0 + gray[j][m];
                            sum1 = sum1 + gray[j - 1][m];
                            sum2 = sum2 + gray[j - 2][m];
                        }
                        if (sum2 > 1400 && sum1 > 1400 && sum0 < 1400) {
                            int nn = 0;//顶点处是否有多个点，并选择最中间的点
                            for (int n = i - 4; n < i + 30 && n < width; n++) {
                                if (gray[j][n] < 90) {
                                    nn++;
                                }
                            }

                            x1 = i + nn / 2;
                            for (int m = j + 10; m < height - 2; m++) {//从上顶点向下查找
                                sum0 = 0;
                                sum1 = 0;
                                sum2 = 0;
                                for (int n = x1 - 4; n < x1 + 5; n++) {
                                    sum0 = sum0 + gray[m][n];
                                    sum1 = sum1 + gray[m + 1][n];
                                    sum2 = sum2 + gray[m + 2][n];
                                }
                                if (sum0 < 1000 && sum1 > 1000 && sum2 > 1000) {
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    if (x1 != 0) {
                        break;
                    }
                }
                x0 = x1;
                //****从第一个中心点出发，分别向下查找第一行圆的中心横线的Y,记录个数num1
                int[] Yver = new int[4];
                int num1 = 0;
                for (int j = 2; j < height - 2; j++) {
                    int sum0 = 0, sum1 = 0, sum2 = 0;
                    for (int m = x0 - 4; m < x0 + 5; m++) {
                        sum0 = sum0 + gray[j][m];
                        sum1 = sum1 + gray[j - 1][m];
                        sum2 = sum2 + gray[j - 2][m];
                    }
                    if (sum1 > 1400 && sum2 > 1400 && sum0 < 1400) {//找到上边界
                        for (int jj = j + 20; jj < height - 2; jj++) {//找下边界
                            sum0 = 0;
                            sum1 = 0;
                            sum2 = 0;
                            for (int m = x0 - 4; m < x0 + 5; m++) {
                                sum0 = sum0 + gray[jj][m];
                                sum1 = sum1 + gray[jj + 1][m];
                                sum2 = sum2 + gray[jj + 2][m];
                            }
                            if (sum0 < 1400 && sum1 > 1400 && sum1 > 1400) {
                                Yver[num1++] = (j + jj) / 2;
                                j = jj + 20;
                                break;
                            }
                        }
                    }
                }
                //****从每行第一个点出发，分别向左查找第一行每个圆的中心点的X,记录个数num2
                int[][] code = new int[num1][num1];
                for (int num = 0; num < num1; num++) {
                    code[num] = JUdgeResult(gray, Yver[num], num1);
                }

                for (int i = 0; i < num1; i++) {
                    for (int j = 0; j < num1; j++) {
                        System.out.print("\t" + code[i][j]);
                    }
                    System.out.println(";");
                }
                System.out.println("=================");
            }
        }
    }

    /**
     * 根据得到的每行的近中心点的纵坐标，计算该行的图案的编码，黑色为0，白色为1
     *
     * @param gray 图像的灰度
     * @param y0   要计算的行的近中心点的纵坐标
     * @param num1 每行图案的个数
     * @return 返回该行的图案的编码
     */
    private static int[] JUdgeResult(int gray[][], int y0, int num1) {
        int[] code = new int[num1];
        int num2 = 0;
        for (int i = 2; i < width - 2; i++) {
            int sum0 = 0, sum1 = 0, sum2 = 0;
            for (int m = y0 - 4; m < y0 + 5; m++) {
                sum0 = sum0 + gray[m][i];
                sum1 = sum1 + gray[m][i - 1];
                sum2 = sum2 + gray[m][i - 2];
            }
            if (sum1 > 1400 && sum2 > 1400 && sum0 < 1400) {//找到左侧边界
                for (int ii = i + 30; ii < width - 2; ii++) {//依次往右找右边界
                    sum0 = 0;
                    sum1 = 0;
                    sum2 = 0;
                    for (int m = y0 - 4; m < y0 + 5; m++) {
                        sum0 = sum0 + gray[m][ii];
                        sum1 = sum1 + gray[m][ii + 1];
                        sum2 = sum2 + gray[m][ii + 2];
                    }
                    if (sum0 < 1400 && sum1 > 1400 && sum2 > 1400) {//找到右侧边缘
                        if (ii - i < 40) {
                            i = ii + 5;
                            break;
                        } else {
                            int x0, sum;
                            x0 = (i + ii) / 2;
                            sum = gray[y0 - 1][x0] + gray[y0][x0 - 1] + gray[y0][x0] + gray[y0 + 1][x0] + gray[y0][x0 + 1];
                            if (sum < 450) {
                                code[num2++] = 1;
                                i = ii + 30;
                                break;
                            } else {
                                code[num2++] = 0;
                                i = ii + 30;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return code;
    }

}
