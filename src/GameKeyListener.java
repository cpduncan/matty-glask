import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameKeyListener implements KeyListener {

      private String output = "";
      private boolean prompting = false;
      private int fieldX1;
      private int fieldX2;
      private int fieldY;
      private Tile[][] tileArray = GameClient.screenTiles;
      private TILEDATA fieldBackground;
      private Tile[] tiles;

      private boolean shiftToggle = false;

      public boolean escapeField = false;

      @Override
      public void keyTyped(KeyEvent e) {
            if (prompting) {
                  if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE || e.getKeyChar() == KeyEvent.VK_DELETE) {
                        output = output.substring(0, output.length() - 1);
                  } else {
                        if (output.length() < fieldX2 - fieldX1)
                              output += ("" + e.getKeyChar()).toUpperCase();
                  }

                  for (int letterPos = 0; letterPos < fieldX2 - fieldX1; letterPos++) {
                        Tile tile = tiles[letterPos];
                        Tile newTile = new Tile(fieldY, fieldX1 + letterPos);
                        newTile.clientSetTile(fieldBackground);
                        if (letterPos < output.length()) {
                              newTile.clientSetText(output.charAt(letterPos) + "");
                              tile.animation.set(0, newTile);
                              tile.clientBufferTile();
                        } else {
                              tile.animation.set(0, newTile);
                              tile.clientBufferTile();
                        }
                  }
            }
            GameClient.gp.repaint();
      }

      @Override
      public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                  shiftToggle = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE && shiftToggle) {
                  GameClient.close();
            }
            if (prompting) {
                  if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        escapeField = true;
                  }
                  if (e.getKeyChar() == '\n') {
                        prompting = false;
                  }
            }
      }

      @Override
      public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                  shiftToggle = false;
            }
      }

      /**
       * Includes fieldX1, excludes fieldX2.
       * 
       * @param startCol
       * @param endCol
       * @param row
       * @return
       */
      public String prompt(int startCol, int endCol, int row, TILEDATA fieldBackground) {
            this.escapeField = false;
            this.fieldX1 = startCol;
            this.fieldX2 = endCol;
            this.fieldY = row;
            this.fieldBackground = fieldBackground;

            tiles = new Tile[endCol - startCol];
            for (int col = 0; col < endCol - startCol; col++) {
                  tiles[col] = tileArray[row][col + startCol];
                  Tile tile = new Tile(row, col + startCol);
                  tile.clientSetTile(fieldBackground);
                  tiles[col].animation.add(tile);
                  tile.clientBufferTile();
            }
            GameClient.gp.repaint();
            output = "";
            prompting = true;
            Thread thread = new Thread(() -> {
                  while (prompting && !escapeField) {
                        try {
                              Thread.sleep(10);
                        } catch (InterruptedException e) {
                              e.printStackTrace();
                        }
                  }
            });
            thread.start();
            try {
                  thread.join();
            } catch (InterruptedException e) {
                  e.printStackTrace();
            }
            for (int col = 0; col < endCol - startCol; col++) {
                  Tile tile = tiles[col];
                  tile.animation.removeFirst();
                  tile.clientBufferTile();
            }
            if (escapeField) {
                  escapeField = false;
                  return "ESCAPE";
            }
            return output;
      }
}