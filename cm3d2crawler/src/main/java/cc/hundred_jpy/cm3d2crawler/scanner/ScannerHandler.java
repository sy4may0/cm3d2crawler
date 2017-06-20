package cc.hundred_jpy.cm3d2crawler.scanner;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cc.hundred_jpy.cm3d2crawler.dao.ModItemDao;
import cc.hundred_jpy.cm3d2crawler.dao.UpLoaderDao;
import cc.hundred_jpy.cm3d2crawler.entitybean.ModItemBean;
import cc.hundred_jpy.cm3d2crawler.entitybean.UpLoaderBean;
import cc.hundred_jpy.cm3d2crawler.exceptions.IllegalInterruptedException;
import cc.hundred_jpy.cm3d2crawler.exceptions.TimeoutException;
import cc.hundred_jpy.cm3d2crawler.utilities.CM3D2CrawlLogger;
import cc.hundred_jpy.cm3d2crawler.utilities.CM3D2CrawlProperties;

/**
 * <Class>				ScannerHandler
 * <Description>		Scannerから発生されたイベントから処理を実行するイベントハンドラです。
 * 	<Static>				このクラスはインスタンスを生成することができません。
 *  <Handler>			このクラスはScannerに対するイベントハンドラです。
 *
 * @author 				100JPY
 * @version 				1.0
 */
public class ScannerHandler {

	/**
	 * <Method>			Constructer
	 * <Description>	明示的にインスタンス化を禁止します。
	 */
	private ScannerHandler() {
	}

	///updateUpLoaderTable//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * <Method>			updateUploaderTable
	 * <Description>	うｐろだのページ情報を更新します。
	 * 							このメソッドはうｐろだのリストを参照し、更新します。
	 * 							paramに示す引数を適宜用意し、実行してください。
	 * 	<Static>			このメソッドは静的メンバです。
	 *
	 * @param 			loaderList すでにデータベースに登録されたうｐろだのArrayListが参照されることを想定します。
	 * @param 			updatedLoaderList 空のArrayListが参照されることを想定します。
	 * @throws 			IOException
	 * @throws TimeoutException
	 * @throws IllegalInterruptedException
	 * @throws SQLException
	 */
	public static void updateUpLoaderTable(LinkedList<UpLoaderBean> loaderList, LinkedList<UpLoaderBean> updatedLoaderList) throws IOException, SQLException, IllegalInterruptedException, TimeoutException {

		scanLoaderList(loaderList);
		CM3D2CrawlLogger.getInstance().info("うｐろだURLリストのスキャンを実行しました。");

		scanLoaderTerminal(loaderList, updatedLoaderList);
		CM3D2CrawlLogger.getInstance().info("うｐろだのページ情報スキャンを実行しました。");

		updateTable(updatedLoaderList);
		CM3D2CrawlLogger.getInstance().info("うｐろだのスキャン及び更新が完了しました。");

	}

