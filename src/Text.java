import java.awt.Color;

public class Text {

      private static Tile[][] screenTiles = GameClient.screenTiles;

      private int startCol;
      private int row;
      private String text;
      private Tile[] tiles;

      Text(int row, int startCol, String text, Color color) {
            text = text.toUpperCase();
            this.text = text;
            this.startCol = startCol;
            this.row = row;
            tiles = new Tile[text.length()];

            for (int col = 0; col < text.length(); col++) {
                  Tile screenTile = screenTiles[row][col + startCol];
                  if (col < text.length()) {
                        Tile tile = new Tile(row, col + startCol);
                        tile.clientSetFg(color);
                        tile.clientSetBg(screenTile.getBg());
                        tile.clientSetText(text.charAt(col) + "");
                        screenTile.animation.add(tile);
                  }
                  tiles[col] = screenTile;
            }
      }

      public void dispose() {
            for (Tile tile : tiles) {
                  tile.animation.clear();
                  tile.clientBufferTile();
            }
      }
}
