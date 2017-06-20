package cc.hundred_jpy.cm3d2crawler.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import cc.hundred_jpy.cm3d2crawler.exceptions.IllegalInterruptedException;
import cc.hundred_jpy.cm3d2crawler.exceptions.IllegalPropertyException;
import cc.hundred_jpy.cm3d2crawler.exceptions.TimeoutException;
import cc.hundred_jpy.cm3d2crawler.utilities.CM3D2CrawlLogger;
import cc.hundred_jpy.cm3d2crawler.utilities.CM3D2CrawlProperties;

/**
 *  <Class>			 	ConnectionManager
 *  <Description> 		このクラスはデータベースのコネクションを提供します。
 *  <ThreadSafe> 		このクラスはスレッドセーフです。
 *  <Singleton> 		このクラスはSingletonです。インスタンスが2つ以上存在しないことを保証します。
 *  <Immutable>		このクラスはImmutableです。各属性の参照が変動しないことを保証します。
 *
 * @author 				100JPY
 * @version 				1.0
 *
 */
public class ConnectionManager {

	/**
	 * <Attribute>		connectionPool
	 * <Description> 	Connectionをプールします。
	 * <Immutable> 	この属性は変更することができません。
	 */
	private final BlockingQueue<Connection> connectionPool;

	/**
	 * <Attribute> 		instance
	 * <Description> 	自身のインスタンスを定義します。
	 * <Immutable> 	この属性は変更する事ができません。
	 * <Static> 			この属性は静的メンバです。
	 */
	private final static ConnectionManager instance = new ConnectionManager();

	/**
	 * <Method>			Constructor
	 * <Description> 	コンストラクタです。
	 * 							コンストラクタではデータベースとのコネクションプールを生成します。
	 * <Singleton> 		このコンストラクタが2回以上実行されるような変更を禁止します。
	 */
	private ConnectionManager() {
		int size = 0;
		String url = null;
		String user = null;
		String pass = null;

		try {
			size = CM3D2CrawlProperties.getInstance().getIntProperty("db_con_pool");
			url = CM3D2CrawlProperties.getInstance().getStringProperty("db_url");
			user = CM3D2CrawlProperties.getInstance().getStringProperty("db_user");
			pass = CM3D2CrawlProperties.getInstance().getStringProperty("db_password");
		} catch (IllegalPropertyException e) {
			// TODO 自動生成された catch ブロック
			CM3D2CrawlLogger.getInstance().fatal("ConnectionManagerの生成に失敗しました。",e);
		}

		this.connectionPool = new LinkedBlockingQueue<Connection>(size);
		CM3D2CrawlLogger.getInstance().info("ConnectionPoolを生成しました。");

		for(int i = 0; i < size; i++) {
			try {
				this.connectionPool.add(DriverManager.getConnection(url,user,pass));
				CM3D2CrawlLogger.getInstance().debug("Connectionを生成しました。");
			} catch (SQLException e) {
				// TODO 自動生成された catch ブロック
				CM3D2CrawlLogger.getInstance().error("Connectionの生成に失敗しました。",e);
			}
		}

	}

	/**
	 * <Method> 		getInstance
	 * <Description> 	自身のインスタンスを返します。このクラスを利用する場合、このメソッドを利用してインスタンスを参照してください。
	 *
	 * @return 			ConnectionManagerのインスタンスを返します。
	 */
	public static ConnectionManager getInstance() {
		return instance;
	}

	/**
	 * <Method> 		getConnection
	 * <Description> 	ConnectionPoolよりコネクションを取得し、返します。
	 * <Caution>			このメソッドを実行後、必ずreturnConnectionを実行してください。
	 * 							このクラスは消失したコネクションを復元する手段を持ちません。
	 *
	 * @return 			データベースのコネクションを返します。コネクションが枯渇している場合、30秒間待機します。
	 * 							タイムアウトとなった場合、例外をスローします。
	 * @throws 			IllegalInterruptedException
	 * @throws TimeoutException
	 */
	public Connection getConnection() throws IllegalInterruptedException, TimeoutException  {
		Connection connection = null;
		try {
			connection = this.connectionPool.poll(30000,TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			throw new IllegalInterruptedException();
		}
		if(connection != null) {
			CM3D2CrawlLogger.getInstance().debug("Connectionが取得されました。HASH :" + connection.hashCode());
			return connection;
		}else {
			throw new TimeoutException();
		}

	}

	/**
	 * <Method>			returnConnection
	 * <Description>	コネクションをプールに返却します。
	 * <Caution>			getConnection後に必ず実行してください。
	 *
	 * @param 			connection 必ずこのインスタンスからgetConnectionにより取得したコネクションを返却してください。
	 * @throws 			IllegalStateException	コネクションプールに空きがない場合発生します。この例外は想定されません。
	 */
	public void returnConnection(Connection connection) throws IllegalStateException{
		this.connectionPool.add(connection);
		CM3D2CrawlLogger.getInstance().debug("Connectionが返却されました。HASH :" + connection.hashCode());
	}


}
