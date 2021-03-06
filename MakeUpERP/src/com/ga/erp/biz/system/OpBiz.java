package com.ga.erp.biz.system;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ga.click.control.DbField;
import com.ga.click.dbutil.DbUtils;
import com.ga.click.dbutil.PageResult;
import com.ga.click.exception.BizException;
import com.ga.click.mvc.UserACL;
import com.ga.click.page.BizUtil;
import com.ga.click.page.QueryPageData;
import com.ga.click.util.GaUtil;
import com.ga.erp.biz.ACLBiz;

public class OpBiz extends ACLBiz {
  public OpBiz(UserACL acl) {
    super(acl);
  }
  
  public final static Long storeRoleId = 3L;
  public final static Long supplierRoleId = 4L;
  
  /**
   * 检测用户名
   * @param userName
   * @return
   */
  public boolean isOpUserName(String userName){
    return DbUtils.queryLong("select count(*) from op where USERNAME= ? ",userName) > 0;
  }
  
  public UserACL login(String userName,String passwd) {
    
    boolean isok = false;
    Long userID = 1L;
    try {
      String sql = "select * from op where USERNAME = ? AND STATE = 1";
      Map<String,Object> userInfo = DbUtils.queryMap(DbUtils.getConnection(),sql, userName);
      if (userInfo == null) {
        throw new BizException("用户名不符");
      }
      if (!userInfo.get("PASSWORD").toString().equals(GaUtil.encodeToMD5(passwd))) {
        throw new BizException("密码错误");
      }          
      List<String> roleId = queryRoleList(Long.parseLong(userInfo.get("OP_ID")+""));
      if(roleId.size() > 0){
        userInfo.put("ROLE_ID", roleId);
      }
      
      String supplierSql = "SELECT s.SUPPLIER_ID,s.SUPPLIER_NAME FROM SUPPLIER s \n" + 
					   " JOIN OP_SUPPLIER os ON s.SUPPLIER_ID = os.SUPPLIER_ID\n" +
					   " WHERE os.OP_ID = ?";
      Map<String, Object> supplierMap = DbUtils.queryMap(DbUtils.getConnection(), supplierSql, userInfo.get("OP_ID"));
      if(supplierMap != null){
        userInfo.putAll(supplierMap);
      }
      
      String storeSql = "SELECT s.STORE_ID,s.STORE_NAME FROM STORE s \n" + 
            " JOIN OP_STORE os ON s.STORE_ID = os.STORE_ID\n" +
            " WHERE os.OP_ID = ?";
      List<Map<String, Object>> storeMapList = DbUtils.queryMapList(DbUtils.getConnection(), storeSql, userInfo.get("OP_ID"));
      if(storeMapList.size() > 0){
        StringBuffer storeId = new StringBuffer("");
//        List<Map<String, Object>> listStore = new ArrayList<Map<String,Object>>();
        Map<String, Object> optionMap = new LinkedHashMap<String, Object>();
        for (Map<String, Object> map : storeMapList) {
          storeId.append(map.get("STORE_ID") + ",");
          optionMap.put(map.get("STORE_ID") + "", map.get("STORE_NAME"));
//          listStore.add(optionMap);
        }
        userInfo.put("STORE_ID", storeId.substring(0, storeId.length() - 1));
        userInfo.put("STORE_NAME", optionMap);
      }
      
      UserACL userAcl = new UserACL(userInfo);
      isok = true;
      userID = userAcl.getUserID();
      //查询资源树信息等
//      userAcl.loadUserPurview(userID);
      return userAcl;
    }  catch (BizException e) {
      throw e;
    }  catch(Exception ex) {
      ex.printStackTrace();
      throw new BizException(BizException.SYSTEM,"系统登录失败",ex);
    }
    finally {
      try {
        DbUtils.begin();
        String sql = "insert into OP_LOG(OP_ID,TYPE,ACTION,CREATE_TIME,OPERATOR_TARGET,NOTE) values (?,?,?,now(),?,?)";
        if (isok) {
          //更新登录次数
          DbUtils.execute(sql,userID,1,"管理员登录",userID,"管理员"+ userName +"登录成功");          
        } else {          
//          DbUtils.execute(sql,userID,1,userID,"管理员"+ userName +"登录失败");
        }
        DbUtils.commit();
      }  catch(Exception ex) {
        DbUtils.rollback();
      }
    }
  }
  
  /**
   * 查询操作员分页列表
   * @param pageData
   * @param fieldList
   * @return
   */
  public PageResult queryOpList(QueryPageData pageData,List<DbField> fieldList, String type) {
    return BizUtil.queryListBySQL("SELECT * FROM OP ", "TYPE = " + type, "OP_ID desc", fieldList, pageData,this.userACL);
  }
  
