package cc.hundred_jpy.cm3d2crawler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

import cc.hundred_jpy.cm3d2crawler.entitybean.TaskBean;
import cc.hundred_jpy.cm3d2crawler.exceptions.IllegalInterruptedException;
import cc.hundred_jpy.cm3d2crawler.exceptions.TimeoutException;
import cc.hundred_jpy.cm3d2crawler.utilities.CM3D2CrawlLogger;

/**
 * <Class> 				TaskDao
 * <Description> 		このクラスはデータベースとの仲介を行います。
 * <ThreadSafe>		このクラスはスレッドセーフです。すべてのメソッドは多重に呼び出されることを想定します。
 * <Static>				このクラスのインスタンスを生成することはできません。
 *
 * @author 				100JPY
 * @version				1.1
 *
 */
public class TaskDao {

	/**
	 * <SQL>				SQL文を定義します。
	 */
	private static final String INSERT = "INSERT INTO cm3d2_crawler_task_m(task_type, message, run_time) VALUES(?,?,?)";
	private static final String DELETE = "DELETE FROM cm3d2_crawler_task_m WHERE id = ?";
	private static final String SELECT_ALL = "SELECT * FROM cm3d2_crawler_task_m";
	private static final String SELECT_TIME = "SELECT DISTINCT run_time FROM cm3d2_crawler_task_m";
	private static final String SELECT_BY_TIME = "SELECT * FROM cm3d2_crawler_task_m WHERE run_time = ?";

	/**
	 * <Method> 		Constractor
	 * <Description>	明示的にインスタンス化を禁止します。
	 */
	private TaskDao() {
	}

	/**
	 * <Method> 		save
	 * <Description>	TaskBeanをデータベースにINSERTします。
	 * 							このメソッドに渡されるオブジェクトが、リレーション側の制約に則っていることを想定します。
	 * <Static>			このメソッドは静的メンバです。
	 *
	 * @param 			taskBean
	 * @throws			IllegalInterruptedException
	 * @throws 			SQLException
	 * @throws 			TimeoutException
	 */
	public static void save(TaskBean taskBean) throws IllegalInterruptedException, SQLException, TimeoutException {
		Connection conn = ConnectionManager.getInstance().getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(INSERT);

			ps.setString(1, taskBean.getTaskType());
			ps.setString(2, taskBean.getMessage());
			ps.setTime(3, Time.valueOf(taskBean.getRunTime()));

			ps.executeUpdate();
			ps.close();
			CM3D2CrawlLogger.getInstance().info("TaskをDBに挿入しました。ID:" + taskBean.getId());

		} finally {
			ConnectionManager.getInstance().returnConnection(conn);
		}

	}

	/**
	 * <Method>			remove
	 * <Description>	データベースからタスクを削除します。
	 * <Static>			このメソッドは静的メンバです。
	 *
	 * @param id   削除するタスクのIDを入力します。
	 * @throws SQLException
	 * @throws IllegalInterruptedException
	 * @throws TimeoutException
	 */
	public static void remove(int id) throws SQLException, IllegalInterruptedException, TimeoutException {
		Connection conn = ConnectionManager.getInstance().getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(DELETE);
			ps.setInt(1, id);

			ps.executeUpdate();
			ps.close();
			CM3D2CrawlLogger.getInstance().info("DBからタスクを削除しました。ID:" + id);

		} finally {
			ConnectionManager.getInstance().returnConnection(conn);
		}

	}

	/**
	 *<Method> 			load
	 *<Description>	データベースからTaskのすべての列を取得します。
	 *<Static>			このメソッドは静的メンバです。
	 *
	 * @return 			全タスクのArrayListを返します。
	 * @throws 			IllegalInterruptedException
	 * @throws 			SQLException
	 * @throws 			TimeoutException
	 */
	public static void load(List<TaskBean> taskList) throws IllegalInterruptedException, SQLException, TimeoutException {

		Connection conn = ConnectionManager.getInstance().getConnection();

		try {
		PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
		ResultSet rs = ps.executeQuery();

		while(rs.next()) {
			TaskBean tb = new TaskBean();
			tb.setId(rs.getInt(1));
			tb.setTaskType(rs.getString(2));
			tb.setMessage(rs.getString(3));
			tb.setRunTime(rs.getTime(4).toLocalTime());
			taskList.add(tb);
		}

		CM3D2CrawlLogger.getInstance().debug("TaskListが生成されました。");

		ps.close();

		} finally {
			ConnectionManager.getInstance().returnConnection(conn);
		}

	}

	/**
	 *
	 * <Method> 		loadTime
	 *<Description>	データベースからTaskのrun_time列を取得します。
	 *							重複列は1つにまとめられます。
	 *<Static>			このメソッドは静的メンバです。
	 *
	 * @param 			timeList
	 * @throws 			SQLException
	 * @throws 			IllegalInterruptedException
	 * @throws 			TimeoutException
	 */
	public static void loadTime(List<LocalTime> timeList) throws SQLException, IllegalInterruptedException, TimeoutException {

		Connection conn = ConnectionManager.getInstance().getConnection();

		try {
		PreparedStatement ps = conn.prepareStatement(SELECT_TIME);
		ResultSet rs = ps.executeQuery();

		while(rs.next()) {
			timeList.add(rs.getTime(1).toLocalTime());
		}

		CM3D2CrawlLogger.getInstance().debug("timeListが生成されました。");

		ps.close();

		} finally {
			ConnectionManager.getInstance().returnConnection(conn);
		}
	}

	/**
	 * <Method>			loadByTime
	 * <Description>	指定のrun_time値を持つ列を取得します。
	 * <Static>			このメソッドは静的メンバです。
	 *
	 * @param			time
	 * @param 			taskList
	 * @throws 			SQLException
	 * @throws 			IllegalInterruptedException
	 * @throws 			TimeoutException
	 */
	public static void loadByTime(LocalTime time, List<TaskBean> taskList) throws SQLException, IllegalInterruptedException, TimeoutException {

		Connection conn = ConnectionManager.getInstance().getConnection();

		try {
		PreparedStatement ps = conn.prepareStatement(SELECT_BY_TIME);
		ps.setTime(1, Time.valueOf(time));
		ResultSet rs = ps.executeQuery();

		while(rs.next()) {
			TaskBean tb = new TaskBean();
			tb.setId(rs.getInt(1));
			tb.setTaskType(rs.getString(2));
			tb.setMessage(rs.getString(3));
			tb.setRunTime(rs.getTime(4).toLocalTime());
			taskList.add(tb);
		}

		CM3D2CrawlLogger.getInstance().debug("timeListが生成されました。");

		ps.close();

		} finally {
			ConnectionManager.getInstance().returnConnection(conn);
		}
	}

}
