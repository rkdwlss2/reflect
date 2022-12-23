package com.cos.reflect.controller;

import com.cos.reflect.ano.RequestMapping;
import com.cos.reflect.controller.dto.JoinDto;
import com.cos.reflect.controller.dto.LoginDto;
import com.cos.reflect.model.User;

public class UserController {

	@RequestMapping("/user/join")
	public String join(JoinDto dto) {
		System.out.println("join() function called");
		System.out.println(dto);
		return "/";
	}
	@RequestMapping("/user/login")
	public String login(LoginDto dto) {
		System.out.println("login() function called");
		System.out.println(dto);
		return "/";
	}
	
	@RequestMapping("/user/list")
	public String list(User user) {
		System.out.println("list() function called");
		System.out.println(user);
		return "/";
	}
	
	@RequestMapping("/user")
	public String user() {
		System.out.println("user() function called");
		return "/";
	}
	
	@RequestMapping("/hello")
	public String hello() {
		System.out.println("hello() function called");
		return "/";
	}
}
