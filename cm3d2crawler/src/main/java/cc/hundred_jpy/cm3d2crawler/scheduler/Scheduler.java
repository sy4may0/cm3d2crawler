package cc.hundred_jpy.cm3d2crawler.scheduler;

import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import cc.hundred_jpy.cm3d2crawler.dao.TaskDao;
import cc.hundred_jpy.cm3d2crawler.entitybean.TaskBean;
import cc.hundred_jpy.cm3d2crawler.exceptions.IllegalInterruptedException;
import cc.hundred_jpy.cm3d2crawler.exceptions.IllegalMessageException;
import cc.hundred_jpy.cm3d2crawler.exceptions.TimeoutException;
import cc.hundred_jpy.cm3d2crawler.utilities.CM3D2CrawlLogger;

/**
 * <Class>			Scheduler
 * <Description>	タスクのスケジュールを制御します。
 * <ThreadSafe>	このクラスはスレッドセーフです。
 * <Thread>			このクラスはスレッドとして動作します。
 *
 * @author 100JPY
 * @version 1.0
 */
public class Scheduler implements Runnable {

	/**
	 * <Attribute>		Several
	 * <Description>	タスク実行スレッドの識別子を定義します。
	 * <Constant>		この属性は定数です。
	 */
	public static final String SCANNER_TASK = "Syoko";
	public static final String DOWNLOAD_TASK = "Koume";
	public static final String HOUSEKEEP_TASK = "Sachiko";

	/**
	 * <Attribute>		Several
	 * <Description>	Schedulerのコマンドを定義します。
	 * <Constant>		この属性は定数です。
	 */
	public static final String C_SCHEDULE_START = "Walking_sex";
	public static final String C_SCHEDULE_STOP = "minami_is_so_cute";
	public static final String C_ADD_TASK = "Miria_also_do_it";
	public static final String C_DELETE_TASK = "Miria_will_not_do_it";
	public static final String C_SHOW_TASK = "Haramiria-alien";
	public static final String C_TERMINATE = "Haru_lets_go_a_love_hotel";

	/**
	 * <Attribute>		terminalMessageQueue
	 * <Description>	コマンドを受信するキューです。
	 * <Immutable>	この属性は変更することができません。
	 */
	private final BlockingQueue<String> terminalMessageQueue;

	/**
	 * <Attrubute>		executer
	 * <Description>	SchedulerWorkerを起動するExecutorServiceです。
	 * <Immutable>	この属性は変更することができません。
	 */
	private final ExecutorService executor;

	/**
	 * <Attribute>		worker
	 * <Description>	SchedulerWorker参照用の属性です。
	 */
	private SchedulerWorker worker = null;

	/**
	 * <Attribute>		isRun
	 * <Description>	このスレッドが実行中かを定義します。
	 */
	private boolean isRun;

	/**
	 * <Attribute>		isSync
	 * <Description>	SchedulerがDBと同期済みであるかを定義します。
	 */
	private boolean isSync;

	/**
	 * <Method>			Constructor
	 * <Description>	コンストラクタです。
	 * @param queue
	 */
	public Scheduler() {
		this.executor = Executors.newSingleThreadExecutor();
		this.terminalMessageQueue = new LinkedBlockingQueue<String>();
		this.isRun = true;
		this.isSync = false;
	}

