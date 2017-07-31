package club.bestchu.downloader;

import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

public class MultiThreadDownLoader {

    private int threadCount;                         //并发下载的线程数

    private String serverPath;                       //资源位置

    private String localPath;                        //本地保存位置

    public CountDownLatch latch;                     //线程计数同步辅助

    public MultiThreadDownLoader(int threadCount, String serverPath, String localPath, CountDownLatch latch) {
        this.threadCount = threadCount;
        this.serverPath = serverPath;
        this.latch = latch;
        this.localPath = localPath;
    }

    public void download() {
        try {
            URL url = new URL(serverPath);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            int code = conn.getResponseCode();
            if(code == 200){
                int length = conn.getContentLength();
                System.out.println("文件总的长度是"+length+"字节（B）");
                RandomAccessFile raf = new RandomAccessFile(localPath, "rwd");
                raf.setLength(length);
                raf.close();
                int blockSize = length/threadCount;
                for(int threadId = 1; threadId <= threadCount; threadId++){
                    int startIndex = (threadId - 1)*blockSize;
                    int endIndex = startIndex + blockSize - 1;
                    if(threadId == threadCount){
                        endIndex = length;
                    }
                    System.out.println("线程"+threadId+"下载："+startIndex+"字节到"+endIndex+"字节");
                    new DownloadThread(threadId, startIndex, endIndex, serverPath, localPath, latch).start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
