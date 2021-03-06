package com.company.shop.dao;

import com.company.shop.entity.Member;

public interface MemberMapper {
    int deleteByPrimaryKey(Integer memberId);

    int insert(Member record);

    int insertSelective(Member record);

    Member selectByPrimaryKey(Integer memberId);

    //part
    int updateByPrimaryKeySelective(Member record);

    //member update info All
    int updateByPrimaryKey(Member record);
    
//member login
    Member selectMember(String memberNum);
    
}