	/**
	 * <Method>			run
	 * <Description>	runメソッドです。
	 */
	@Override
	public void run() {
		while(this.isRun) {
			try {
				String[] command = this.terminalMessageQueue.take().split("[\\s]+");
				if(command[0].equals(C_ADD_TASK)) {
					System.out.println(command[0]);
				}

				/*
				 * Schedulerを開始します。
				 */
				if(command[0].equals(C_SCHEDULE_START)) {
					worker = new SchedulerWorker();
					this.executor.submit(worker);
					this.isSync = true;
					System.out.println("Schedulerを開始します。");
					CM3D2CrawlLogger.getInstance().info("Schedulerを開始します。");


				/*
				 *Schedulerを停止します。
				 */
				}else if(command[0].equals(C_SCHEDULE_STOP)) {
					if(this.worker != null) {
						this.stopWorker();
						System.out.println("Schedulerを停止します。");
						CM3D2CrawlLogger.getInstance().info("Schedulerを停止します。");
					}


				/*
				 * タスクを追加します。
				 */
				}else if(command[0].equals(C_ADD_TASK)) {
					TaskBean task = new TaskBean();
					task.setTaskType(command[1]);
					task.setMessage(command[2]);
					task.setRunTime(LocalTime.of(Integer.parseInt(command[3]), Integer.parseInt(command[4])));
					TaskDao.save(task);
					task = null;
					this.isSync = false;
					System.out.println("タスクを追加します。");

				/*
				 * タスクを削除します。
				 */
				}else if(command[0].equals(C_DELETE_TASK)) {
					this.isSync = false;
					TaskDao.remove(Integer.parseInt(command[1]));
					System.out.println("タスクを削除しました。");


				/*
				 * タスクを表示します。
				 */
				}else if(command[0].equals(C_SHOW_TASK)) {
					LinkedList<TaskBean> taskList = new LinkedList<TaskBean>();
					TaskDao.load(taskList);
					TaskBean task;
					StringBuilder sb = new StringBuilder();
					sb.append("|" + String.format("%-3s", "id"));
					sb.append("|" +String.format("%-20s", "task_code"));
					sb.append("|" +String.format("%-30s", "message_code"));
					sb.append("|" +String.format("%-12s", "run_time") + "|");
					System.out.println(sb.toString());
					System.out.println("--------------------------------------------------------------------------");
					while((task = taskList.poll()) != null) {
						sb = new StringBuilder();
						sb.append("|" + String.format("%-3s", task.getId()));
						sb.append("|" +String.format("%-20s", task.getTaskType()));
						sb.append("|" +String.format("%-30s", task.getMessage()));
						sb.append("|" +String.format("%-12s", task.getRunTime().format(DateTimeFormatter.ofPattern("HH:mm"))) + "|");
						System.out.println(sb.toString());
					}
					if( this.worker != null && ! this.isSync) {
						System.out.println("注意:この設定は現在実行中のタスクと同期されていません。");
					}

				}else if(command[0].equals(C_TERMINATE)) {
					this.isRun = false;
					CM3D2CrawlLogger.getInstance().info("Schedulerのコマンド待機を終了します。");
					if(this.worker != null) {
						this.stopWorker();
						System.out.println("Schedulerを停止します。");
						CM3D2CrawlLogger.getInstance().info("Schedulerを停止します。");
					};
					break;

				}else {
					throw new IllegalMessageException(command[0]);
				}

			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				try {
					throw new IllegalInterruptedException();
				} catch (IllegalInterruptedException e1) {
					// TODO 自動生成された catch ブロック
					CM3D2CrawlLogger.getInstance().error("Schedulerで不正な割り込みが発生しました。", e1);
					continue;
				}
			} catch (IllegalInterruptedException | SQLException | TimeoutException  e) {
				// TODO 自動生成された catch ブロック
				CM3D2CrawlLogger.getInstance().error("Schedulerの処理に失敗しました。", e);
			} catch (IllegalMessageException e) {
				// TODO 自動生成された catch ブロック
				CM3D2CrawlLogger.getInstance().error("Schedulerに不正なメッセージが送信されました。", e);
			}
		}
	}

	/**
	 * <Method>			operation
	 * <Description>	Schedulerにコマンドを送信します。
	 *
	 * @param message
	 * @throws IllegalInterruptedException
	 */
	public void operation(String message) throws IllegalInterruptedException {
		try {
			this.terminalMessageQueue.put(message);
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			throw new IllegalInterruptedException();
		}
	}

	private void stopWorker() throws IllegalInterruptedException {
		if(this.worker.isRun()) {
			while(worker.isLoading()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					throw new IllegalInterruptedException();
				}
			}
			this.executor.shutdownNow();
		}
	}

}
