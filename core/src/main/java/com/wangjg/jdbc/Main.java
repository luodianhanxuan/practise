package com.wangjg.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.sql.*;

@Slf4j
public class Main {

    @Test
    public void testQuery() throws SQLException, ClassNotFoundException {
        // 1.加载驱动
//        Class.forName("com.mysql.cj.jdbc.Driver");
        Class.forName("net.sf.log4jdbc.sql.jdbcapi.DriverSpy");
        // 2 创建和数据库之间的连接
        String username = "root";
        String password = "MyRoot";
//        String url = "jdbc:mysql://localhost:30306/employees?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai";
        String url = "jdbc:log4jdbc:mysql://localhost:30306/employees?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai";
        Connection conn = DriverManager.getConnection(url, username, password);
        // 3.准备发送SQL
        String sql = "select * from departments";
        PreparedStatement pstm = conn.prepareStatement(sql);
        // 4.执行SQL，接收结果集
        ResultSet rs = pstm.executeQuery();
        // 5 处理结果集
        while (rs.next()) {
            /*
               rs.getXxx(列顺序从1开始) 或者 rs.getXxx("列名") 获取指定列的数据，Xxx为数据类型
               实战中多使用列名，可读性强
             */
            /*
            int personId2 = rs.getInt(1);
            String personName2 = rs.getString(2);
            int age2 = rs.getInt(3);
            String sex2 = rs.getString(4);
            String mobile2 = rs.getString(5);
            String address2 = rs.getString(6);
            System.out.println("personId="+personId2+",personName="+personName2
                    +",age="+age2+",sex="+sex2+",mobile="+mobile2+",address="+address2);
            */

            String deptNo = rs.getString("dept_no");
            String deptName = rs.getString("dept_name");

//            log.info("deptNo {} ,  deptName {}", deptNo, deptName);
        }
        // 6.释放资源
        rs.close();
        pstm.close();
        conn.close();
    }

}