package cc.hundred_jpy.cm3d2crawler.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import cc.hundred_jpy.cm3d2crawler.exceptions.IllegalPropertyException;

/**
 * <Class> 				CM3D2CrawlProperties
 * <Description>		プロパティを提供します。
 * <Singleton>			このクラスはSingletonです。インスタンスが2つ以上存在しないことを保証します。
 * <ThreadSafe>		このクラスはスレッドセーフです。
 *
 * @author 100JPY
 * @version 1.0
 */
public class CM3D2CrawlProperties {

	/**
	 * ---(仮設定)---
	 * ログファイルを定義します。
	 */
	public static final String PROP_FILE = "cm3d2crawler.properties";

	/**
	 * <Attribute> 		properties
	 * <Description>	このクラスのプロパティインスタンスです。
	 * 	<Immutable>	この属性は変更することができません。
	 */
	private final Properties properties;

	/**
	 * <Attribute>		instance
	 * <Description>	このクラスのインスタンスを定義します。
	 * <Immutable>	この属性は変更することができません。
	 * <static>			この属性は静的メンバです。
	 */
	private static final CM3D2CrawlProperties instance = new CM3D2CrawlProperties();

	/**
	 * <Method> 		Constructor
	 * <Description>	コンストラクタです。このクラスのPropertiesが読み込まれます。
	 *
	 */
	private CM3D2CrawlProperties() {
		this.properties = new Properties();
		try {
			this.refleshProperties();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	/**
	 * <Method>			refleshProperties
	 * <Description>	このクラスのpropertiesを更新します。
	 *
	 * @throws IOException
	 */
	public void refleshProperties() throws IOException {
		InputStream is = new FileInputStream(PROP_FILE);
		this.properties.load(is);
		is.close();

	}

	/**
	 * <Method>			getInstance
	 * <Description>	自身のインスタンスを返します。このクラスを他クラスから利用する場合、このメソッドを利用してください。
	 *
	 * @return			CM3D2CrawlPropertiesのインスタンスを返します。
	 */
	public static CM3D2CrawlProperties getInstance() {
		return instance;
	}

	/**
	 *<Method>			getStringProperty
	 *<Description>	プロパティの値をString型で返します。
	 *
	 * @param 			key
	 * @return			プロパティをString型で返します。
	 */
	public String getStringProperty(String key) {
		return this.properties.getProperty(key);
	}

	/**
	 * <Method>			getIntProperty
	 * <Description>	プロパティをInteger型で返します。
	 *
	 * @param 			key
	 * @return			プロパティをInteger型で返します。デコードに失敗した場合、例外をスローします。
	 * @throws 			IllegalPropertyException	デコードに失敗した場合スローされます。
	 */
	public Integer getIntProperty(String key) throws IllegalPropertyException {
		String prop = this.properties.getProperty(key);
		Integer result = null;
		try {
			result = Integer.decode(prop);
			return result;
		}catch(NumberFormatException e) {
			throw new IllegalPropertyException(key,prop);
		}
	}

	public boolean getBoolPropertiy(String key) throws IllegalPropertyException {
		String prop = this.properties.getProperty(key);
		if(prop.equals("enable")) {
			return true;
		}else if(prop.equals("disable")) {
			return false;
		}else {
			throw new IllegalPropertyException(key,prop);
		}
	}

}
