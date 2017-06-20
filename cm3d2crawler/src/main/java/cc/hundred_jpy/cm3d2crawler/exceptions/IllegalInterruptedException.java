package cc.hundred_jpy.cm3d2crawler.exceptions;
/**
 * <Class>				IllegalInterruptedException
 * <Description>		割り込みが発生するべきではない処理でInterruptedExceptionが発生した場合にスローします。
 * <Exception>			このクラスは例外を定義します。
 *
 * @author 				100JPY
 * @version 				1.0
 *
 */
public class IllegalInterruptedException extends Exception {

	/**
	 * <Method>			constructor
	 * <Description>	コンストラクタです。
	 */
	public IllegalInterruptedException() {
		super("不正な割り込みが発生しました。");
	}
}
