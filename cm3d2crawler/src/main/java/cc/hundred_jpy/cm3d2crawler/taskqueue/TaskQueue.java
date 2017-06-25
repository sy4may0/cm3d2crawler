package cc.hundred_jpy.cm3d2crawler.taskqueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import cc.hundred_jpy.cm3d2crawler.exceptions.IllegalInterruptedException;
/**
 * <Class> 				TaskQueue
 * <Description>		このクラスはメッセージ交換用ブロッキングキューを提供します。
 * <ThreadSafe>		このクラスはスレッドセーフです。
 * <Singleton>			このクラスはSingletonです。このクラスのインスタンスが2つ以上存在しないことを保証します。
 * <Immutable>		このクラスはImmutableです。各属性の参照が変動しないことを保証します。
 *
 * @author 				100JPY
 * @version 				1.1
 *
 */
public class TaskQueue {

	/**
	 *  <Attribute> 		scannerTaskQueue
	 *  <Description>	Scannerスレッドを制御するメッセージを同期するキュー。
	 *  						Scannerはこのキューからメッセージを取得し、メッセージにより指定された操作を実行します。
	 *
	 *  <Immutable>	この属性は変更することができません。
	 */
	private final BlockingQueue<String> scannerTaskQueue;

	/**
	 * <Attribute>		downloadTaskQueue
	 * <Description>	Dwonloaderスレッドを制御するメッセージを同期するキュー。
	 * 							Donwloaderはこのキューからメッセージを取得し、メッセージにより指定された操作を実行します。
	 *
	 * <Immutable>	この属性は変更することができません。
	 */
	private final BlockingQueue<String> downloaderTaskQueue;

	/**
	 * <Attribute>		housekeeperTaskQueue
	 * <Description>	Housekeeperスレッドを制御するメッセージを同期するキュー。
	 *							Housekeeperスレッドははこのキューからメッセージを取得し、メッセージにより指定された操作を実行します。
	 *
	 * <Immutable>	この属性は変更することができません。
	 */
	private final BlockingQueue<String> housekeeperTaskQueue;

	/**
	 * <Attribute>		instance
	 * <Description>	自身のインスタンスを定義します。
	 * <Immutable>	この属性は変更することができません。
	 * <Static>			この属性は静的メンバです。
	 */
	private static final TaskQueue instance = new TaskQueue();

	/**
	 * <Method> 		Constructor
	 * <Description>	コンストラクタです。すべてのキューを初期化します。
	 * <Singleton>		このコンストラクタが2回以上実行されるような変更を禁止します。
	 */
	private TaskQueue() {
		this.scannerTaskQueue = new LinkedBlockingQueue<String>();
		this.downloaderTaskQueue = new LinkedBlockingQueue<String>();
		this.housekeeperTaskQueue = new LinkedBlockingQueue<String>();

	}

	/**
	 * <Method>			getInstance
	 * <Description>	自身のインスタンスを返します。このクラスを利用する場合、このメソッドを利用してインスタンスを参照してください。
	 * @return			このクラスのインスタンスを返します。
	 */
	public static TaskQueue getInstance() {
		return instance;
	}

	/**
	 * <Method>			takeScannerTask
	 * <Description>	Scannerのタスク実行メッセージを返します。
	 * 							待機したタスクが存在しない場合、スレッドをブロックします。
	 *
	 * @return			キューイングされたメッセージを返します。
	 * @throws 			IllegalInterruptedException
	 */
	public String takeScannerTask() throws IllegalInterruptedException {
		String message = null;
		try {
			message = this.scannerTaskQueue.take();
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			throw new IllegalInterruptedException();
		}
		return message;
	}

	/**
	 * <Method>			entryScannerTask
	 * <Description>	Scannerのタスク実行メッセージを登録します。
	 *
	 * @param 			message
	 */
	public void entryScannerTask(String message) {
		this.scannerTaskQueue.add(message);
	}

	/**
	 * <Method>			takeDownloaderTask
	 * <Description>	Downloaderのタスク実行メッセージを返します。
	 * 							待機したタスクが存在しない場合、スレッドをブロックします。
	 *
	 * @return			キューイングされたメッセージを返します。
	 * @throws 			IllegalInterruptedException
	 */
	public String takeDownloaderTask() throws IllegalInterruptedException {
		try {
			return this.downloaderTaskQueue.take();
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			throw new IllegalInterruptedException();
		}
	}

	/**
	 * <Method>			entryDownloaderTask
	 * <Description>	Downloaderのタスク実行メッセージを登録します。
	 *
	 * @param 			message
	 */
	public void entryDownloaderTask(String message) {
		this.downloaderTaskQueue.add(message);
	}

	/**
	 * <Method>			takeHousekeeperTask
	 * <Description>	Housekeeperのタスク実行メッセージを返します。
	 * 							待機したタスクが存在しない場合、スレッドをブロックします。
	 *
	 * @return			キューイングされたメッセージを返します。
	 * @throws 			IllegalInterruptedException
	 */
	public String takeHousekeeperTask() throws IllegalInterruptedException {
		try {
			return this.housekeeperTaskQueue.take();
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			throw new IllegalInterruptedException();
		}
	}

	/**
	 * <Method>			entryHousekeeperTask
	 * <Description>	Housekeeperのタスク実行メッセージを登録します。
	 *
	 * @param 			message
	 */
	public void entryHousekeeperTask(String message)  {
		this.housekeeperTaskQueue.add(message);
	}

}
