import java.io.*;
import java.net.*;
import java.util.Date;

public class HttpServer extends Thread {

  private Socket socket;
  private DataOutputStream out = null;
  private String request;
  private String root = System.getenv("ROOTDIR");

  private HttpServer(Socket s) {
    socket = s;
  }

  public void run() {
    BufferedReader in = null;
    try {
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new DataOutputStream(socket.getOutputStream());
      request = in.readLine();
      String path = request.substring(4, request.length() - 9).trim();
      System.out.println(path);
      if (request.startsWith("GET") || request.startsWith("HEAD")) {
        if (path.equals("/")) {
          sendDefaultResponse(200, "OK", "No content was generated for this request.");
        } else {
          String filepath = "./" + root + path;
          File file = new File(filepath);
          if (file.isFile()) {
            sendFileResponse(200, "OK", file);
          } else {
            sendDefaultResponse(
                404, "File not found", "The requested URL was not found on this server.");
          }
        }
      } else {
        sendDefaultResponse(
            400, "Bad Request", "The client sent a request that this server could not understand.");
      }
    } catch (Exception e) {
      System.err.println(e);
    }
  }

  // Default Server Response
  private void sendDefaultResponse(int statusCode, String statusTitle, String message)
      throws Exception {
    writeGeneralResponseHeaders(statusCode, statusTitle);
    out.writeBytes("content-type: " + "text/plain" + "\r\n");
    out.writeBytes("\r\n");
    if (!request.startsWith("HEAD")) {
      out.writeBytes(statusCode + " " + statusTitle + " " + message + "\r\n");
    }
    out.flush();
    out.close();
  }

  // File Response
  private void sendFileResponse(int statusCode, String statusTitle, File file) throws Exception {
    writeGeneralResponseHeaders(statusCode, statusTitle);
    out.writeBytes("content-type: " + getFileMimeType(file) + "\r\n");
    out.writeBytes("content-length: " + file.length() + "\r\n");
    out.writeBytes("last-modified: " + new Date(file.lastModified()) + "\r\n");
    out.writeBytes("\r\n");
    if (request.startsWith("GET")) {
      writeFile(file);
    }
    out.flush();
    out.close();
  }

  private void writeGeneralResponseHeaders(int statusCode, String statusTitle) throws Exception {
    String http = request.substring(request.length() - 8, request.length()).trim();
    String status = http + " " + statusCode + " " + statusTitle + "\r\n";
    out.writeBytes(status);
    out.writeBytes("Server: Java File Server" + "\r\n");
    out.writeBytes("date: " + new Date() + "\r\n");
  }

  private void writeFile(File file) {
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
