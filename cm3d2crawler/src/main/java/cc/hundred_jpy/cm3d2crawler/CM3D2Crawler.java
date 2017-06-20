package cc.hundred_jpy.cm3d2crawler;

import java.io.IOException;
import java.sql.SQLException;

import cc.hundred_jpy.cm3d2crawler.downloader.Downloader;
import cc.hundred_jpy.cm3d2crawler.exceptions.IllegalInterruptedException;
import cc.hundred_jpy.cm3d2crawler.exceptions.TimeoutException;
import cc.hundred_jpy.cm3d2crawler.scanner.Scanner;
import cc.hundred_jpy.cm3d2crawler.taskqueue.TaskQueue;

/**
 * Hello world!
 *
 */
public class CM3D2Crawler {
    public static void main( String[] args ) throws IOException, IllegalInterruptedException, TimeoutException, SQLException
    {
    	Downloader downloader = new Downloader();
    	Scanner scanner = new Scanner();

    	Thread downloaderThread = new Thread(downloader);
    	Thread scannerThread = new Thread(scanner);

    	downloaderThread.start();
    	scannerThread.start();

    	//TaskQueue.getInstance().entryScannerTask(Scanner.SCAN_MOD);

    	TaskQueue.getInstance().entryDownloaderTask(Downloader.DOWNLOAD);

    	TaskQueue.getInstance().entryDownloaderTask(Downloader.TERMINATE);

    	TaskQueue.getInstance().entryScannerTask(Scanner.TERMINATE);


    }
}
