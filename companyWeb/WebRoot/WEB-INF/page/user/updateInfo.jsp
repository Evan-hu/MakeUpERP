<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="page" uri="/WEB-INF/page.tld" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  <base href="<%=basePath%>">
  <title>完善资料</title>
  <meta http-equiv="pragma" content="no-cache">
  <meta http-equiv="cache-control" content="no-cache">
  <meta http-equiv="expires" content="0">    
  <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
  <meta http-equiv="description" content="This is my page">
  <link rel="stylesheet" href="<%=basePath%>common/css/common.css" />
  <link rel="stylesheet" href="<%=basePath%>css/updatemember.css" />
  </head>
  
  <body>
  <!-- header -->
  <jsp:include page="../common/header.jsp"/>
  <div class="hl">
  <div class="mh">
              当前位置 : <a href="<%=basePath%>user/goto.do?memberId=${member.memberId}&&type=1">会员中心</a> > 更新资料 
  </div>
  </div>
  <div class="container">
      <div class="cz">
            <div class="ct">
              <div class="memberlogo"></div>
                <div class="mborder"></div>
            </div>
            <div class="mc">
            <div class="cNav">
              <div class="navPanel">
                    <div class="navItem curItem" onclick="forward('<%=basePath%>user/goto.do?memberId=${member.memberId}&&type=1')">修改资料</div>
                    <div class="navItem" onclick="forward('<%=basePath%>user/goto.do?memberId=${member.memberId}&&type=2')">修改密码</div>
                    <div class="navItem" onclick="forward('<%=basePath%>user/mangGift.do?memberId=${member.memberId}&&state=1')">礼卷管理</div>
                    <div class="navItem" onclick="forward('<%=basePath%>user/queryScore.do?memberId=${member.memberId}&&type=1')">积分查询</div>
                    <div class="navItem" onclick="forward('<%=basePath%>user/myMessageList.do?memberId=${member.memberId}')">站内信</div>
                 </div>
             </div>
             <div class="content">
                <p class="rp">
                      <span class="myscore">资料修改</span>
                 </p>
                     
                  <div class="mcd">
                    <p class="mtitle">基本信息</p>
                    <form action="user/updateInfo.do" method="post" id="member">
                        <div class="mi">
                            <ul>
                                <li><span class="label">会员账号</span>
                                <input type="hidden" name="memberId" value="${member.memberId}">
                                <input type="hidden" name="memberNum" value="${member.memberNum}">
                                <span class="mv">${member.memberNum}</span></li>
                                <li><span class="label">真实姓名</span>
                                    <span class="mv" style="margin-left:-4px;">
                                    
                                    <c:if test="${member.truename == null}">
                                      <input type="text" name="truename" id="truename" maxlength="20" class="truename" />
                                    </c:if>
                                    
                                    <c:if test="${member.truename != null}">
                                        <input type="text" value="${member.truename}" name="truename" id="truename" maxlength="20" class="truename" readonly="readonly"/>
                                    </c:if>
                                    
                                        <span class="note">不可修改,请认真填写，以便以后礼品寄送</span>
                                    </span>
                                </li>
                                <li>
                                    <span class="label">性别</span>
                                    <span class="mv">
                                    <c:if test="${member.sex == null}">
                                        <span class="sexPanel"><input type="radio" name="sex" id="sex" value="2" class="sex"/>男</span>
                                        <span class="sexPanel"><input type="radio" name="sex" id="sex" value="1" class="sex"/>女</span>
                                    </c:if>
                                    <c:if test="${member.sex != null}">
                                        <span class="sexPanel"><input type="radio" <c:if test="${2==member.sex}"> checked="checked"</c:if> name="sex" id="sex" value="2" class="sex" disabled />男</span>
                                        <span class="sexPanel"><input type="radio" <c:if test="${1==member.sex}"> checked="checked"</c:if> name="sex" id="sex" value="1" class="sex" disabled />女</span>
                                    </c:if>
                                    </span>
                                </li>
                                <li>
                                    <span class="label">出生日期</span>
                                    <span class="mv">
                                    <c:if test="${member.birth == null}" >
                                        <input type="text" name="year" id="year" class="year" onclick="SelectDate(this,'yyyy')" />年
                                        <input type="text" name="month" id="month" class="month" onclick="SelectDate(this,'MM')" />月
                                        <input type="text" name="day" id="day" class="day" onclick="SelectDate(this,'dd')" />日
                                        <input type="hidden" name="birth" class="birth">
                                    </c:if>
                                    <c:if test="${member.birth != null}">
                                      <input type="text" name="birth" value="<fmt:formatDate pattern="yyyy-MM-dd" value="${member.birth}" type="both"/>" readonly="readonly" class="birth" />
                                    </c:if>
                                        <span class="note">不可修改,请认真填写，以便以后礼品寄送</span>
                                    </span>
                                </li>
                                <li>
                                    <span class="label">手机号码</span>
                                    <span class="mv">
                                        <input type="text" value="${member.tel}" name="tel" id="tel" class="tel" />
                                        <span class="apply"></span>
                                        短信激活码
                                        <input type="text" name="code" id="code" class="code" />
                                        <span class="sure"></span>
                                    </span>
                                </li>
                                <li>
                                    <span class="label">通信地址</span>
                                    <span class="mv">
                                        <input type="text" value="${member.addr}" name="addr" id="address" maxlength="40" class="address"/>
                                    </span>
                                </li>
                                <li>
                                    <span class="label">Email</span>
                                    <span class="mv">
                                        <input type="text" value="${member.email}" name="email" id="email" maxlength="40" class="email"/>
                                    </span>
                                </li>
                            </ul>
                            
                            <div class="submitPanel">
                              <input type="button" class="submit" />
                            </div>
                        </div>
                    </form>
                  </div>
                </div>
          </div>
        </div>
    </div>
      
  <!-- link -->    
  <jsp:include page="../common/link.jsp"/>
  <!-- footer -->
  <jsp:include page="../common/footer.jsp"/>
     
  </body>
  <script type="text/javascript" src="<%=basePath%>common/js/jquery-1.7.min.js"></script>
  <script type="text/javascript" src="<%=basePath%>common/js/common.js"></script>
  <script type="text/javascript" src="<%=basePath%>common/js/page.js"></script>
  <script type="text/javascript" src="<%=basePath%>js/button.skip.js"></script>
  <!-- JiaThis Button BEGIN -->
  <script type="text/javascript" src="http://v3.jiathis.com/code/jia.js?uid=1399183794965592" charset="utf-8"></script>
  <script type="text/javascript" src="<%=basePath%>js/Calendar.js" charset="utf-8"></script>
  <!-- JiaThis Button END -->
  <script type="text/javascript">
  $(function(){
      $(".submit").click(function(){
    	  $("#member").submit();
      });
      
      var value = ${member.memberId};
      $(".member_1").click(function(){
          if(value == null){
            window.location="/user/goto.do?type=1&&memberId=${memberId}"
          } else 
        	  window.location="/user/goto.do?type=1&&memberId=${member.memberId}"
        });
      
      //显示提示信息
      var msg = "${msg}";
      if(msg == "更新成功" || msg == "更新失败"){
    	  alert(msg);
      }
      
      //添加日期
      $("#day").blur(function(){
      var birth = $("#year").val()+"-"+$("#month").val()+"-"+$("#day").val();
      alert(birth);
      $("input[class='birth']").attr('value',birth);
      });
      
      
   }); 
  </script>
