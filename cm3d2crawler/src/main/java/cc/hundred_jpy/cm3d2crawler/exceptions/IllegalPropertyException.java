package cc.hundred_jpy.cm3d2crawler.exceptions;
/**
 * <Class>				IllegalPropertyException
 * <Description>		 cm3d2crawler.propertiesからの設定読み込み時、不正な値が入力された場合スローする。
 * <Exception>			このクラスは例外を定義します。
 *
 * @author 				100JPY
 * @version 				1.0
 */
public class IllegalPropertyException extends Exception {
	/**
	 * <Attribute>		key
	 * <Description>	不正な値を持つPropertyのキーを定義します。
	 */
	private String key = null;
	/**
	 * <Attribute>		value
	 * <Description>	Propertyに入力された不正な値
	 */
	private String value = null;

	/**
	 * <Method>			constructor
	 * <Description>	コンストラクタです。
	 *
	 * @param 			key
	 * @param 			value
	 */
	public IllegalPropertyException(String key, String value) {
		super("不正なプロパティ設定があります。 [キー]: " + key + ", [値]: " + value);
		this.key = key;
		this.value = value;

	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

}
