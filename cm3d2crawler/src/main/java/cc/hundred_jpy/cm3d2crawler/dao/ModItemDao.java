package cc.hundred_jpy.cm3d2crawler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;

import cc.hundred_jpy.cm3d2crawler.entitybean.ModItemBean;
import cc.hundred_jpy.cm3d2crawler.exceptions.IllegalInterruptedException;
import cc.hundred_jpy.cm3d2crawler.exceptions.TimeoutException;
import cc.hundred_jpy.cm3d2crawler.utilities.CM3D2CrawlLogger;

/**
 * <Class>				ModItemDao
 * <Description>		このクラスはデータベースとの仲介を行います。
 * <ThreadSafe>		このクラスはスレッドセーフです。すべてのメソッドは多重に呼び出されることを想定します。
 * <Static>				このクラスはインスタンスを生成することができません。
 *
 * @author 				100JPY
 * @version				1.0
 *
 */
public class ModItemDao {

	/**
	 * <SQL>				SQL文を定義します。
	 */
	private static final String INSERT = "INSERT INTO cm3d2_crawler_mod_m"
					+ "(comment,orig_file_name,url,file_name,file_size,nominal_size,upload_date,is_download,master_url) "
					+ "VALUES(?,?,?,?,?,?,?,?,?)";
	private static final String UPDATE = "UPDATE cm3d2_crawler_mod_m SET is_download = ?, file_size = ? WHERE id = ?";
	private static final String SELECT_ALL = "SELECT * FROM cm3d2_crawler_mod_m";
	private static final String SELECT_ALL_WITH_LIMIT = "SELECT * FROM cm3d2_crawler_mod_m LIMIT ?";
	private static final String SELECT_BY_URL = "SELECT * FROM cm3d2_crawler_mod_m WHERE url = ?";

	/**
	 * <Metod>			Constructor
	 * <Description>	明示的にインスタンス化を禁止します。
	 */
	private ModItemDao() {
	}

	/**
	 * <Method>			save
	 * <Description>	ModItemをデータベースにINSERTします。
	 * 							このメソッドに渡されるオブジェクトが、リレーション側の制約に則っていることを想定します。
	 * 							ModItemがすでに登録されている場合、処理をスキップします。
	 * <Static>			このメソッドは静的メンバです。
	 *
	 * @param 			modItem
	 * @throws 			IllegalInterruptedException
	 * @throws 			SQLException
	 * @throws 			TimeoutException
	 */
	public static void save(ModItemBean modItem) throws IllegalInterruptedException, SQLException, TimeoutException {
		Connection conn = ConnectionManager.getInstance().getConnection();

		PreparedStatement ps = conn.prepareStatement(SELECT_BY_URL);
		ps.setString(1, modItem.getUrl());
		ResultSet rs = ps.executeQuery();

		if(rs.next()) {
			ps.close();
			ConnectionManager.getInstance().returnConnection(conn);
			CM3D2CrawlLogger.getInstance().debug(modItem.getFileName() + "の追加処理が発生しましたが、スキップされました。");
			return;
		}

		try {
			ps = conn.prepareStatement(INSERT);

			ps.setString(1, modItem.getComment());
			ps.setString(2, modItem.getOrigFileName());
			ps.setString(3, modItem.getUrl());
			ps.setString(4, modItem.getFileName());
			ps.setLong(5, modItem.getFileSize());
			ps.setString(6, modItem.getNominalSize());
			ps.setTimestamp(7, Timestamp.valueOf(modItem.getUploadDate()));
			ps.setBoolean(8, modItem.isDownload());
			ps.setInt(9, modItem.getMasterUrl());

			ps.executeUpdate();
			CM3D2CrawlLogger.getInstance().info(modItem.getFileName() + "がデータベースに追加されました。ID:");

		} finally {
			if(ps != null) {
				ps.close();
			}
			ConnectionManager.getInstance().returnConnection(conn);
		}

	}

