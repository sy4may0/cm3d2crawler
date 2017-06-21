package cc.hundred_jpy.cm3d2crawler.downloader;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;

import cc.hundred_jpy.cm3d2crawler.dao.ModItemDao;
import cc.hundred_jpy.cm3d2crawler.entitybean.ModItemBean;
import cc.hundred_jpy.cm3d2crawler.exceptions.IllegalInterruptedException;
import cc.hundred_jpy.cm3d2crawler.exceptions.IllegalMessageException;
import cc.hundred_jpy.cm3d2crawler.exceptions.IllegalPropertyException;
import cc.hundred_jpy.cm3d2crawler.exceptions.TimeoutException;
import cc.hundred_jpy.cm3d2crawler.taskqueue.TaskQueue;
import cc.hundred_jpy.cm3d2crawler.utilities.CM3D2CrawlLogger;
import cc.hundred_jpy.cm3d2crawler.utilities.CM3D2CrawlProperties;

/**
 * <Class>				Downloader
 * <Description>		このクラスはメッセージを待機します。
 * 								メッセージを受信した時、Downloaderのイベントを発行します。
 * <Thread>				このクラスはスレッドです。
 * <Listenner>			このスレッドはイベントリスナーとして動作します。
 *
 * @author 				100JPY
 * @version				1.0
 *
 */
public class Downloader implements Runnable {

	/**
	 * <Attrubyte>		DOWNLOAD
	 * <Description>	ダウンロードを実行するメッセージを定義します。
	 * <Constant>		この属性は定数です。
	 */
	public static final String DOWNLOAD = "That's right. (Piggybacking)";

	/**
	 * <Attribute>		TERMINATE
	 * <Description>	スレッド停止メッセージを定義します。
	 * <Constant>		この属性は定数です。
	 */
	public static final String TERMINATE = "Beast senior";

	/**
	 * <Attribute>		headers
	 * <Description>	POSTリクエスト送信時のヘッダを定義します。
	 * <Immutable>	この属性は変更することができません。
	 */
	private final HashMap<String, String> headers;

	/**
	 * <Attribute>		modItemList
	 * <Description>	ダウンロードするMODを格納する領域です。
	 * <Immutable>	この属性は変更することができません。
	 */
	private final LinkedList<ModItemBean> modItemList;

	/**
	 * <Attribute>		taskQueue
	 * <Description>	メッセージ送受信を行うキューを定義します。
	 * <Immutable>	この属性は変更することができません。
	 */
	private final TaskQueue taskQueue;

	/**
	 * <Method>			Constructor
	 * <Description>	コンストラクタです。
	 */
	public Downloader() {
		this.modItemList = new LinkedList<ModItemBean>();

		this.headers = new HashMap<String, String>();
		this.headers.put("User-Agent", CM3D2CrawlProperties.getInstance().getStringProperty("user_agent"));
		this.headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		this.headers.put("Referer", "INITIAL");

		this.taskQueue = TaskQueue.getInstance();
	}

	/**
	 * <Method>			run
	 * <Description>	runメソッドです。
	 * 							メッセージの待機を開始します。
	 */
	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		while(true) {
			try {
				String message = this.taskQueue.takeDownloaderTask();

				/*
				 * MODダウンロードを実行します。
				 */
				if(message.equals(DOWNLOAD)) {
					int maxDownload = CM3D2CrawlProperties.getInstance().getIntProperty("max_download");
					try {
						ModItemDao.load(this.modItemList, maxDownload);
					} catch (SQLException | TimeoutException e) {
						// TODO 自動生成された catch ブロック
						CM3D2CrawlLogger.getInstance().error("ダウンロード対象MODのロードに失敗しました。処理を中断します。",e);
						continue;
					}

					ModItemBean mi;
					try {
						while((mi = modItemList.poll()) != null) {
							DownloaderHandler.downloadModItem(mi, headers);
						}
					} catch (IOException | SQLException | TimeoutException | IllegalInterruptedException e) {
						CM3D2CrawlLogger.getInstance().error("MODのダウンロードに失敗しました。処理を中断します。", e);
						continue;
					}

				/*
				 * スレッドを終了します。
				 */
				}else if(message.equals(TERMINATE)) {
					CM3D2CrawlLogger.getInstance().info("Downloaderを停止します。");
					break;



				}else {
					throw new IllegalMessageException(message);
				}

			} catch (IllegalInterruptedException | IllegalPropertyException | IllegalMessageException e) {
				// TODO 自動生成された catch ブロック
				CM3D2CrawlLogger.getInstance().error("Downloaderに失敗しました。処理を中断します。", e);
				continue;
			}

		}

	}

}
