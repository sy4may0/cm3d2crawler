package cc.hundred_jpy.cm3d2crawler.entitybean;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * <Class> 				TaskBean
 * <Description> 		テーブル"cm3d2_crawler_schedule_m"に対するPOJOです。
 *                       		タスクの実行時間、タスクの実行命令メッセージを保持します。
 * <Example>			このクラスのインスタンス生成例を以下に示します。
 * 										id 				: scner00
 * 										taskType	: SCNER
 * 										message	: U_UPLD
 * 										time			: 05:30:00
 * 									この例では、05:30:00に、ScannerクラスにUploaderのアップデートを命令するタスクが実行されます。
 * 									命令(message)やタスク識別子(taskType)の種類は、各タスク実行クラスを確認してください。
 * <NotThreadSafe> 	このクラスはスレッドセーフではありません。
 * <Serializable>		このクラスは直列化できます。
 *
 * @author 100JPY
 * @version 1.0
 *
 */
public class TaskBean implements Serializable {

	/**
	 * <Attribute>		id
	 * <Description> 	このタスクのIDを定義します。
	 * <PrimaryKey>	DBスキーマ側で主キー制約を持ちます。
	 * <NotNull>			DBスキーマ側でNULL禁止制約を持ちます。
	 */
	private String id = null;

	/**
	 * <Attribute> 		taskType
	 * <Description>	タスク識別子を定義します。このタスクの実行クラスを判定する文字列を指定します。
	 * <NotNull>			DBスキーマ側でNULL禁止制約を持ちます。
	 */
	private String taskType = null;

	/**
	 * <Attribute>		message
	 * <Description>	タスク実行メッセージを定義します。タスク実行メッセージは各タスク実行クラスを参照してください。
	 * <NotNull>			DBスキーマ側でNULL禁止制約を持ちます。
	 */
	private String message = null;

	/**
	 * <Attribute>		runTime
	 * <Description>	タスク実行時刻を定義します。
	 * <NotNull>			DBスキーマ側でNULL禁止制約を持ちます。
	 */
	private LocalTime runTime = null;

	/*
	 * 以下はgetter/setterです。
	 */

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public LocalTime getRunTime() {
		return runTime;
	}
	public void setRunTime(LocalTime runTime) {
		this.runTime = runTime;
	}



}
