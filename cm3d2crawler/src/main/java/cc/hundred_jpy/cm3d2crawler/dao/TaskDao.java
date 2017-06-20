package cc.hundred_jpy.cm3d2crawler.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;

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
 * @version				1.0
 *
 */
public class TaskDao {

	/**
	 * <SQL>				SQL文を定義します。
	 */
	private static final String INSERT = "INSERT INTO cm3d2_crawler_task_m VALUES(?,?,?,?)";
	private static final String SELECT_ALL = "SELECT * FROM cm3d2_crawler_task_m";

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

			ps.setString(1, taskBean.getId());
			ps.setString(2, taskBean.getTaskType());
			ps.setString(3, taskBean.getMessage());
			ps.setTime(4, Time.valueOf(taskBean.getRunTime()));

			ps.executeUpdate();
			ps.close();
			CM3D2CrawlLogger.getInstance().info("TaskをDBに挿入しました。ID:" + taskBean.getId());

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
	public static ArrayList<TaskBean> load() throws IllegalInterruptedException, SQLException, TimeoutException {

		Connection conn = ConnectionManager.getInstance().getConnection();
		ArrayList<TaskBean> resultList = new ArrayList<TaskBean>();

		try {
		PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
		ResultSet rs = ps.executeQuery();

		while(rs.next()) {
			TaskBean tb = new TaskBean();
			tb.setId(rs.getString(1));
			tb.setTaskType(rs.getString(2));
			tb.setMessage(rs.getString(3));
			tb.setRunTime(rs.getTime(4).toLocalTime());
			resultList.add(tb);
		}

		CM3D2CrawlLogger.getInstance().debug("TaskListが生成されました。");

		ps.close();

		} finally {
			ConnectionManager.getInstance().returnConnection(conn);
		}

		return resultList;

	}

}
