package shibie;

/**
 * 可识别手机拍照
 * Created by me on 2017/4/16.
 */

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class certains {

    private static String ImageContent = "E://image";
    private static BufferedImage imagedate;
    // 图像高度属性
    private static int width = 0;
    private static int height = 0;

    private static BufferedWriter writer = null;

    public static void main(String[] args) throws IOException {
        int progress = 0;//进度条，每进行一次进度条加1
        //打开文本文件testimage，若不存在，创建新的，写入编码
        File excelIn = new File("E:" + "\\" + "code.txt");//编码文件存在E盘code
        if (excelIn.exists()) {
            excelIn.delete();
        }
        excelIn.createNewFile();
        //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
        writer = new BufferedWriter(new FileWriter(excelIn, false));

        File mImgDir = new File(ImageContent);//ImageContent是图像文件夹目录
        if (mImgDir.isDirectory()) {//判断file是否是文件目录 若是返回TRUE
            String fileNames[] = mImgDir.list(); //fileNames存储file文件夹中的文件名
            for (int PictureNum = 0; PictureNum < fileNames.length; PictureNum++) {
                File file = new File(ImageContent, fileNames[PictureNum]);//读取文件夹中的文件
                try {
                    imagedate = ImageIO.read(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                width = imagedate.getWidth();
                height = imagedate.getHeight();
                //*****图像转为灰度图******//
                int[][] gray = new int[height][width];
                for (int row = 0; row < height; row++) {
                    for (int col = 0; col < width; col++) {
                        Object data = imagedate.getRaster().getDataElements(col, row, null);//获取该点像素，并以object类型表示
                        int red = imagedate.getColorModel().getRed(data);
                        int green = imagedate.getColorModel().getBlue(data);
                        int blue = imagedate.getColorModel().getGreen(data);
                        gray[row][col] = (int) Math.round((double) red * 0.3 + (double) green * 0.59 + (double) blue * 0.11);

                    }
                }
                //****************灰度图做滤波****************//
                //int[][] grayfilter = new int[height][width];
                //grayfilter=ImageFilter.MedianFilter(gray);
                //grayfilter=ImageFilter.AverageFilter(gray);
                //ResultExcel(gray,fileNames[PictureNum]);
                //归一化
                //gray=normalization(gray);
                //ResultExcel(gray,fileNames[PictureNum]);
                //梯度
                //int [][]grad =new int [height][width];
                //grad=getSobelBitmap(gray);
                //ResultExcel(grad,fileNames[PictureNum]);
                //File Picturegrad = new File("E:\\testimage" + "\\"+ fileNames[PictureNum]);
                //SaveResult(Picturegrad,grad);
                for (int j = 0; j < height; j++) {
                    for (int i = 2; i < width; i++) {
                        if (gray[j][i] > 95) {
                            gray[j][i] = 255;
                        }
                    }
                }
                //********找出图中第一个圆圈的中心点，记为（x0,y0）
                int x0 = 0, y0 = 0, x1 = 0, x2 = 0, y1 = 0, y2 = 0;
                int XX = 0;
                XX = (width / 4 + width / 3) / 2;
                for (int j = 4; j < height - 5; j++) {//找出第一个圆的中心点x0,y0
                    for (int i = 4; i < XX - 5; i++) {
                        int sum0 = 0, sum1 = 0, sum2 = 0;
                        for (int m = i - 4; m < i + 5; m++) {
                            sum0 = sum0 + gray[j][m];
                            sum1 = sum1 + gray[j - 1][m];
                            sum2 = sum2 + gray[j - 2][m];
                        }
                        if (sum2 > 1000 && sum1 > 1000 && sum0 < 1000) {
                            int nn = 0;//顶点处是否有多个点，并选择最中间的点
                            for (int n = i; n < i + 30 && n < width; n++) {
                                if (gray[j][n] < 90) {
                                    nn++;
                                } else {
                                    break;
                                }
                            }
                            if (nn > 3)
                                x1 = i + nn / 2;
                            y1 = j;//找到上顶点(x1,y1)
                            for (int m = j + 2; m < height - 2; m++) {//从上顶点向下查找
                                sum0 = 0;
                                sum1 = 0;
                                sum2 = 0;
                                for (int n = x1 - 4; n < x1 + 5; n++) {
                                    sum0 = sum0 + gray[m][n];
                                    sum1 = sum1 + gray[m + 1][n];
                                    sum2 = sum2 + gray[m + 2][n];
                                }
                                if (sum0 < 1000 && sum1 > 1000 && sum2 > 1000) {
                                    y2 = m;//下边界，也就是B点的坐标
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
                y0 = (y1 + y2) / 2;
                //****从第一个中心点出发，分别向下查找第一行圆的中心横线的Y,记录个数num1
                int[] Yver = new int[4];
                int y = 0;
                int num1 = 0;
                for (int j = 2; j < height - 2; j++) {
                    int sum0 = 0, sum1 = 0, sum2 = 0;
                    for (int m = x0 - 4; m < x0 + 5; m++) {
                        sum0 = sum0 + gray[j][m];
                        sum1 = sum1 + gray[j - 1][m];
                        sum2 = sum2 + gray[j - 2][m];
                    }
                    if (sum1 > 1400 && sum2 > 1400 && sum0 < 1400) {//找到上边界
                        for (int jj = j + 3; jj < height - 2; jj++) {//找下边界
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
                                j = jj + 5;
                                y = jj;
                                break;
                            }
                        }
                    }
                }
                if (height - y < height / 4) {
                    num1 = num1;
                } else if (height - y < (height / 4 + height / 3) / 2 && height - y >= height / 4) {
                    num1 = num1 + 1;
                } else if (height - y < (height / 3 * 2 + height / 2) / 2 && height - y >= (height / 4 + height / 3) / 2) {
                    num1 = num1 + 2;
                } else {
                    num1 = num1 + 3;
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
    public static int[] JUdgeResult(int gray[][], int y0, int num1) {
        int[] code = new int[num1];
        int num2 = 0;
        int x = 0;
        for (int i = 2; i < width - 2; i++) {
            //if((i>0&&i<width/(num1*2))||(i>width*2/(num1*2)&&i<width*3/(num1*2))
            //		||(i>width*4/(num1*2)&&i<width*5/(num1*2))||(i>width*6/(num1*2)&&i<width*7/(num1*2))){
            int sum0 = 0, sum1 = 0, sum2 = 0;
            for (int m = y0 - 4; m < y0 + 5; m++) {
                sum0 = sum0 + gray[m][i];
                sum1 = sum1 + gray[m][i - 1];
                sum2 = sum2 + gray[m][i - 2];
            }
            if (sum1 > 1400 && sum2 > 1400 && sum0 < 1400) {//找到左侧边界
                for (int ii = i + 3; ii < width - 2; ii++) {//依次往右找右边界
                    sum0 = 0;
                    sum1 = 0;
                    sum2 = 0;
                    if ((ii - i) > width / num1) {
                        i = ii;
                        break;
                    }
                    for (int m = y0 - 4; m < y0 + 5; m++) {
                        sum0 = sum0 + gray[m][ii];
                        sum1 = sum1 + gray[m][ii + 1];
                        sum2 = sum2 + gray[m][ii + 2];
                    }
                    if (sum0 < 1400 && sum1 > 1400 && sum2 > 1400) {//找到右侧边缘
                        int x0 = 0, sum = 0;
                        x0 = (i + ii) / 2;
                        sum = gray[y0 - 1][x0] + gray[y0][x0 - 1] + gray[y0][x0] + gray[y0 + 1][x0] + gray[y0][x0 + 1];

                        if (x0 > 0 && x0 < width * 2 / (num1 * 2)) {
                            if (sum < 450) {
                                code[num2++] = 1;
                                i = ii + 5;
                                x = ii;
                                break;
                            } else if (sum > 450) {
                                code[num2++] = 0;
                                i = ii + 5;
                                x = ii;
                                break;
                            }
                        } else if (x0 > width * 2 / (num1 * 2) && x0 < width * 4 / (num1 * 2)) {
                            num2 = 1;
                            if (sum < 450) {
                                code[num2++] = 1;
                                i = ii + 5;
                                x = ii;
                                break;
                            } else if (sum > 450) {
                                code[num2++] = 0;
                                i = ii + 5;
                                x = ii;
                                break;
                            }
                        } else if (ii > width * 4 / (num1 * 2) && ii < width * 6 / (num1 * 2)) {
                            num2 = 2;
                            if (sum < 450) {
                                code[num2++] = 1;
                                i = ii + 5;
                                x = ii;
                                break;
                            } else if (sum > 450) {
                                code[num2++] = 0;
                                i = ii + 5;
                                x = ii;
                                break;
                            }
                        } else {
                            num2 = 3;
                            if (sum < 450) {
                                code[num2++] = 1;
                                i = ii + 5;
                                x = ii;
                                break;
                            } else if (sum > 450) {
                                code[num2++] = 0;
                                i = ii + 5;
                                x = ii;
                                break;
                            }
                        }
                            /*else{
                                sum=gray[y0-2][x0]+gray[y0][x0-2]+gray[y0][x0]+gray[y0+2][x0]+gray[y0][x0+2];
								if(sum>300){
									code[num2++]=0;
									i=ii+5;
									break;
								}
								else{
									code[num2++]=1;
									i=ii+5;
									break;
								}
							}*/
                    }
                }
            }
            //}
        }
        return code;
    }

    /**
     * 将计算的结果编码写入表格
     *
     * @param
     * @param Picturename
     */
    public static void ResultExcel(int code[][], String Picturename) {
        int len = code.length;
        int h = code.length;
        int w = code[0].length;
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
            /*for(int i=0;i<h;i++){
				for(int j=0;j<w;j++){
					writer.write("\t"+code[i][j]);
				}
				writer.write("\r\n");
			}*/
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

    /**
     * 归一化
     * a为计算的梯度值
     */
    private static int[][] normalization(int a[][]) {
        int h = a.length;
        int w = a[0].length;
        int max = 0, min = 255;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (a[i][j] > max) {
                    max = a[i][j];
                }
                if (a[i][j] != 0 && a[i][j] < min) {
                    min = a[i][j];
                }
            }
        }
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                a[i][j] = 255 * (a[i][j] - min) / (max - min);
            }
        }
        return a;
    }

    public static int[][] getSobelBitmap(int gray[][]) {
        int h = gray.length;
        int w = gray[0].length;
        int[][] sobel = new int[h][w];
        // 计算梯度
        int gx, gy;
        for (int j = 1; j < h - 1; j++) {
            for (int i = 1; i < w - 1; i++) {
                // 3X3的模板
                gx = (gray[j - 1][i + 1] + 2 * gray[j][i + 1] + gray[j + 1][i + 1])
                        - (gray[j - 1][i - 1] + 2 * gray[j][i - 1] + gray[j + 1][i - 1]);
                gy = (gray[j - 1][i - 1] + 2 * gray[j - 1][i] + gray[j - 1][i + 1])
                        - (gray[j + 1][i - 1] + 2 * gray[j + 1][i] + gray[j + 1][i + 1]);
                sobel[j][i] = (int) Math.round(Math.sqrt(Math.pow(gx, 2) + Math.pow(gy, 2)));
            }
        }
        return sobel;
    }
}

