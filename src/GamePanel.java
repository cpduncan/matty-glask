import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class GamePanel extends JPanel {

      public static BufferedImage screenBuffer;

      private int offsetX;
      private int offsetY;

      public void setOffsetX(int offsetX) {
            this.offsetX = offsetX;
      }

      public void setOffsetY(int offsetY) {
            this.offsetY = offsetY;
      }

      public GamePanel(int w, int h) {
            this.setPreferredSize(new Dimension(w, h));
            screenBuffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
      }

      public static void bufferTile(int row, int col, Tile tile) {
            tile.compile();
            BufferedImage tileImage = tile.tileImage;
            Graphics2D g2 = (Graphics2D) screenBuffer.getGraphics();
            g2.drawImage(tileImage, col * GameClient.tileWidth, row * GameClient.tileHeight, null);
            g2.dispose();
      }

      @Override
      protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.drawImage(screenBuffer, offsetX, offsetY, null);
      }

}
