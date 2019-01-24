package com.gupaoedu.vip.jdbc.framework;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.core.common.Page;
import javax.core.common.utils.BeanUtils;
import javax.core.common.utils.DataUtils;
import javax.core.common.utils.GenericsUtils;
import javax.core.common.utils.StringUtils;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.TypeUtils;

/**
 * BaseDao 扩展类,主要功能是支持自动拼装sql语句，必须继承方可使用
 * 需要重写和实现以下三个方法
 *  //设定主键列
 *	private String getPKColumn() {return "id";}
 *	//重写对象反转为Map的方法  
 *	protected Map<String, Object> parse(Object entity) {return utils.parse((Entity)entity);}
 *	//重写结果反转为对象的方法
 *	protected Entity mapRow(ResultSet rs, int rowNum) throws SQLException {return utils.parse(rs);}
 *
 *
 * @author Tom
 */
public abstract class BaseDaoSupport<T extends Serializable, PK extends Serializable>{
	private Logger log = Logger.getLogger(BaseDaoSupport.class);
	
	private String tableName = "";

	private SimpleJdbcTemplate jdbcTemplateWrite;
	private SimpleJdbcTemplate jdbcTemplateReadOnly;

	private DataSource dataSourceReadOnly;
	private DataSource dataSourceWrite;
	
	private EntityOperation<T> op;
	
