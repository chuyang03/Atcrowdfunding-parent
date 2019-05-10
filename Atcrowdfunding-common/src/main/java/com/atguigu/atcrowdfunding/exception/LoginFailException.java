package com.atguigu.atcrowdfunding.exception;

public class LoginFailException extends RuntimeException {

    //自定义异常只需要实现RuntimeException，并构造一个带参数的构造器，参数就是显示的信息
    public LoginFailException(String message){

        super(message);
    }
}
