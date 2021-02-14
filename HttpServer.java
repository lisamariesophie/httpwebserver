import java.io.*;
import java.net.*;
import java.util.Date;

public class HttpServer extends Thread {

  private Socket client;
  private DataOutputStream out;
  private String request;

  private HttpServer(Socket socket) {
    client = socket;
  }

  public void run() {
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
      out = new DataOutputStream(client.getOutputStream());
      request = in.readLine();
      String path = request.substring(4, request.length() - 9).trim();
      if (request.startsWith("GET") || request.startsWith("HEAD")) {
        if (path.equals("/")) {
          sendResponse(200, "OK", "No content was generated for this request.", null);
        } else {
          path = "." + request.substring(4, request.length() - 9).trim();
          File file = new File(path);
          if (file.isFile()) {
            sendResponse(200, "OK", "", file);
          } else {
            sendResponse(
                404, "File not found", "The requested URL was not found on this server.", null);
          }
        }
      } else {
        sendResponse(
            400,
            "Bad Request",
            "Your browser sent a request that this server could not understand.",
            null);
      }
      out.flush();
    } catch (Exception e) {
      System.err.println(e);
    }
  }

  private void sendResponse(int statusCode, String statusTitle, String message, File file)
      throws Exception {
    String http = request.substring(request.length() - 8, request.length()).trim();
    String status = http + " " + statusCode + " " + statusTitle + "\r\n";
    out.writeBytes(status);
    out.writeBytes("Server: Java File Server" + "\r\n");
    out.writeBytes("date: " + new Date() + "\r\n");
    if (file != null) {
      out.writeBytes("content-type: " + getFileMimeType(file) + "\r\n");
      out.writeBytes("content-length: " + file.length() + "\r\n");
      out.writeBytes("last-modified: " + new Date(file.lastModified()) + "\r\n");
      out.writeBytes("\r\n");
      sendFile(file);
    } else {
      out.writeBytes("content-type: " + "text/plain" + "\r\n");
      out.writeBytes("\r\n");
      if (!request.startsWith("HEAD")) out.writeBytes(status + message + "\r\n");
    }
    out.flush();
    out.close();
  }

  private void sendFile(File file) {
    try {
      FileInputStream fileInputStream = new FileInputStream(file);
      byte[] buffer = new byte[1000];
      while (fileInputStream.available() > 0) {
        out.write(buffer, 0, fileInputStream.read(buffer));
      }
      fileInputStream.close();
    } catch (IOException e) {
      System.err.println(e);
    }
  }

  private String getFileMimeType(File file) {
    String url = file.getAbsolutePath();
    FileNameMap fileNameMap = URLConnection.getFileNameMap();
    return fileNameMap.getContentTypeFor("file://" + url);
  }

  public static void main(String[] args) {
    try {
      ServerSocket server = new ServerSocket(8080);
      while (true) {
        Socket socket = server.accept();
        new HttpServer(socket).start();
      }
    } catch (IOException e) {
      System.err.println("Could not start Server: " + e);
      System.exit(-1);
    }
  }
}
