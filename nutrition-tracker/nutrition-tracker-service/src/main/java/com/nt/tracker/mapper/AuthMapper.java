package com.nt.tracker.mapper;

import com.nt.tracker.domain.dto.User;
import com.nt.tracker.domain.dto.UserDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AuthMapper {

    /**
    添加新用户
     */
    @Insert("insert into user (username, password, create_time) values (#{username}, #{password}, now())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addUser(User user);

    /**
    根据用户名和密码查询员工
     */
    @Select("select * from user where username = #{username} and password = #{password}")
    User getUserByNameAndPassword(UserDTO user);

}
