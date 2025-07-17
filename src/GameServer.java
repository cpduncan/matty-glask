import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import Terrain.SmoothNoise;

public class GameServer extends Thread {

      private Socket[] clients;
      private ServerSocket serverSocket;
      private int port;

      private static final int mapRows = 37;
      private static final int mapCols = 37;
      private Tile[][] tileMap = new Tile[mapRows][mapCols];

      public GameServer(int port, int clientMax) {
            this.port = port;
            this.start();
            clients = new Socket[clientMax];
            newMap();
      }

      public void close() {
            for (Socket client : clients) {
                  if (client != null && !client.isClosed()) {
                        try {
                              client.close();
                        } catch (IOException e) {
                              e.printStackTrace();
                        }
                  }
            }
            try {
                  if (serverSocket != null)
                        serverSocket.close();
            } catch (IOException e) {
                  e.printStackTrace();
            }
      }

      public void newMap() {
            double[][] heightMap = SmoothNoise.getMap(mapRows, mapCols, 0.1, 0.55);
            for (int row = 0; row < mapRows; row++) {
                  for (int col = 0; col < mapCols; col++) {
                        tileMap[row][col] = new Tile(row, col, heightMap[row][col]);
                  }
            }
      }

      public String getLocalSocketAddress() {
            try {
                  return InetAddress.getLocalHost().getHostAddress() + ":" + port;
            } catch (UnknownHostException e) {
                  return null;
            }
      }

      @Override
      public void run() {
            try {
                  serverSocket = new ServerSocket(port);
                  ObjectInputStream[] inputStreams = new ObjectInputStream[clients.length];
                  ObjectOutputStream[] outputStreams = new ObjectOutputStream[clients.length];

                  for (int clientNum = 0; clientNum < clients.length; clientNum++) {
                        Socket client = serverSocket.accept();
                        clients[clientNum] = client;
                        outputStreams[clientNum] = new ObjectOutputStream(client.getOutputStream());
                        inputStreams[clientNum] = new ObjectInputStream(client.getInputStream());
                        System.out.println("> Connect: Client " + (clientNum + 1));
                  }

                  while (true) {
                        for (int i = 0; i < clients.length; i++) {
                              testTurn(inputStreams[i], outputStreams[i]);
                              sendUpdates(outputStreams);
                        }
                  }

            } catch (Exception e) {
                  e.printStackTrace();
            }
      }

      private void sendUpdates(ObjectOutputStream[] outputStreams) {
            try {
                  for (int k = 0; k < clients.length; k++) {
                        outputStreams[k].reset();
                        outputStreams[k].writeObject("UPDATE");
                        outputStreams[k].writeObject(tileMap);
                        outputStreams[k].flush();
                  }
            } catch (IOException e) {
                  e.printStackTrace();
            }
      }

      private void testTurn(ObjectInputStream inputStream, ObjectOutputStream outputStream)
                  throws IOException, ClassNotFoundException {

            outputStream.writeObject("TURN");
            outputStream.flush();

            outputStream.writeObject("d -> reroll_map *-> skip");
            outputStream.flush();

            if ((char) inputStream.readObject() == 'D') {
                  newMap();
            }
      }
}
