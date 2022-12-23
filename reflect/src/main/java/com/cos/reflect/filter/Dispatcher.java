package com.cos.reflect.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cos.reflect.controller.UserController;
import com.cos.reflect.controller.dto.LoginDto;
import com.cos.reflect.ano.RequestMapping;

// 분기 시키기 router의 역
public class Dispatcher implements Filter{

	private boolean isMatching = false;
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
//		System.out.println("디스패쳐 진입 ");
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;
		
//		System.out.println("contextPath : "+ request.getContextPath()); // 프로젝트 시작주소
//		System.out.println("식별자 주소 : "+request.getRequestURI()); // 끝주소
//		System.out.println("전체주소 : "+request.getRequestURL()); // 전체주소
		
		// /user 남기기
		String endPoint =request.getRequestURI().replaceAll(request.getContextPath(), "");
		System.out.println("엔드포인트:"+endPoint);
		
		UserController userController = new UserController();
//		if (endPoint.equals("/join")) {
//			userController.join();
//		}else if (endPoint.equals("/login")) {
//			userController.login();
//		}else if (endPoint.equals("/user")) {
//			userController.user();
//		}
		
//		System.out.println(LoginDto.class);
		
		Method[] methods = userController.getClass().getDeclaredMethods();
		
		for (Method method : methods) { // 4바퀴 (join,login,user,hello
			Annotation annotation = method.getDeclaredAnnotation(RequestMapping.class);
			RequestMapping requestMapping = (RequestMapping) annotation;
//			System.out.println(requestMapping.value());
			
			if (requestMapping.value().equals(endPoint)) {
				isMatching = true;
				try {
					
					Parameter[] params = method.getParameters();
					String path = null;
					if (params.length != 0) {

//						System.out.println("params[0].getType():"+params[0].getType());
						
						// 해당 오브젝트를 리플렉션 하면 됨 (username,password)
						Object dtoInstance = params[0].getType().newInstance(); 
//						String username = request.getParameter("username");
//						String password = request.getParameter("password");
//						System.out.println("username : "+username);
//						System.out.println("password : "+password);
						
						setData(dtoInstance,request);
						// keys 값을 변형해서 setUsername으로 치환
						path = (String) method.invoke(userController, dtoInstance);
						
					}else {
						path = (String)method.invoke(userController);
					}
					
					RequestDispatcher dis = request.getRequestDispatcher(path); // dispatcher는 filter를 다시 안탐
					dis.forward(request, response);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
		if (isMatching == false) {

				
				response.setContentType("text/html; charset=utf-8");
				PrintWriter out =response.getWriter();
				out.println("잘못된 주소 요청입니다. 404 에");
				out.flush();
			
		}
	}

	private <T> void setData(T instance,HttpServletRequest request) {
		Enumeration<String> keys = request.getParameterNames(); // 크기 :2 username,password
		while(keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String methodKey = keyToMethodKey(key);
			
			Method[] methods = instance.getClass().getDeclaredMethods(); // 5
			
			for (Method method:methods){
				if (method.getName().equals(methodKey)) {
					try {
						method.invoke(instance, request.getParameterValues(key));
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}
	}
	
	private String keyToMethodKey(String key) {
		String firstKey = "set";
		String upperKey = key.substring(0,1).toUpperCase();
		String remainKey = key.substring(1);
	    return firstKey + upperKey+remainKey;
	}
}
