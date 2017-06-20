package cc.hundred_jpy.cm3d2crawler.scheduler;

import java.time.LocalTime;

/**
 * TimerTask
 * [abstract]
 * このクラスは抽象メソッドを持つ。
 *
 * Schedulerから実行するタスクを定義するクラス。
 * Schedulerはこのクラスのrunメソッドを実行する。
 * runメソッドはサブクラスに定義する。
 *
 * @author 100JPY
 * @version 1.0
 */
public abstract class TimerTask {

	/*
	 * message
	 * [final]
	 * 送信するメッセージ。
	 * 送信可能なメッセージはTaskQueueクラスを参照する。
	 */
	protected final String message;

	/*
	 * runTime
	 * [final]
	 * このタスクを実行する時刻を定義する。
	 */
	protected final LocalTime runTime;

	/**
	 * コンストラクタ
	 * 送信するメッセージを指定する。
	 *
	 * @param message
	 */
	public TimerTask(String message, LocalTime runTime) {
		this.message = message;
		this.runTime = runTime;
	}

	/**
	 * run
	 * [abstract]
	 * このクラスが実行するタスクを定義する。
	 */
	public abstract void run();

	public String getMessage() {
		return this.message;
	}

	public LocalTime getRunTime() {
		return this.runTime;
	}
}
