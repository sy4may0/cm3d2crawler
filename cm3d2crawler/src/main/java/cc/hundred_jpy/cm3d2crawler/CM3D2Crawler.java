package cc.hundred_jpy.cm3d2crawler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

import cc.hundred_jpy.cm3d2crawler.downloader.Downloader;
import cc.hundred_jpy.cm3d2crawler.exceptions.IllegalInterruptedException;
import cc.hundred_jpy.cm3d2crawler.exceptions.TimeoutException;
import cc.hundred_jpy.cm3d2crawler.scanner.UploaderScanner;
import cc.hundred_jpy.cm3d2crawler.scheduler.Scheduler;
import cc.hundred_jpy.cm3d2crawler.taskqueue.TaskQueue;

/**
 * <Class>			CM3D2Crawler
 * <Descroption>	カスタムメイド3D2非公式うｐろだのダウンローダーです。
 * 							定期的にページを探索し、更新があった場合ダウンロードします。
 *
 *
 */
public class CM3D2Crawler {

	/**
	 * <Attribute>		Several
	 * <Description>	 各スレッドインスタンスです。
	 */
	private static Downloader downloader = new Downloader();
	private static UploaderScanner scanner = new UploaderScanner();
	private static Scheduler scheduler = new Scheduler();

	/**
	 * <Attribute>		addTask
	 * <Description>	タスク追加コマンドを送信します。
	 * @param scan
	 * @throws IllegalInterruptedException
	 */
	private static void addTask(Scanner scan) throws IllegalInterruptedException {
		StringBuilder sb = new StringBuilder();
		sb.append(Scheduler.C_ADD_TASK + " ");

		String hour = null;
		String minute = null;
		String taskType = null;
		String message = null;

		int correct = 3;
		while(correct != 0) {
			System.out.print("operation >");
			String s = scan.next();

			if(s.matches("hour=\\d\\d")) {
				if(hour == null) {
					hour = s.split("=")[1];
					correct--;
				}else {
					System.out.println("ERROR:hourはすでに定義済みです。");
				}


			}else if(s.matches("minute=\\d\\d")) {
				if(minute == null) {
					minute = s.split("=")[1];
					correct--;
				}else {
					System.out.println("ERROR:minuteはすでに定義済みです。");
				}



			}else if(s.equals("scan-loader")) {
				if(message == null && taskType == null) {
					taskType = Scheduler.SCANNER_TASK;
					message = UploaderScanner.SCAN_LOADER;
					correct--;
				}else {
					System.out.println("ERROR:messageはすでに定義済みです。");
				}



			}else if(s.equals("scan-mod")) {
				if(message == null && taskType == null) {
					taskType = Scheduler.SCANNER_TASK;
					message = UploaderScanner.SCAN_MOD;
					correct--;
				}else {
					System.out.println("ERROR:messageはすでに定義済みです。");
				}



			}else if(s.equals("download")) {
				if(message == null && taskType == null) {
					taskType = Scheduler.DOWNLOAD_TASK;
					message = Downloader.DOWNLOAD;
					correct--;
				}else {
					System.out.println("ERROR:messageはすでに定義済みです。");
				}


			}else if(s.equals("cancel")) {
				return;
			}
		}

		System.out.print("タスクを追加します。よろしいですか？[y/n]>");

		if(scan.next().matches("y|Y")) {
			sb.append(taskType + " ");
			sb.append(message + " ");
			sb.append(hour + " ");
			sb.append(minute + " ");
			scheduler.operation(sb.toString());
		}else {
			System.out.println("キャンセルします。");
		}

	}

	/**
	 * <Method>			main
	 * <Description>	mainメソッドです。コマンドを受取り、各処理を実行します。
	 * @param args
	 * @throws IOException
	 * @throws IllegalInterruptedException
	 * @throws TimeoutException
	 * @throws SQLException
	 */
    public static void main( String[] args ) throws IOException, IllegalInterruptedException, TimeoutException, SQLException
    {


    	Thread downloaderThread = new Thread(downloader);
    	Thread scannerThread = new Thread(scanner);
    	Thread schedulerThread = new Thread(scheduler);


    	downloaderThread.start();
    	scannerThread.start();
    	schedulerThread.start();
    	try(Scanner scan = new Scanner(System.in)) {
    		while(true) {

    			try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
    			System.out.print("scheduler >");
    			String str = scan.next();
    			if(str.equals("start")) {
    				scheduler.operation(Scheduler.C_SCHEDULE_START);

    			}else if(str.equals("stop")) {
    				scheduler.operation(Scheduler.C_SCHEDULE_STOP);

    			}else if(str.equals("add-task")) {
    				addTask(scan);

    			}else if(str.equals("remove-task")) {
    				System.out.print("id >");
    				String id = scan.next();
    				System.out.print("タスクを削除します。よろしいですか？[y/n]>");

    				if(scan.next().matches("y|Y")) {
    					scheduler.operation(Scheduler.C_DELETE_TASK + " " + id);
    				}else {
    					System.out.println("キャンセルします。");
    				}

    			}else if(str.equals("show-task")) {
    				scheduler.operation(Scheduler.C_SHOW_TASK);


    			}else if(str.equals("terminate")) {
    				scheduler.operation(Scheduler.C_TERMINATE);
    				TaskQueue.getInstance().entryDownloaderTask(Downloader.TERMINATE);
    				TaskQueue.getInstance().entryScannerTask(UploaderScanner.TERMINATE);
    				//TaskQueue.getInstance().entryHousekeeperTask("kari");

    				System.out.println("終了します。");
    				break;
    			}
    		}
    	}
    }
}