	@SuppressWarnings("unchecked")
	protected BaseDaoSupport(){
		try{
	//		Class<T> entityClass = (Class<T>)((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
			Class<T> entityClass = GenericsUtils.getSuperClassGenricType(getClass(), 0);
			op = new EntityOperation<T>(entityClass,this.getPKColumn());
			this.setTableName(op.tableName);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected String getTableName() {
		return tableName;
	}

	protected DataSource getDataSourceReadOnly() {
		return dataSourceReadOnly;
	}

	protected DataSource getDataSourceWrite() {
		return dataSourceWrite;
	}

	/**
	 * 动态切换表名
	 */
	protected void setTableName(String tableName) {
		if(StringUtils.isEmpty(tableName)){
			this.tableName = op.tableName;
		}else{
			this.tableName = tableName;
		}
	}

	protected void setDataSourceWrite(DataSource dataSourceWrite) {
		this.dataSourceWrite = dataSourceWrite;
		jdbcTemplateWrite = new SimpleJdbcTemplate(dataSourceWrite);
	}

	protected void setDataSourceReadOnly(DataSource dataSourceReadOnly) {
		this.dataSourceReadOnly = dataSourceReadOnly;
		jdbcTemplateReadOnly = new SimpleJdbcTemplate(dataSourceReadOnly);
	}

	private SimpleJdbcTemplate jdbcTemplateReadOnly() {
		return this.jdbcTemplateReadOnly;
	}

	private SimpleJdbcTemplate jdbcTemplateWrite() {
		return this.jdbcTemplateWrite;
	}

	
	/**
	 * 还原默认表名
	 */
	protected void restoreTableName(){
		this.setTableName(op.tableName);
	}
	
	/**
	 * 将对象解析为Map
	 * @param entity
	 * @return
	 */
	protected Map<String,Object> parse(T entity){
		return op.parse(entity);
	}
	
	
	
	/**
	 * 根据ID获取对象. 如果对象不存在，返回null.<br>
	 */
	protected T get(PK id) throws Exception {
		return (T) this.doLoad(id, this.op.rowMapper);
	}

	/**
	 * 获取全部对象. <br>
	 * 
	 * @return 全部对象
	 */
	protected List<T> getAll() throws Exception {
		String sql = "select " + op.allColumn + " from " + getTableName();
		return this.jdbcTemplateReadOnly().query(sql, this.op.rowMapper, new HashMap<String, Object>());
	}
	
	/**
	 * 插入并返回id
	 * @param entity
	 * @return
	 */
	protected PK insertAndReturnId(T entity) throws Exception{
		return (PK)this.doInsertRuturnKey(parse(entity));
	}
	
	/**
	 * 插入一条记录
	 * @param entity
	 * @return
	 */
	protected boolean insertOne(T entity) throws Exception{
		return this.doInsert(parse(entity));
	}
	

	/**
	 * 保存对象,如果对象存在则更新,否则插入.<br>
	 * </code>
	 * </pre>
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	protected boolean saveOne(T entity) throws Exception {
		PK pkValue = (PK)op.pkField.get(entity);
		if(this.exists(pkValue)){
			return this.doUpdate(pkValue, parse(entity)) > 0;
		}else{
			return this.doInsert(parse(entity));
		}
	}

	/**
	 * 保存并返回新的id,如果对象存在则更新,否则插入
	 * @param entity
	 * @return
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	protected PK saveAndReturnId(T entity) throws Exception{
		Object o = op.pkField.get(entity);
		if(null == o){
			return (PK)this.doInsertRuturnKey(parse(entity));
			//return (PK)id;
		}
		PK pkValue = (PK)o;
		if(this.exists(pkValue)){
			this.doUpdate(pkValue, parse(entity));
			return pkValue;
		}else{
			return (PK)this.doInsertRuturnKey(parse(entity));
		}
	}
	
	/**
	 * 更新对象.<br>
	 * 例如：以下代码将对象更新到数据库
	 * <pre>
	 * 		<code>
	 * User entity = service.get(1);
	 * entity.setName(&quot;zzz&quot;);
	 * // 更新对象
	 * service.update(entity);
	 * </code>
	 * </pre>
	 * 
	 * @param obj 待更新对对象
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	protected int update(T entity) throws Exception {
		return this.doUpdate(op.pkField.get(entity), parse(entity));
	}

	/**
	 * 使用SQL语句更新对象.<br>
	 * 例如：以下代码将更新id="0002"的name值更新为“张三”到数据库
	 * <pre>
	 * 		<code>
	 * String name = "张三";
	 * String id = "0002";
	 * String sql = "UPDATE SET name = ? WHERE id = ?";
	 * // 更新对象
	 * service.update(sql,name,id)
	 * </code>
	 * </pre>
	 * 
	 * @param sql 更新sql语句
	 * @param args 参数对象
	 * 
	 * @return 更新记录数
	 */
	protected int update(String sql,Object... args) throws Exception{
		return jdbcTemplateWrite().update(sql, args);
	}
	
	/**
	 * 使用SQL语句更新对象.<br>
	 * 例如：以下代码将更新id="0002"的name值更新为“张三”到数据库
	 * <pre>
	 * 		<code>
	 * Map<String,Object> map = new HashMap();
	 * map.put("name","张三");
	 * map.put("id","0002");
	 * String sql = "UPDATE SET name = :name WHERE id = :id";
	 * // 更新对象
	 * service.update(sql,map)
	 * </code>
	 * </pre>
	 * 
	 * @param sql 更新sql语句
	 * @param args 参数对象
	 * 
	 * @return 更新记录数
	 */
	protected int update(String sql,Map<String,?> paramMap) throws Exception{
		return jdbcTemplateWrite().update(sql, paramMap);
	}
	/**
	 * 批量保存对象.<br>
	 * 例如：以下代码将对象保存到数据库
	 * <pre>
	 * 		<code>
	 * List&lt;Role&gt; list = new ArrayList&lt;Role&gt;();
	 * for (int i = 1; i &lt; 8; i++) {
	 * 	Role role = new Role();
	 * 	role.setId(i);
	 * 	role.setRolename(&quot;管理quot; + i);
	 * 	role.setPrivilegesFlag(&quot;1,2,3&quot;);
	 * 	list.add(role);
	 * }
	 * service.saveAll(list);
	 * </code>
	 * </pre>
	 * 
	 * @param list 待保存的对象List
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	protected int saveAll(List<T> list) throws Exception {
		int count = 0 ,len = list.size(),step = 50000;
		Map<String, PropertyMapping> pm = op.mappings;
		int maxPage = (len % step == 0) ? (len / step) : (len / step + 1);
		for (int i = 1; i <= maxPage; i ++) {
			Page<T> page = pagination(list, i, step);
			String sql = "insert into " + getTableName() + "(" + op.allColumn + ") values ";// (" + valstr.toString() + ")";
			StringBuffer valstr = new StringBuffer();
			Object[] values = new Object[pm.size() * page.getRows().size()];
			for (int j = 0; j < page.getRows().size(); j ++) {
				if(j > 0 && j < page.getRows().size()){ valstr.append(","); }
				valstr.append("(");
				int k = 0;
				for (PropertyMapping p : pm.values()) {
					values[(j * pm.size()) + k] = p.getter.invoke(page.getRows().get(j));
					if(k > 0 && k < pm.size()){ valstr.append(","); }
					valstr.append("?");
					k ++;
				}
				valstr.append(")");
			}
			int result = jdbcTemplateWrite().update(sql + valstr.toString(), values);
			count += result;
		}
			
		return count;
	}
	
	
	protected boolean replaceOne(T entity) throws Exception{
		return this.doReplace(parse(entity));
	}
	
	
	protected int replaceAll(List<T> list) throws Exception {
		int count = 0 ,len = list.size(),step = 50000;
		Map<String, PropertyMapping> pm = op.mappings;
		int maxPage = (len % step == 0) ? (len / step) : (len / step + 1);
		for (int i = 1; i <= maxPage; i ++) {
			Page<T> page = pagination(list, i, step);
			String sql = "replace into " + getTableName() + "(" + op.allColumn + ") values ";// (" + valstr.toString() + ")";
			StringBuffer valstr = new StringBuffer();
			Object[] values = new Object[pm.size() * page.getRows().size()];
			for (int j = 0; j < page.getRows().size(); j ++) {
				if(j > 0 && j < page.getRows().size()){ valstr.append(","); }
				valstr.append("(");
				int k = 0;
				for (PropertyMapping p : pm.values()) {
					values[(j * pm.size()) + k] = p.getter.invoke(page.getRows().get(j));
					if(k > 0 && k < pm.size()){ valstr.append(","); }
					valstr.append("?");
					k ++;
				}
				valstr.append(")");
			}
			int result = jdbcTemplateWrite().update(sql + valstr.toString(), values);
			count += result;
		}
		return count;
	}
	

	/**
	 * 删除对象.<br>
	 * 例如：以下删除entity对应的记录
	 * <pre>
	 * 		<code>
	 * service.remove(entity);
	 * </code>
	 * </pre>
	 * 
	 * @param obj 待删除的实体对象
	 */
	protected int delete(Object entity) throws Exception {
		return this.doDelete(op.pkField.get(entity));
	}

	/**
	 * 删除对象.<br>
	 * 例如：以下删除entity对应的记录
	 * <pre>
	 * 		<code>
	 * service.remove(entityList);
	 * </code>
	 * </pre>
	 * 
	 * @param entityList 待删除的实体对象列表
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	protected int deleteAll(List<T> list) throws Exception {
		String pkName = op.pkField.getName();
		int count = 0 ,len = list.size(),step = 1000;
		Map<String, PropertyMapping> pm = op.mappings;
		int maxPage = (len % step == 0) ? (len / step) : (len / step + 1);
		for (int i = 1; i <= maxPage; i ++) {
			StringBuffer valstr = new StringBuffer();
			Page<T> page = pagination(list, i, step);
			Object[] values = new Object[page.getRows().size()];
			
			for (int j = 0; j < page.getRows().size(); j ++) {
				if(j > 0 && j < page.getRows().size()){ valstr.append(","); }
				values[j] = pm.get(pkName).getter.invoke(page.getRows().get(j));
				valstr.append("?");
			}
			
			String sql = "delete from " + getTableName() + " where " + pkName + " in (" + valstr.toString() + ")";
			int result = jdbcTemplateWrite().update(sql, values);
			count += result;
		}
		return count;
	}

	/**
	 * 根据ID删除对象.如果有记录则删之，没有记录也不报异常<br>
	 * 例如：以下删除主键唯一的记录
	 * <pre>
	 * 		<code>
	 * service.removeByPK(1);
	 * </code>
	 * </pre>
	 * 
	 * @param id 序列化对id
	 */
	protected void deleteByPK(PK id)  throws Exception {
		this.doDelete(id);
	}
	
	/**
	 * 根据ID删除对象.如果有记录则删之，没有记录也不报异常<br>
	 * 例如：以下删除主键唯一的记录
	 * <pre>
	 * 		<code>
	 * service.remove(1);
	 * </code>
	 * </pre>
	 * 
	 * @param id 序列化对id
	 * 
	 * @return 删除是否成功
	 */
	protected boolean delete(PK id)  throws Exception {
		return this.doDelete(id) > 0;
	}

	/**
	 * 根据属性名查询出内容等于属性值的唯一对象，没符合条件的记录返回null.<br>
	 * 例如，如下语句查找id=5的唯一记录：
	 * 
	 * <pre>
	 *     <code>
	 * User user = service.findUnique(User.class, &quot;id&quot;, 5);
	 * </code>
	 * </pre>
	 * 
	 * @param propertyName 属性名
	 * @param value 属性值
	 * @return 符合条件的唯一对象 or null if not found.
	 */
	protected T findUnique(String propertyName,Object value) throws Exception {
		QueryRule queryRule = QueryRule.getInstance();
		queryRule.andEqual(propertyName, value);
		return this.findUnique(queryRule);
	}

	/**
	 * 根据主键判断对象是否存在. 例如：以下代码判断id=2的User记录是否存在
	 * 
	 * <pre>
	 * 		<code>
	 * boolean user2Exist = service.exists(User.class, 2);
	 * </code> 
	 * </pre> 
	 * @param id 序列化对象id	 
	 * @return 存在返回true，否则返回false
	 */
	protected boolean exists(PK id)  throws Exception {
		return null != this.doLoad(id, this.op.rowMapper);
	}

	/**
	 * 查询满足条件的记录数，使用hql.<br>
	 * 例如：查询User里满足条件?name like "%ca%" 的记录数
	 * 
	 * <pre>
	 * 		<code>
	 * long count = service.getCount(&quot;from User where name like ?&quot;, &quot;%ca%&quot;);
	 * </code>
	 * </pre>
	 * 
	 * @param sql SQL语句
	 * @param values 参数List
	 * @return 满足条件的记录数
	 */
	protected long getCount(QueryRule queryRule) throws Exception {
		QueryRuleSqlBulider bulider = new QueryRuleSqlBulider(queryRule);
		Object [] values = bulider.getValues();
		String ws = removeFirstAnd(bulider.getWhereSql());
		String whereSql = ("".equals(ws) ? ws : (" where " + ws));
		String countSql = "select count(1) from " + getTableName() + whereSql;
		return this.jdbcTemplateReadOnly().queryForLong(countSql, values);
	}

	/**
	 * 根据某个属性值倒序获得第一个最大值
	 * @param propertyName
	 * @return
	 */
	protected T getMax(String propertyName) throws Exception{
		QueryRule queryRule = QueryRule.getInstance();
		queryRule.addDescOrder(propertyName);
		Page<T> result = this.find(queryRule,1,1);
		if(null == result.getRows() || 0 == result.getRows().size()){
			return null;
		}else{
			return result.getRows().get(0);
		}
	}
	
	/**
	 * 查询函数，使用查询规
	 * 例如以下代码查询条件为匹配的数据
	 * 
	 * <pre>
	 *		<code>
	 * QueryRule queryRule = QueryRule.getInstance();
	 * queryRule.addLike(&quot;username&quot;, user.getUsername());
	 * queryRule.addLike(&quot;monicker&quot;, user.getMonicker());
	 * queryRule.addBetween(&quot;id&quot;, lowerId, upperId);
	 * queryRule.addDescOrder(&quot;id&quot;);
	 * queryRule.addAscOrder(&quot;username&quot;);
	 * list = userService.find(User.class, queryRule);
	 * </code> 
	 * </pre>
	 * 
	 * @param queryRule 查询规则
	 * @return 查询出的结果List
	 */
	protected List<T> find(QueryRule queryRule) throws Exception{
		QueryRuleSqlBulider bulider = new QueryRuleSqlBulider(queryRule);
		String ws = removeFirstAnd(bulider.getWhereSql());
		String whereSql = ("".equals(ws) ? ws : (" where " + ws));
		String sql = "select " + op.allColumn + " from " + getTableName() + whereSql;
		Object [] values = bulider.getValues();
		String orderSql = bulider.getOrderSql();
		orderSql = (StringUtils.isEmpty(orderSql) ? " " : (" order by " + orderSql));
		sql += orderSql;
		log.debug(sql);
		return (List<T>) this.jdbcTemplateReadOnly().query(sql, this.op.rowMapper, values);
	}
	
	/**
	 * 根据SQL语句执行查询，参数为Map
	 * @param sql语句
	 * @param pamam 为Map，key为属性名，value为属性值
	 * @return 符合条件的所有对象
	 */
	protected List<Map<String,Object>> findBySql(String sql,Map<String,?> pamam) throws Exception{
		return this.jdbcTemplateReadOnly().queryForList(sql,pamam);
	}
	
	/**
	 * 根据SQL语句查询符合条件的唯一对象，没符合条件的记录返回null.<br>
	 * @param sql语句
	 * @param pamam 为Map，key为属性名，value为属性值
	 * @return 符合条件的唯一对象，没符合条件的记录返回null.
	 */
	protected Map<String,Object> findUniqueBySql(String sql,Map<String,?> pamam) throws Exception{
		List<Map<String,Object>> list = findBySql(sql,pamam);
		if (list.size() == 0) {
			return null;
		} else if (list.size() == 1) {
			return list.get(0);
		} else {
			throw new IllegalStateException("findUnique return " + list.size() + " record(s).");
		}
	}
	
	/**
	 * 根据SQL语句执行查询，参数为Object数组对象
	 * @param sql查询语句
	 * @param args 为Object数组
	 * @return 符合条件的所有对象
	 */
	protected List<Map<String,Object>> findBySql(String sql,Object... args) throws Exception{
		return this.jdbcTemplateReadOnly().queryForList(sql,args);
	}

	/**
	 * 根据SQL语句查询符合条件的唯一对象，没符合条件的记录返回null.<br>
	 * @param sql查询语句
	 * @param args 为Object数组
	 * @return 符合条件的唯一对象，没符合条件的记录返回null.
	 */
	protected Map<String,Object> findUniqueBySql(String sql,Object... args) throws Exception{
		List<Map<String,Object>> list = findBySql(sql, args);
		if (list.size() == 0) {
			return null;
		} else if (list.size() == 1) {
			return list.get(0);
		} else {
			throw new IllegalStateException("findUnique return " + list.size() + " record(s).");
		}
	}
	
	/**
	 * 根据SQL语句执行查询，参数为List对象
	 * @param sql查询语句
	 * @param list<Object>对象
	 * @return 符合条件的所有对象
	 */
	protected List<Map<String,Object>> findBySql(String sql,List<Object> list) throws Exception{
		return this.jdbcTemplateReadOnly().queryForList(sql,list.toArray());
	}
	
	/**
	 * 根据SQL语句查询符合条件的唯一对象，没符合条件的记录返回null.<br>
	 * @param sql查询语句
	 * @param listParam 属性值List
	 * @return 符合条件的唯一对象，没符合条件的记录返回null.
	 */
	protected Map<String,Object> findUniqueBySql(String sql,List<Object> listParam) throws Exception{
		List<Map<String,Object>> listMap = findBySql(sql, listParam);
		if (listMap.size() == 0) {
			return null;
		} else if (listMap.size() == 1) {
			return listMap.get(0);
		} else {
			throw new IllegalStateException("findUnique return " + listMap.size() + " record(s).");
		}
	}
	
	/**
	 * 分页查询函数，使用查询规则<br>
	 * 例如以下代码查询条件为匹配的数据
	 * 
	 * <pre>
	 *		<code>
	 * QueryRule queryRule = QueryRule.getInstance();
	 * queryRule.addLike(&quot;username&quot;, user.getUsername());
	 * queryRule.addLike(&quot;monicker&quot;, user.getMonicker());
	 * queryRule.addBetween(&quot;id&quot;, lowerId, upperId);
	 * queryRule.addDescOrder(&quot;id&quot;);
	 * queryRule.addAscOrder(&quot;username&quot;);
	 * page = userService.find(User.class, queryRule, pageNo, pageSize);
	 * </code> 
	 * </pre>
	 * 
	 * @param queryRule 查询规则
	 * @param pageNo 页号,从1开始
	 * @param pageSize  每页的记录条数
	 * @return 查询出的结果Page
	 */
	protected Page<T> find(QueryRule queryRule,final int pageNo, final int pageSize) throws Exception{
		QueryRuleSqlBulider bulider = new QueryRuleSqlBulider(queryRule);
		Object [] values = bulider.getValues();
		String ws = removeFirstAnd(bulider.getWhereSql());
		String whereSql = ("".equals(ws) ? ws : (" where " + ws));
		String countSql = "select count(1) from " + getTableName() + whereSql;
		long count = this.jdbcTemplateReadOnly().queryForLong(countSql, values);
		if (count == 0) {
			return new Page<T>();
		}
		long start = (pageNo - 1) * pageSize;
		// 有数据的情况下，继续查询
		String orderSql = bulider.getOrderSql();
		orderSql = (StringUtils.isEmpty(orderSql) ? " " : (" order by " + orderSql));
		String sql = "select " + op.allColumn +" from " + getTableName() + whereSql + orderSql + " limit " + start + "," + pageSize;
		List<T> list = (List<T>) this.jdbcTemplateReadOnly().query(sql, this.op.rowMapper, values);
		log.debug(sql);
		return new Page<T>(start, count, pageSize, list);
	}

	
	/**
	 * 分页查询特殊SQL语句
	 * @param sql 语句
	 * @param param  查询条件
	 * @param pageNo	页码
	 * @param pageSize	每页内容
	 * @return
	 */
	protected Page<Map<String,Object>> findBySqlToPage(String sql, Map<String,?> param, final int pageNo, final int pageSize) throws Exception {
		String countSql = "select count(1) from (" + sql + ") a";
		long count = this.jdbcTemplateReadOnly().queryForLong(countSql, param);
		if (count == 0) {
			return new Page<Map<String,Object>>();
		}
		long start = (pageNo - 1) * pageSize;
		// 有数据的情况下，继续查询
		sql = sql + " limit " + start + "," + pageSize;
		List<Map<String,Object>> list = (List<Map<String,Object>>) this.jdbcTemplateReadOnly().queryForList(sql, param);
		log.debug(sql);
		return new Page<Map<String,Object>>(start, count, pageSize, list);
	}
	
	
	/**
	 * 分页查询特殊SQL语句
	 * @param sql 语句
	 * @param param  查询条件
	 * @param pageNo	页码
	 * @param pageSize	每页内容
	 * @return
	 */
	protected Page<Map<String,Object>> findBySqlToPage(String sql, Object [] param, final int pageNo, final int pageSize) throws Exception {
		String countSql = "select count(1) from (" + sql + ") a";
		long count = this.jdbcTemplateReadOnly().queryForLong(countSql, param);
		if (count == 0) {
			return new Page<Map<String,Object>>();
		}
		long start = (pageNo - 1) * pageSize;
		sql = sql + " limit " + start + "," + pageSize;
		List<Map<String,Object>> list = (List<Map<String,Object>>) this.jdbcTemplateReadOnly().queryForList(sql, param);
		log.debug(sql);
		return new Page<Map<String,Object>>(start, count, pageSize, list);
	}
	
	/**
	 * 根据<属性名和属属性值Map查询符合条件的唯一对象，没符合条件的记录返回null.<br>
	 * 例如，如下语句查找sex=1,age=18的所有记录：
	 * 
	 * <pre>
	 *     <code>
	 * Map properties = new HashMap();
	 * properties.put(&quot;sex&quot;, &quot;1&quot;);
	 * properties.put(&quot;age&quot;, 18);
	 * User user = service.findUnique(User.class, properties);
	 * </code>
	 * </pre>
	 * 
	 * @param properties 属性值Map，key为属性名，value为属性值
	 * @return 符合条件的唯一对象，没符合条件的记录返回null.
	 */
	protected T findUnique(Map<String, Object> properties) throws Exception {
		QueryRule queryRule = QueryRule.getInstance();
		for (String key : properties.keySet()) {
			queryRule.andEqual(key, properties.get(key));
		}
		return findUnique(queryRule);
	}

	/**
	 * 根据查询规则查询符合条件的唯一象，没符合条件的记录返回null.<br>
	 * <pre>
	 *     <code>
	 * QueryRule queryRule = QueryRule.getInstance();
	 * queryRule.addLike(&quot;username&quot;, user.getUsername());
	 * queryRule.addLike(&quot;monicker&quot;, user.getMonicker());
	 * queryRule.addBetween(&quot;id&quot;, lowerId, upperId);
	 * User user = service.findUnique(User.class, queryRule);
	 * </code>
	 * </pre>
	 * 
	 * @param queryRule  查询规则
	 * @return 符合条件的唯一对象，没符合条件的记录返回null.
	 */
	protected T findUnique(QueryRule queryRule) throws Exception {
		List<T> list = find(queryRule);
		if (list.size() == 0) {
			return null;
		} else if (list.size() == 1) {
			return list.get(0);
		} else {
			throw new IllegalStateException("findUnique return " + list.size() + " record(s).");
		}
	}

	
	/**
	 * 根据当前list进行相应的分页返回
	 * @param <T>
	 * @param objList
	 * @param pageNo
	 * @param pageSize
	 * @return Page
	 */
	protected Page<T> pagination(List<T> objList, int pageNo, int pageSize) throws Exception {
		List<T> objectArray = new ArrayList<T>(0);
		int startIndex = (pageNo - 1) * pageSize;
		int endIndex = pageNo * pageSize;
		if(endIndex >= objList.size()){
			endIndex = objList.size();
		}
		for (int i = startIndex; i < endIndex; i++) {
			objectArray.add(objList.get(i));
		}
		return new Page<T>(startIndex, objList.size(), pageSize, objectArray);
	}

	/**
	 * 合并PO List对象.(如果POJO中的值为null,则继续使用PO中的值）
	 * 
	 * @param pojoList  传入的POJO的List
	 * @param poList 传入的PO的List
	 * @param idName ID字段名称
	 */
	protected void mergeList(List<T> pojoList, List<T> poList, String idName) throws Exception {
		mergeList(pojoList, poList, idName, false);
	}

	/**
	 * 合并PO List对象.
	 * 
	 * @param pojoList 传入的POJO的List
	 * @param poList 传入的PO的List
	 * @param idName  ID字段名称
	 * @param isCopyNull 是否拷贝null(当POJO中的值为null时，如果isCopyNull=ture,则用null,否则继续使用PO中的值）
	 */
	protected void mergeList(List<T> pojoList, List<T> poList, String idName,boolean isCopyNull) throws Exception {
		Map<Object, Object> map = new HashMap<Object, Object>();
		Map<String, PropertyMapping> pm = op.mappings;
		for (Object element : pojoList) {
			Object key;
			try {
				key = pm.get(idName).getter.invoke(element);
				map.put(key, element);
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}
		for (Iterator<T> it = poList.iterator(); it.hasNext();) {
			Object element = (Object) it.next();
			try {
				Object key = pm.get(idName).getter.invoke(element);
				if (!map.containsKey(key)) {
					delete(element);
					it.remove();
				} else {
					DataUtils.copySimpleObject(map.get(key), element, isCopyNull);
				}
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}
		T[] pojoArray = (T[])pojoList.toArray();
		for (int i = 0; i < pojoArray.length; i++) {
			T element = pojoArray[i];
			try {
				Object key = pm.get(idName).getter.invoke(element);
				if (key == null) {
					poList.add(element);
				}
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	private String removeFirstAnd(String sql){
		if(StringUtils.isEmpty(sql)){return sql;}
		return sql.trim().toLowerCase().replaceAll("^\\s*and", "") + " ";
	}
	
	private EntityOperation<T> getOp(){
		return this.op;
	}
	
	
	
	/**
	 * ResultSet -> Object
	 * 
	 * @param <T>
	 * 
	 * @param rs
	 * @param obj
	 */
	private <T> T populate(ResultSet rs, T obj) {
		try {
			ResultSetMetaData metaData = rs.getMetaData(); // 取得结果集的元元素
			int colCount = metaData.getColumnCount(); // 取得所有列的个数
			Field[] fields = obj.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Field f = fields[i];
				// rs的游标从1开始，需要注意
				for (int j = 1; j <= colCount; j++) {
					Object value = rs.getObject(j);
					String colName = metaData.getColumnName(j);
					if (!f.getName().equalsIgnoreCase(colName)) {
						continue;
					}

					// 如果列名中有和字段名一样的，则设置值
					try {
						BeanUtils.copyProperty(obj, f.getName(), value);
					} catch (Exception e) {
						log.warn("BeanUtils.copyProperty error, field name: "
								+ f.getName() + ", error: " + e);
					}

				}
			}
		} catch (Exception e) {
			log.warn("populate error...." + e);
		}
		return obj;
	}

	/**
	 * 封装一下SimpleJdbcTemplate的queryForObject（默认查不到会抛异常）方法，
	 * 
	 * @param sql
	 * @param mapper
	 * @param args
	 * @return 如查询不到，返回null，不抛异常；查询到多个，也抛出异常
	 */
	private <T> T queryForObject(String sql, RowMapper<T> mapper,
			Object... args) {
		List<T> results = this.jdbcTemplateReadOnly().query(sql, mapper, args);
		return DataAccessUtils.singleResult(results);
	}

	protected byte[] getBlobColumn(ResultSet rs, int columnIndex)
			throws SQLException {
		try {
			Blob blob = rs.getBlob(columnIndex);
			if (blob == null) {
				return null;
			}

			InputStream is = blob.getBinaryStream();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			if (is == null) {
				return null;
			} else {
				byte buffer[] = new byte[64];
				int c = is.read(buffer);
				while (c > 0) {
					bos.write(buffer, 0, c);
					c = is.read(buffer);
				}
				return bos.toByteArray();
			}
		} catch (IOException e) {
			throw new SQLException(
					"Failed to read BLOB column due to IOException: "
							+ e.getMessage());
		}
	}

	protected void setBlobColumn(PreparedStatement stmt, int parameterIndex,
			byte[] value) throws SQLException {
		if (value == null) {
			stmt.setNull(parameterIndex, Types.BLOB);
		} else {
			stmt.setBinaryStream(parameterIndex,
					new ByteArrayInputStream(value), value.length);
		}
	}

	protected String getClobColumn(ResultSet rs, int columnIndex)
			throws SQLException {
		try {
			Clob clob = rs.getClob(columnIndex);
			if (clob == null) {
				return null;
			}

			StringBuffer ret = new StringBuffer();
			InputStream is = clob.getAsciiStream();

			if (is == null) {
				return null;
			} else {
				byte buffer[] = new byte[64];
				int c = is.read(buffer);
				while (c > 0) {
					ret.append(new String(buffer, 0, c));
					c = is.read(buffer);
				}
				return ret.toString();
			}
		} catch (IOException e) {
			throw new SQLException(
					"Failed to read CLOB column due to IOException: "
							+ e.getMessage());
		}
	}

	protected void setClobColumn(PreparedStatement stmt, int parameterIndex,
			String value) throws SQLException {
		if (value == null) {
			stmt.setNull(parameterIndex, Types.CLOB);
		} else {
			stmt.setAsciiStream(parameterIndex,
					new ByteArrayInputStream(value.getBytes()), value.length());
		}
	}

	/**
	 * 分页查询支持，支持简单的sql查询分页（复杂的查询，请自行编写对应的方法）
	 * @param <T>
	 * 
	 * @param sql
	 * @param rowMapper
	 * @param args
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	private <T> Page simplePageQuery(String sql, RowMapper<T> rowMapper, Map<String, ?> args, long pageNo, long pageSize) {
		long start = (pageNo - 1) * pageSize;
		return simplePageQueryByStart(sql,rowMapper,args,start,pageSize);
	}

	/**
	 * 
	 * @param sql
	 * @param rowMapper
	 * @param args
	 * @param start
	 * @param pageSize
	 * @return
	 */
	private <T> Page simplePageQueryByStart(String sql, RowMapper<T> rowMapper, Map<String, ?> args, long start, long pageSize) {
		// 首先查询总数
		String countSql = "select count(*) " + removeSelect(removeOrders(sql));
		long count = this.jdbcTemplateReadOnly().queryForLong(countSql, args);
		if (count == 0) {
			log.debug("no result..");
			return new Page();
		}
		// 有数据的情况下，继续查询
		sql = sql + " limit " + start + "," + pageSize;
		log.debug(StringUtils.format("[Execute SQL]sql:{0},params:{1}", sql, args));
		List<T> list = this.jdbcTemplateReadOnly().query(sql, rowMapper, args);
		return new Page(start, count, (int)pageSize, list);
	}
	
	protected long queryCount(String sql,Map<String, ?> args){
		String countSql = "select count(*) " + removeSelect(removeOrders(sql));
		return this.jdbcTemplateReadOnly().queryForLong(countSql, args);
	}
	
	protected <T> List<T> simpleListQueryByStart(String sql, RowMapper<T> rowMapper,
			Map<String, ?> args, long start, long pageSize) {
		
		sql = sql + " limit " + start + "," + pageSize;
		log.debug(StringUtils.format("[Execute SQL]sql:{0},params:{1}", sql, args));
		List<T> list = this.jdbcTemplateReadOnly().query(sql, rowMapper, args);
		if(list == null){
			return new ArrayList<T>();
		}
		return list;
	}
	
	/**
	 * 分页查询支持，支持简单的sql查询分页（复杂的查询，请自行编写对应的方法）
	 * 
	 * @param sql
	 * @param rm
	 * @param args
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	private Page simplePageQueryNotT(String sql, RowMapper rm, Map<String, ?> args, long pageNo, long pageSize) {
		// 首先查询总数
		String countSql = "select count(*) " + removeSelect(removeOrders(sql));
		long count = this.jdbcTemplateReadOnly().queryForLong(countSql, args);
		if (count == 0) {
			log.debug("no result..");
			return new Page();
		}
		// 有数据的情况下，继续查询
		long start = (pageNo - 1) * pageSize;
		sql = sql + " limit " + start + "," + pageSize;
		log.debug(StringUtils.format("[Execute SQL]sql:{0},params:{1}", sql, args));
		List list = this.jdbcTemplateReadOnly().query(sql, rm, args);
		return new Page(start, count, (int)pageSize, list);
	}
	
	/**
	 * 去掉order
	 * 
	 * @param sql
	 * @return
	 */
	private String removeOrders(String sql) {
		Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(sql);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}
	
	/**
	 * 去掉select
	 * 
	 * @param sql
	 * @return
	 */
	private String removeSelect(String sql) {
		int beginPos = sql.toLowerCase().indexOf("from");
		return sql.substring(beginPos);
	}


	private long getMaxId(String table, String column) {
		String sql = "SELECT max(" + column + ") FROM " + table + " ";
		long maxId = this.jdbcTemplateReadOnly().queryForLong(sql, new Object[] {});
		return maxId;
	}
	
	/**
	 * 生成简单对象UPDATE语句，简化sql拼接
	 * @param tableName
	 * @param pkName
	 * @param pkValue
	 * @param params
	 * @return
	 */
	private String makeSimpleUpdateSql(String tableName, String pkName, Object pkValue, Map<String, Object> params){
		if(StringUtils.isEmpty(tableName) || params == null || params.isEmpty()){
			return "";
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("update ").append(tableName).append(" set ");
		//添加参数
		Set<String> set = params.keySet();
		int index = 0;
		for (String key : set) {
			 sb.append(key).append(" = :").append(key);
			 if(index != set.size() - 1){
				 sb.append(",");
			 }
			 index++;
		}
		sb.append(" where ").append(pkName).append(" = :").append(pkName) ;
		
		return sb.toString();
	}
	
	
	/**
	 * 生成简单对象UPDATE语句，简化sql拼接
	 * @param pkName
	 * @param pkValue
	 * @param params
	 * @return
	 */
	private String makeSimpleUpdateSql(String pkName, Object pkValue, Map<String, Object> params){
		if(StringUtils.isEmpty(getTableName()) || params == null || params.isEmpty()){
			return "";
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("update ").append(getTableName()).append(" set ");
		//添加参数
		Set<String> set = params.keySet();
		int index = 0;
		for (String key : set) {
			 sb.append(key).append(" = :").append(key);
			 if(index != set.size() - 1){
				 sb.append(",");
			 }
			 index++;
		}
		sb.append(" where ").append(pkName).append(" = :").append(pkName) ;
		
		return sb.toString();
	}
	
	
	
	/**
	 * 生成对象INSERT语句，简化sql拼接
	 * @param tableName
	 * @param params
	 * @return
	 */
	private String makeSimpleReplaceSql(String tableName, Map<String, Object> params){
		if(StringUtils.isEmpty(tableName) || params == null || params.isEmpty()){
			return "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("replace into ").append(tableName);
		
		StringBuffer sbKey = new StringBuffer();
		StringBuffer sbValue = new StringBuffer();
		
		sbKey.append("(");
		sbValue.append("(");
		//添加参数
		Set<String> set = params.keySet();
		int index = 0;
		for (String key : set) {
			sbKey.append(key);
			sbValue.append(" :").append(key);
			if(index != set.size() - 1){
				sbKey.append(",");
				sbValue.append(",");
			}
			index++;
		}
		sbKey.append(")");
		sbValue.append(")");
		
		sb.append(sbKey).append("VALUES").append(sbValue);
		
		return sb.toString();
	}
	
	/**
	 * 生成对象INSERT语句，简化sql拼接
	 * @param tableName
	 * @param params
	 * @return
	 */
	private String makeSimpleReplaceSql(String tableName, Map<String, Object> params,List<Object> values){
		if(StringUtils.isEmpty(tableName) || params == null || params.isEmpty()){
			return "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("replace into ").append(tableName);
		
		StringBuffer sbKey = new StringBuffer();
		StringBuffer sbValue = new StringBuffer();
		
		sbKey.append("(");
		sbValue.append("(");
		//添加参数
		Set<String> set = params.keySet();
		int index = 0;
		for (String key : set) {
			sbKey.append(key);
			sbValue.append(" ?");
			if(index != set.size() - 1){
				sbKey.append(",");
				sbValue.append(",");
			}
			index++;
			values.add(params.get(key));
		}
		sbKey.append(")");
		sbValue.append(")");
		
		sb.append(sbKey).append("VALUES").append(sbValue);
		
		return sb.toString();
	}
	
	
	
	/**
	 * 生成对象INSERT语句，简化sql拼接
	 * @param tableName
	 * @param params
	 * @return
	 */
	private String makeSimpleInsertSql(String tableName, Map<String, Object> params){
		if(StringUtils.isEmpty(tableName) || params == null || params.isEmpty()){
			return "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("insert into ").append(tableName);
		
		StringBuffer sbKey = new StringBuffer();
		StringBuffer sbValue = new StringBuffer();
		
		sbKey.append("(");
		sbValue.append("(");
		//添加参数
		Set<String> set = params.keySet();
		int index = 0;
		for (String key : set) {
			sbKey.append(key);
			sbValue.append(" :").append(key);
			if(index != set.size() - 1){
				sbKey.append(",");
				sbValue.append(",");
			}
			index++;
		}
		sbKey.append(")");
		sbValue.append(")");
		
		sb.append(sbKey).append("VALUES").append(sbValue);
		
		return sb.toString();
	}
	
	/**
	 * 生成对象INSERT语句，简化sql拼接
	 * @param tableName
	 * @param params
	 * @return
	 */
	private String makeSimpleInsertSql(String tableName, Map<String, Object> params,List<Object> values){
		if(StringUtils.isEmpty(tableName) || params == null || params.isEmpty()){
			return "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("insert into ").append(tableName);
		
		StringBuffer sbKey = new StringBuffer();
		StringBuffer sbValue = new StringBuffer();
		
		sbKey.append("(");
		sbValue.append("(");
		//添加参数
		Set<String> set = params.keySet();
		int index = 0;
		for (String key : set) {
			sbKey.append(key);
			sbValue.append(" ?");
			if(index != set.size() - 1){
				sbKey.append(",");
				sbValue.append(",");
			}
			index++;
			values.add(params.get(key));
		}
		sbKey.append(")");
		sbValue.append(")");
		
		sb.append(sbKey).append("VALUES").append(sbValue);
		
		return sb.toString();
	}
	
	
	private Serializable doInsertRuturnKey(Map<String,Object> params){
		final List<Object> values = new ArrayList<Object>();
		final String sql = makeSimpleInsertSql(getTableName(),params,values);
		KeyHolder keyHolder = new GeneratedKeyHolder();
		final JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSourceWrite());
		  try {				 

			    jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(

						Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);


					for (int i = 0; i < values.size(); i++) {
						ps.setObject(i+1, values.get(i)==null?null:values.get(i));

					}
					return ps;
			    }

		   }, keyHolder);
		  } catch (DataAccessException e) {
			  log.error("error",e);
		  }
		  


		if (keyHolder == null) { return ""; }


		Map<String, Object> keys = keyHolder.getKeys();
		if (keys == null || keys.size() == 0 || keys.values().size() == 0) {
			return "";
		}
		Object key = keys.values().toArray()[0];
		if (key == null || !(key instanceof Serializable)) {
			return "";
		}
		if (key instanceof Number) {
			//Long k = (Long) key;
			Class clazz = key.getClass();
//			return clazz.cast(key);
			return (clazz == int.class || clazz == Integer.class) ? ((Number) key).intValue() : ((Number)key).longValue();


		} else if (key instanceof String) {
			return (String) key;
		} else {
			return (Serializable) key;
		}


	}
	
	
	/**
	 * 生成默认的对象UPDATE语句，简化sql拼接
	 * @param pkValue
	 * @param params
	 * @return
	 */
	private String makeDefaultSimpleUpdateSql(Object pkValue, Map<String, Object> params){
		return this.makeSimpleUpdateSql(getTableName(), getPKColumn(), pkValue, params);
	}

	/**
	 * 生成默认的对象INSERT语句，简化sql拼接
	 * @param params
	 * @return
	 */
	private String makeDefaultSimpleInsertSql(Map<String, Object> params){
		return this.makeSimpleInsertSql(this.getTableName(), params);
	}
	
	/**
	 * 获取一个实例对象
	 * @param tableName
	 * @param pkName
	 * @param pkValue
	 * @param rm
	 * @return
	 */
	private Object doLoad(String tableName, String pkName, Object pkValue, RowMapper rm){
		StringBuffer sb = new StringBuffer();
		sb.append("select * from ").append(tableName).append(" where ").append(pkName).append(" = ?");
		List<Object> list = this.jdbcTemplateReadOnly().query(sb.toString(), rm, pkValue);
		if(list == null || list.isEmpty()){
			return null;
		}
		return list.get(0);
	}
	
	/**
	 * 获取默认的实例对象
	 * @param <T>
	 * @param pkValue
	 * @param rowMapper
	 * @return
	 */
	private <T> T doLoad(Object pkValue, RowMapper<T> rowMapper){
		Object obj = this.doLoad(getTableName(), getPKColumn(), pkValue, rowMapper);
		if(obj != null){
			return (T)obj;
		}
		return null;
	}
	
	
	/**
	 * 删除实例对象，返回删除记录数
	 * @param tableName
	 * @param pkName
	 * @param pkValue
	 * @return
	 */
	private int doDelete(String tableName, String pkName, Object pkValue) {
		StringBuffer sb = new StringBuffer();
		sb.append("delete from ").append(tableName).append(" where ").append(pkName).append(" = ?");
		int ret = this.jdbcTemplateWrite().update(sb.toString(), pkValue);
		return ret;
	}
	
	/**
	 * 删除默认实例对象，返回删除记录数
	 * @param pkValue
	 * @return
	 */
	private int doDelete(Object pkValue){
		return this.doDelete(getTableName(), getPKColumn(), pkValue);
	}
	
	/**
	 * 更新实例对象，返回删除记录数
	 * @param tableName
	 * @param pkName
	 * @param pkValue
	 * @param params
	 * @return
	 */
	private int doUpdate(String tableName, String pkName, Object pkValue, Map<String, Object> params){
		params.put(pkName, pkValue);
		String sql = this.makeSimpleUpdateSql(tableName, pkName, pkValue, params);
		int ret = this.jdbcTemplateWrite().update(sql, params);
		return ret;
	}
	
	/**
	 * 更新实例对象，返回删除记录数
	 * @param pkName
	 * @param pkValue
	 * @param params
	 * @return
	 */
	private int doUpdate( String pkName, Object pkValue, Map<String, Object> params){
		params.put(pkName, pkValue);
		String sql = this.makeSimpleUpdateSql( pkName, pkValue, params);
		int ret = this.jdbcTemplateWrite().update(sql, params);
		return ret;
	}
	
	/**
	 * 更新实例对象，返回删除记录数
	 * @param pkValue
	 * @param params
	 * @return
	 */
	private int doUpdate(Object pkValue, Map<String, Object> params){
		//
		String sql = this.makeDefaultSimpleUpdateSql(pkValue, params);
		params.put(this.getPKColumn(), pkValue);
		int ret = this.jdbcTemplateWrite().update(sql, params);
		return ret;
	}
	
	
	private boolean doReplace(Map<String, Object> params) {
		String sql = this.makeSimpleReplaceSql(this.getTableName(), params);
		int ret = this.jdbcTemplateWrite().update(sql, params);
		return ret > 0;
	}
	
	private boolean doReplace(String tableName, Map<String, Object> params){
		String sql = this.makeSimpleReplaceSql(tableName, params);
		int ret = this.jdbcTemplateWrite().update(sql, params);
		return ret > 0;
	}
	
	
	/**
	 * 插入
	 * @param tableName
	 * @param params
	 * @return
	 */
	private boolean doInsert(String tableName, Map<String, Object> params){
		String sql = this.makeSimpleInsertSql(tableName, params);
		int ret = this.jdbcTemplateWrite().update(sql, params);
		return ret > 0;
	}
	
	/**
	 * 插入
	 * @param params
	 * @return
	 */
	private boolean doInsert(Map<String, Object> params) {
		String sql = this.makeSimpleInsertSql(this.getTableName(), params);
		int ret = this.jdbcTemplateWrite().update(sql, params);
		return ret > 0;
	}
	
	/**
	 * 获取主键列名称 建议子类重写
	 * @return
	 */
	protected abstract String getPKColumn();
	
	protected abstract void setDataSource(DataSource dataSource);
	
	private Map<String,Object> convertMap(Object obj){
		Map<String,Object> map = new HashMap<String,Object>();
		
		 List<FieldInfo> getters = TypeUtils.computeGetters(obj.getClass(), null);
		 for(int i=0,len=getters.size();i<len;i++){
			 FieldInfo fieldInfo = getters.get(i);
			 String name = fieldInfo.getName();
			try {
				Object value = fieldInfo.get(obj);
				map.put(name,value);
			} catch (Exception e) {
				log.error(String.format("convertMap error object:%s  field: %s",obj.toString(),name));
			} 
		 }
		
		return map;
	}
	
}