	/**
	 * <Method>			update
	 * <Description>	データベースのMODを更新します。
	 * 							ModItemではis_downloadとfile_size以外のカラムは更新されません。
	 * <Static>			このメソッドは静的メンバです。
	 *
	 * @param 			modItem
	 * @throws 			IllegalInterruptedException
	 * @throws 			TimeoutException
	 * @throws 			SQLException
	 */
	public static void update(ModItemBean modItem) throws IllegalInterruptedException, TimeoutException, SQLException {
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement ps = conn.prepareStatement(UPDATE);

		try {
			ps.setBoolean(1, modItem.isDownload());
			ps.setLong(2, modItem.getFileSize());
			ps.setInt(3, modItem.getId());

			ps.executeUpdate();
			CM3D2CrawlLogger.getInstance().info(modItem.getFileName() + "が更新されました。");

		} finally {
			if(ps != null) {
				ps.close();
			}
			ConnectionManager.getInstance().returnConnection(conn);
		}

	}

	/**
	 *	<Method>			load
	 * <Description>	データベースからModの全ての列を取得します。
	 * <Static>			このメソッドは静的メンバです。
	 *
	 *	@param			resultList  この参照するリストにロードされます。
	 * @throws 			IllegalInterruptedException
	 * @throws 			SQLException
	 * @throws 			TimeoutException
	 */
	public static void load(LinkedList<ModItemBean> resultList) throws IllegalInterruptedException, SQLException, TimeoutException {
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement ps = conn.prepareStatement(SELECT_ALL);

		try {
			ResultSet rs = ps.executeQuery();

			while(rs.next()) {
				ModItemBean mi = new ModItemBean();
				mi.setId(rs.getInt(1));
				mi.setComment(rs.getString(2));
				mi.setOrigFileName(rs.getString(3));
				mi.setUrl(rs.getString(4));
				mi.setFileName(rs.getString(5));
				mi.setFileSize(rs.getLong(6));
				mi.setNominalSize(rs.getString(7));
				mi.setUploadDate(rs.getTimestamp(8).toLocalDateTime());
				mi.setDownload(rs.getBoolean(9));
				mi.setMasterUrl(rs.getInt(10));

				resultList.add(mi);

			}
			CM3D2CrawlLogger.getInstance().debug("ModItemListが生成されました。");


		} finally {
			if(ps != null) {
				ps.close();
			}
			ConnectionManager.getInstance().returnConnection(conn);
		}
	}


	/**
	 *	<Method>			load
	 * <Description>	データベースからModの列を取得します。
	 * 							limitに指定した行数を取得します。
	 * <Static>			このメソッドは静的メンバです。
	 *
	 *	@param			resultList  この参照するリストにロードされます。
	 * @param			limit	ロードする上限数を指定します。
	 * @throws 			IllegalInterruptedException
	 * @throws 			SQLException
	 * @throws 			TimeoutException
	 */
	public static void load(LinkedList<ModItemBean> resultList, int limit) throws IllegalInterruptedException, SQLException, TimeoutException {
		Connection conn = ConnectionManager.getInstance().getConnection();
		PreparedStatement ps = conn.prepareStatement(SELECT_ALL_WITH_LIMIT);

		try {
			ps.setInt(1, limit);

			ResultSet rs = ps.executeQuery();

			while(rs.next()) {
				ModItemBean mi = new ModItemBean();
				mi.setId(rs.getInt(1));
				mi.setComment(rs.getString(2));
				mi.setOrigFileName(rs.getString(3));
				mi.setUrl(rs.getString(4));
				mi.setFileName(rs.getString(5));
				mi.setFileSize(rs.getLong(6));
				mi.setNominalSize(rs.getString(7));
				mi.setUploadDate(rs.getTimestamp(8).toLocalDateTime());
				mi.setDownload(rs.getBoolean(9));
				mi.setMasterUrl(rs.getInt(10));

				resultList.add(mi);

			}
			CM3D2CrawlLogger.getInstance().debug("ModItemListが生成されました。");


		} finally {
			if(ps != null) {
				ps.close();
			}
			ConnectionManager.getInstance().returnConnection(conn);
		}
	}
}
