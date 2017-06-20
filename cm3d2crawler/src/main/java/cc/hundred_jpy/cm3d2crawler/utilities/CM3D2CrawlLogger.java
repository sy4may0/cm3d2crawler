package cc.hundred_jpy.cm3d2crawler.utilities;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cc.hundred_jpy.cm3d2crawler.exceptions.IllegalPropertyException;

/**
 * CM3D2CrawlLogger
 * [singleton]
 * このクラスはsingletonパターンで動作する。
 *
 * [説明]
 * ログを制御する。
 * ログの出力レベルを動的に制御するため、
 * ログレベルの出力可否をメソッド内で判定する。
 * 出力はエラーモードまたは通常モードの2つで制御される。
 *
 * @author 100JPY
 * @version 1.0
 *
 */
public class CM3D2CrawlLogger {

	/*
	 * Logger
	 * ロガーを定義する。
	 */
	private final Logger logger;

	/*
	 * instance
	 * [singleton]
	 * 自身のインスタンス。
	 * このクラスのインスタンスが2つ以上存在しないことを保証する。
	 */
	private static final CM3D2CrawlLogger instance = new CM3D2CrawlLogger();

	/*
	 * ログのレベルを制御する。
	 * true	: デバッグモードでログを出力する。
	 * false	: 通常モードでログを出力する。
	 */
	private boolean isDebug = false;

	/**
	 * コンストラクタ
	 * [singleton]
	 * 可視性をクラス内に限定し、 他クラスからのインスタンス化を禁止する。
	 *
	 * [操作]
	 * isDebagの参照先を定義し、loggeを構成する。
	 */
	private CM3D2CrawlLogger() {
		PropertyConfigurator.configure("log4j.properties");
		this.logger = Logger.getLogger("cm3d2CrawlLogger");

		try {
			this.isDebug = CM3D2CrawlProperties.getInstance().getBoolPropertiy("debug_log");
		} catch (IllegalPropertyException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

	/**
	 * getInstance
	 * [singleton]
	 * 他クラスがこのクラスを利用する場合、本メソッドによりインスタンスを獲得する。
	 *
	 * [操作]
	 * 自身のインスタンスを返す。
	 * @return
	 */
	public static CM3D2CrawlLogger getInstance() {
		return instance;
	}

	/**
	 * debug
	 * [操作]
	 * デバッグログを出力する。
	 * デバッグモードがdisableの場合、出力しない。
	 * @param message
	 */
	public void debug(String message) {
		if(this.isDebug) {
			this.logger.debug(message);
		}
	}

	/**
	 * info
	 * [操作]
	 * 情報ログを出力する。
	 * デバッグモードがdisableの場合、出力しない。
	 * @param message
	 */
	public void info(String message) {
		if(this.isDebug) {
			this.logger.info(message);
		}
	}

	/**
	 * warn
	 * [操作]
	 * 警告ログを出力する。
	 * @param message
	 */
	public void warn(String message) {
		this.logger.warn(message);
	}

	/**
	 * warn
	 * [操作]
	 * 警告ログを出力する。
	 * @param message
	 */
	public void warn(String message, Throwable t) {
		this.logger.warn(message, t);
	}

	/**
	 * error
	 * [操作]
	 * エラーログを出力する。
	 * @param message
	 */
	public void error(String message) {
		this.logger.error(message);
	}

	/**
	 * error
	 * [操作]
	 * エラーログを出力する。
	 * @param message
	 */
	public void error(String message, Throwable t) {
		this.logger.error(message,t);
	}

	/**
	 * fatal
	 * [操作	]
	 * 致命敵エラーログを出力する。
	 * @param message
	 */
	public void fatal(String message) {
		this.logger.fatal(message);
	}

	/**
	 * fatal
	 * [操作	]
	 * 致命敵エラーログを出力する。
	 * @param message
	 */
	public void fatal(String message, Throwable t) {
		this.logger.fatal(message,t);
	}


}