<<<<<<< .mine
  </head>
  
  <body>
     <div align="center">
     <sf:form method="post" action="user/updateInfo.do" commandName="member">
     <table>
        <tr>
          <td align="right">姓名：</td>
          <td align="left">
          <sf:input path="truename" value="${member.truename}" readonly="true" />
          </td>
          <td></td>
        </tr>
        <tr>
          <td align="right">性别：</td>
          <td align="left">
          <sf:radiobutton path="sex" value="2" readonly="true" />男
          <sf:radiobutton path="sex" value="1" readonly="true" />女
          </td>
        </tr>
        <tr>
          <td align="right">生日：</td>
          <td align="left">
          <sf:input path="birth" value="${member.birth}" readonly="true"/>
          </td>
        </tr>
        <tr>
          <td align="right">手机号码：</td>
          <td align="left">
          <sf:input path="tel" value="${member.tel}"  readonly="true"/>
          </td>
        </tr>
        <tr>
          <td align="right">E_mail：</td>
          <td align="left">
          <sf:input path="email" value="${member.email}"/>
          </td>
        </tr>
        <tr>
          <td></td>
          <td align="right">
          <a href="javascript:;" class="memberForm">提交</a>
          </td>
          <td></td>
        </tr>
     </table>
     </sf:form>
     <sf:errors>lala</sf:errors>
     </div>
  </body>
=======
>>>>>>> .r145
</html>