	/**
	 * <Method>			loadLoaderList
	 * <Description>	うｐろだのURLリストを生成します。
	 * 							このメソッドはURLの探索のみを行います。うｐろだのページ数を解決しません。
	 * <Static>			このメソッドは静的メンバです。
	 *
	 *	@param			loaderList	データベースから取得されたうｐろだのリストを想定します。
	 * @parm 			updatedLoaderoaderList		初期化された、かつ空のリストを想定します。
	 * @throws 			IOException
	 */
	private static void scanLoaderList(LinkedList<UpLoaderBean> loaderList) throws IOException {

		Connection conn = Jsoup.connect(CM3D2CrawlProperties.getInstance().getStringProperty("root_url"));
		Document doc = conn.get();
		CM3D2CrawlLogger.getInstance().debug("root_urlからHTMLテキストを読み込みました。");

		String description = doc.getElementsByAttributeValueMatching("name", "description").toString();
		CM3D2CrawlLogger.getInstance().debug("root_urlのHTMLテキストからうｐろだの必要部分を抽出します。");

		UpLoaderBean ulb = new UpLoaderBean();
		ulb.setName("ろだA");
		ulb.setUrl(CM3D2CrawlProperties.getInstance().getStringProperty("root_url"));

		boolean isExists = false;
		for(UpLoaderBean ul : loaderList) {
			if(ul.getUrl().equals(ulb.getUrl())) {
				isExists = true;
			}
		}
		if(! isExists) {
			ulb.setModAmount(0);
			ulb.setTermination(0);
			ulb.setSync(false);
			loaderList.add(ulb);
			CM3D2CrawlLogger.getInstance().debug(ulb.getName() + "をL1更新リストに追加しました。URL:" + ulb.getUrl());
		}


		for(String line : description.split("\n")) {
			if(line.contains("ux.getuploader.com")) {
				UpLoaderBean ul = new UpLoaderBean();
				String[] s = line.split("\\s");
				ul.setName(s[0]);
				ul.setUrl(s[1]);

				isExists = false;
				for(UpLoaderBean u : loaderList) {
					if(ul.getUrl().equals(u.getUrl())){
						isExists = true;
					}
				}
				if(! isExists) {
					ul.setModAmount(0);
					ul.setTermination(0);
					ul.setSync(false);
					loaderList.add(ul);
					CM3D2CrawlLogger.getInstance().debug(ul.getName() + "をL1更新リストに追加しました。URL:" + ul.getUrl());
				}
			}
		}

	}

	/**
	 * <Method>			scanLoaderTerminal
	 * <Description>	うｐろだのページ数及び保有MOD数を探索します。
	 * 							このメソッドはscanLoaderList後に呼び出されることを想定しています。
	 * <Static>			このメソッドは静的メンバです。
	 * @param upLoaderList
	 * @param updatedLoaderList
	 * @throws IOException
	 */
	private static void scanLoaderTerminal(LinkedList<UpLoaderBean> upLoaderList, LinkedList<UpLoaderBean> updatedLoaderList) throws IOException {
		UpLoaderBean ul = null;;
		while ((ul = upLoaderList.poll()) != null) {
			int i = 1;
			int modAmount = 0;
			while(true) {
				String url = ul.getUrl() + "index/" + i + "/date/desc";

				Document doc = Jsoup.connect(url).get();
				Element table = doc.select("tbody").get(0);
				Elements row = table.select("tr");

				modAmount += row.size();

				if(row.size() == 0) {
					if(ul.getModAmount() < modAmount) {
						ul.setModAmount(modAmount);
						CM3D2CrawlLogger.getInstance().warn("うｐろだ側で削除されたMODが存在します。削除されたMODのデータはローカルに保持されます。");
					}
					if(ul.getTermination() != (i-1)) {
						ul.setTermination(i-1);
						ul.setSync(false);
						CM3D2CrawlLogger.getInstance().debug(ul.getName() + "のTerminationを探索しました。Termination:" + ul.getTermination());
					}
					if(ul.getModAmount() < modAmount) {
						ul.setModAmount(modAmount);
						ul.setSync(false);
						CM3D2CrawlLogger.getInstance().debug(ul.getName() + "のModAmountを探索しました。ModAmount:" + ul.getModAmount());
					}
					if(! ul.isSync()) {
						updatedLoaderList.add(ul);
						CM3D2CrawlLogger.getInstance().debug(ul.getName() + "をL2更新リストに追加しました。");
					}
					break;
				}
				i++;
			}
		}
	}

	/**
	 * <Method>			updateTable
	 * <Description>	うpろだのテーブルをアップデートします。
	 * 							このメソッドはscanLoaderTerminalの後に呼び出されることそ想定します。
	 * <Static>			このメソッドは静的メンバです。
	 *
	 * @param 			upLoaderList
	 * @throws	 		SQLException
	 * @throws 			IllegalInterruptedException
	 * @throws 			TimeoutException
	 */
	private static void updateTable(LinkedList<UpLoaderBean> upLoaderList) throws SQLException, IllegalInterruptedException, TimeoutException {
		UpLoaderBean ul = null;;
		while ((ul = upLoaderList.poll()) != null) {
			UpLoaderDao.updateOrSave(ul);
		}

	}

