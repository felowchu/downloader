package club.bestchu.downloader;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Currency;
import java.util.concurrent.CountDownLatch;

public class DownloadThread extends Thread {
    private int threadId;

    private int startIndex;

    private int endIndex;

    private String serverPath;

    private String localPath;

    private CountDownLatch latch;

    public DownloadThread(int threadId, int startIndex, int endIndex, String serverPath, String localPath, CountDownLatch latch){
        this.threadId = threadId;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.serverPath = serverPath;
        this.localPath = localPath;
        this.latch = latch;
    }

    public void run(){
        try{
            System.out.println("线程"+threadId+"正在下载");
            URL url = new URL(serverPath);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Range", "bytes="+startIndex+"-"+endIndex);
            conn.setConnectTimeout(5000);
            int code = conn.getResponseCode();

            System.out.println("线程"+threadId+"请求返回code"+code);

            InputStream is = conn.getInputStream();

            RandomAccessFile raf = new RandomAccessFile(localPath, "rwd");
            raf.seek(startIndex);

            int len = 0;
            byte[] buffer = new byte[1024];
            while((len = is.read()) > 0){
                raf.write(buffer,0,len);
            }
            is.close();
            raf.close();
            System.out.println("线程"+threadId+"下载完毕");
            latch.countDown();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
