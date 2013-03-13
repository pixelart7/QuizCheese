package me.pix7.quizcheese;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class QuizCheeseCommandExecutor implements CommandExecutor{
	
	QuizCheese pl;
	
	public QuizCheeseCommandExecutor(QuizCheese plugin) {
		
		this.pl = plugin;
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		String quizadmin = ChatColor.DARK_RED+"(QuizAdmin) "+ChatColor.WHITE;
		String quiz = ChatColor.DARK_RED+"(Quiz) "+ChatColor.WHITE;
		Boolean debug = false;
		
		if(cmd.getName().equalsIgnoreCase("aq") || cmd.getName().equalsIgnoreCase("answerquiz")){
			
			StringBuilder strfinal = new StringBuilder(); 
			for(String str : args) {
				strfinal.append(str);
				strfinal.append(" ");
			}
			if(strfinal.length() == 0)return false;
			strfinal.deleteCharAt(strfinal.length()-1);
			String playerAnswer = strfinal.toString();
			
			String currentQuizOwner = pl.getConfig().getString("currentQuizOwner");
			String currentAnswer = pl.getConfig().getString("questions."+currentQuizOwner+".answer");
			
			Boolean isWaitingAnswer = pl.getConfig().getBoolean("isWaitingAnswer");
			if(!(isWaitingAnswer)){
				sender.sendMessage(quiz+"No quiz is running this time or someone answered it already.");
				return true;
			}
			
			for(Player player : Bukkit.getOnlinePlayers()){
	            if(player.hasPermission("quizcheese.create")){
	                player.sendMessage(quizadmin+sender.getName()+" answered: "+playerAnswer);
	            }
	        }
			
			if(debug)Bukkit.getServer().broadcastMessage("Ans:"+playerAnswer+",RealAns:"+currentAnswer+";");
			
			if(playerAnswer.equalsIgnoreCase(currentAnswer)){
			
				pl.getConfig().set("currentQuizOwner", null);
				pl.getConfig().set("isWaitingAnswer", false);
				Bukkit.getServer().broadcastMessage(quiz+sender.getName()+" answered the question correctly!");
				Bukkit.getServer().broadcastMessage(quiz+"The answer was: "+currentAnswer);
				int currentTaskId = pl.getConfig().getInt("currentTaskId");
				Bukkit.getServer().getScheduler().cancelTask(currentTaskId);
			
			}else{
				
				sender.sendMessage(quiz+"Wrong!");			
				
			}
			
			return true;
			
		}else if(cmd.getName().equalsIgnoreCase("createquiz")){
			
			StringBuilder strfinal = new StringBuilder(); 
			for(String str : args) {
				strfinal.append(str);
				strfinal.append(" ");
			}
			if(strfinal.length() == 0)return false;
			strfinal.deleteCharAt(strfinal.length()-1);
			String question = strfinal.toString();
			
			pl.getConfig().set("questions."+sender.getName()+".question", question);
			sender.sendMessage(quizadmin+"Use /setanswer to set answer and it will broadcast!");
			
			return true;
			
		}else if(cmd.getName().equalsIgnoreCase("setanswer")){
			
			String question = pl.getConfig().getString("questions."+sender.getName()+".question");
			
			if(question == null){
				sender.sendMessage(quizadmin+"Use /createquiz <question> before set answer.");
			}else{
				
				Boolean isWaitingAnswer = pl.getConfig().getBoolean("isWaitingAnswer");
				if(isWaitingAnswer == true){
					
					sender.sendMessage(quizadmin+"Someone is quizing! Please wait until someone answer it and use this command again.");
					return true;
					
				}
				
				StringBuilder strfinal = new StringBuilder(); 
				for(String str : args) {
					strfinal.append(str);
					strfinal.append(" ");
				}
				if(strfinal.length() == 0)return false;
				strfinal.deleteCharAt(strfinal.length()-1);
				String setanswer = strfinal.toString();
				
				pl.getConfig().set("questions."+sender.getName()+".answer", setanswer);
				pl.getConfig().set("currentQuizOwner", sender.getName());
				pl.getConfig().set("isWaitingAnswer", true);
				Bukkit.getServer().broadcastMessage(quiz+ChatColor.AQUA+sender.getName()+" asks: "+ChatColor.WHITE+question);
				Bukkit.getServer().broadcastMessage(quiz+ChatColor.WHITE+"Use '/aq <answer>' to answer this quiz.");
				int timedoutSeconds = pl.getConfig().getInt("timed_out");
				int timedoutTicks = timedoutSeconds * 20;
				BukkitTask task = new QuizCheeseTimedOutTask(pl).runTaskLater(pl, timedoutTicks);
				if(debug)Bukkit.getServer().broadcastMessage("TaskId:"+task.getTaskId());
				pl.getConfig().set("currentTaskId",task.getTaskId());
				
			}
			
			return true;
			
		}else if(cmd.getName().equalsIgnoreCase("cancelquiz")){
			
			String currentQuizOwner = pl.getConfig().getString("currentQuizOwner");
			pl.getConfig().set("questions."+currentQuizOwner+".question", null);
			pl.getConfig().set("currentQuizOwner", null);
			pl.getConfig().set("isWaitingAnswer", false);
			int currentTaskId = pl.getConfig().getInt("currentTaskId");
			Bukkit.getServer().getScheduler().cancelTask(currentTaskId);
			Bukkit.getServer().broadcastMessage(quiz+"Current quiz got cancelled.");
			
			return true;
			
		}else if(cmd.getName().equalsIgnoreCase("quizcheese")){
			
			sender.sendMessage(quizadmin+"/createquiz <question> - to create quiz with question.");
			sender.sendMessage(quizadmin+"/setanswer <answer> - to set answer for your question.");
			sender.sendMessage(quizadmin+"/cancelquiz - to cancel current quiz.");
			sender.sendMessage(quizadmin+"/repeatquiz - to repeat current quiz.");
			
		}else if(cmd.getName().equalsIgnoreCase("repeatquiz")){
			
			Boolean isWaitingAnswer = pl.getConfig().getBoolean("isWaitingAnswer");
			
			if(!(isWaitingAnswer)){
				
				sender.sendMessage(quiz+"No quiz is running at this moment.");
				return true;
				
			}else{
			
				String currentQuizOwner = pl.getConfig().getString("currentQuizOwner");
				String question = pl.getConfig().getString("questions."+currentQuizOwner+".question");
				Bukkit.getServer().broadcastMessage(quiz+ChatColor.AQUA+sender.getName()+" asks(repeat): "+ChatColor.WHITE+question);
				
				return true;
				
			}
			
		}
		
		return false;
		
	}

}
