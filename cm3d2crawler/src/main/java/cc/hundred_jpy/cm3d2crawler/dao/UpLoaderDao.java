package cc.hundred_jpy.cm3d2crawler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import cc.hundred_jpy.cm3d2crawler.entitybean.UpLoaderBean;
import cc.hundred_jpy.cm3d2crawler.exceptions.IllegalInterruptedException;
import cc.hundred_jpy.cm3d2crawler.exceptions.TimeoutException;
import cc.hundred_jpy.cm3d2crawler.utilities.CM3D2CrawlLogger;

/**
 * <Class>				UpLoaderDao
 * <Description>		このクラスはデータベースとの仲介を行います。
 * <ThreadSafe>		このクラスはスレッドセーフです。すべてのメソッドは多重に呼び出されることを想定します。
 * <Static>				このクラスはインスタンスを生成することができません。
 *
 * @author 				100JPY
 * @version				1.0
 *
 */
public class UpLoaderDao {
	/**
	 * <SQL>				SQL文を定義します。
	 */
	private static final String INSERT = "INSERT INTO cm3d2_crawler_loader_m(name,url,termination,mod_amount,is_sync) VALUES(?,?,?,?,?)";
	private static final String SELECT_ALL = "SELECT * FROM cm3d2_crawler_loader_m";
	private static final String SELECT_BY_URL = "SELECT * FROM cm3d2_crawler_loader_m WHERE url = ?";
	private static final String SELECT_BY_SYNC = "SELECT * FROM cm3d2_crawler_loader_m WHERE is_sync = ?";
	private static final String UPDATE = "UPDATE cm3d2_crawler_loader_m SET termination=?, mod_amount=?,is_sync=? WHERE id=?";
	private static final String UPDATE_SYNC = "UPDATE cm3d2_crawler_loader_m SET is_sync = ? WHERE id = ?";

