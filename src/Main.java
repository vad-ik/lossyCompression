import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Main {

    static boolean save = true;

    public static void main(String[] args) {


        FileKeeper fileKeeper = new FileKeeper();
        PhotoWorker worker = new PhotoWorker();
        String str = "E:\\загрузки\\1200px-Lenna — копия.png";
        //int[][] a=worker.getMatrixPhoto(str);

        int[][] in = worker.getMatrixPhoto(str);//получить матрицу фото
        if (false){//рандомная картинка
            Random random = new Random();
            for (int i = 0; i < in.length; i++) {
                for (int i1 = 0; i1 < in[0].length; i1++) {
                    int a=random.nextInt()&255;
                    int r=random.nextInt()&255;
                    int g=random.nextInt()&255;
                    int b=random.nextInt()&255;



                    in[i][i1] = (a<<24)|(r<<16)|(g<<8)|b;
                }
            }
        }
        if (save) {
            fileKeeper.savePhoto(in, "imageOriginal.txt");
        }
        worker.savePhoto(worker.getImageMatrix(in), "imageOriginal.bmp");

        int[][] zigzagY = new int[in.length * in[0].length / 64][64];
        int[][] zigzagCb = new int[in.length * in[0].length / 64][64];
        int[][] zigzagCr = new int[in.length * in[0].length / 64][64];

        //  int[] dcY =new int[in.length*in[0].length/64];
        //  int[][] dcC =new int[in.length*in[0].length/64][2];
        ArrayList<Integer>[] acY = new ArrayList[in.length * in[0].length / 64];
        ArrayList<Integer>[][] acC = new ArrayList[in.length * in[0].length / 64][2];

        int[][] ycc = worker.getMatrixYCC(in);
        int[][] y = worker.getShiftMatrix(ycc, 16, true);//разделить на каналы
        int[][] c1F = worker.getShiftMatrix(ycc, 8, true);
        int[][] c2F = worker.getShiftMatrix(ycc, 0, true);

        if (save) {
            fileKeeper.savePhoto(y, c1F, c2F, "imageYcbcr.txt");
        }


        c1F= worker.downsamplingCell(c1F, 2, true);
        c2F=worker.downsamplingCell(c2F, 2, true);


        if (save) {
            fileKeeper.savePhoto(y, c1F, c2F, "imageYcbcr2.txt");
        }
        // for (int b = 1; b < 100; b+=1) {
        boolean qwant = true;
        int b = 50;
        {


            DCT dct = new DCT();
            int[][][][] matrixY;
            int[][][][] matrixCb;
            int[][][][] matrixCr;
            if (save) {
                matrixY = dct.getDCTPhoto(y, false, b);
                matrixCb = dct.getDCTPhoto(c1F, false, b);
                matrixCr = dct.getDCTPhoto(c2F, false, b);
                fileKeeper.savePhotoDCT(matrixY, matrixCb, matrixCr, "photoDCT" + b + ".txt");
            }

            matrixY = dct.getDCTPhoto(y, qwant, b);
            matrixCb = dct.getDCTPhoto(c1F, qwant, b);
            matrixCr = dct.getDCTPhoto(c2F, qwant, b);
            fileKeeper.savePhotoDCT(matrixY, matrixCb, matrixCr, "photoDCTQwant" + b + ".txt");

            getZigZag(matrixY, zigzagY);
            getZigZag(matrixCb, zigzagCb);
            getZigZag(matrixCr, zigzagCr);

            Rle.getDcAc(acC, acY, zigzagY, zigzagCb, zigzagCr);

            //  if (save) {
            fileKeeper.savePhotoQ(acY, acC, "photoQRle" + b + ".txt", in.length, in[0].length, b);
            b = fileKeeper.getPhotoQ(acY, acC, "photoQRle" + b + ".txt");
            // }


            Rle.getBackDcAc(acC, acY, zigzagY, zigzagCb, zigzagCr);

            getBackZigZag(matrixY, zigzagY);
            getBackZigZag(matrixCb, zigzagCb);
            getBackZigZag(matrixCr, zigzagCr);

            int[][] yOut = dct.getPhoto(matrixY, qwant, b);
            int[][] c1 = dct.getPhoto(matrixCb, qwant, b);
            int[][] c2 = dct.getPhoto(matrixCr, qwant, b);


            c1=worker.sampling(c1, 2);
            c2= worker.sampling(c2, 2);

            yOut = worker.getShiftMatrix(yOut, 16, false);//ниже объединение каналов
            c1 = worker.getShiftMatrix(c1, 8, false);

            yOut = worker.getcombineMatrix(yOut, c1);
            yOut = worker.getcombineMatrix(yOut, c2);

            yOut = worker.getMatrixRGB(yOut);//перевод в ргб

            worker.savePhoto(worker.getImageMatrix(yOut), "image" + b + ".bmp");
        }

    }

    static void getZigZag(int[][][][] matrix, int[][] result) {

        int k = 0;
        PhotoWorker worker = new PhotoWorker();
        for (int i = 0; i < matrix.length; i++) {
            for (int i1 = 0; i1 < matrix[0].length; i1++) {
                worker.zigzag(matrix[i][i1], result[k++], true);
            }
        }
    }


    static void getBackZigZag(int[][][][] matrix, int[][] result) {

        int k = 0;
        PhotoWorker worker = new PhotoWorker();
        for (int i = 0; i < matrix.length; i++) {
            for (int i1 = 0; i1 < matrix[0].length; i1++) {
                worker.zigzag(matrix[i][i1], result[k++], false);
                for (int ii = 0; ii < 8; ii++) {
                    for (int j = ii + 1; j < 8; j++) {
                        int temp = matrix[i][i1][ii][j];
                        matrix[i][i1][ii][j] = matrix[i][i1][j][ii];
                        matrix[i][i1][j][ii] = temp;
                    }
                }
            }
        }

    }

    static void sayArray(int[][] a) {
        for (int i = 0; i < a.length; i++) {
            for (int i1 = 0; i1 < a[0].length; i1++) {
                System.out.print(a[i][i1] + " ");
            }
            System.out.println();
        }
    }

    static void saySArray(int[][] a) {
        for (int i = 0; i < 8; i++) {
            for (int i1 = 0; i1 < 8; i1++) {
                System.out.print(a[i][i1] + " ");
            }
            System.out.println();
        }
    }

    static void addAll(int[][] matrix, int n) {
        for (int i = 0; i < matrix.length; i++) {
            for (int i1 = 0; i1 < matrix[0].length; i1++) {

                matrix[i][i1] += n;

            }
        }
    }


}
