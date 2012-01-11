package main;

import java.util.ArrayList;

/**
 * ����� ������ ����.
 * @author aNNiMON
 */
public class BotFloodIt {
    
    /* ���������� ������ � ���� */
    private static final int MAX_COLORS = 6;
    /* �� ������� ����� ����� ������������ ��� */
    private static final int FILL_STEPS = 4;
    
    /* ������� ���� */
    private byte[][] table;
    /* �����, ��������������� ID */
    private int[] colors;

    public BotFloodIt(int[][] table) {
        colors = new int[MAX_COLORS];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = -1;
        }
        this.table = colorsToIds(table);
    }
    
    /**
     * �������� ����� ������ � �������
     * @return ������ ������ RGB
     */
    public int[] getColors() {
        return colors;
    }
    
    /**
     * �������� ������������������ ������� ������
     * @return ������ � ���������������� ������ ��� �������
     */
    public byte[] getFillSequence() {
        byte[][] copyTable = copyTable(table);
        ArrayList<Byte> seq = new ArrayList<Byte>();
        while(!gameCompleted(copyTable)) {
            seq.add(getNextFillColor(copyTable));
        }
        byte[] out = new byte[seq.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = seq.get(i).byteValue();
        }
        return out;
    }
    
    /*
     * �������� ������ ���������� ����� ��� �������
     */
    private byte getNextFillColor(byte[][] table) {
        // ���������� ��������� �������
        int fillSize = (int) Math.pow(MAX_COLORS, FILL_STEPS);
        int[] fillRate = new int[fillSize];
        // ��������� ���������� ������� �������
        int[] fillPow = new int[FILL_STEPS];
        for (int i = 0; i < FILL_STEPS; i++) {
            fillPow[i] = (int) Math.pow(MAX_COLORS, i);
        }
        // �������� FILL_STEPS ��� MAX_COLORS ���������
        for (int i = 0; i < fillSize; i++) {
            byte[][] iteration = copyTable(table);
            for (int j = 0; j < FILL_STEPS; j++) {
                byte fillColor =  (byte) (i / fillPow[j] % MAX_COLORS);
                fillTable(iteration, fillColor);
            }
            // ������������ ����� ������� �����
            fillRate[i] = getFillCount(iteration);
        }
        // ������ ���� ����������� ������� ������� �� FILL_STEPS �������� �������
        int maxArea = fillRate[0];
        int maxColor = 0;
        for (int i = 1; i < fillSize; i++) {
            if (fillRate[i] > maxArea) {
                maxColor = i;
                maxArea = fillRate[i];
            }
        }
        // �������� ���� � ���������� �������� ���������� �������
        byte colorID = (byte) (maxColor % MAX_COLORS);
        fillTable(table, colorID);
        return colorID;
    }
    
    /*
     * �������������� ������� � ������� � ������ � ����������������
     */
    private byte[][] colorsToIds(int[][] tableColor) {
        int size = tableColor.length;
        byte[][] out = new byte[size][size];
        int colorsReaded = 1; // ������� ������ ����������
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int color = tableColor[i][j];
                for (byte k = 0; k < colorsReaded; k++) {
                    // ��������� ���� � �������
                    if (colors[k] == -1) {
                        colors[k] = color;
                        colorsReaded++;
                        if (colorsReaded > MAX_COLORS) colorsReaded = MAX_COLORS;
                    }
                    // ���� ���� ��� � �������, �� ����������� ��� ID
                    if (color == colors[k]) {
                        out[i][j] = k;
                        break;
                    }
                }
            }
        }
        return out;
    }
    
    /**
     * ������ �������� ���� ������ color
     * @param table ������� ���� ��� �������
     * @param color ���� �������
     */
    private void fillTable(byte[][] table, byte color) {
        if (table[0][0] == color) return;
        fill(table, 0, 0, table[0][0], color);
    }
    
    /*
     * ������� ���� �� �����������
     */
    private void fill(byte[][] table, int x, int y, byte prevColor, byte color) {
        // �������� �� ����� �� ������� �������� ����
        if ( (x < 0) || (y < 0) || (x >= table.length) || (y >= table.length) ) return;
        if (table[x][y] == prevColor) {
            table[x][y] = color;
            // �������� ������� �������
            fill(table, x-1, y, prevColor, color);
            fill(table, x+1, y, prevColor, color);
            fill(table, x, y-1, prevColor, color);
            fill(table, x, y+1, prevColor, color);
        }
    }
    
    /**
     * �������� ���������� ������� �����
     * @param table ������� ����
     */
    private int getFillCount(byte[][] table) {
        return getCount(table, 0, 0, table[0][0]);
    }
    
    /*
     * ������� ������� ����� �� �����������
     */
    private int getCount(byte[][] table, int x, int y, byte color) {
        // �������� �� ����� �� ������� �������� ����
        if ( (x < 0) || (y < 0) || (x >= table.length) || (y >= table.length) ) return 0;
        int count = 0;
        if (table[x][y] == color) {
            table[x][y] = -1;
            count = 1;
            // ������� ������� ������
            count += getCount(table, x-1, y, color);
            count += getCount(table, x+1, y, color);
            count += getCount(table, x, y-1, color);
            count += getCount(table, x, y+1, color);
        }
        return count;
    }
    
    /*
     * ��������, ������ �� ��� ������� ����� ������
     */
    private boolean gameCompleted(byte[][] table) {
        byte color = table[0][0];
        int size = table.length;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (table[i][j] != color) return false;
            }
        }
        return true;
    }
    
    /*
     * ����������� ������� �������� ����
     */
    private byte[][] copyTable(byte[][] table) {
        int size = table.length;
        byte[][] out = new byte[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(table[i], 0, out[i], 0, size);
        }
        return out;
    }
}
