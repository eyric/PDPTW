package cn.edu.bupt.pdptw.model;

import lombok.ToString;

@ToString
public enum RequestType {
	PICKUP("pickup"),
	DELIVERY("delivery");
	
	private final String name;
	
	RequestType(String name) {
		this.name = name;
	}
}
