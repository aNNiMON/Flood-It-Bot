package main;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * ������ � ������� Robot. 
 * @author aNNiMON
 */
public class RobotUtils {

    private static final int CLICK_DELAY = 300;
    private Robot robot;

    /**
     * �����������
     * @throws AWTException ������ ������������� Robot
     */
    public RobotUtils() throws AWTException {
        robot = new Robot();
    }

    /**
     * �������� � ������ �����
     * @param click ����� �� ������� ����� ��������
     */
    public void clickPoint(Point click) {
        robot.mouseMove(click.x, click.y);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.delay(CLICK_DELAY);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }
    
    /**
     * ������������� ������������� �������� ������������������ �������
     * @param buttons ���������� �����, ���� ������� ��������
     * @param result ������������������ id ��� �������� �� ������ ������
     */
    public void autoClick(Point[] buttons, byte[] result) {
        for (int i = 0; i < result.length; i++) {
            clickPoint(buttons[result[i]]);
        }
    }
    /**
     * �������������� ��������� ���������
     * @param text "����������" �����
     */
    public void writeMessage(String text) {
        for (char symbol : text.toCharArray()) {
            boolean needShiftPress = Character.isUpperCase(symbol) && Character.isLetter(symbol);
            if(needShiftPress) {
                robot.keyPress(KeyEvent.VK_SHIFT);
            }
            int event = KeyEvent.getExtendedKeyCodeForChar(symbol);
            try {
                robot.keyPress(event);
            } catch (Exception e) {}
            if(needShiftPress) {
                robot.keyRelease(KeyEvent.VK_SHIFT);
            }
        }
    }
    
    /*
     * ��������� �������� �������� [width x height] � ������ � ������� [x, y]
     * ���� width ��� height ����� -1, �� ���������� ���� �����.
     */
    public BufferedImage getImage(int x, int y, int width, int height) {
        Rectangle area;
        if ((width == -1) || (height == -1)) {
            area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        } else area = new Rectangle(x, y, width, height);
        return robot.createScreenCapture(area);
    }

}
