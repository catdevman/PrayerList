package com.lucaspearson.prayerlist;

public class Prayer {
	private int id;
	private String name;
	private String description;
	private int priority;
	private String category;

	public int getId() {
		return this.id;
	}
	public void setId(int id){
		this.id = id;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name){
		this.name = name;
	}
	
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description){
		this.description = description;
	}
	public int getPriority() {
		return this.priority;
	}
	public void setPriority(int priority){
		this.priority = priority;
	}
	
	public String getCategory() {
		return this.category;
	}
	public void setCategory(String category){
		this.category = category;
	}
	
	@Override
	public String toString(){
		return this.name+"\n"+this.category;
	}
	
}