  /**
   * 查询操作员详细
   * @param id
   * @param fieldList
   * @return
   */
  public Map<String, Object> queryOpDetail(Long id,List<DbField> fieldList, String type){
    String sql ="select * from op where OP_ID = ? AND TYPE = ?";
    return DbUtils.queryMap(fieldList, sql, id, type);
  }
  
  
  private void chkValue(Map<String, Object> valueMap){
    StringBuffer buffer = new StringBuffer("");
    int count = 1;
    if(valueMap.get("USERNAME") == null || String.valueOf(valueMap.get("USERNAME")).trim().equals("")){
      buffer.append("<br>"+count+"，请填写帐号！");
      count++;
    } else if(isOpUserName(String.valueOf(valueMap.get("USERNAME")).trim())){
      buffer.append("<br>"+count+"，该帐号存在！");
      count++;
    }
    if(valueMap.get("PASSWORD") == null || String.valueOf(valueMap.get("PASSWORD")).trim().equals("d41d8cd98f00b204e9800998ecf8427e")){
      buffer.append("<br>"+count+"，用户密码为空！");
      count++;
    } else if(!String.valueOf(valueMap.get("PASSWORD")).equals(String.valueOf(valueMap.get("RE_PASSWORD")))){
      buffer.append("<br>"+count+"，两次密码不一致！");
      count++;
    }
    if(buffer.length() > 0){
      throw new BizException(buffer.toString());
   }
    
  }
  
  /**
   * 添加操作员
   * @param vMap
   */
  public void addOp(Map<String,Object> vMap, Map<String,String> funcMap) {
    try {
      chkValue(vMap);
      String fields = "USERNAME,PASSWORD,TRUENAME,STATE,EMAIL,POSTCODE,LINK_TEL,LINK_ADDR,CREATE_TIME,TYPE,NOTE";
      vMap.put("STATE", "1");
      if(GaUtil.isValidStr(vMap.get("END_TIME") + "")){
        funcMap.put("END_TIME","DATE_FORMAT(?,'%Y-%m-%d %H:%i:%s')");
        fields += ",END_TIME";
      }
      funcMap.put("CREATE_TIME", "NOW()");      
      DbUtils.begin();
      DbUtils.add("OP", vMap, funcMap,fields);
      /******************************门店或者供应商管理员*************************************/
      if(vMap.get("TYPE") != null && !(vMap.get("TYPE") + "").equals("1")){
        String objId = "";
        Long roleId = 0L;
        if(vMap.get("STORE_ID") != null){
          objId = vMap.get("STORE_ID") + "";
          roleId = storeRoleId;
        } else if(vMap.get("SUPPLIER_ID") != null){
          objId = vMap.get("SUPPLIER_ID") + "";
          roleId = supplierRoleId;
        } else{
          throw new BizException("管理员类型不确定");
        }
        if(GaUtil.isValidStr(objId)){
          String target = roleId == storeRoleId ? "STORE" : "SUPPLIER";
          Long opId = GaUtil.getLastId();
          DbUtils.execute("insert into OP_" + target + " (OP_ID," + target + "_ID) values(?,?)", opId, objId);
          DbUtils.execute("insert into OP_ROLE (OP_ID,ROLE_ID) values(?,?)", opId, roleId);
        }
       }
      /******************************门店或者供应商管理员*************************************/
      DbUtils.commit();
    } catch (BizException e) {
      throw e;
    }
    catch(Exception ex) {
      throw new BizException("添加操作员失败");
    } finally {
      DbUtils.rollback();
    }    
  }
  
  /**
   * 修改     
   * @param valueMap
   */
  public void updateOp(Map<String,Object> valueMap,Map<String,String> funcMap){
    if(valueMap.get("END_TIME") != null && !String.valueOf(valueMap.get("END_TIME")).equals("")){
      funcMap.put("END_TIME","DATE_FORMAT(?,'%Y-%m-%d %H:%i')");
    }
    String fields = "TRUENAME,EMAIL,POSTCODE,LINK_TEL,LINK_ADDR,END_TIME,NOTE";
    try {      
      DbUtils.begin();
      DbUtils.update("OP", valueMap, funcMap, fields,"OP_ID");
      DbUtils.commit();
    } catch(Exception ex) {
      throw new BizException(BizException.SYSTEM,"修改管理员失败",ex);
    }
    finally {
      DbUtils.rollback();
    }
  }
  
  /**
   * 启用废止操作员
   * @param opId
   * @param type
   */
  public void disableOrEnabdleOp(Long opId, int type){
    try {      
      DbUtils.update(DbUtils.getConnection(), "update OP set STATE =? where OP_ID =?", type, opId);
    } catch(Exception ex) {
      throw new BizException(BizException.SYSTEM,"执行失败",ex);
    }
    finally {
      DbUtils.rollback();
    }
  }
  
  /**
   * 查询操作员密码
   * @param id
   * @param fieldList
   * @return
   */
  public Map<String, Object> queryOpPasswordDetail(Long id,List<DbField> fieldList){
    String sql ="select OP_ID,USERNAME,TRUENAME from op where OP_ID = ?";
    return DbUtils.queryMap(fieldList, sql, id);
  }
  
