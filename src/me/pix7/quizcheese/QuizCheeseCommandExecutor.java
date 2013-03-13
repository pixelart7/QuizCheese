package me.pix7.quizcheese;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
			
			for(Player player : Bukkit.getOnlinePlayers()){
	            if(player.hasPermission("quizcheese.create")){
	                player.sendMessage(quizadmin+sender.getName()+" answered: "+playerAnswer);
	            }
	        }
			//Bukkit.getServer().broadcast(quizadmin+sender.getName()+" answered: "+playerAnswer , "QuizCheese.create");//this line didn't work!
			if(debug)Bukkit.getServer().broadcastMessage("Ans:"+playerAnswer+",RealAns:"+currentAnswer+";");
			
			if(playerAnswer.equalsIgnoreCase(currentAnswer)){
			
				pl.getConfig().set("currentQuizOwner", null);
				pl.getConfig().set("isWaitingAnswer", false);
				Bukkit.getServer().broadcastMessage(quiz+sender.getName()+" answered the question correctly!");
				Bukkit.getServer().broadcastMessage(quiz+"The answer was: "+currentAnswer);
			
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
				int timedoutSeconds = pl.getConfig().getInt("timed_out");
				int timedoutTicks = timedoutSeconds * 20;
				new QuizCheeseTimedOutTask(pl).runTaskLater(pl, timedoutTicks);
				
			}
			
			return true;
			
		}else if(cmd.getName().equalsIgnoreCase("cancelquiz")){
			
			String currentQuizOwner = pl.getConfig().getString("currentQuizOwner");
			pl.getConfig().set("questions."+currentQuizOwner+".question", null);
			pl.getConfig().set("currentQuizOwner", null);
			pl.getConfig().set("isWaitingAnswer", false);
			Bukkit.getServer().broadcastMessage(quiz+"Current quiz got cancelled.");
			
			return true;
			
		}
		
		return false;
		
	}

}
