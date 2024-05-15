import java.awt.*;

public class Main {
    public static void main(String[] args) {


        PhotoWorker worker = new PhotoWorker();
        String str = "E:\\загрузки\\1200px-Lenna.png";
        //int[][] a=worker.getMatrixPhoto(str);

        int[][] in = worker.getMatrixPhoto(str);//получить матрицу фото

        worker.savePhoto(worker.getImageMatrix(in), "imageOriginal.bmp");
        int[][] ycc = worker.getMatrixYCC(in);
        int[][] y = worker.getShiftMatrix(ycc, 16, true);//разделить на каналы
        int[][] c1F = worker.getShiftMatrix(ycc, 8, true);
        int[][] c2F = worker.getShiftMatrix(ycc, 0, true);


        for (int b = 1; b < 100; b+=10) {
        boolean qwant = true;
      //  int b = 1;{


            DCT dct = new DCT();

            int[][][][] matrixY = dct.getDCTPhoto(y, qwant, b);
            int[][][][] matrixCb = dct.getDCTPhoto(c1F, qwant, b);
            int[][][][] matrixCr = dct.getDCTPhoto(c2F, qwant, b);

//            {
//            int[][] result = new int[matrix.length * 8][matrix[0].length * 8];
//            for (int i = 0; i < matrix.length; i++) {
//                for (int j = 0; j < matrix[0].length; j++) {
//                    for (int k = 0; k < 8; k++) {
//                        for (int l = 0; l < 8; l++) {
//                            result[i * 8 + k][j * 8 + l] = matrix[i][j][k][l] & 255;
//                        }
//                    }
//                }
//            }
//            result = worker.getShiftMatrix(result, 16, false);//ниже объединение каналов
//            result = worker.getcombineMatrix(result, c1);
//            result = worker.getcombineMatrix(result, c2);
//            result = worker.getMatrixRGB(result);//перевод в ргб
//            worker.savePhoto(worker.getImageMatrix(result), "imageTR" + b + ".bmp");
//        }


            // int[] img = worker.zigzag(result);
            //  RLE rle = new RLE();
            // StringBuilder sb = rle.coderV2(img);
            // img = rle.autoDeCompression(sb);
//обратный зигзаг
            //  matrix = dct.get8x8(result, 0);
            int[][] out = dct.getPhoto(matrixY, qwant, b);
            int[][] c1 = dct.getPhoto(matrixCb, qwant, b);
            int[][] c2 = dct.getPhoto(matrixCr, qwant, b);


            out = worker.getShiftMatrix(out, 16, false);//ниже объединение каналов
            c1 = worker.getShiftMatrix(c1, 8, false);

            out = worker.getcombineMatrix(out, c1);
            out = worker.getcombineMatrix(out, c2);

            out = worker.getMatrixRGB(out);//перевод в ргб

            worker.savePhoto(worker.getImageMatrix(out), "image" + b + ".bmp");
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





}