	/**
	 * <Method>			updateModItemTable
	 * <Description>	ModItemTableをアップデートします。
	 * 							paramに示すリストを適宜用意し、実行してください。
	 * <Static>			このメソッドは静的メンバです。
	 *
	 * @param 			notSyncLoaderList  非同期のうｐろだリストが参照されることを想定します。このリストが空の場合、非同期リストを再構成します。
	 * @param 			modItemLis MODのリストを構成します。空のリストが参照されることを想定しています。
	 * @throws 			IllegalInterruptedException
	 * @throws 			TimeoutException
	 * @throws 			SQLException
	 * @throws 			IOException
	 */

	///updateModItemTable//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void updateModItemTable(LinkedList<UpLoaderBean> notSyncLoaderList, LinkedList<ModItemBean> modItemList) throws IllegalInterruptedException, TimeoutException, SQLException, IOException {
		if(notSyncLoaderList.size() == 0) {
			UpLoaderDao.loadBySync(false, notSyncLoaderList);
			CM3D2CrawlLogger.getInstance().debug("非同期うｐろだのリストを検索しました。");
		}
		scanModItem(notSyncLoaderList, modItemList);
		CM3D2CrawlLogger.getInstance().info("MODリストをスキャンしました。");

		updateModItem(modItemList);
		CM3D2CrawlLogger.getInstance().info("MODのリストのスキャン及び更新が完了しました。");


	}

	/**
	 * <Method>			scanModItem
	 * <Description>	うｐろだからMOD情報を探索します。
	 * <Static>			このメソッドは静的メンバです。
	 *
	 * @param 			notSyncLoaderList
	 * @param 			modItemList
	 * @throws 			IOException
	 * @throws 			SQLException
	 * @throws 			IllegalInterruptedException
	 * @throws 			TimeoutException
	 */
	private static void scanModItem(LinkedList<UpLoaderBean> notSyncLoaderList, LinkedList<ModItemBean> modItemList) throws IOException, SQLException, IllegalInterruptedException, TimeoutException {
		UpLoaderBean ul = notSyncLoaderList.poll();

		int i = ul.getTermination();
		while(i != 0) {
			Document doc = Jsoup.connect(ul.getUrl() + "/index/" + i + "/date/desc").get();
			Element table = doc.select("tbody").get(0);
			Elements row = table.select("tr");

			for(Element el : row) {
				Elements td = el.select("td");
				ModItemBean mi = new ModItemBean();

				mi.setUrl(td.get(0).getElementsByAttribute("href").attr("href"));
				mi.setFileName(td.get(0).text());
				mi.setComment(td.get(1).text());
				mi.setOrigFileName(td.get(2).text());
				mi.setFileSize(-1);
				mi.setNominalSize(td.get(3).text());

				DateTimeFormatter f = DateTimeFormatter.ofPattern("uu/M/d H:m");
				LocalDateTime d = LocalDateTime.parse(td.get(4).text(), f);
				mi.setUploadDate(d);
				mi.setDownload(false);
				mi.setMasterUrl(ul.getId());

				modItemList.add(mi);
				CM3D2CrawlLogger.getInstance().debug(mi.getFileName() + "を更新リストに追加しました。");

			}
			i--;
		}
	}

	/**
	 * <Method>			updateModItem
	 * <Description>	探索したMOD情報をDBに反映します。
	 * 							このメソッドはscanModItem後に呼び出されることを想定します。
	 * <Static>			このメソッドは静的メンバです。
	 *
	 * @param 			modItemList
	 * @throws 			IllegalInterruptedException
	 * @throws 			SQLException
	 * @throws 			TimeoutException
	 */
	private static void updateModItem(LinkedList<ModItemBean> modItemList) throws IllegalInterruptedException, SQLException, TimeoutException {
		ModItemBean mi;
		int id = modItemList.peek().getMasterUrl();

		while((mi = modItemList.poll()) != null) {
			ModItemDao.save(mi);
		}

		UpLoaderDao.updateSync(id, true);

		CM3D2CrawlLogger.getInstance().info("うｐろだとの同期が完了しました。");

	}
}
