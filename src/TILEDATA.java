import java.awt.Color;

public enum TILEDATA {

      BORDER_0("GUI_BORDER_1", "§§", new Color(180, 180, 180), new Color(60, 60, 60)),
      BLANK_0("GUI_BLANK_1", "  ", new Color(80, 80, 80), new Color(4, 4, 3)),
      TEXT_0("GUI_TEXT_1", "  ", new Color(175, 0, 7), new Color(4, 4, 3)),
      FIELD_0("GUI_FIELD_1", "  ", new Color(175, 0, 7), new Color(4, 4, 3)),

      TERRAIN_0("0", "  ", new Color(80, 80, 80), new Color(20, 20, 20)),
      TERRAIN_1("1", "\'\'", new Color(120, 120, 120), new Color(30, 30, 30)),
      TERRAIN_2("2", "//", new Color(140, 140, 140), new Color(40, 40, 40)),
      TERRAIN_3("3", "tt", new Color(160, 160, 160), new Color(60, 60, 60)),
      TERRAIN_4("4", "HH", new Color(180, 180, 180), new Color(80, 80, 80)),
      TERRAIN_5("5", "@@", new Color(200, 200, 200), new Color(100, 100, 100)),

      ;

      public final String key;
      public final String text;
      public final Color fg;
      public final Color bg;

      private TILEDATA(String key, String text, Color fg, Color bg) {
            this.key = key;
            this.text = text;
            this.fg = fg;
            this.bg = bg;
      }

      public static TILEDATA getByKey(String key) {
            for (TILEDATA e : values()) {
                  if (e.key.equals(key)) {
                        return e;
                  }
            }
            return null;
      }

}
