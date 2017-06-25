package cc.hundred_jpy.cm3d2crawler.scheduler;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cc.hundred_jpy.cm3d2crawler.dao.TaskDao;
import cc.hundred_jpy.cm3d2crawler.entitybean.TaskBean;
import cc.hundred_jpy.cm3d2crawler.exceptions.IllegalInterruptedException;
import cc.hundred_jpy.cm3d2crawler.exceptions.TimeoutException;
import cc.hundred_jpy.cm3d2crawler.utilities.CM3D2CrawlLogger;

/**
 * <Class>			SchedulerWorker
 * <Description>	タスクの生成を行います。
 * <Thread>			このクラスはスレッドです。
 *
 * @author 100JPY
 * @version 1.0
 */
public class SchedulerWorker implements Runnable {

	/**
	 * <Attribute>		runner
	 * <Description>	タスクを実行するランナーを定義します。
	 * <Immutable>	この属性は変更することができません。
	 */
	private final TaskRunner runner;

	/**
	 * <Attribute>		timeList
	 * <Description>	タスク実行時刻のリストです。
	 * <Immutable>	この属性は変更することができません。
	 */
	private final List<LocalDateTime> timeList;

	/**
	 * <Attribute>		taskList
	 * <Description>	ある時刻に実行するタスクの一時領域として使用します。
	 * <Immutable>	この属性は変更することができません。
	 */
	private final LinkedList<TaskBean> taskList;

	/**
	 * <Attribute>		isRun
	 * <Description>	このスレッドの実行状態を定義します。
	 */
	private boolean isRun;

	/**
	 * <Attribute>		isLoading
	 * <Description>	このスレッドがデータベースアクセス中かを定義します。
	 */
	private boolean isLoading = false;

	/**
	 * <Method>			Constructor
	 * <Description>	コンストラクタです。
	 * @param timeList
	 */
	public SchedulerWorker() {
		this.timeList = new ArrayList<LocalDateTime>();
		this.taskList = new LinkedList<TaskBean>();
		this.isRun = false;
		this.runner = new TaskRunner();

	}

	/**
	 * <Method>			run
	 * <Description>	runメソッドです。
	 */
	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ

		this.isRun = true;
		LinkedList<LocalTime> tList = new LinkedList<LocalTime>();

		/*
		 * データベースから実行時刻リストを取得します。
		 */
		this.isLoading = true;
		try {
			TaskDao.loadTime(tList);
		} catch (SQLException | IllegalInterruptedException | TimeoutException e1) {
			// TODO 自動生成された catch ブロック
			CM3D2CrawlLogger.getInstance().error("タスク実行時刻のロードに失敗しました。", e1);
		}
		this.isLoading = false;

		LocalDate nowDate = LocalDate.now();
		LocalDate nextDate = LocalDate.now().plusDays(1);

		LocalTime now = LocalTime.now();
		LocalTime runTime0;

		/*
		 * 実行時刻(Time)を現在日時と合わせLocalDateTimeに初期化します。
		 */
		while((runTime0 = tList.poll()) != null) {
			if(runTime0.compareTo(now) > 0) {
				this.timeList.add(LocalDateTime.of(nowDate, runTime0));
			}else {
				this.timeList.add(LocalDateTime.of(nextDate, runTime0));
			}
		}
		Collections.sort(this.timeList);

		tList = null;

		CM3D2CrawlLogger.getInstance().debug("タスクの実行時間リストを生成しました。");

		/*
		 * タスク実行ループを開始します。
		 * 現在時刻とタスク実行時刻のDuration量待機し、タスク実行後、実行時刻の日時を1日進めめます。
		 */
		while(this.isRun) {
			for(LocalDateTime runTime1 : this.timeList) {

				Duration duration = Duration.between(LocalDateTime.now(), runTime1);
				long delay = duration.toMillis();

				if(delay >= 0) {

					CM3D2CrawlLogger.getInstance().debug("タスク実行を待機します。次回実行:" + runTime1.toString());
					try {
						Thread.sleep(delay);

					} catch (InterruptedException e) {
						this.isRun = false;
						CM3D2CrawlLogger.getInstance().info("Schedulerを実行を停止します。");
						break;
					}

					try {
						TaskDao.loadByTime(runTime1.toLocalTime(), this.taskList);
					} catch (SQLException | IllegalInterruptedException | TimeoutException e) {
						// TODO 自動生成された catch ブロック
						CM3D2CrawlLogger.getInstance().error("タスクのロードに失敗しました。", e);
					}


					this.runner.setRunTime(runTime1);
					this.runner.setTaskList(this.taskList);
					this.runner.run();
					runTime1 = runTime1.plusDays(1);
				}
			}

		}
	}

	public boolean isRun() {
		return isRun;
	}

	public boolean isLoading() {
		return isLoading;
	}

}
