package me.pix7.quizcheese;

import org.bukkit.plugin.java.JavaPlugin;

public class QuizCheese extends JavaPlugin{
	
	public void onEnable(){
		
		this.saveDefaultConfig();
		this.getConfig();
		getCommand("createquiz").setExecutor(new QuizCheeseCommandExecutor(this));
		getCommand("setanswer").setExecutor(new QuizCheeseCommandExecutor(this));
		getCommand("aq").setExecutor(new QuizCheeseCommandExecutor(this));
		getCommand("answerquiz").setExecutor(new QuizCheeseCommandExecutor(this));
		getCommand("cancelquiz").setExecutor(new QuizCheeseCommandExecutor(this));
		this.getConfig().set("isWaitingAnswer", false);
		
	}
	
	public void onDisable(){
		
		
		
	}
	
}
