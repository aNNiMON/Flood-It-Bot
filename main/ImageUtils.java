package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * ����� ��������� �����������.
 * @author aNNiMON
 */
public class ImageUtils {
    
    /* �� ������� ����� �� ����� �������� ������������� ��� */
    private static final int MAX_COLOR_POINTS = 50;
    
    /* ���������������� � ������ ������ */
    private static final int FIND_BUTTON_TOLERANCE = 20;
    
    /* ����������� ���� */
    private BufferedImage image;
    /* ������ ����������� */
    private int w, h;
    /* ����������� ���� */
    private int boardSize;
    /* ������ ����� */
    private int cellSize;
    /* ���������� ���� �������� ���� */
    private Point board;
    /* ����������� ������������� ����������� */
    private boolean[] monochrome;
    
    /**
     * ����������� ��� ����������� ��������
     * @param image
     * @param boardSize 
     */
    public ImageUtils(BufferedImage image, int boardSize) {
        this.image = image;
        this.boardSize = boardSize;
        w = image.getWidth();
        h = image.getHeight();
    }
    
    /**
     * ����������� ��� �������� ��������
     * @param image 
     * @param boardSize
     * @param cellSize
     * @param x
     * @param y 
     */
    public ImageUtils(BufferedImage image, int boardSize, int cellSize, int x, int y) {
        this.image = image;
        this.boardSize = boardSize;
        this.cellSize = cellSize;
        this.board = new Point(x, y);
        w = image.getWidth();
        h = image.getHeight();
    }
    
    /**
     * �������� ������ ������
     * @return 
     */
    public int getCellSize() {
        return cellSize;
    }
    
    /**
     * �������� ���������� �������� ����
     * @return ����� � ������������ ������ �������� ���� ����
     */
    public Point getBoardParameters() {
        int[] pixels = new int[w * h];
        image.getRGB(0, 0, w, h, pixels, 0, w);
        monochrome = threshold(pixels, 64);
        board = getBoardXY(boardSize);
        return board;
    }
    
    /**
     * �������� ����������� �������� ����
     * @return �������� �������� ����
     */
    public BufferedImage getBoardImage() {
        int size = cellSize * boardSize;
        try {
            return image.getSubimage(board.x, board.y, size, size);
        } catch (Exception e) {
            return image;
        }
    }
    
    /**
     * �������� ���������� ������ ��� ��������������� �������
     * @param colors ������ ������, �� ������� ����� ������ ������
     * @return ������ ��������� � �������, ��� null - ���� �� ������� �����
     */
    public Point[] getButtons(int[] colors) {
        Point[] out = new Point[colors.length];
        // ������ �������� ���� � ��������
        int size = boardSize * cellSize;
        // ������� ������ �����������, �� ������� ����� ������ ������
        Rectangle[] partsOfImage = new Rectangle[] {
            new Rectangle(0, board.y, board.x, size),   // ����� �� ����
            new Rectangle(0, 0, w, board.y),            // ������ �� ����
            new Rectangle(board.x+size, board.y,
                          w-board.x-size, size),        // ������ �� ����
            new Rectangle(0, board.y+size,
                          w, h-board.x-size)            // ����� �� ����
        };
        
        for (int i = 0; i < partsOfImage.length; i++) {
            Rectangle rect = partsOfImage[i];
            BufferedImage part = image.getSubimage(rect.x, rect.y, rect.width, rect.height);
            // �������� ����� �����������, � ������� ����� ������
            boolean found = true;
            for (int j = 0; j < colors.length; j++) {
                if (colors[i] == -1) continue;
                Point pt = findButton(part, colors[j]);
                if (pt != null) {
                    // ��������� �������� ������������ ������ ��������
                    pt.translate(rect.x, rect.y);
                    out[j] = pt;
                } else {
                    found = false;
                    break;
                }
            }
            if (found) return out;
        }
        // �� ������� ����� ��� �����
        return null;
    }
    
