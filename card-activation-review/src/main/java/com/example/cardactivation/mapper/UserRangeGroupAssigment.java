package com.example.cardactivation.mapper;


import com.example.cardactivation.entity.RangeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserRangeGroupAssigment {




    @Select("select  RANGE_GROUP_ID from USER_RANGE_GROUP_ASSIGMENT where user_id = #{userId}")
    Long findRangeGroupId(Long userId);




    @Select(" select R.* from EBWS.RANGE_ASSIGNMENT RA " +
            "inner join RANGE R on R.ID = RA.RANGE_ID " +
            "inner join RANGE_GROUP RG on RG.ID = RA.RANGE_GROUP_ID " +
            "where RA.RANGE_GROUP_ID = #{rangeGroupId} " +
            "and RG.IS_ACTIVE = 1 " +
            "and R.IS_ACTIVE = 1")
    List<RangeEntity> findActiveRangesByRangeGroupId(Long rangeGroupId);

}


