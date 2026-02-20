package com.poker.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.poker.common.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(String username);
}
