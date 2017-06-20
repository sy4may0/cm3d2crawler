package cc.hundred_jpy.cm3d2crawler.exceptions;

/**
 * <Class>				IllegalMessageException
 * <Description>		定義されていないメッセージが処理された場合にスローします。
 * <Exception>			このクラスは例外を定義します。
 * @author 100JPY
 */
public class IllegalMessageException extends Exception{

	/**
	 * <Attribute>		illegalMessage
	 * <Description>	問題のあったメッセージを定義します。
	 */
	private String illegalMessage;

	/**
	 * <Method>			Constructor
	 * <Description>	コンストラクタです。
	 */
	public IllegalMessageException(String illegalMessage) {
		super("定義されていないメッセージが発生しました。" + illegalMessage);
		this.illegalMessage = illegalMessage;
	}

	public String getIllegalMessage() {
		return illegalMessage;
	}

}