  /**
   * 修改     
   * @param valueMap
   */
  public void updateOpPassword(Map<String,Object> valueMap){
    try {      
      //执行查询
      DbUtils.begin();
      DbUtils.update("OP", valueMap, null, "PASSWORD","OP_ID");
      DbUtils.commit();
    } catch(Exception ex) {
      throw new BizException(BizException.SYSTEM,"修改密码失败！",ex);
    }
    finally {
      DbUtils.rollback();
    }
  }
  
  /**
   * 查询所属会员的角色
   * @param pageData
   * @param fieldList
   * @return
   */
  public List<Map<String, Object>> queryOpRoleList(Long id) {
    String sql =
      "select opr.op_role_id OP_ROLE_ID, r.role_name ROLE_NAME\n" +
      "  from OP_ROLE opr\n" + 
      "  join OP o\n" + 
      "    on opr.op_id = o.op_id\n" + 
      "  join ROLE r\n" + 
      "    on opr.role_id = r.role_id\n" + 
      " where r.state = 1\n" + 
      "   and o.op_id = ?";
    return DbUtils.queryMapList(DbUtils.getConnection(), sql, id);
  }
  
  public List<String> queryRoleList(Long opId) {
    String sql = "SELECT opr.ROLE_ID FROM OP_ROLE opr JOIN ROLE r on opr.ROLE_ID = r.ROLE_ID WHERE opr.OP_ID = 1 AND r.STATE = 1";
    List<Map<String, Object>> list = DbUtils.queryMapList(DbUtils.getConnection(), sql, opId);
    List<String> roleId = new ArrayList<String>();
    for (Map<String, Object> map : list) {
      roleId.add(map.get("ROLE_ID")+"");
    }
    return roleId;
  }
  
  /**
   * 删除操作员的角色
   * @param id
   */
  public void delOpRole(Long id){
    try {      
      DbUtils.begin();
      DbUtils.del("OP_ROLE", "OP_ROLE_ID", id);
      DbUtils.commit();
    } catch(Exception ex) {
      throw new BizException(BizException.SYSTEM,"执行删除失败",ex);
    }
    finally {
      DbUtils.rollback();
    }
  }
  
  public List<Map<String, Object>> bindAllLog(Long opId) {
	    try {
	    	String where = " where 1 = 1 ";
	    	if(opId != null){
	    		where += " AND B.OP_ID = " + opId;
	    	}
	      return DbUtils.queryMapList(DbUtils.getConnection(), " select A.*,B.TRUENAME " +
	      		"from OP_LOG A JOIN OP B on A.OPERATOR_TARGET = B.OP_ID" + 
	    		 where + " LIMIT 0,10");
	    }
	    catch(BizException ex) {
	      throw ex;
	    }
	    catch(Exception e) {
	      throw new BizException("查询业务日志异常");
	    }
	  }
  
  /**
   * 新建管理员角色
   * @param shopId
   */
  public void setStoreRole(Long opId, Long roleId){
    String sql = "insert into OP_ROLE (OP_ID,ROLE_ID) values (?,?)";
    DbUtils.update(sql, opId, roleId);
  }
  
  private void chkValue(String opId, String storeId){
    Boolean isExist = DbUtils.queryLong("select count(*) from OP_STORE where OP_ID = ? AND STORE_ID in (" + storeId + ")", opId) > 0;
    if(storeId.equals("")){
      throw new BizException("请选择门店");
    } else if(isExist){
      throw new BizException("已选门店含有此管理员现管理门店，请重新选择");
    }
  }
  
  /**
   * 新建门店管理员
   * @param shopId
   */
  public void addStoreOp(String opId, String storeId){
    chkValue(opId, storeId);
    String sql = "insert into OP_STORE (OP_ID,STORE_ID) values (?,?)";
    String[] storeIds = storeId.split(",");
    for (String id: storeIds) {
      DbUtils.update(sql, opId, id);
    }
  }
  
  public void delOpStore(Long opStoreId){
   DbUtils.execute("delete from OP_STORE where OP_STORE_ID = ?", opStoreId);
  }
  
  public void delOpSupplier(Long opSupplierId){
    DbUtils.execute("delete from OP_SUPPLIER where OP_SUPPLIER_ID = ?", opSupplierId);
   }
  
  public List<Map<String, Object>> queryOpStoreList(Long opId){
    return DbUtils.queryMapList(DbUtils.getConnection(), "select * from OP_STORE os JOIN STORE s " +
                                "on os.STORE_ID = s.STORE_ID where os.OP_ID = ?", opId);
  }
  
  public List<Map<String, Object>> queryStoreOpList(Long storeId){
    return DbUtils.queryMapList(DbUtils.getConnection(), "select * from OP_STORE os JOIN OP o " +
                                "on os.OP_ID = o.OP_ID where os.STORE_ID = ?", storeId);
  }
  
  public List<Map<String, Object>> querySupplierOpList(Long supplierId){
    return DbUtils.queryMapList(DbUtils.getConnection(), "select * from OP_SUPPLIER os JOIN OP s " +
                                "on os.OP_ID = s.OP_ID where os.SUPPLIER_ID = ?", supplierId);
  }
  
}
