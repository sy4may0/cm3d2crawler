package cc.hundred_jpy.cm3d2crawler.scheduler;

import java.time.LocalDateTime;
import java.util.LinkedList;

import cc.hundred_jpy.cm3d2crawler.entitybean.TaskBean;
import cc.hundred_jpy.cm3d2crawler.taskqueue.TaskQueue;
import cc.hundred_jpy.cm3d2crawler.utilities.CM3D2CrawlLogger;

/**
 * <Class>				TaskRunner
 * <Description>		Taskを実行するTimerTaskです。
 * <UnThreadSafe>	このクラスはスレッドセーフではありません。
 *
 * @author 100JPY
 * @version 1.0
 */
public class TaskRunner {

	/**
	 * <Attribute>		taskList
	 * <Description>	実行するTaskのリストです。
	 */
	private LinkedList<TaskBean> taskList = null;

	/**
	 * <Attribute>		runTime
	 * <Description>	タスクを実行する時刻です。
	 */
	private LocalDateTime runTime = null;

	/**
	 * <Method>			Constructor
	 * <Description>	コンストラクタです。
	 *
	 * @param runTime このタスクを実行する時刻です。
	 * @param taskList 実行するタスクのリストです。空のリストを推奨します。何らかの追加タスクが必要な場合のみ、このリストにタスクを追加してください。
	 */
	public TaskRunner() {
	}

	/**
	 * <Method>			run
	 * <Description>	タスクを実行します。。
	 */
	public void run() {
		// TODO 自動生成されたメソッド・スタブ

		TaskBean task;

		while((task = this.taskList.poll()) != null) {
			if(task.getTaskType().equals(Scheduler.SCANNER_TASK)) {
				TaskQueue.getInstance().entryScannerTask(task.getMessage());
				CM3D2CrawlLogger.getInstance().info("Scannerのタスクをエントリーしました。ID" + task.getId());

			}else if(task.getTaskType().equals(Scheduler.DOWNLOAD_TASK)) {
				TaskQueue.getInstance().entryDownloaderTask(task.getMessage());
				CM3D2CrawlLogger.getInstance().info("Downloaderのタスクをエントリーしました。ID" + task.getId());

			}else if(task.getTaskType().equals(Scheduler.HOUSEKEEP_TASK)) {
				TaskQueue.getInstance().entryHousekeeperTask(task.getMessage());
				CM3D2CrawlLogger.getInstance().info("Housekeeperのタスクをエントリーしました。ID" + task.getId());
			}
		}
	}

	public LinkedList<TaskBean> getTaskList() {
		return taskList;
	}

	public void setTaskList(LinkedList<TaskBean> taskList) {
		this.taskList = taskList;
	}

	public LocalDateTime getRunTime() {
		return runTime;
	}

	public void setRunTime(LocalDateTime runTime) {
		this.runTime = runTime;
	}
}
