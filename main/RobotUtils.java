package main;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * –абота с классом Robot. 
 * @author aNNiMON
 */
public class RobotUtils {

    private static final int CLICK_DELAY = 300;
    private Robot robot;

    /**
     *  онструктор
     * @throws AWTException ошибка инициализации Robot
     */
    public RobotUtils() throws AWTException {
        robot = new Robot();
    }

    /**
     *  ликнуть в нужную точку
     * @param click точка по которой нужно кликнуть
     */
    public void clickPoint(Point click) {
        robot.mouseMove(click.x, click.y);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.delay(CLICK_DELAY);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }
    
    /**
     * јвтоматически воспроизвести заданную последовательность нажатий
     * @param buttons координаты точек, куда следует нажимать
     * @param result последовательность id дл€ указани€ на нужную кнопку
     */
    public void autoClick(Point[] buttons, byte[] result) {
        for (int i = 0; i < result.length; i++) {
            clickPoint(buttons[result[i]]);
        }
    }
    /**
     * јвтоматическое написание сообщени€
     * @param text "печатаемый" текст
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
     * ѕолучение картинки размером [width x height] с экрана с позиции [x, y]
     * ≈сли width или height равны -1, то возвращаем весь экран.
     */
    public BufferedImage getImage(int x, int y, int width, int height) {
        Rectangle area;
        if ((width == -1) || (height == -1)) {
            area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        } else area = new Rectangle(x, y, width, height);
        return robot.createScreenCapture(area);
    }

}
