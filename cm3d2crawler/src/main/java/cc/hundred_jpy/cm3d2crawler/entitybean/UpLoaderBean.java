package cc.hundred_jpy.cm3d2crawler.entitybean;

import java.io.Serializable;

/**
 * <Class>				UpLoaderBean
 * <Description>		テーブル"cm3d2_crawler_loader_m"に対するPOJOです。
 * 								うｐろだのページ情報を保持します。
 * <Example>			このインスタンスの生成例を以下に示します。
 * 										id 					: L_A
 * 										name			: CM3D2 MOD ろだA
 * 										url					: http://ux.getuploader.com/cm3d2/
 * 										termination	: 10
 *
 * <UnThreadSafe>	このクラスはスレッドセーフではありません。
 * <Serializable>		このクラスは直列化できます。
 *
 * @author 100JPY
 *
 */
public class UpLoaderBean implements Serializable {

	/**
	 * <Attribute>		id
	 * <Description>	このうｐろだのIDを定義します。
	 * <PrimeryKey>	DBスキーマ側で主キー制約を持ちます。
	 * <NotNull>			DBスキーマ側でNULL禁止制約を持ちます。
	 */
	private int id = -1;

	/**
	 * <Attribute>		name
	 * <Description>	このうｐろだの名前を定義します。
	 * <NotNull>			DBスキーマ側でNLL禁止制約を持ちます。
	 */
	private String name = null;

	/**
	 * <Attribute>		url
	 * <Description>	このうｐろだのURLを定義します。
	 * <Unique>			DBスキーマ側で一意制約を持ちます。
	 * <NotNull>			DBスキーマ側でNULL禁止制約を持ちます。
	 */
	private String url = null;

	/**
	 * <Attribute>		termination
	 * <Description>	このうｐろだの最終ページ番号を定義します。
	 * <NotNull>			DBスキーマ側でNULL禁止制約を持ちます。
	 */
	private int termination = -1;

	/**
	 * <Attribute>		modAmount
	 * <Description>	このうｐろだが保有するMOD数を定義します。
	 * <NotNull>			DBスキーマ側でNULL禁止制約を持ちます。
	 */
	private int modAmount = -1;

	/**
	 * <Attribute>		isReload
	 * <Description>	このうｐろだがデータベースと同期されているかを定義します。
	 * 	<NotNull>			DBスキーマ側でNULL禁止制約を持ちます。
	 */
	private boolean isSync = true;


	/*
	 * 以下getter/setterです。
	 */

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getTermination() {
		return termination;
	}

	public void setTermination(int termination) {
		this.termination = termination;
	}

	public int getModAmount() {
		return modAmount;
	}

	public void setModAmount(int modAmount) {
		this.modAmount = modAmount;
	}

	public boolean isSync() {
		return isSync;
	}

	public void setSync(boolean isSync) {
		this.isSync = isSync;
	}




}