	/**
	 * <Method>			save
	 * <Description>	UpLoaderBeanをデータベースにINSERTします。
	 * 							このメソッドに渡されるオブジェクトが、リレーション側の制約に則っていることを想定します。
	 * 							IDはURLのCRC32値が自動で算出されます。
	 * <Static>			このクラスは静的メンバです。
	 *
	 * @param 			upLoader
	 * @throws 			IllegalInterruptedException
	 * @throws 			TimeoutException
	 * @throws 			SQLException
	 */
	public static void save(UpLoaderBean upLoader) throws IllegalInterruptedException, TimeoutException, SQLException {
		Connection conn = ConnectionManager.getInstance().getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(INSERT);

			ps.setString(1, upLoader.getName());
			ps.setString(2, upLoader.getUrl());
			ps.setInt(3, upLoader.getTermination());
			ps.setInt(4, upLoader.getModAmount());
			ps.setBoolean(5, upLoader.isSync());

			ps.executeUpdate();
			ps.close();
			CM3D2CrawlLogger.getInstance().info(upLoader.getName() + "がデータベースに挿入されました。 ID:" + upLoader.getId());

		} finally {
			ConnectionManager.getInstance().returnConnection(conn);
		}
	}

	/**
	 * <Method> 		updateOrSave
	 * <Description>	UpLoaderBeanをデータベースにINSERTします。
	 * 							重複したUpLoaderBeanが存在する場合、UPDATEします。
	 * 							IDはURLのCRC32値が自動で算出されます。
	 * <Static>			このメソッドは静的メンバです。
	 * @param 			upLoader
	 * @throws 			SQLException
	 * @throws 			IllegalInterruptedException
	 * @throws 			TimeoutException
	 */
	public static void updateOrSave(UpLoaderBean upLoader) throws SQLException, IllegalInterruptedException, TimeoutException {
		Connection conn = ConnectionManager.getInstance().getConnection();

		try {
			PreparedStatement ps = conn.prepareStatement(SELECT_BY_URL);
			ps.setString(1, upLoader.getUrl());
			ResultSet rs = ps.executeQuery();

			if(rs.next()) {
				PreparedStatement pst = conn.prepareStatement(UPDATE);
				pst.setInt(1, upLoader.getTermination());
				pst.setInt(2, upLoader.getModAmount());
				pst.setBoolean(3, upLoader.isSync());
				pst.setInt(4, rs.getInt(1));

				pst.executeUpdate();
				pst.close();
				CM3D2CrawlLogger.getInstance().info(upLoader.getName() + "が更新されました。 ID:" + upLoader.getId());

			} else {
				PreparedStatement pst = conn.prepareStatement(INSERT);

				pst.setString(1, upLoader.getName());
				pst.setString(2, upLoader.getUrl());
				pst.setInt(3, upLoader.getTermination());
				pst.setInt(4, upLoader.getModAmount());
				pst.setBoolean(5, upLoader.isSync());

				pst.executeUpdate();
				pst.close();
				CM3D2CrawlLogger.getInstance().info(upLoader.getName() + "がデータベースに挿入されました。 ID:" + upLoader.getId());
			}
			ps.close();

		} finally {
			ConnectionManager.getInstance().returnConnection(conn);
		}
	}

	/**
	 * <Method>			updateSync
	 * <Description>	うｐろだの同期状態を更新します。
	 * <Static>			このメソッドは静的メンバです。
	 *
	 * @param 			id
	 * @param 			isSync
	 * @throws 			SQLException
	 * @throws 			IllegalInterruptedException
	 * @throws 			TimeoutException
	 */
	public static void updateSync(int id, boolean isSync) throws SQLException, IllegalInterruptedException, TimeoutException {
		Connection conn = ConnectionManager.getInstance().getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(UPDATE_SYNC);
			st.setBoolean(1, isSync);
			st.setInt(2, id);

			st.executeUpdate();
			st.close();
			CM3D2CrawlLogger.getInstance().info("同期状態が更新されました。 ID:" + id);

		} finally {
			ConnectionManager.getInstance().returnConnection(conn);
		}

	}

	/**
	 * <Method>			load
	 * <Description>	データベースから全てのUpLoaderの列を取得します。
	 * <Static>			このメソッドは静的メンバです。
	 *
	 * @param			resultList  この参照するリストにロードされます。
	 * @throws 			IllegalInterruptedException
	 * @throws 			TimeoutException
	 * @throws 			SQLException
	 */
	public static void load(LinkedList<UpLoaderBean> resultList) throws IllegalInterruptedException, TimeoutException, SQLException {
		Connection conn = ConnectionManager.getInstance().getConnection();

		try {
			PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
			ResultSet rs = ps.executeQuery();

			while(rs.next()) {
				UpLoaderBean ul = new UpLoaderBean();
				ul.setId(rs.getInt(1));
				ul.setName(rs.getString(2));
				ul.setUrl(rs.getString(3));
				ul.setTermination(rs.getInt(4));
				ul.setModAmount(rs.getInt(5));
				ul.setSync(rs.getBoolean(6));
				resultList.add(ul);
			}
			CM3D2CrawlLogger.getInstance().debug("UpLoaderListを作成しました。");

			ps.close();

		} finally {
			ConnectionManager.getInstance().returnConnection(conn);
		}

	}

	/**
	 * <Method>			loadByUrl
	 * <Description>	データベースからURLに一致するうｐろだを検索します。
	 * 	<Static>			このメソッドは静的メンバです。
	 *
	 * @param 			url
	 * @return			URL検索にヒットしたUpLoaderBeanを返します。ヒットしなかった場合、NULLを返します。
	 * @throws 			IllegalInterruptedException
	 * @throws 			TimeoutException
	 * @throws 			SQLException
	 */
	public static UpLoaderBean loadByUrl(String url) throws IllegalInterruptedException, TimeoutException, SQLException {
		Connection conn = ConnectionManager.getInstance().getConnection();
		UpLoaderBean ul = null;

		try {
			PreparedStatement ps = conn.prepareStatement(SELECT_BY_URL);
			ps.setString(1, url);
			ResultSet rs = ps.executeQuery();

			if(rs.first()) {
				ul = new UpLoaderBean();
				ul.setId(rs.getInt(1));
				ul.setName(rs.getString(2));
				ul.setUrl(rs.getString(3));
				ul.setTermination(rs.getInt(4));
				ul.setModAmount(rs.getInt(5));
				ul.setSync(rs.getBoolean(6));

				CM3D2CrawlLogger.getInstance().debug("うｐろだの検索を行いました。結果を1件取得しました。 URL:" + url);

			} else {
				CM3D2CrawlLogger.getInstance().debug("うｐろだの検索を行いましたが、該当の列は見つかりませんでした。URL:" + url);
			}

			ps.close();

		} finally {
			ConnectionManager.getInstance().returnConnection(conn);
		}

		return ul;
	}

	/**
	 * <Method>			loadBySync
	 * <Description>	同期状態からうｐろだの情報を検索します。
	 * <Static>			このメソッドは静的メンバです。
	 *
	 * @param 			isSync
	 * @param			resultList  この参照するリストにロードされます。
	 * @throws 			IllegalInterruptedException
	 * @throws 			TimeoutException
	 * @throws 			SQLException
	 */
	public static void loadBySync(Boolean isSync, LinkedList<UpLoaderBean> resultList) throws IllegalInterruptedException, TimeoutException, SQLException {
		Connection conn = ConnectionManager.getInstance().getConnection();

		try {
			PreparedStatement ps = conn.prepareStatement(SELECT_BY_SYNC);
			ps.setBoolean(1, isSync);
			ResultSet rs = ps.executeQuery();

			while(rs.next()) {
				UpLoaderBean ul = new UpLoaderBean();
				ul.setId(rs.getInt(1));
				ul.setName(rs.getString(2));
				ul.setUrl(rs.getString(3));
				ul.setTermination(rs.getInt(4));
				ul.setModAmount(rs.getInt(5));
				ul.setSync(rs.getBoolean(6));
				resultList.add(ul);
			}
			CM3D2CrawlLogger.getInstance().debug("非同期のUpLoaderListを作成しました。");

			ps.close();

		} finally {
			ConnectionManager.getInstance().returnConnection(conn);
		}
	}
}