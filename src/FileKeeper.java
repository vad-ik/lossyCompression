import java.io.*;
import java.util.ArrayList;

public class FileKeeper {
    public void savePhoto(int[][] a, String path) {
        try {
            FileOutputStream stream = new FileOutputStream(path);
            stream.write(a.length);
            stream.write(a[0].length);
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < a[0].length; j++) {
                    byte r = (byte) (a[i][j] >> 16 & 0xFF);
                    byte g = (byte) (a[i][j] >> 8 & 0xFF);
                    byte b = (byte) (a[i][j] & 0xFF);

                    stream.write(r);
                    stream.write(g);
                    stream.write(b);
                }
            }
            stream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void savePhoto(int[][] y, int[][] cb, int[][] cr, String path) {
        try {
            FileOutputStream stream = new FileOutputStream(path);
            stream.write(y.length);
            stream.write(y[0].length);

            for (int i = 0; i < y.length; i++) {
                for (int j = 0; j < y[0].length; j++) {
                    stream.write((byte) (y[i][j] & 0xFF));
                }
            }
            for (int i = 0; i < cb.length; i++) {
                for (int j = 0; j < cb[0].length; j++) {
                    stream.write((byte) (cb[i][j] & 0xFF));
                }
            }
            for (int i = 0; i < cr.length; i++) {
                for (int j = 0; j < cr[0].length; j++) {
                    stream.write((byte) (cr[i][j] & 0xFF));
                }
            }
            stream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void savePhotoDCT(int[][][][] y, int[][][][] cb, int[][][][] cr, String path) {
        try {
            FileOutputStream stream = new FileOutputStream(path);
            stream.write(y.length);
            stream.write(y[0].length);
            write(y, stream);
            write(cb, stream);
            write(cr, stream);

            stream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void savePhotoQ(ArrayList<Integer>[] acY, ArrayList<Integer>[][] acC, String path, int x, int y, int qwant) {
        try {
            StringBuilder sb = new StringBuilder();
            int type = findMax(acY, acC);//1-8bit,2-16bit на пиксель в канале
            sb.append(type - 1);
            sb.append(String.format("%7s", Integer.toBinaryString(qwant)).replace(' ', '0'));
            sb.append(String.format("%15s", Integer.toBinaryString(x)).replace(' ', '0'));
            sb.append(String.format("%15s", Integer.toBinaryString(y)).replace(' ', '0'));


            for (int i = 0; i < acY.length; i++) {
                for (int j = 0; j < acY[i].size(); j += 2) {
                    sb.append(String.format("%5s", Integer.toBinaryString(acY[i].get(j))).replace(' ', '0'));

                    int l = acY[i].get(j + 1);
                    if (l < 0) {

                        l *= -1;
                        if (type == 1) {
                            l |= 128;
                        } else {
                            l |= 1024;
                        }
                    }
                    if (type == 1) {
                        l = l & 255;
                        sb.append(String.format("%8s", Integer.toBinaryString(l)).replace(' ', '0'));

                    } else {
                        l = l & 2047;
                        sb.append(String.format("%11s", Integer.toBinaryString(l)).replace(' ', '0'));
                    }
                }
            }
            for (int k = 0; k < 2; k++) {


                for (int i = 0; i < acC.length; i++) {
                    for (int j = 0; j < acC[i][k].size(); j += 2) {
                        sb.append(String.format("%5s", Integer.toBinaryString(acC[i][k].get(j))).replace(' ', '0'));

                        int l = acC[i][k].get(j + 1);
                        if (l < 0) {

                            l *= -1;
                            if (type == 1) {
                                l |= 128;
                            } else {
                                l |= 1024;
                            }
                        }
                        if (type == 1) {
                            l = l & 255;
                            sb.append(String.format("%8s", Integer.toBinaryString(l)).replace(' ', '0'));

                        } else {
                            l = l & 2047;
                            sb.append(String.format("%11s", Integer.toBinaryString(l)).replace(' ', '0'));
                        }
                    }
                }
            }
            FileOutputStream stream = new FileOutputStream(path);


                int last =8- sb.length() % 8;
                for (int i = 0; i < last; i++) {
                    sb.append("0");
                }
                sb.append(String.format("%8s", Integer.toBinaryString(last)).replace(' ', '0'));

                for (int i = 0; i < sb.length(); ) {
                    int k = 0;
                    for (int j = 0; j < 8; j++, i++) {
                        k = k << 1;
                        k += Integer.parseInt(sb.charAt(i) + "");
                    }
                    stream.write((byte) k);
                }

            stream.close();


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getPhotoQ(ArrayList<Integer>[] acY, ArrayList<Integer>[][] acC, String path) {
        int qwant = 0;
        try {
            StringBuilder sb = new StringBuilder();
            FileInputStream streamIn = new FileInputStream(path);
            BufferedInputStream streamBuff = new BufferedInputStream(streamIn);
            int ch;
            while ((ch = streamBuff.read()) != -1) {
                sb.append(String.format("%8s", Integer.toBinaryString(ch)).replace(' ', '0'));
            }
            streamBuff.close();
         int last=Integer.parseInt(sb.substring(sb.length()-8, sb.length()), 2);
            sb.delete(sb.length() - 8-last, sb.length());

            int type = Integer.parseInt(sb.substring(0, 1)) + 1;
            sb.delete(0, 1);
            qwant = Integer.parseInt(sb.substring(0, 7), 2);
            sb.delete(0, 7);
            int x = Integer.parseInt(sb.substring(0, 15), 2);
            sb.delete(0, 15);
            int y = Integer.parseInt(sb.substring(0, 15), 2);
            sb.delete(0, 15);


            acY = new ArrayList[x / 8 * y / 8];
            acC = new ArrayList[x / 8 * y / 8/2][2];

int cord=0;
            for (int i = 0; i < x / 8 * y / 8; i++) {
                acY[i] = new ArrayList<>();
                int n = 0;
                int j = 0;
                while (n < 64) {
                    int tmp=0;
                    for (int k = 0; k <5 ; k++) {
                        tmp <<= 1;
                        tmp += Integer.parseInt(sb.charAt(cord++) + "", 2);
                    }
                    acY[i].add( tmp);
                    tmp=0;
                    int l=0;
                    if (type==1) {
                        l=8;
                    }else {
                        l=11;
                    }
                    for (int k = 0; k <l ; k++) {
                        tmp <<= 1;
                        tmp += Integer.parseInt(sb.charAt(cord++) + "", 2);
                    }
                    if (type==1&&tmp>127) {
                      tmp-=128;
                      tmp*=(-1);
                    }else if (type==2&&tmp>1024){
                        tmp-=1024;
                        tmp*=(-1);
                    }
                    acY[i].add(j + 1, tmp);
                    n += acY[i].get(j) + 1;
                    j++;
                    j++;
                }
            }
            int cSize =x*y/128;


            for (int k = 0; k < 2; k++) {
                for (int i = 0; i < cSize; i++) {
                    int n = 0;
                    int j = 0;
                    acC[i][k] = new ArrayList<>();
                    while (n < 64) {
                        int tmp=0;
                        for (int b = 0; b <5 ; b++) {
                            tmp <<= 1;
                            tmp += Integer.parseInt(sb.charAt(cord++) + "", 2);
                        }
                        acC[i][k].add(j, tmp);
                        tmp=0;
                        int l=0;
                        if (type==1) {
                            l=8;
                        }else {
                            l=11;
                        }
                        for (int b = 0; b <l ; b++) {
                            tmp <<= 1;
                            tmp += Integer.parseInt(sb.charAt(cord++) + "", 2);
                        }
                        if (type==1&&tmp>127) {
                            tmp-=128;
                            tmp*=(-1);
                        }else if (type==2&&tmp>1024){
                            tmp-=1024;
                            tmp*=(-1);
                        }
                        acC[i][k].add(j + 1, tmp);
                        n += acC[i][k].get(j) + 1;

                        j++;
                        j++;
                    }
                }
            }


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return qwant;
    }

    int findMax(ArrayList<Integer>[] acY, ArrayList<Integer>[][] acC) {
        int max = 0;
        int min = 2048;
        for (int i = 0; i < acC.length; i++) {
            for (int j = 0; j < acC[i].length; j++) {
                for (int i1 = 0; i1 < acC[i][j].size(); i1++) {
                    if (acC[i][j].get(i1) > max) {
                        max = acC[i][j].get(i1);
                    }
                    if (acC[i][j].get(i1) < min) {
                        min = acC[i][j].get(i1);
                    }
                }
            }
        }

        for (int i = 0; i < acY.length; i++) {
            for (int j = 0; j < acY[i].size(); j++) {
                if (acY[i].get(j) > max) {
                    max = acY[i].get(j);
                }
                if (acY[i].get(j) < min) {
                    min = acY[i].get(j);
                }
            }
        }
        if (max > 127 || min < -127) {
            return 2;
        } else {
            return 1;
        }

    }

    void write(int[][][][] a, FileOutputStream stream) throws IOException {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                for (int k = 0; k < a[0][0].length; k++) {
                    for (int l = 0; l < a[0][0][0].length; l++) {
                        stream.write((byte) (a[i][j][k][l] >> 8 & 0xFF));

                        stream.write((byte) (a[i][j][k][l] & 0xFF));
                    }
                }

            }
        }
    }

}
