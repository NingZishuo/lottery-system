package com.lottery.infrastructure.persistent.dao;


import com.lottery.infrastructure.persistent.po.Award;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 奖品表 DAO
 */
@Mapper
public interface IAwardDao {

   List<Award> queryAwardList();

   /**
    * 综合查询
    * @param award
    * @return
    */
   Award queryAward(Award award);
}

