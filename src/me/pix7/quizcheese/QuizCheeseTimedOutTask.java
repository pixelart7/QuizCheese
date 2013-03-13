package me.pix7.quizcheese;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class QuizCheeseTimedOutTask extends BukkitRunnable{

	private final JavaPlugin pl;
	
	public QuizCheeseTimedOutTask(JavaPlugin plugin){
		
		this.pl = plugin;
		
	}
	
	public void run(){
		
		String quiz = ChatColor.DARK_RED+"(Quiz) "+ChatColor.WHITE;
		
		String currentQuizOwner = pl.getConfig().getString("currentQuizOwner");
		pl.getConfig().set("questions."+currentQuizOwner+".question", null);
		pl.getConfig().set("currentQuizOwner", null);
		pl.getConfig().set("isWaitingAnswer", false);
		Bukkit.getServer().broadcastMessage(quiz+"No one answer it in time!");
		
	}
	
}
