/**
 * Copyright (c) 2014 GA
 * All right reserved.
 */
package com.company.shop.action.web;

import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.company.pager.Page;
import com.company.shop.action.BaseController;
import com.company.shop.entity.Application;
import com.company.shop.entity.Card;
import com.company.shop.entity.Member;
import com.company.shop.entity.Messagesend;
import com.company.shop.entity.ScoreLog;
import com.company.shop.service.CardService;
import com.company.shop.service.MsgService;
import com.company.shop.service.ScoreService;
import com.company.shop.service.MemberService;

/**
 * 类描述
 * @author ht
 * @create_time 2014-7-15 下午5:13:13
 * @project companyWeb
 */

@Controller
@RequestMapping("/user")
public class MemberController extends BaseController{

  @Autowired
  MemberService memberService;
  @Autowired
  ScoreService scoreService;
  @Autowired
  CardService cardService;
  @Autowired
  MsgService msgService;
  
  //更新数据跳转
  @RequestMapping("/goto.do")
  public String gotoPage(ModelMap modelMap, int memberId, String type) throws Exception {
    Member member = memberService.findMember(memberId);
    modelMap.put("member", member);
    if("1".contains(type)){// "1" 更新member 资料
      return "/user/updateInfo";
    } else if("2".contains(type)){// "2" 更新 passwd 
      return "/user/updataPasswd";
    }
      return "/user/updateInfo";
  }
  
  @RequestMapping("/gotoval.do")
  public String gotovalidate(ModelMap modelMap,String type) throws Exception{
    if("3".contains(type)){
      return "/user/validate";
    } else if("4".contains(type)){
      Application application = new Application();
      modelMap.put("application", application);
      return "/application/addAppl";
    } else if("5".contains(type)){
      return "/test";
    }
    return "/login";
  }
  
  @RequestMapping("/validate.do")
  public String validate(ModelMap modelMap, String tel,  String memberNum) throws Exception{
    if(1 == this.memberService.validate(tel, memberNum)){
      modelMap.put("msg", "密码已更新，请重新登陆");
      return "/login";
    }
    modelMap.put("msg", "用户不存在或者电话号码错误");
    return "/user/validate";
  }
  
  //member登陆  
  @RequestMapping(value="/login.do", method=RequestMethod.POST)
  public String Login(HttpServletRequest request, ModelMap modelMap, String userName, String passwd) throws Exception {
    try {
      if(userName != null && passwd != null){
        Member member = memberService.quUser(userName, passwd);
        if(passwd.equals(member.getPwd())){
          HttpSession session = request.getSession();
          session.setAttribute("memberId", member.getMemberId());
          modelMap.put("member", member);
          return "/user/updateInfo";
        }
        return "/login";
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    modelMap.put("msg", "密码错误！");
    return "/login";
  }
  

  //member 修改资料updateInfo.do
  @RequestMapping(value="/updateInfo.do", method=RequestMethod.POST)
  public String updateInfo(HttpServletRequest request,ModelMap modelMap,Member member) throws Exception {
   int i = memberService.updateInfo(member);
    if(1 == i){
      modelMap.addAttribute("msg", "更新成功");
      modelMap.put("member", this.memberService.findMember(member.getMemberId()));
    } else {
      modelMap.addAttribute("msg", "更新失败");
    }
    return "/user/updateInfo";
  }
  

  //member 管理礼券
  @RequestMapping("/mangGift.do")
  public String mangGift(ModelMap modelMap,Integer memberId, Page<Card> page, Integer state) throws Exception {
    memberId = page.optParamInt("memberId", memberId);
    state = page.optParamInt("state", state);
    String keyword = page.optParamStr("keyword", null);
    if(null != keyword){
      keyword = URLDecoder.decode(keyword, "utf-8");
      page.addParam("keyword", keyword);
    }
      page.setResults(this.cardService.queryCard(page));
      modelMap.put("memberId", memberId);
      modelMap.put("page", page);
      if(1 == state){
        modelMap.put("state", 1);
      } else {
        modelMap.put("state", 2);
      }
      return "/user/myGift";
  }
  
  
  //member 积分查询
  @RequestMapping("/queryScore.do")
  public String queryScore(ModelMap modelMap, Integer memberId, Integer type, Page<ScoreLog> page) throws Exception {
    memberId = page.optParamInt("memberId", memberId);
    String keyword = page.optParamStr("keyword", null);
    if(null != keyword){
      keyword = URLDecoder.decode(keyword, "utf-8");
      page.addParam("keyword", keyword);
    }
    page.setResults(this.scoreService.queryListScorelog(page, type));
    Member member = this.memberService.findMember(memberId);
    if(member.getMemberId() != null ){
      modelMap.put("page", page);
      modelMap.put("scoreBalance", member.getScoreBalance());
      modelMap.put("memberId", memberId);
      modelMap.put("type", type);
      return "/user/myScore";
    }
    modelMap.put("member", this.memberService.findMember(memberId));
    return "/user/userindex";
  }
  
  
  //member 查看站内信
  @RequestMapping("/myMessageList.do")
  public String myMessageList(ModelMap modelMap,Integer memberId, Page<Messagesend> page) throws Exception {
    memberId = page.optParamInt("memberId", memberId);
    String keyword = page.optParamStr("keyword", null);
    if(null != keyword){
      keyword = URLDecoder.decode(keyword, "utf-8");
      page.addParam("keyword", keyword);
    }
    page.setResults(this.msgService.queryAllMsg(page));
    modelMap.put("page", page);
    modelMap.put("memberId", memberId);
    return "/user/myMessage";
  }
  
  
  //member 修改密码
  @RequestMapping(value="/updatePasswd.do", method=RequestMethod.POST)
  public String updatePasswd(ModelMap modelMap, Integer memberId,
      String passwdOld, @RequestParam(value="passwd", required=false)String passwd
      ) throws Exception {
    String msg="";
    Member member = memberService.findMember(memberId);
    if(!passwdOld.equals(member.getPwd())){
      msg="密码错误";
    } else {
      member.setPwd(passwd);
      if(memberService.updatePasswd(member)){
        msg="更新密码成功";
      } else {
        msg="更新密码出错";
      }
    }
    modelMap.put("member", member);
    modelMap.addAttribute("msg", msg);
    return "/user/updataPasswd";
  }
  
}
