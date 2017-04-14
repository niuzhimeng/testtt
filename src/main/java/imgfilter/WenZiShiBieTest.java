package imgfilter;

import org.junit.Test;

import java.io.File;

/**
 * Created by me on 2017/4/13.
 */
public class WenZiShiBieTest {
    @Test
    public void test() {
        try {
            File testDataDir = new File("D:\\test");
            int i = 0;
            for (File file : testDataDir.listFiles()) {
                i++;
                String recognizeText = new WenZiShiBie().recognizeText(file);
                System.out.print(recognizeText + "\t");

                if (i % 5 == 0) {
                    System.out.println();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
