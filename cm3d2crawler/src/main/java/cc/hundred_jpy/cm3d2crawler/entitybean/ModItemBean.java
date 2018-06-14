package cc.hundred_jpy.cm3d2crawler.entitybean;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <Class> 				ModItemBean
 * <Description> 		テーブル"cm3d2_crawler_mod_m"に対するPOJOです。
 * 								各うｐろだにアップロードされたMODの情報を保持します。
 *
 * <UnThreadSafe>	このクラスはスレッドセーフではありません。
 * <Serializable>		このクラスは直列化できます。
 *
 * @author 100JPY
 * @version 1.0
 *
 */
public class ModItemBean implements Serializable {
	/**
	 * <Attribute>		id
	 * <Description>	このMODのIDを定義します。
	 * <PrimaryKey>	DBスキーマ側で主キー制約を持ちます。
	 * <NotNull>			DBスキーマ側でNULL禁止制約を持ちます。
	 */
	private int id = -1;

	/**
	 * <Attribute>		comment
	 * <Description>	MODのコメントを定義します。
	 */
	private String comment = null;

	/**
	 * <Attribute>		origFileName
	 * <Description>	MODのオリジナルファイルネームを定義します。
	 * <NotNull>			DBスキーマ側でNULL禁止制約を持ちます。
	 */
	private String origFileName = null;

	/**
	 * <Attribute>		url
	 * <Description>	MODのURLを定義します。
	 * 							このURLはMODページのURLであり、ダウンロードURLではありません。
	 * <NotNull>			DBスキーマ側でNULL禁止制約を持ちます。
	 * <Unique>			DBスキーマ側で一意制約を持ちます。
	 */
	private String url = null;

	/**
	 * <Attribute>		fileName
	 * <Description>	MODファイル名を定義します。
	 * <NotNull>			DBスキーマ側でNULL禁止制約を持ちます。
	 */
	private String fileName = null;

	/**
	 * <Attribute>		fileSize
	 * <Description>	MODデータのファイルサイズを定義します。
	 * 							このファイルサイズはダウンロードが完了した量を示します。
	 * <NotNull>			DBスキーマ側でNULL禁止制約を持ちます。
	 */
	private long fileSize = -1;

	/**
	 * <Attribute>		nominalSize
	 * <Description>	MODデータの公称ファイルサイズを定義します。
	 * <NotNull>			DBスキーマ側でNULL禁止制約を持ちます。
	 */
	private String nominalSize = null;

	/**
	 * <Attribute>		uploadDate
	 * <Description>	MODがアップロードされた日時を定義します。
	 * <NotNull>			DBスキーマ側でNULL禁止制約を持ちます。
	 */
	private LocalDateTime uploadDate = null;

	/**
	 * <Attribute>		isDownload
	 * <Description>	MODファイルがダウンロード状況を定義します。
	 * 							ダウンロードされていた場合を正とします。
	 * <NotNull>			DBスキーマ側でNULL禁止制約を持ちます。
	 */
	private boolean isDownload = false;

	/**
	 * <Attribute>		masterUrl
	 * <Description>	このMODがアップロードされたうｐろだのURLを定義します。
	 * <ForeignKey>	DBスキーマ側で外部キー制約を持ちます。外部キーはUploaderBean.urlです。
	 * <NotNull>			DBスキーマ側でNULL禁止制約を持ちます。
	 */
	private int masterUrl = -1;

	/*
	 * 以下はgetter/setterです。
	 */

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getOrigFileName() {
		return origFileName;
	}

	public void setOrigFileName(String origFileName) {
		this.origFileName = origFileName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getNominalSize() {
		return nominalSize;
	}

	public void setNominalSize(String nominalSize) {
		this.nominalSize = nominalSize;
	}

	public LocalDateTime getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(LocalDateTime uploadDate) {
		this.uploadDate = uploadDate;
	}

	public boolean isDownload() {
		return isDownload;
	}

	public void setDownload(boolean isDownload) {
		this.isDownload = isDownload;
	}

	public int getMasterUrl() {
		return masterUrl;
	}

	public void setMasterUrl(int masterUrl) {
		this.masterUrl = masterUrl;
	}


}
