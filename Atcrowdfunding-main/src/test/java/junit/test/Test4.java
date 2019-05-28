package junit.test;

import com.atguigu.atcrowdfunding.bean.User;
import com.atguigu.atcrowdfunding.manager.service.UserService;
import com.atguigu.atcrowdfunding.util.MD5Util;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test4 {

    public static void main(String[] args) {
        ApplicationContext ioc = new ClassPathXmlApplicationContext("spring/spring*.xml");

        UserService userService = ioc.getBean(UserService.class);


        for (int i = 1; i <= 100; i++) {
            User user = new User();
            user.setLoginacct("test"+i);
            user.setUsername("test"+1);
            user.setUserpswd(MD5Util.digest("123"));
            user.setEmail("test"+i+"@163.com");
            user.setCreatetime("2019-05-23 21:31:30");

            userService.saveUser(user);
        }
    }
}
