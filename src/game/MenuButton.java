package game;

import util.Constants;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuButton {
    private Rectangle bounds;
    private String text;
    private boolean hovered;
    private Runnable action;

    public MenuButton(String text, int x, int y, int width, int height, Runnable action) {
        this.text = text;
        this.bounds = new Rectangle(x, y, width, height);
        this.hovered = false;
        this.action = action;
    }

    public void draw(Graphics2D g2d) {
        Color buttonColor = hovered ? Constants.MENU_BUTTON_HOVER : Constants.MENU_BUTTON_COLOR;

        g2d.setColor(buttonColor);
        g2d.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 20, 20);

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 20, 20);

        Font font = loadChineseFont(Font.BOLD, 24);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int textX = bounds.x + (bounds.width - fm.stringWidth(text)) / 2;
        int textY = bounds.y + (bounds.height + fm.getAscent() - fm.getDescent()) / 2;

        g2d.setColor(Color.BLACK);
        g2d.drawString(text, textX, textY);
    }

    private Font loadChineseFont(int style, int size) {
        String[] fontNames = {"微软雅黑", "SimHei", "STSong", "KaiTi", "FangSong"};

        for (String fontName : fontNames) {
            Font font = new Font(fontName, style, size);
            if (font.canDisplay('中') && font.canDisplay('文')) {
                return font;
            }
        }

        return new Font(Font.SANS_SERIF, style, size);
    }

    public boolean containsPoint(int x, int y) {
        return bounds.contains(x, y);
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public void click() {
        if (action != null) {
            action.run();
        }
    }

    public String getText() {
        return text;
    }
}
