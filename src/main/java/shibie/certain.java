package shibie;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 图片识别
 * Created by me on 2017/4/14.
 */
public class certain {
    //读取图片位置
    private static final String IMAGE_CONTENT = "E:\\image";
    private static BufferedImage imageDate;
    // 图像高度属性
    private static int width = 0;
    private static int height = 0;

    private static BufferedWriter writer = null;

    @Test
    public void myTest() throws IOException {
        int progress = 0;//进度条，每进行一次进度条加1
        //打开文本文件testimage，若不存在，创建新的，写入编码
        File excelIn = new File("E:\\code.txt");//编码文件存在E盘code
        if (excelIn.exists()) {
            excelIn.delete();
        }
        excelIn.createNewFile();
        //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
        writer = new BufferedWriter(new FileWriter(excelIn, false));

        File mImgDir = new File(IMAGE_CONTENT);//ImageContent是图像文件夹目录
        if (mImgDir.isDirectory()) {//判断file是否是文件目录 若是返回TRUE
            String fileNames[] = mImgDir.list(); //fileNames存储file文件夹中的文件名
            for (int PictureNum = 0; PictureNum < fileNames.length; PictureNum++) {
                File file = new File(IMAGE_CONTENT, fileNames[PictureNum]);//读取文件夹中的文件
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

                //********找出图中第一个圆圈的中心点A，记为（x0,y0）
                int x0, x1 = 0;
                int XX;
                XX = (width / 4 + width / 3) / 2;
                for (int j = 2; j < height; j++) {//找出第一个圆的中心点x0,y0
                    for (int i = 2; i < XX; i++) {
                        if (gray[j - 2][i] > 200 && gray[j - 1][i] > 200 && gray[j][i] < 50) {
                            int nn = 0;//顶点处是否有多个点，并选择最中间的点
                            for (int n = i; n < width; n++) {
                                if (gray[j][n] < 50) {
                                    nn++;
                                } else {
                                    break;
                                }
                            }
                            x1 = i + nn / 2;
                            for (int m = j; m < height - 2; m++) {//从上顶点向下查找
                                if (gray[m][i] < 50 && gray[m + 1][i] > 200 && gray[m + 2][i] > 200) {
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
                //控制数组最大行列
                int[] Yver = new int[10];
                int num1 = 0;
                for (int j = 2; j < height - 5; j++) {
                    if (gray[j - 2][x0] > 200 && gray[j - 1][x0] > 200 && gray[j][x0] < 50) {//找到上边界
                        for (int jj = j + 5; jj < height - 3; jj++) {//找下边界
                            if (gray[jj][x0] < 50 && gray[jj + 1][x0] > 200 && gray[jj + 2][x0] > 200) {
                                Yver[num1++] = (j + jj) / 2;
                                j = jj + 5;
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
                ResultExcel(code, fileNames[PictureNum]);
                progress++;
                System.out.println("进行数目：" + progress + "," + 100 * progress / fileNames.length + "%");
            }
        }
        writer.close();
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
            if (gray[y0][i - 2] > 200 && gray[y0][i - 1] > 200 && gray[y0][i] < 50) {//找到左侧边界

                for (int ii = i + 5; ii < width - 2; ii++) {//依次往右找右边界
                    if (gray[y0][ii] < 50 && gray[y0][ii + 1] > 200 && gray[y0][ii + 2] > 200) {//找到右侧边缘
                        int x0, sum;
                        x0 = (i + ii) / 2;
                        sum = gray[y0 - 1][x0] + gray[y0][x0 - 1] + gray[y0][x0] + gray[y0 + 1][x0] + gray[y0][x0 + 1];
                        if (sum < 200) {
                            code[num2++] = 1;
                            i = ii + 5;
                            break;
                        } else if (sum > 1000) {
                            code[num2++] = 0;
                            i = ii + 5;
                            break;
                        } else {
                            sum = gray[y0 - 2][x0] + gray[y0][x0 - 2] + gray[y0][x0] + gray[y0 + 2][x0] + gray[y0][x0 + 2];
                            if (sum > 700) {
                                code[num2++] = 0;
                                i = ii + 5;
                                break;
                            } else {
                                code[num2++] = 1;
                                i = ii + 5;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return code;
    }

    /**
     * 将计算的结果编码写入表格
     */
    private static void ResultExcel(int code[][], String Picturename) {
        int len = code.length;
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer.write(Picturename + ":");//图像名
            for (int i = 0; i < len; i++) {
                for (int j = 0; j < len; j++) {
                    writer.write("\t" + code[i][j]);
                }
                writer.write(";");
            }
            writer.write("\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将结果图像存入文件夹
     */
    public static void SaveResult(File fileName, int a[][]) {
        int h = a.length;
        int w = a[0].length;
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_USHORT_565_RGB);
        int alpha = 255 << 24;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int gray = alpha | (a[i][j] << 16) | (a[i][j] << 8) | a[i][j];
                image.setRGB(j, i, gray);
            }
        }
        try {
            ImageIO.write(image, "png", fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
