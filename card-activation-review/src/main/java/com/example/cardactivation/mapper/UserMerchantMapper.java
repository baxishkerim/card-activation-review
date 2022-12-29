package com.example.cardactivation.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMerchantMapper {



    @Select("select * from user_merchants where user_id = #{userId} and merchant_id = #{merchantId}")
    Long findMerchantForUser(Long userId, Long merchantId);





}