    /**
     * ������������� ������ ������ � ����������� ���
     * @param ids ������ ��������������� ������������������
     * @param palette ������ ������� ������
     * @return ����������� ������������������ ������
     */
    public BufferedImage sequenceToImage(byte[] ids, int[] palette) {
        final int size = 20; // ������ ������ ������
        // ��������� ����� �� 10 ������ �� ������
        final int CELLS_IN_ROW = 10;
        int width = CELLS_IN_ROW * size;
        if (width == 0) width = size;
        int rows = ids.length / CELLS_IN_ROW;
        
        BufferedImage out = new BufferedImage(width, (rows*size)+size, BufferedImage.TYPE_INT_RGB);
        Graphics G = out.getGraphics();
        for (int i = 0; i < ids.length; i++) {
            G.setColor(new Color(palette[ids[i]]));
            G.fillRect(i % CELLS_IN_ROW * size,
                       i / CELLS_IN_ROW * size,
                       size, size);
        }
        G.dispose();
        return out;
    }
    
    /**
     * ������������� ������� ����������� � �����������.
     * ����� ����� ������, ��� ���� ���� ����������� �� �������
     * ����, �� ���������� ������������� �����������, �����
     * �������� �������� ����� ������� �� ����� ����.
     * @param pixels ������ �������� �����������
     * @param value ����������� ��������
     * @return ������ boolean, true - �����, false - ������
     */
    private boolean[] threshold(int[] pixels, int value) {
        boolean inverse = isBackgroundLight(MAX_COLOR_POINTS);
        if (inverse) value = 255 - value;
        boolean[] bw = new boolean[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            int brightNess = getBrightness(pixels[i]);
            bw[i] = (brightNess >= value) ^ inverse;
        }
        return bw;
    }
    
    /**
     * ��������� ��������� ������� ����.
     * @param numPoints ������� ����� ����� ��� �����������.
     * @return true - ��� �������, false - �����
     */
    private boolean isBackgroundLight(int numPoints) {
        // �������� numPoints ��������� �����
        Random rnd = new Random();
        int[] colors = new int[numPoints];
        for (int i = 0; i < numPoints; i++) {
            int x = rnd.nextInt(w);
            int y = rnd.nextInt(h);
            colors[i] = image.getRGB(x, y);
        }
        // ������� ������� ������� ���� numPoints �����
        long sum = 0;
        for (int i = 0; i < numPoints; i++) {
            int brightness = getBrightness(colors[i]);
            sum = sum + brightness;
        }
        sum = sum / numPoints;
        return (sum > 128);
    }
    
    /**
     * ���������� ���������� ����� ������� ������ �������� ����.
     * @param boardSize ����������� ���� (10x10, 14x14 � �.�.)
     * @return ���������� ������ �������� ��������������
     */
    private Point getBoardXY(int boardSize) {
        /*
         * ������� ���������� ���������� ����� ����� �� ����������� � ���������
         */
        int[] horizontal = new int[h];
        for (int i = 0; i < h; i++) {
            int count = 0;
            for (int j = 0; j < w; j++) {
                if (getBWPixel(j, i)) count++;
            }
            horizontal[i] = count;
        }

        int[] vertical = new int[w];
        for (int i = 0; i < w; i++) {
            int count = 0;
            for (int j = 0; j < h; j++) {
                if (getBWPixel(i, j)) count++;
            }
            vertical[i] = count;
        }
        
        /*
         * ����� "�����������" ������: ���������� ������� ��������
         * � �� ��� ������ ����� ������������ ������ � �������.
         */
        horizontal = filterByMean(horizontal);
        vertical = filterByMean(vertical);
        
        /*
         * ���� ���������� ��������� ������������������.
         * ������� ������ ������������������ � ����� ���������� ������� ����.
         */
        int[] vParam = getParamsFromSequence(horizontal);
        int[] hParam = getParamsFromSequence(vertical);
        

        int outX = hParam[0];
        int outY = vParam[0];
        int outWidth = hParam[1];
        int outHeight = vParam[1];
        // ������� ������� ������
        cellSize = Math.max((outWidth / boardSize), (outHeight / boardSize));
        return new Point(outX, outY); 
    }
    
