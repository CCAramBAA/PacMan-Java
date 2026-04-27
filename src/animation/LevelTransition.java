package animation;

import util.Constants;

import java.awt.*;

public class LevelTransition extends Animation {
    private String message;
    private int panelWidth, panelHeight;

    public LevelTransition(String message, int width, int height) {
        super(60, 50);
        this.message = message;
        this.panelWidth = width;
        this.panelHeight = height;
    }

    @Override
    public void draw(Graphics2D g2d, int x, int y) {
        float progress = getProgress();

        if (progress < 0.2) {
            float alpha = progress / 0.2f;
            g2d.setColor(new Color(255, 255, 0, (int)(255 * alpha)));
        } else if (progress > 0.8) {
            float alpha = (1 - progress) / 0.2f;
            g2d.setColor(new Color(255, 255, 0, (int)(255 * alpha)));
        } else {
            g2d.setColor(Constants.MENU_TITLE_COLOR);
        }

        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (panelWidth - fm.stringWidth(message)) / 2;
        int textY = panelHeight / 2;

        g2d.drawString(message, textX, textY);

        if (progress > 0.3 && progress < 0.7) {
            g2d.setFont(new Font("Arial", Font.PLAIN, 24));
            String subMessage = "Get Ready!";
            fm = g2d.getFontMetrics();
            int subX = (panelWidth - fm.stringWidth(subMessage)) / 2;
            int subY = panelHeight / 2 + 60;
            g2d.drawString(subMessage, subX, subY);
        }
    }
}
