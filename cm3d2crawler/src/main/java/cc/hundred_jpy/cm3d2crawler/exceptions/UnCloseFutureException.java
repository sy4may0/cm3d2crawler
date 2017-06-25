package cc.hundred_jpy.cm3d2crawler.exceptions;

import java.util.concurrent.Future;

/**
 * <Class>				UnCloseFutureException
 * <Description>		正常にクローズされていないFutureオブジェクトが存在した場合にスローされます。
 * <Exception>			このクラスは例外を定義します。
 *
 * @author 100JPY
 * @version 1.0
 *
 */
public class UnCloseFutureException extends Exception {

	/**
	 * <Attribute>		future
	 * <Description>	問題のあったFutureオブジェクト
	 * <Immutable>	この属性は変更することができません。
	 */
	private final Future<String> future;

	/**
	 * <Method>			Constructor
	 * <Description>	コンストラクタです。
	 */
	public UnCloseFutureException(Future<String> future) {
		super("不正なFutureオブジェクトが存在します。Future:" + future.toString());
		this.future = future;
	}

	public Future<String> getFuture() {
		return future;
	}

}
