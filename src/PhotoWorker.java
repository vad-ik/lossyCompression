import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PhotoWorker {
    int[][] getMatrixPhoto(String path) {
        BufferedImage in;
        int[][] result;
        try {
            in = ImageIO.read(new File(path));

            int height = in.getHeight();
            int width = in.getWidth();
            result = new int[width][height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    result[j][i] = (in.getRGB(j, i));
                }
            }
        } catch (IOException e) {
            System.out.println("ошибка файла");
            throw new RuntimeException(e);
        }
        return result;
    }

    BufferedImage getImageMatrix(int[][] photo) {
        BufferedImage out = new BufferedImage(photo.length, photo[0].length, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < photo.length; i++) {
            for (int i1 = 0; i1 < photo[0].length; i1++) {
                out.setRGB(i, i1, photo[i][i1]);
            }
        }
        return out;
    }

    void savePhoto(BufferedImage image, String path) {
        try {
            ImageIO.write(image, "png", new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    int[][] getMatrixYCC(int[][] photoRGB) {
        int[][] result = new int[photoRGB.length][photoRGB[0].length];

        for (int i = 0; i < photoRGB.length; i++) {
            for (int i1 = 0; i1 < photoRGB[0].length; i1++) {


                int r = photoRGB[i][i1] >> (8 * 2) & (255);
                int g = photoRGB[i][i1] >> (8) & (255);
                int b = photoRGB[i][i1] & (255);

                int y = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                int cb = (int) (128 - 0.168736 * r - 0.331264 * g + 0.5 * b);
                int cr = (int) (128 + 0.5 * r - 0.419 * g - 0.081312 * b);
                if (y < 0) {
                    y = -y;
                }
                if (cb < 0) {
                    cb = -cb;
                }
                if (cr < 0) {
                    cr = -cr;
                }
                y=y&255;
                cb=cb&255;
                cr=cr&255;
                result[i][i1] = (y << 16) + (cb << 8) + cr;

            }
        }
        return result;
    }

    int[][] getMatrixRGB(int[][] photoYCC) {
        int[][] result = new int[photoYCC.length][photoYCC[0].length];
        for (int i = 0; i < photoYCC.length; i++) {
            for (int i1 = 0; i1 < photoYCC[0].length; i1++) {
                int y = photoYCC[i][i1] >> (16) & (255);
                int cb = photoYCC[i][i1] >> (8) & (255);
                int cr = photoYCC[i][i1] & (255);

                int r = (int) (y + 1.402 * (cr - 128));
                int g = (int) (y - 0.34414 * (cb - 128) - 0.71414 * (cr - 128));
                int b = (int) (y + 1.772 * (cb - 128));
                if (r < 0) {
                    r = -r;
                }
                if (g < 0) {
                    g = -g;
                }
                if (b < 0) {
                    b = -b;
                }
                r=r&255;
                g=g&255;
                b=b&255;
                result[i][i1] = (255 << 24) + (r << 16) + (g << 8) + b;
            }
        }


        return result;
    }

int[][] getShiftMatrix(int[][] matrix, int shift,boolean side) {
    int[][] result = new int[matrix.length][matrix[0].length];
    for (int i = 0; i < matrix.length; i++) {
        for (int i1 = 0; i1 < matrix[0].length; i1++) {
            if (side){
                result[i][i1] = (matrix[i][i1] >> shift)&255;
            }else {
                result[i][i1] = (matrix[i][i1]&255) << shift;
            }

        }
    }
    return result;
}
    int[][] getcombineMatrix(int[][] matrix, int[][] matrix2) {
        int[][] result = new int[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int i1 = 0; i1 < matrix[0].length; i1++) {
                result[i][i1] = matrix[i][i1]+matrix2[i][i1];
            }
        }
        return result;
    }

    void zigzag(int[][] matrix,int[] result,boolean side) {
        int maxWidth = matrix.length;
        int maxHeight = matrix[0].length;
        if (side) {
            result[maxWidth * maxHeight - 1] = matrix[maxWidth - 1][maxHeight - 1];
        }else {
             matrix[maxWidth - 1][maxHeight - 1]=result[maxWidth * maxHeight - 1];

        }
        int x = 0;
        int y = 0;
        int n = 0;
        boolean up = true;
        while (!(x == maxWidth - 1 && y == maxHeight - 1)) {
            if (side) {
                result[n] = matrix[x][y];
            }else {
                  matrix[y][x]=result[n];
            }
            n++;
            if (up) {
                if (y > 0 || x == maxWidth - 1) {
                    if (x < maxWidth - 1) {
                        x++;
                        y--;
                    } else {
                        up = false;
                        y++;
                    }
                } else {
                    up = false;
                    x++;
                }
            } else {
                if (x > 0 || y == maxHeight - 1) {
                    if (y < maxHeight - 1) {
                        x--;
                        y++;
                    } else {
                        up = true;
                        x++;
                    }
                } else {
                    up = true;
                    y++;
                }
            }

        }
    }



    int[][] downsamplingDelLine(int[][] matrix, int step) {
        int[][] result = new int[(matrix.length / step)][(matrix[0].length / step)];

        int x = 0;
        int y = 0;
        for (int i = 0; i < matrix.length; i+=step) {

                y = 0;
                for (int j = 0; j < matrix[0].length; j+=step) {

                        result[x][y] = matrix[i][j];
                        y++;

                }
                x++;

        }
        return result;
    }

    int[][] downsamplingCell(int[][] matrix, int step, boolean average) {
        int[][] result = new int[matrix.length / step + (matrix.length % step > 0 ? 1 : 0)][matrix[0].length / step + (matrix[0].length % step > 0 ? 1 : 0)];
        for (int i = 0; i < matrix.length; i += step) {
            for (int j = 0; j < matrix[0].length; j += step) {
                int[] color = new int[4];//поиск среднего
                int amaunt = 0;
                for (int k = 0; k < 3 && (i + k) < matrix.length; k++) {
                    for (int l = 0; l < 3 && (j + l) < matrix[0].length; l++) {
                        amaunt++;
                        color[0] += ((matrix[i + k][j + l] >> (24)) & (255));
                        color[1] += ((matrix[i + k][j + l] >> (16)) & (255));
                        color[2] += ((matrix[i + k][j + l] >> (8)) & (255));
                        color[3] += ((matrix[i + k][j + l]) & (255));
                    }
                }
                color[0] /= amaunt;
                color[1] /= amaunt;
                color[2] /= amaunt;
                color[3] /= amaunt;
                int newColor = (color[0] << 24) + (color[1] << 16) + (color[2] << 8) + (color[3]);

                if (!average) {
                    int len = Math.abs(color[0] - ((matrix[i][j] >> (24)) & (255))) +
                            Math.abs(color[1] - ((matrix[i][j] >> (16)) & (255))) +
                            Math.abs(color[2] - ((matrix[i][j] >> (8)) & (255))) +
                            Math.abs(color[3] - ((matrix[i][j]) & (255)));
                    for (int k = 0; k < 3 && i + k < matrix.length; k++) {
                        for (int l = 0; l < 3 && j + l < matrix[0].length; l++) {
                            int thisLen = Math.abs(color[0] - ((matrix[i + k][j + l] >> (24)) & (255))) +
                                    Math.abs(color[1] - ((matrix[i + k][j + l] >> (16)) & (255))) +
                                    Math.abs(color[2] - ((matrix[i + k][j + l] >> (8)) & (255))) +
                                    Math.abs(color[3] - ((matrix[i + k][j + l]) & (255)));
                            if (thisLen < len) {
                                len = thisLen;
                                newColor = matrix[i + k][j + l];
                            }
                        }
                    }
                }
                result[i / step][j / step] = newColor;
            }

        }
        return result;

    }

    int[][] sampling(int[][] img, int multiplier) {
        int[][] result = new int[img.length * multiplier][img[0].length * multiplier];
        for (int i = 0; i < img.length; i++) {
            for (int i1 = 0; i1 < img[0].length; i1++) {
                for (int j = 0; j < multiplier; j++) {
                    for (int k = 0; k < multiplier; k++) {
                        result[i*multiplier + j][i1*multiplier + k] = img[i][i1];
                    }
                }
            }
        }
        return result;
    }

}

