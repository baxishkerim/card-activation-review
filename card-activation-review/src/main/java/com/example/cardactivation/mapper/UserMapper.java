package com.example.cardactivation.mapper;

import com.example.cardactivation.entity.UsersEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {



    @Select("select * from users where username = #{username}")
    UsersEntity getUserByUsername(String username);
}
