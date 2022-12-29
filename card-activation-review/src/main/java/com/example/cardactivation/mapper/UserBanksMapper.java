package com.example.cardactivation.mapper;


import com.example.cardactivation.entity.BanksEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserBanksMapper {


    ///нашел банк usera
    @Select("select bank_id from user_banks where user_id = #{userId}")
    Long findBankIdByUserId(Long userId);

    ///Вытянул данные банка
    @Select("select * from banks where id = #{bank_id}")
    BanksEntity getBankList(Long bankId);
}