    /**
     * ������ ������������������ �� ������������ ��������.
     * @param source ������������������ ��������� �����
     * @return ��������������� ������ �� ���������� 0 � 1
     */
    private int[] filterByMean(int[] source) {
        long mean = 0;
        for (int i = 0; i < source.length; i++) {
            mean += source[i];
        }
        mean = mean / source.length;
        for (int i = 0; i < source.length; i++) {
            source[i] = (source[i] > mean) ? 1 : 0;
        }
        return source;
    }
    
    /**
     * ����� ����� ������� ������������������ � �������.
     * @param source ������� ������������������ �� ����� � ������
     * @return ������ ���������� - ������ ������ ������������������ � � �����
     */
    private int[] getParamsFromSequence(int[] source) {
        int maxStart = 0, start = 0;
        int maxLength = 0, length = 0;
        for (int i = 1; i < source.length; i++) {
            if (source[i] == 0) {
                start = 0;
                length = 0;
                continue;
            }
            if (source[i] == source[i-1]) {
                length++;
                if (maxLength < length) {
                    maxStart = start;
                    maxLength = length;
                }
            } else {
                // ���� ���������� ������� ��� ������� - �������� ����� ������������������
                start = i;
            }
        }
        return new int[] {maxStart, maxLength};
    }
    
    /**
     * ����� ���������� ������ � ������ template
     * @param img �����������, �� ������� ����� ������
     * @param template ������ �����
     * @return ���������� X. Y, ��� null ���� �� �����
     */
    private Point findButton(BufferedImage img, int template) {
        int h2 = img.getHeight() / 2;
        // ������ ����� � �������� �� ���������, ��� ������� �����
        for (int y = 0; y < h2; y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int color = img.getRGB(x, h2 - y);
                if (isEquals(color, template, FIND_BUTTON_TOLERANCE)) {
                    return new Point(x, h2 - y);
                }
                color = img.getRGB(x, h2 + y);
                if (isEquals(color, template, FIND_BUTTON_TOLERANCE)) {
                    return new Point(x, h2 + y);
                }
            }
        }
        // �� �����
        return null;
    }
    
    /**
     * �������� �� ������������ ������ ���� �����
     * @param color1 ������ ����
     * @param color2 ������ ����
     * @param tolerance ����������������
     * @return true - �������������, false - ���
     */
    private boolean isEquals(int color1, int color2, int tolerance) {
        if (tolerance < 2) return color1 == color2;

        int r1 = (color1 >> 16) & 0xff;
        int g1 = (color1 >> 8) & 0xff;
        int b1 = color1 & 0xff;
        int r2 = (color2 >> 16) & 0xff;
        int g2 = (color2 >> 8) & 0xff;
        int b2 = color2 & 0xff;
        return (Math.abs(r1 - r2) <= tolerance) &&
               (Math.abs(g1 - g2) <= tolerance) &&
               (Math.abs(b1 - b2) <= tolerance);
    }
    
    /**
     * ��������� ������� �����
     * @param color �������� ����
     * @return ������� (0..255)
     */
    private int getBrightness(int color) {
        int qr = (color >> 16) & 0xff;
        int qg = (color >> 8) & 0xff;
        int qb = color & 0xff;
        return (qr + qg + qb) / 3;
    }
    
    /*
     * ��������� ����� �� ������������ �����������.
     * return true - �����, false - ������
     */
    private boolean getBWPixel(int x, int y) {
        if ((x < 0) || (y < 0) || (x >= w) || (y >= h)) return false;
        return monochrome[y * w + x];
    }

}