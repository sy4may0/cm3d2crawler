package cc.hundred_jpy.cm3d2crawler.downloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.HashMap;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import cc.hundred_jpy.cm3d2crawler.dao.ModItemDao;
import cc.hundred_jpy.cm3d2crawler.entitybean.ModItemBean;
import cc.hundred_jpy.cm3d2crawler.exceptions.IllegalInterruptedException;
import cc.hundred_jpy.cm3d2crawler.exceptions.TimeoutException;
import cc.hundred_jpy.cm3d2crawler.utilities.CM3D2CrawlLogger;
import cc.hundred_jpy.cm3d2crawler.utilities.CM3D2CrawlProperties;

/**
 * <Class>				DownloaderHandler
 * <Description>		Downloaderから発生されたイベントから処理を実行するイベントハンドラです。
 * 	<Static>				このクラスはインスタンスを生成することができません。
 *
 * @author 				100JPY
 * @version 				1.0
 */
public class DownloaderHandler {

	/**
	 * <Method>			Constructor
	 * <Description>	明示的にインスタンス化を禁止します。
	 */
	private  DownloaderHandler() {
	}

	/**
	 * <Method>			downloadModItem
	 * <Description>	MODをダウンロードします。
	 * 							paramに指定する値を適宜用意し、実行してください。
	 * <Static>			このメソッドは静的メンバです。
	 * <Handler>		このクラスはDownloaderに対するイベントハンドラです。
	 *
	 * @param 			modItem ダウンロードするModItemを参照することを想定します。
	 * @param 			headers POST時のHeaderを指定してください。
	 * @throws 			IOException
	 * @throws 			SQLException
	 * @throws 			TimeoutException
	 * @throws 			IllegalInterruptedException
	 */
	public static void downloadModItem(ModItemBean modItem, HashMap<String, String> headers) throws IOException, IllegalInterruptedException, TimeoutException, SQLException {
		String token = getToken(modItem);
		CM3D2CrawlLogger.getInstance().debug(modItem.getFileName() + "のダウンロードトークンを取得しました。");

		String url = getDownloadUrl(modItem, headers, token);
		CM3D2CrawlLogger.getInstance().debug(modItem.getFileName() + "のダウンロードURLを取得しました。URL:" + url);

		download(modItem,url);
		CM3D2CrawlLogger.getInstance().debug(modItem.getFileName() + "をダウンロードしました。SIZE:" + modItem.getFileSize());

		updateTable(modItem);
	}

	/**
	 * <Method>			getToken
	 * <Description>	POST時に必要なトークンを取得します。
	 * <Static>			このメソッドは静的メンバです。
	 * @param 			modItem
	 * @return			トークンを返します。
	 * @throws 			IOException
	 */
	private static String getToken(ModItemBean modItem) throws IOException {
		Document doc = Jsoup.connect(modItem.getUrl()).get();
		Elements form = doc.getElementsByAttributeValue("name", "agree");
		Elements input = form.get(0).getElementsByAttributeValue("name", "token");
		return input.attr("value");
	}

	/**
	 * <Method>			getDownloadUrl
	 * <Description>	トークンからダウンロードURLを取得します。
	 * 							このメソッドはgetTokenによりトークンを取得した後実行されることを想定します。
	 * <Static>			このメソッドは静的メンバです。
	 *
	 * @param 			modItem
	 * @param 			headers POSTリクエスト送信時のヘッダを指定してください。
	 * @param 			token
	 * @return			ダウンロードURLを返します。
	 * @throws 			IOException
	 */
	private static String getDownloadUrl(ModItemBean modItem, HashMap<String, String> headers, String token) throws IOException {
		Connection conn = Jsoup.connect(modItem.getUrl());
		headers.replace("Referer", modItem.getUrl());
		conn.headers(headers);
		conn.data("token", token);

		Document doc = conn.post();

		Elements href = doc.getElementsByAttributeValue("class", "download");
		return href.attr("href");
	}

	/**
	 * <Method>			download
	 * <Description>	Modをダウンロードします。
	 * 							このメソッドはgetDownloadUrlによりダウンロードURL取得後に実行されることを想定しています。
	 * <Caution>			このメソッドは長時間のIOが発生します。
	 * <Static>			このメソッドは静的メンバです。
	 * @param			modItem
	 * @param 			downloadUrl
	 * @throws 			IOException
	 */
	private static void download(ModItemBean modItem, String downloadUrl) throws IOException {
		URL url = new URL(downloadUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("User-Agent", CM3D2CrawlProperties.getInstance().getStringProperty("user_agent"));

		if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			try(InputStream is = conn.getInputStream()) {
				File file = new File(CM3D2CrawlProperties.getInstance().getStringProperty("download_dir") + "/" + modItem.getFileName());
				modItem.setFileSize(Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING));
				modItem.setDownload(true);
			}
		}
	}

	/**
	 * <Method>			updateTable
	 * <Description>	データベースの情報をアップデートします。
	 * 							全てのダウンロード処理完了後に実行してください。
	 * <Static>			このメソッドは静的メンバです。
	 * @param			modItem
	 * @throws 			IllegalInterruptedException
	 * @throws 			TimeoutException
	 * @throws 			SQLException
	 */
	private static void updateTable(ModItemBean modItem) throws IllegalInterruptedException, TimeoutException, SQLException {
		ModItemDao.update(modItem);
	}


}
