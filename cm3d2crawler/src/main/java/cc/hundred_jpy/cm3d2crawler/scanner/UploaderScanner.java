package cc.hundred_jpy.cm3d2crawler.scanner;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;

import cc.hundred_jpy.cm3d2crawler.dao.UpLoaderDao;
import cc.hundred_jpy.cm3d2crawler.entitybean.ModItemBean;
import cc.hundred_jpy.cm3d2crawler.entitybean.UpLoaderBean;
import cc.hundred_jpy.cm3d2crawler.exceptions.IllegalInterruptedException;
import cc.hundred_jpy.cm3d2crawler.exceptions.IllegalMessageException;
import cc.hundred_jpy.cm3d2crawler.exceptions.TimeoutException;
import cc.hundred_jpy.cm3d2crawler.taskqueue.TaskQueue;
import cc.hundred_jpy.cm3d2crawler.utilities.CM3D2CrawlLogger;

/**
 * <Class>				Scanner
 * <Description>		このクラスはメッセージを待機します。
 * 								メッセージを受信した時、Scannerのイベントを発行します。
 * <Thread>				このクラスはスレッドです。
 * <Listenner>			このスレッドはイベントリスナーとして動作します。
 *
 * @author 				100JPY
 * @version				1.0
 *
 */
public class UploaderScanner implements Runnable {

	/**
	 * <Attrubyte>		SCAN_LOADER
	 * <Description>	うｐろだ更新を実行するメッセージを定義します。
	 * <Constant>		この属性は定数です。
	 */
	public static final String SCAN_LOADER = "A_big_ass_manager";

	/**
	 * <Attribute>		SCAN_MOD
	 * <Description>	MOD更新を実行するメッセージを定義します。
	 * <Constant>		この属性は定数です。
	 */
	public static final String SCAN_MOD = "Where_is_a_Inari_sushi";

	/**
	 * <Attribute>		TERMINATE
	 * <Description>	スレッド停止メッセージを定義します。
	 * <Constant>		この属性は定数です。
	 */
	public static final String TERMINATE = "First_of_all_there_is_a_rooftop_at_my_house";

	/**
	 * <Attribute>		taskQueue
	 * <Description>	メッセージ送受信を行うキューを定義します。
	 * <Immutable>	この属性は変更することができません。
	 */
	private final TaskQueue taskQueue;

	/**
	 * <Attribute>		upLoaderList
	 * <Description>	うｐろだのリストを格納します。
	 * <Immutable>	この属性は変更することができません。
	 */
	private final LinkedList<UpLoaderBean>	upLoaderList;

	/**
	 * <Attribute>		updatedUpLoaderList
	 * <Description>	情報が更新されたと思われるうｐろだのリストを格納します。
	 * <Immutable>	この属性は変更することができません。
	 */
	private final LinkedList<UpLoaderBean>	upadatedUpLoaderList;

	/**
	 * <Attribute>		notSyncUploaderList
	 * <Description>	データベースと同期が取られていないMODを持つうｐろだのリストを格納します。
	 * <Immutable>	この属性は変更することができません。
	 */
	private final LinkedList<UpLoaderBean> notSyncUploaderList;

	/**
	 * <Attribute>		modItemList
	 * <Description>	更新するMODのリストを格納します。
	 * <Immutable>	この属性は変更することができません。
	 */
	private final LinkedList<ModItemBean> modItemList;

	/**
	 * <Method>			Constructor
	 * <Description>	コンストラクタです。
	 */
	public UploaderScanner() {
		this.taskQueue = TaskQueue.getInstance();
		this.upLoaderList = new LinkedList<UpLoaderBean>();
		this.upadatedUpLoaderList = new LinkedList<UpLoaderBean>();
		this.notSyncUploaderList = new LinkedList<UpLoaderBean>();
		this.modItemList = new LinkedList<ModItemBean>();

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
				CM3D2CrawlLogger.getInstance().debug("メッセージの待機を開始します。");
				String message = this.taskQueue.takeScannerTask();
				CM3D2CrawlLogger.getInstance().debug(message + "を取得しました。イベントハンドラを起動します。");

				/*
				 * ページスキャンを実行します。
				 */
				if(message.equals(SCAN_LOADER)) {
					try {
						UpLoaderDao.load(this.upLoaderList);
					} catch (TimeoutException | SQLException e) {
						// TODO 自動生成された catch ブロック
						CM3D2CrawlLogger.getInstance().error("うｐろだ情報のロードに失敗しました。処理を中断します。", e);
						continue;
					}
					try {
						ScannerHandler.updateUpLoaderTable(this.upLoaderList, this.upadatedUpLoaderList);
					} catch (IOException | SQLException | TimeoutException e) {
						// TODO 自動生成された catch ブロック
						CM3D2CrawlLogger.getInstance().error("うｐろだの情報更新に失敗しました。処理を中断します。", e);
						continue;
					}

					this.upLoaderList.clear();
					this.upadatedUpLoaderList.clear();

				}

				/*
				 * MODスキャンを実行します。
				 */
				else if(message.equals(SCAN_MOD)) {

					try {
						UpLoaderDao.loadBySync(false, notSyncUploaderList);
					} catch (TimeoutException | SQLException e) {
						// TODO 自動生成された catch ブロック
						CM3D2CrawlLogger.getInstance().error("非同期うｐろだリストのロードに失敗しました。処理を中断します。", e);
						continue;
					}

					try {
						ScannerHandler.updateModItemTable(this.notSyncUploaderList, modItemList);
					} catch (TimeoutException | SQLException | IOException e) {
						// TODO 自動生成された catch ブロック
						CM3D2CrawlLogger.getInstance().error("MODリストの情報更新に失敗しました。処理を中断します。", e);
						continue;
					}

					this.notSyncUploaderList.clear();
					this.modItemList.clear();
				}

				/*
				 * スレッドを終了します。
				 */
				else if(message.equals(TERMINATE)) {
					CM3D2CrawlLogger.getInstance().info("Scannerを停止します。");
					break;
				}


				else {
					throw new IllegalMessageException(message);
				}


			} catch (IllegalInterruptedException | IllegalMessageException e) {
				// TODO 自動生成された catch ブロック
				CM3D2CrawlLogger.getInstance().error("Scannerに問題が発生しました。処理を中断します。" , e);
				continue;
			}
		}

	}

}
