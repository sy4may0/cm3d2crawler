package cc.hundred_jpy.cm3d2crawler.exceptions;

/**
 * <Class>				TimeoutException
 * <Description>		スレッドブロッキング時、タイムアウトが発生した場合スローします。
 *
 * @author 				100JPY
 * @version				1.0
 */
public class TimeoutException extends Exception{

	/**
	 * <Method>			Constructor
	 * <Description>	コンストラクタです。
	 */
	public TimeoutException() {
		super("タイムアウトが発生しました。");
	}

}
