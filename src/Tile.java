import Entities.Entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;

public class Tile implements Serializable {

      private static final long serialVersionUID = 1L;

      public double terrainHeight = 0;

      public ArrayList<Entity> enteties = new ArrayList<>();
      public ArrayList<Tile> animation = new ArrayList<>(); // ALWAYS BUFFERS INDEX 0 OVER TILE

      private String text = "  ";
      private Color fg = Color.WHITE;
      private Color bg = Color.BLACK;

      private int row;
      private int col;

      public BufferedImage tileImage;

      public Color getBg() {
            return bg;
      }

      public void clientSetText(String text) {
            this.text = text;
            GamePanel.bufferTile(row, col, this);
      }

      public void clientSetFg(Color fg) {
            this.fg = fg;
            GamePanel.bufferTile(row, col, this);
      }

      public void clientSetBg(Color bg) {
            this.bg = bg;
            GamePanel.bufferTile(row, col, this);
      }

      /**
       * Use Tile(int row, int col)
       */
      Tile() {
            super();
      }

      /*
       * ALSO BUFFERS TILE
       */
      Tile(Tile tile) {
            text = tile.text;
            fg = tile.fg;
            bg = tile.bg;
            animation = tile.animation;
            GamePanel.bufferTile(row, col, this);
      }

      Tile(int row, int col, double terrainHeight) {
            super();
            this.row = row;
            this.col = col;
            setTerrainHeight(terrainHeight);
      }

      Tile(int row, int col) {
            super();
            this.row = row;
            this.col = col;
      }

      public void clientBufferTile() {
            if (animation.size() > 0) {
                  GamePanel.bufferTile(row, col, animation.get(0));
                  return;
            }
            GamePanel.bufferTile(row, col, this);
      }

      /**
       * BUFFERS TILE AS WELL
       * 
       * @param tile
       */
      public void clientSetTile(Tile tile) {
            text = tile.text;
            fg = tile.fg;
            bg = tile.bg;
            GamePanel.bufferTile(row, col, this);
      }

      public void clientSetTile(TILEDATA tile) {
            text = tile.text;
            fg = tile.fg;
            bg = tile.bg;
            GamePanel.bufferTile(row, col, this);
      }

      public void serverSetTile(TILEDATA tile) {
            text = tile.text;
            fg = tile.fg;
            bg = tile.bg;
      }

      public void compile() {
            tileImage = new BufferedImage(GameClient.tileHeight, GameClient.tileWidth, BufferedImage.TYPE_INT_ARGB);
            Graphics2D tileGraphics = tileImage.createGraphics();
            tileGraphics.setColor(bg);
            tileGraphics.fillRect(0, 0, GameClient.tileWidth, GameClient.tileHeight);
            tileGraphics.setFont(GameClient.font);
            tileGraphics.setColor(fg);
            tileGraphics.drawString(text, (int) (GameClient.displayScale * (9 - (text.length() * 4.5))),
                        (int) (10 * GameClient.displayScale));
            tileGraphics.dispose();
      }

      public void setTerrainHeight(double terrainHeight) {
            this.terrainHeight = terrainHeight;
            double steps = 5;
            double threshold = 0.1;
            if (terrainHeight > threshold) {
                  double val = terrainHeight;
                  int translatedValue = 5;
                  for (double i = steps; i > 0; i--) {
                        if (val > threshold + (((1 - threshold) / steps) * (i - 1))) {
                              serverSetTile(TILEDATA.getByKey("" + translatedValue));
                              return;
                        }
                        translatedValue--;
                  }
            }
            serverSetTile(TILEDATA.getByKey("0"));
      }

}