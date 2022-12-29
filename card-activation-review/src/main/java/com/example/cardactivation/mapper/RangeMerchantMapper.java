package com.example.cardactivation.mapper;



import com.example.cardactivation.entity.RangeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RangeMerchantMapper {




    @Select("select R.\"from\", R.\"to\"\n" +
            "from RANGE R\n" +
            "         inner join RANGE_ASSIGNMENT RA ON RA.RANGE_ID = R.ID\n" +
            "         inner join RANGE_GROUP RG on RG.ID = RA.RANGE_GROUP_ID\n" +
            "where RG.ID = #{id} and R.IS_ACTIVE = 1")
     List<RangeEntity> findActiveRangeList(Long id);


}
