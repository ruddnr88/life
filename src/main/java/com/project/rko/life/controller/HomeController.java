package com.project.rko.life.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
	@Autowired
	@RequestMapping("/usr/home/main")
	public String showMain() {
	return "/home/main";
	}
	
	@RequestMapping("/usr/home/main2")
	public String showMain3() {
	return "/home/main2";
	}
	@RequestMapping("/")
	public String showMain2() {
	return "/home/main";
	}
}
