import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class GameClient {

      private static final String version = "0.4";

      public static JFrame frame;
      public static GamePanel gp;
      public static Font font;
      private static GameKeyListener input;
      private static GameServer server;
      private static Socket socket;
      private static ObjectOutputStream outputStream = null;
      private static ObjectInputStream inputStream = null;

      public static int tileHeight;
      public static int tileWidth;
      public static double displayScale;

      private static final double fontScale = 0.030;
      private static final int screenRows = 37;
      private static final int screenCols = 65;
      public static Tile[][] screenTiles = new Tile[screenRows][screenCols];
      private static ArrayList<Text> texts = new ArrayList<>();

      public static void main(String[] args) {

            initClient();
            splashScreen();
            connect();

            // TEMPORARY
            for (int row = 0; row < screenRows; row++) {
                  for (int col = 37; col < screenCols; col++) {
                        screenTiles[row][col].clientSetTile(TILEDATA.BLANK_0);
                  }
            }

            while (true) {
                  try {
                        switch ((String) inputStream.readObject()) {
                              case "TURN":
                                    testTurn(inputStream, outputStream);
                                    break;
                              case "UPDATE":
                                    recieveUpdate(inputStream);
                                    break;
                        }
                  } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                  }
            }
      }

      private static void splashScreen() {
            Thread thread = new Thread(() -> {
                  try {
                        Thread.sleep(200);
                        showScreen(new File("res/splash1.png"));
                        Thread.sleep(150);
                        showScreen(new File("res/splash2.png"));
                        Thread.sleep(1200);
                        showScreen(new File("res/splash3.png"));
                        Thread.sleep(1500);
                  } catch (Exception e) {
                        e.printStackTrace();
                  }
            });
            thread.start();
            try {
                  thread.join();
            } catch (InterruptedException e) {
                  e.printStackTrace();
            }
      }

      private static void showScreen(File file) {
            BufferedImage img = null;
            try {
                  img = ImageIO.read(file);
            } catch (IOException e) {
                  e.printStackTrace();
            }
            int width = img.getWidth();
            int height = img.getHeight();
            for (int y = 0; y < height; y++) {
                  for (int x = 0; x < width; x++) {
                        int pixel = img.getRGB(x, y);
                        Color color = new Color(pixel, true);
                        Tile tile = screenTiles[y][x];
                        tile.clientSetBg(color);
                  }
            }
            gp.repaint();
      }

      private static void testTurn(ObjectInputStream inputStream, ObjectOutputStream outputStream)
                  throws ClassNotFoundException, IOException {

            String prompt = (String) inputStream.readObject();
            texts.add(new Text(3, 39, prompt, TILEDATA.TEXT_0.fg));
            gp.repaint();

            char thingInput = (input.prompt(39, 40, 5, TILEDATA.FIELD_0) + " ").charAt(0);
            outputStream.writeObject(thingInput);
            outputStream.flush();
      }

      private static void setTilemap(Tile[][] tileMap) {
            for (int row = 0; row < 37; row++) {
                  for (int col = 0; col < 37; col++) {
                        screenTiles[row][col].clientSetTile(tileMap[row][col]);
                  }
            }
      }

      public static void recieveUpdate(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
            setTilemap((Tile[][]) inputStream.readObject());
            gp.repaint();
      }

      public static void close() {
            try {
                  if (outputStream != null)
                        outputStream.close();
                  if (inputStream != null)
                        inputStream.close();
                  if (socket != null && !socket.isClosed())
                        socket.close();
                  if (server != null)
                        server.close();
            } catch (IOException e) {
                  e.printStackTrace();
            }
            frame.dispose();
            System.exit(0);
      }

      private static void disposeText() {
            for (Text text : texts) {
                  text.dispose();
            }
      }

      private static void connect() {
            String error = "";
            while (socket == null) {
                  disposeText();
                  if (!error.isEmpty()) {
                        texts.add(new Text(18, 31 - (error.length() / 2), error, TILEDATA.TEXT_0.fg));
                        error = "";
                  }
                  String[] ipSplit;
                  texts.add(new Text(17, 17, "campaign     host        join", TILEDATA.TEXT_0.fg));
                  texts.add(new Text(19, 27, ">", TILEDATA.TEXT_0.fg));
                  String choice = input.prompt(29, 37, 19, TILEDATA.FIELD_0);
                  if (choice == "ESCAPE") {
                        close();
                  }
                  home: switch (choice) {
                        case "1":
                        case "C":
                        case "CAMP":
                        case "CAMPAIGN":
                              disposeText();
                              server = new GameServer(5000, 1);
                              String serverIp = server.getLocalSocketAddress();
                              ipSplit = serverIp.split(":");
                              try {
                                    socket = new Socket(ipSplit[0], Integer.parseInt(ipSplit[1]));
                              } catch (NumberFormatException | IOException e) {
                                    error = "failed to establish server connection";
                              }
                              break;
                        case "2":
                        case "H":
                        case "HOST":
                              int players = 0;
                              disposeText();
                              texts.add(new Text(17, 26, "playercount", TILEDATA.TEXT_0.fg));
                              texts.add(new Text(19, 29, ">", TILEDATA.TEXT_0.fg));
                              while (players == 0 || players > 4 || players < 0) {
                                    try {
                                          players = Integer
                                                      .parseInt(input.prompt(31, 33, 19, TILEDATA.FIELD_0));
                                    } catch (Exception e) {
                                          break home;
                                    }
                              }
                              disposeText();
                              server = new GameServer(5000, players);
                              serverIp = server.getLocalSocketAddress();
                              texts.add(new Text(1, 39, serverIp, TILEDATA.TEXT_0.fg));
                              gp.repaint();
                              ipSplit = serverIp.split(":");
                              try {
                                    socket = new Socket(ipSplit[0], Integer.parseInt(ipSplit[1]));
                              } catch (NumberFormatException | IOException e) {
                                    error = "failed to establish server connection";
                              }
                              break;
                        case "3":
                        case "J":
                        case "JOIN":
                              disposeText();
                              texts.add(new Text(16, 28, "ip:port", TILEDATA.TEXT_0.fg));
                              serverIp = input.prompt(22, 43, 18, TILEDATA.FIELD_0);
                              if (serverIp == "ESCAPE") {
                                    break home;
                              }
                              try {
                                    ipSplit = serverIp.split(":");
                                    socket = new Socket(ipSplit[0], Integer.parseInt(ipSplit[1]));
                                    disposeText();
                                    break;
                              } catch (IOException e) {
                                    error = "failed to establish server connection";
                              } catch (ArrayIndexOutOfBoundsException e) {
                                    error = "incorrect ip pattern";
                              }
                              break;
                        default:
                              break;
                  }
            }
            try {
                  outputStream = new ObjectOutputStream(socket.getOutputStream());
                  inputStream = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                  e.printStackTrace();
            }
      }

      private static void initClient() {
            frame = new JFrame("MattyGlask" + version);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().setBackground(Color.BLACK);
            frame.setLayout(new GridBagLayout());
            frame.setSize(800, 600);
            frame.setUndecorated(true);

            GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            if (graphicsDevice.isFullScreenSupported()) {
                  graphicsDevice.setFullScreenWindow(frame);
            } else {
                  frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }

            int screenWidth = frame.getWidth();
            int screenHeight = frame.getHeight();

            double ratioWidthHeight = (double) 16 / 9;
            double ratioHeightWidth = (double) 9 / 16;
            double whCalc = screenHeight * screenHeight * ratioWidthHeight;
            double hwCalc = screenWidth * screenWidth * ratioHeightWidth;

            int gameWidth = screenWidth;
            int gameHeight = screenHeight;
            if (hwCalc < whCalc) {
                  gameHeight = (int) (screenWidth * ratioHeightWidth);
            } else {
                  gameWidth = (int) (screenHeight * ratioWidthHeight);
            }

            gp = new GamePanel(gameWidth, gameHeight);
            gp.setBackground(Color.BLACK);
            tileHeight = gameHeight / screenRows;
            tileWidth = gameWidth / screenCols;
            gp.setOffsetX((gameWidth - (tileWidth * screenCols)) / 2);
            gp.setOffsetY((gameHeight - (tileHeight * screenRows)) / 2);
            frame.add(gp);

            frame.setVisible(true);

            int tileSize = Math.min(tileHeight, tileWidth);

            try {
                  int testSize = 12;
                  font = Font.createFont(Font.TRUETYPE_FONT, new File("res/bpdots.unicasesquare-bold.otf"))
                              .deriveFont((float) tileSize * 0.8f);
                  FontMetrics fm = gp.getGraphics().getFontMetrics();
                  int fontHeight = fm.getHeight();
                  displayScale = (gameHeight * fontScale) / fontHeight;
                  int finalFontSize = (int) (testSize * displayScale);
                  font = font.deriveFont((float) finalFontSize);
                  gp.getGraphics().setFont(font);
            } catch (FontFormatException | IOException e) {
                  e.printStackTrace();
            }

            input = new GameKeyListener();
            frame.addKeyListener(input);

            for (int row = 0; row < screenRows; row++) {
                  for (int col = 0; col < screenCols; col++) {
                        Tile tile = new Tile(row, col);
                        tile.clientSetTile(TILEDATA.BLANK_0);
                        tile.clientBufferTile();
                        screenTiles[row][col] = tile;
                  }
            }
            gp.repaint();
      }

}
