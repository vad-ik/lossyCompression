import java.util.ArrayList;

public class Rle {
    static ArrayList getRle(int[] zigzag, int start) {
        ArrayList<Integer> ans = new ArrayList<>();//15
        int zero = 0;
        for (int i = start; i < zigzag.length - 1; i++) {
            if (zigzag[i] == 0) {
                zero++;
                if (true) {

                    if (zero == 32) {
                        ans.add(zero - 1);
                        ans.add(zigzag[i]);
                        zero = 0;
                    }
                }
            } else {
                ans.add(zero);
                ans.add(zigzag[i]);
                zero = 0;
            }
        }
        ans.add(zero);
        ans.add(zigzag[zigzag.length - 1]);
        return ans;
    }

    static int[] getBackRle(ArrayList<Integer> zigzag, int start) {
        int[] ans = new int[64];
        ans[0] = 0;
        int k = start;
        for (int i = 0; i < zigzag.size(); i += 2) {
            int n = zigzag.get(i);
            for (int j = 0; j < n; j++) {
                ans[k++] = 0;
            }
            ans[k++] = zigzag.get(i + 1);
        }
        return ans;
    }

    static void getDcAc(ArrayList<Integer>[][] ac, int[][] dc, int[][] zigzagY, int[][] zigzagCb, int[][] zigzagCr) {
        dc[0][0] = zigzagY[0][0];
        dc[0][1] = zigzagCb[0][0];
        dc[0][2] = zigzagCr[0][0];

        ac[0][0] = getRle(zigzagY[0], 1);
        ac[0][1] = getRle(zigzagCb[0], 1);
        ac[0][2] = getRle(zigzagCr[0], 1);

        for (int i = 1; i < zigzagY.length; i++) {
            dc[i][0] = zigzagY[i][0] - dc[i - 1][0];
            dc[i][1] = zigzagCb[i][0] - dc[i - 1][1];
            dc[i][2] = zigzagCr[i][0] - dc[i - 1][2];

            ac[i][0] = getRle(zigzagY[i], 1);
            ac[i][1] = getRle(zigzagCb[i], 1);
            ac[i][2] = getRle(zigzagCr[i], 1);
        }
    }

    static void getDcAc(ArrayList<Integer>[][] acC, ArrayList<Integer>[] acY, int[][] zigzagY, int[][] zigzagCb, int[][] zigzagCr) {


        acY[0] = getRle(zigzagY[0], 0);
        acC[0][0] = getRle(zigzagCb[0], 0);
        acC[0][1] = getRle(zigzagCr[0], 0);

        for (int i = 1; i < zigzagY.length; i++) {


            acY[i] = getRle(zigzagY[i], 0);
        }

        for (int i = 1; i < zigzagY.length; i++) {
            acC[i][0] = getRle(zigzagCb[i], 0);
            acC[i][1] = getRle(zigzagCr[i], 0);
        }


    }

    static void getBackDcAc(ArrayList<Integer>[][] ac, int[][] dc, int[][] zigzagY, int[][] zigzagCb, int[][] zigzagCr) {


        zigzagY[0] = getBackRle(ac[0][0], 1);
        zigzagCb[0] = getBackRle(ac[0][1], 1);
        zigzagCr[0] = getBackRle(ac[0][2], 1);

        zigzagY[0][0] = dc[0][0];
        zigzagCb[0][0] = dc[0][1];
        zigzagCr[0][0] = dc[0][2];

        for (int i = 1; i < ac.length; i++) {
            zigzagY[i] = getBackRle(ac[i][0], 1);
            zigzagCb[i] = getBackRle(ac[i][1], 1);
            zigzagCr[i] = getBackRle(ac[i][2], 1);

            zigzagY[i][0] = dc[i][0] + dc[i - 1][0];
            zigzagCb[i][0] = dc[i][1] + dc[i - 1][1];
            zigzagCr[i][0] = dc[i][2] + dc[i - 1][2];
        }
    }

    static void getBackDcAc(ArrayList<Integer>[][] acC, ArrayList<Integer>[] acY, int[][] zigzagY, int[][] zigzagCb, int[][] zigzagCr) {


        zigzagY[0] = getBackRle(acY[0], 0);
        zigzagCb[0] = getBackRle(acC[0][0], 0);
        zigzagCr[0] = getBackRle(acC[0][1], 0);


        for (int i = 0; i < acY.length; i++) {
            zigzagY[i] = getBackRle(acY[i], 0);
        }
        for (int i = 0; i < acC.length; i++) {
            zigzagCb[i] = getBackRle(acC[i][0], 0);
            zigzagCr[i] = getBackRle(acC[i][1], 0);
        }
    }
}