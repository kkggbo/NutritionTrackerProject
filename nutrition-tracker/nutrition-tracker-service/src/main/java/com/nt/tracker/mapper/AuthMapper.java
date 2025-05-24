package com.nt.tracker.mapper;

import com.nt.tracker.domain.dto.UserDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AuthMapper {

    /*
    添加新用户
     */
    @Insert("insert into user (username, password, create_time) values (#{username}, #{password}, now())")
    void addUser(String username, String password);

    /*
    根据用户名和密码查询员工
     */
    @Select("select * from user where username = #{username} and password = #{password}")
    UserDTO getUserByNameAndPassword(UserDTO user);

}
