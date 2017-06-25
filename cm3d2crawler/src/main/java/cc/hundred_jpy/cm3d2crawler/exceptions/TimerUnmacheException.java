package cc.hundred_jpy.cm3d2crawler.exceptions;

/**
 * <Class>				TimerUnmacheException
 * <Description>		不正なタスク実行時刻が発生した際にスローされます。。
 * <Exception>			このクラスは例外を定義します。
 * @author 100JPY
 * @version 1.0
 */
public class TimerUnmacheException extends Exception {
	/**
	 * <Method>			Constructor
	 * <Description>	コンストラクタです。
	 */
	public TimerUnmacheException() {
		super("不正なタスク実行時刻設定があります。");
	}
}
