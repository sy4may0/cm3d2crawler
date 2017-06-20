package cc.hundred_jpy.cm3d2crawler.scheduler;

import java.time.LocalTime;

import cc.hundred_jpy.cm3d2crawler.exceptions.IllegalInterruptedException;
import cc.hundred_jpy.cm3d2crawler.taskqueue.TaskQueue;
import cc.hundred_jpy.cm3d2crawler.utilities.CM3D2CrawlLogger;

/**
 * ScannerTimerTask
 * Scannerを制御するメッセージを生成する。
 * Schedulerクラスにより管理され、SchedulerのTimerに定義された時刻にrunメソッドを実行する。
 *
 * @author 100JPY
 * @version 1.0
 */
public class ScannerTimerTask extends TimerTask {

	/**
	 * コンストラクタ
	 *
	 * @param message
	 */
	public ScannerTimerTask(String message, LocalTime runTime) {
		super(message, runTime);
		CM3D2CrawlLogger.getInstance().info("DownloadTimerTaskが生成されました。message : " + message);
	}

	/**
	 * run
	 * [操作]
	 * Timerクラスから実行される。
	 * メッセージをTaskQueueに登録する。
	 *
	 */
	public void run() {
		try {

			CM3D2CrawlLogger.getInstance().debug("ScannerTaskの登録を待機します。  message : " + super.message);
			TaskQueue.getInstance().entryScannerTask(super.message);
			CM3D2CrawlLogger.getInstance().debug("ScannerTaskを登録しました。  message : " + super.message);

		} catch (IllegalInterruptedException e) {
			// TODO 自動生成された catch ブロック
			CM3D2CrawlLogger.getInstance().error("TimerTaskの実行中に不正な割り込みが発生しました。", e);
		}

	}

}
