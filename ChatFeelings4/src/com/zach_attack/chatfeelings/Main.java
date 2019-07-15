package com.zach_attack.chatfeelings;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import com.zach_attack.chatfeelings.other.Metrics;
import com.zach_attack.chatfeelings.other.Updater;
import com.earth2me.essentials.Essentials;
import com.zach_attack.chatfeelings.Msgs;

public class Main extends JavaPlugin implements Listener {
	  
	public static boolean outdatedplugin = false;
	public static String outdatedpluginversion = "0";
	
	HashMap<Player, Long> cooldown = new HashMap<Player, Long>();
	HashMap<Player, String> ignorecooldown = new HashMap<Player, String>();
	
	public ArrayList<String> disabledsendingworlds = (ArrayList<String>) getConfig().getStringList("General.Disable-Sending-Worlds");
	public ArrayList<String> disabledreceivingworlds = (ArrayList<String>) getConfig().getStringList("General.Disable-Receiving-Worlds");
	
	public void removeAll(Player p) {
		cooldown.remove(p);
		ignorecooldown.remove(p);
	}
	
	public void onDisable() {
		   disabledsendingworlds.clear();
		   disabledreceivingworlds.clear();
		   
        if(Bukkit.getOnlinePlayers().size() > 0) {
	    for(Player online : Bukkit.getServer().getOnlinePlayers())
	      {
	    	removeAll(online.getPlayer());
	      }}
	}
	
	public void purgeOldFiles() {
	  Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
		boolean debug = getConfig().getBoolean("Other.Debug");
		
		if(!getConfig().getBoolean("Other.Player-Files.Cleanup"))  {
			if(debug) {
				getLogger().info("[Debug] Purging disabled 'Cleanup' was set to False.");
			}
			return;
		}
		
		File folder = new File(this.getDataFolder(), File.separator + "Data");
		int maxDays = getConfig().getInt("Other.Player-Files.Cleanup-After-Days");
		
		for(File cachefile:folder.listFiles())
        {
			
			File f = new File(cachefile.getPath());
			FileConfiguration setcache = YamlConfiguration.loadConfiguration(f);
		
			long daysAgo = Math.abs(((setcache.getLong("Last-On"))/86400000) - (System.currentTimeMillis()/86400000));
			
			String playername = setcache.getString("Username");
			
			if(debug) {
				getLogger().info("[Debug] File for " + playername + " is " + daysAgo + " days old.");
			}
			
			if(daysAgo >= maxDays) {
				f.delete();
				if(debug) {
					getLogger().info("[Debug] Deleted " + playername + "'s data file because it's " + daysAgo + "s old. (Maximum of " + maxDays + " Days)");
				}
			} else {
				if(debug) {
					getLogger().info("[Debug] Keeping " + playername + "'s data file. (" + daysAgo + "/" + maxDays + " days left)");
				}
			}
        } // end of For loop
		
	  }); // End of Async;
	}
	
	  public void addMetric() {
		  double version = Double.parseDouble(System.getProperty("java.specification.version"));
		  if(version < 1.8) {
			  getLogger().warning("Java " + version + " detected. ChatFeelings requires Java 1.8 (8) or higher to fully function.");
			  getLogger().info("TIP: Use version v2.0.1 or below for Java " + version + "support.");
			  return;
		  } 
		  
		  if(getConfig().getBoolean("Other.Metrics")) {
		    Metrics metrics = new Metrics(this);
	        metrics.addCustomChart(new Metrics.SimplePie("server_version", () -> {
	            try {
	                Class.forName("com.destroystokyo.paper.PaperConfig");
	                return "Paper";
	            }
	            catch (Exception NotPaper) {
	                try {
	                    Class.forName("org.spigotmc.SpigotConfig");
	                    return "Spigot";
	                }
	                catch (Exception Other) {
	                    return "Bukkit / Other";
	                }
	            }
	        }));
	        
	        metrics.addCustomChart(new Metrics.SimplePie("update_notifications", () -> {
	            if(getConfig().getBoolean("Other.Updates.Check")) {
	                return "Enabled";
	            } else {
	            	return "Disabled";
	            }
	        }));
	        
	        metrics.addCustomChart(new Metrics.SimplePie("bypass_version", () -> {
	            if(getConfig().getBoolean("Other.Bypass-Version-Block")) {
	                return "Yes";
	            } else {
	            	return "No";
	            }
	        }));
	        
		  }} // End Metrics
	  
	    public void pop(CommandSender sender) {
	    	if(sender instanceof Player) {
	        Player p = (Player)sender;
	        if (!Bukkit.getBukkitVersion().contains("1.8")) {
	            p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 2.0f, 2.0f);
	        } else {
	            p.playSound(p.getLocation(), Sound.valueOf((String)"CHICKEN_EGG_POP"), 2.0f, 2.0f);
	        }}
	    }
	    
	    public void bass(CommandSender sender) {
	    	if(sender instanceof Player) {
		        Player p = (Player)sender;
	        if (!Bukkit.getBukkitVersion().contains("1.8")) {
	            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 2.0f, 1.3f);
	        } else {
	            p.playSound(p.getLocation(), Sound.valueOf((String)"NOTE_BASS"), 2.0f, 1.3f);
	        }}
	    }
	    
	    public void levelup(CommandSender sender) {
	    	if(sender instanceof Player) {
		        Player p = (Player)sender;
	        if (!Bukkit.getBukkitVersion().contains("1.8")) {
	            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2.0f, 2.0f);
	        } else {
	            p.playSound(p.getLocation(), Sound.valueOf((String)"LEVEL_UP"), 2.0f, 2.0f);
	        }}
	    }
	  
	    public void updateLastOn(Player p) {
	     Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
			File cache = new File(this.getDataFolder(), File.separator + "Data");
			File f = new File(cache, File.separator + "" + p.getUniqueId().toString() + ".yml");
			FileConfiguration setcache = YamlConfiguration.loadConfiguration(f);
			  
			  if(!f.exists()) {
				  try {
				  setcache.save(f);
				  }catch(Exception err) {}
			  }
			  
			  String UUID = p.getUniqueId().toString();
			  if(!setcache.contains(UUID)) {
			  setcache.set("UUID", UUID);
			  setcache.set("Allow-Feelings", true);
			  }
			  
			  setcache.set("Username", p.getName().toString());
			  setcache.set("Last-On", System.currentTimeMillis());
			  try {
				setcache.save(f);
			} catch (Exception err) {}
	     });
	    }
	    
	    public String hasPlayedNameGetUUID(String inputsearch) {
			File folder = new File(this.getDataFolder(), File.separator + "Data");
			
			for(File AllData:folder.listFiles())
          {
				File f = new File(AllData.getPath());
				FileConfiguration setcache = YamlConfiguration.loadConfiguration(f);
				
				String playername = setcache.getString("Username");
				String UUID = setcache.getString("UUID");
				
					if(inputsearch.equalsIgnoreCase(playername)) {
							return UUID;
						}
          }
			// No Match Found
			return "0";
	    }
	    
	    public boolean isTargetIgnoringSender(Player target, Player sender) {
	    	
	    	return false;
	    }
	    
	    public String hasPlayedUUIDGetName(String inputsearch) {
			File folder = new File(this.getDataFolder(), File.separator + "Data");
			
			for(File AllData:folder.listFiles())
          {
				File f = new File(AllData.getPath());
				FileConfiguration setcache = YamlConfiguration.loadConfiguration(f);
				
				String playername = setcache.getString("Username");
				String UUID = setcache.getString("UUID");
				
				if(inputsearch.equalsIgnoreCase(UUID)) {
					return playername;
				}
          }
			// No Match Found
			return "0";
	    }
	    
	  @Override
	  public void onEnable() 
	  {		  
		  if(!Bukkit.getBukkitVersion().contains("1.14") && !Bukkit.getBukkitVersion().contains("1.13")) {
			  if(!getConfig().getBoolean("Other.Bypass-Version-Block")) {
			  getConfig().options().copyDefaults(true);
			  saveConfig();
			  
			  getLogger().info("---------------------------------------------------");
			  getLogger().info("This version of ChatFeelings is only compatible with: 1.14 & 1.13");
			  getLogger().info("Use a different version of ChatFeelings to add support for this version.");
			  getLogger().info(" ");
			  getLogger().info("If you are willing to manually update your emotes.yml sound variables and wish to");
			  getLogger().info("continue using " + Bukkit.getBukkitVersion().replace("-SNAPSHOT", "") + " with this version of ChatFeelings, you may set");
			  getLogger().info("'Bypass-Version-Block' in your config to 'true' & restart your server.");
			  getLogger().info("");
			  getLogger().info("By setting this config variable to true, you agree & understand you");
			  getLogger().info("will recieve NO SUPPORT for this version in this environment.");
			  getLogger().info("---------------------------------------------------");
			  this.getPluginLoader().disablePlugin(this);
			  return;
			  }
			  getLogger().info("---------------------------------------------------");
			  getLogger().info("WARNING: While ChatFeelings may work for " + Bukkit.getBukkitVersion().replace("-SNAPSHOT", "") + ", you may encouter various issues.");
			  getLogger().info("         It's important to make sure you have properly configured your emotes.yml variables!");
			  getLogger().info("         If your emotes.yml sound variables are incorrect, errors may be thrown.");
			  getLogger().info("---------------------------------------------------");
		  }
		  getConfig().options().copyDefaults(true);
		  getConfig().options().header("Thanks for downloading ChatFeelings!\nMessages for feelings can be found in the Emotes.yml, and other message in the Messages.yml.\n\nHaving trouble? Join our support discord: https://discord.gg/6ugXPfX");
		  saveConfig();	 
		  
		  boolean debug = getConfig().getBoolean("Other.Debug");
		   disabledsendingworlds.clear();
		   disabledreceivingworlds.clear();
		   disabledsendingworlds.addAll(getConfig().getStringList("General.Disabled-Sending-Worlds"));
		   disabledreceivingworlds.addAll(getConfig().getStringList("General.Disabled-Receiving-Worlds"));
		   
	  outdatedplugin = false;
	  addMetric();
	  Bukkit.getServer().getPluginManager().registerEvents(this, this);
	  
	   if(debug) {
	   getLogger().info("[Debug] Disabled Clear Worlds: " + disabledsendingworlds.toString());
	   getLogger().info("[Debug] Disabled Fortune Worlds: " + disabledreceivingworlds.toString());
	   }
	   
	  if (getConfig().getBoolean("Other.Updates.Check")) {
		    try
		    {
		    	new Updater(this).checkForUpdate();
		    }catch(Exception e) {
		    	getLogger().warning("There was an issue while trying to check for updates.");
		    }
	  } else {
		  getLogger().info("[!] Update checking has been disabled in the config.yml");
	  }
	 
	  FileSetup.enableFiles();
	  purgeOldFiles();
	  
	  int onlinecount = Bukkit.getOnlinePlayers().size();
	  if(onlinecount >= 1) {
	  for(Player online:Bukkit.getOnlinePlayers()) {
			  removeAll(online);
			  updateLastOn(online); // Generates files for players who are on during restart that didn't join normally.
		  }
	  getLogger().info("Reloaded with " + onlinecount + " players online...");
	  }
	} // [!] End of OnEnable Event
	  
	   private boolean isVanished(Player player) {
		   if(getConfig().getBoolean("Others.Vanished-Players.Check")) {
		      try {
		         if (this.getServer().getPluginManager().isPluginEnabled("Essentials") && this.getServer().getPluginManager().getPlugin("Essentials") != null) {
		            Essentials ess = (Essentials)Bukkit.getPluginManager().getPlugin("Essentials");
		            if (ess.getVanishedPlayers().contains(player.getName())) {
		               return true;
		            }

		            return false;
		         }

		         for (MetadataValue meta : player.getMetadata("vanished")) {
		             if (meta.asBoolean()) return true;
		         }

		      } catch (Exception err) {
		         this.getLogger().info("Error when checking for vanished players.");
		      }
		      
	    	  if(getConfig().getBoolean("Other.Vanished-Players.Use-Legacy")) {
	    		  if(player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
	    		  return true;
	    		  }
	    		  return false;
	    	  }

		      return false;
		   } else { // Vanish Check is Off
			   return false;
		   }
		 }
	   
	   public void noPermission(CommandSender sender) {
		   	  File folder = Bukkit.getServer().getPluginManager().getPlugin("ChatFeelings").getDataFolder();
			  File msgsfile = new File(folder, File.separator + "messages.yml");
			  FileConfiguration msg = YamlConfiguration.loadConfiguration(msgsfile);
			  Msgs.sendPrefix(sender, msg.getString("No-Permission"));
			  bass(sender);
	   }
	   

	    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args)
	    {   
	  	  File folder = Bukkit.getServer().getPluginManager().getPlugin("ChatFeelings").getDataFolder();
		  File msgsfile = new File(folder, File.separator + "messages.yml");
		  FileConfiguration msg = YamlConfiguration.loadConfiguration(msgsfile);
		  
		  File emotesfile = new File(folder, File.separator + "emotes.yml");
		  FileConfiguration emotes = YamlConfiguration.loadConfiguration(emotesfile);
		  
	      if (cmd.getName().equalsIgnoreCase("chatfeelings") && args.length == 0) {
	    	  Msgs.send(sender, "");
	    	  Msgs.send(sender, "&a&lC&r&ahat &f&lF&r&feelings");
	    	  Msgs.send(sender, "&8&l> &7/cf help &7&ofor commands & settings.");
	    	  Msgs.send(sender, "");
	    		pop(sender);
	    	 return true;
	      }
	      
	      if (cmd.getName().equalsIgnoreCase("chatfeelings") && args.length >= 1 && args[0].equalsIgnoreCase("version")) {
	    	  Msgs.send(sender, "");
	    	  Msgs.send(sender, "&a&lC&r&ahat &f&lF&r&feelings");
	    	  Msgs.send(sender, "&8&l> &7You are currently running &f&lv" + getDescription().getVersion());
	    	  Msgs.send(sender, "");
	    		pop(sender);
	    	 return true;
	      }
	      
	      if (cmd.getName().equalsIgnoreCase("chatfeelings") && args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
	    	  if(!sender.hasPermission("chatfeelings.admin") && !sender.isOp()) {
	    		  noPermission(sender);
	    		  return true;
	    	  }
	    	  
	    	  Msgs.send(sender, "");
	    	  Msgs.send(sender, "&a&lC&r&ahat &f&lF&r&feelings");
	    	  try {
	    		  reloadConfig();
	    		  
	    		   disabledsendingworlds.clear();
	    		   disabledreceivingworlds.clear();
	    		   disabledsendingworlds.addAll(getConfig().getStringList("General.Disabled-Sending-Worlds"));
	    		   disabledreceivingworlds.addAll(getConfig().getStringList("General.Disabled-Receiving-Worlds"));
	    		   
	    	  } catch (Exception err2) {
	    		  getLogger().info("Error occured when trying to reload your config: ----------");
	    		  err2.printStackTrace();
	    		  getLogger().info("-----------------------[End of Error]-----------------------");
	    		  Msgs.send(sender, "&a&lChat&f&lFeelings &8&l> &4&lError! &fSomething in your config isn't right. Check console!");  
				  bass(sender);
	    		  return true;
	    	  }
	    	  try {
	    	  Msgs.send(sender, msg.getString("Reload"));
	    	  } catch (Exception err) {
	    		  Msgs.send(sender, "&8&l> &2&l✓ &aConfiguration Reloaded.");  
	    	  }
	    	  Msgs.send(sender, "");
	           levelup(sender);
	    	 return true;
	      }
	      
	      if (cmd.getName().equalsIgnoreCase("chatfeelings") && args.length >= 1 && args[0].equalsIgnoreCase("help")) {
	    	  Msgs.send(sender, "");
	    	  Msgs.send(sender, "&a&lC&r&ahat &f&lF&r&feelings");
	    	  Msgs.send(sender, "&8&l> &e&l/cf help &7Shows you this page.");
	    	  if(sender.hasPermission("chatfeelings.ignore") || sender.isOp()) {
	    	  Msgs.send(sender, "&8&l> &e&l/cf ignore (player) &7Ignore/Unignore feelings from players.");
	    	  Msgs.send(sender, "&8&l> &e&l/cf ignore all &7Blocks/Unblocks anyone from using feelings on you.");
	    	  }
	    	  if(sender.hasPermission("chatfeelings.admin") || sender.isOp()) {
	    	  Msgs.send(sender, "&8&l> &e&l/cf version &7Shows you the plugin version.");
	    	  Msgs.send(sender, "&8&l> &e&l/cf reload &7Reloads the plugin.");
	    	  }
	    	  Msgs.send(sender, "&8&l> &6&l/feelings &7Shows a list of available feelings.");
	    	  Msgs.send(sender, "");
	    		pop(sender);
	    	 return true;
	      }
	      
	      if (cmd.getName().equalsIgnoreCase("chatfeelings") && args.length >= 1 && args[0].equalsIgnoreCase("ignore")) {
	    	  if(!sender.hasPermission("chatfeelings.ignore") && !sender.isOp()) {
	    		  noPermission(sender);
	    		  return true;
	    	  }
	    	  
	    	    if(args.length == 1) {
	    	    	Msgs.sendPrefix(sender, msg.getString("No-Player-Ignore"));
	    	    	bass(sender);
	    	    	return true;
	    	    }
	    	    
		    	  if(!(sender instanceof Player)) {
		    	    	Msgs.sendPrefix(sender, "&c&lSorry. &fOnly players can ignore other players.");
		    		  return true;
		    	  }
		    	  
		    	  if(args[1].equalsIgnoreCase("console")) {
		    		  Msgs.sendPrefix(sender, msg.getString("Console-Not-Player")); 
		    		  bass(sender);
		    		  return true;
		    	  }
		    	  
		    	  if(args[1].equalsIgnoreCase(sender.getName())) {
		    		  bass(sender);
		    		  Msgs.sendPrefix(sender, msg.getString("Cant-Ignore-Self"));
		    		  return true;
		    	  }
		    	  
		        Player p = (Player)sender;
		        
		        if(getConfig().getBoolean("Cooldowns.Ignoring.Enabled")) {
		        if(ignorecooldown.containsKey(p)) {
		    		  bass(sender);
		    		  Msgs.sendPrefix(sender, msg.getString("Ignore-Cooldown"));
		        	return true;
		        }
		        
		        ignorecooldown.put(p, p.getName());
		        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
		        {
		          public void run()
		          {
		            ignorecooldown.remove(p);
		          }
		        }, 20*getConfig().getInt("General.Cooldowns.Ignoring.Seconds"));
		      }
		        
				File cache = new File(this.getDataFolder(), File.separator + "Data");
				File f = new File(cache, File.separator + "" + p.getUniqueId().toString() + ".yml");
				FileConfiguration setcache = YamlConfiguration.loadConfiguration(f);
					
				  if(!f.exists()) {
					  try {
					  setcache.save(f);
					  }catch(Exception err) {}
				  }
				  
				  if(args[1].equalsIgnoreCase("all")) {
					  if(setcache.getBoolean("Allow-Feelings")) {
						  setcache.set("Allow-Feelings", false);
						  Msgs.sendPrefix(sender, msg.getString("Ingoring-On-All"));
					  } else {
						  setcache.set("Allow-Feelings", true);	  
						  Msgs.sendPrefix(sender, msg.getString("Ingoring-Off-All"));
					  }
					  
					  pop(sender);
					  
					  try {
						  setcache.save(f);
						  }catch(Exception err) {}
					  return true;
				  }
				  
				List<String> ignoredplayers = new ArrayList<String>();
				ignoredplayers.clear();
				ignoredplayers.addAll(setcache.getStringList("Ignoring"));
				
					String ignoreUUID = hasPlayedNameGetUUID(args[1]);
					if(ignoreUUID == "0" || ignoreUUID == null) {
						bass(sender);
						Msgs.sendPrefix(sender, msg.getString("Player-Never-Joined").replace("%player%", args[1]));
						return true;
					}
					
					try {
					if(ignoredplayers.contains(ignoreUUID)) {
						Msgs.sendPrefix(sender, msg.getString("Ingoring-Off-Player").replace("%player%", args[1]));
						
						ignoredplayers.remove(ignoreUUID);
						setcache.set("Ignoring", ignoredplayers);
						  try {
						  setcache.save(f);
						  }catch(Exception err) {}
						
			    		pop(sender);
			    		ignoredplayers.clear();
						return true;
					}
					} catch (Exception searcherr) { 
						getLogger().warning("Error trying to search for: " + args[1] + " in the Data folder." );   }
					
					ignoredplayers.add(ignoreUUID);
					setcache.set("Ignoring", ignoredplayers);
					  try {
					  setcache.save(f);
					  }catch(Exception err) {}
					Msgs.sendPrefix(sender, msg.getString("Ingoring-On-Player").replace("%player%", args[1]));
		    		pop(sender);
		    		ignoredplayers.clear();
	    	 return true;
	      }
	      
	      if (cmd.getName().equalsIgnoreCase("feelings")) {
	    	  if((args.length == 0) || (args.length >= 1 && (args[0].equalsIgnoreCase("1")||args[0].equalsIgnoreCase("0")))) {
		    	  Msgs.send(sender, "");
	          Msgs.send(sender, msg.getString("Feelings-Help") + "                        " + msg.getString("Feelings-Help-Page").replace("%page%", "1").replace("%pagemax%", "2"));
	    	  Msgs.send(sender, "&8&l> &f&l/hug (player) &7Give someone a nice warm hug!");
	    	  Msgs.send(sender, "&8&l> &f&l/slap (player) &7Slap some sense back into someone.");
	    	  Msgs.send(sender, "&8&l> &f&l/poke (player) &7Poke someone to get their attention.");
	    	  Msgs.send(sender, "&8&l> &f&l/highfive (player) &7Show your suopport, and give a highfive!");
	    	  Msgs.send(sender, "&8&l> &f&l/facepalm (player) &7Need to show some disapproval?");
	    	  Msgs.send(sender, "&8&l> &f&l/yell (player) &7Yell at someone as loud as possible!");
	    	  Msgs.send(sender, "&8&l> &f&l/bite (player) &7Bite a player right on the arm.");
	    	  Msgs.send(sender, "&8&l> &f&l/snuggle (player) &7Snuggle up with the power of warm hugs!");
	    	  Msgs.send(sender, "&8&l> &f&l/shake (player) &7Shake a player to their feet.");
	    	  Msgs.send(sender, "&8&l> &f&l/stab (player) &7Stab someone with a knife. Ouch!");
	    	  Msgs.send(sender, "&7To go to the 2nd page do &a/feelings 2");
	    	  pop(sender);
	    	  Msgs.send(sender, "");
	    	  } else
	    	  if(args.length >= 1 && args[0].equalsIgnoreCase("2")) {
		    	  Msgs.send(sender, "");
	          Msgs.send(sender, msg.getString("Feelings-Help") + "                        " + msg.getString("Feelings-Help-Page").replace("%page%", "2").replace("%pagemax%", "2"));
	    	  Msgs.send(sender, "&8&l> &f&l/kiss (player) &7Make sweet sweet love. uwu");
	    	  Msgs.send(sender, "&8&l> &f&l/punch (player) &7Punch the lights out of someone!");
	    	  Msgs.send(sender, "&8&l> &f&l/murder (player) &7Finna kill someone here.");
	    	  Msgs.send(sender, "&8&l> &f&l/boi (player) &7Living in 2016? Boi at a player.");
	    	  Msgs.send(sender, "&8&l> &f&l/cry (player) &7Real sad hours? Cry at someone.");
	    	  Msgs.send(sender, "&8&l> &f&l/dab (player) &7Freshly dab on someone.");
	    	  Msgs.send(sender, "&8&l> &f&l/lick (player) &7Lick someone like an ice-cream sundae!");
	    	  Msgs.send(sender, "&8&l> &f&l/pat (player) &7Pat a players head for being good.");
	    	  Msgs.send(sender, "&8&l> &f&l/stalk (player) &7Stalk a player carefully... carefully.");
	    	  pop(sender);
	    	  Msgs.send(sender, "");
	    	  } else {
	    		 bass(sender);
	    		 Msgs.sendPrefix(sender, msg.getString("Page-Not-Found"));
	    	  }
	    	 return true;
	      }
	      
	      
	      if (cmd.getName().equalsIgnoreCase("hug") || cmd.getName().equalsIgnoreCase("poke") || 
	    	cmd.getName().equalsIgnoreCase("slap") || cmd.getName().equalsIgnoreCase("highfive") ||
	    	cmd.getName().equalsIgnoreCase("facepalm") || cmd.getName().equalsIgnoreCase("yell") || 
	    	cmd.getName().equalsIgnoreCase("bite") || cmd.getName().equalsIgnoreCase("snuggle") || 
	    	cmd.getName().equalsIgnoreCase("shake") || cmd.getName().equalsIgnoreCase("stab") ||
	    	cmd.getName().equalsIgnoreCase("stab") || cmd.getName().equalsIgnoreCase("kiss") ||
	    	cmd.getName().equalsIgnoreCase("punch") || cmd.getName().equalsIgnoreCase("murder") ||
	    	cmd.getName().equalsIgnoreCase("boi") || cmd.getName().equalsIgnoreCase("cry") ||
	    	cmd.getName().equalsIgnoreCase("dab") || cmd.getName().equalsIgnoreCase("lick") ||
	    	cmd.getName().equalsIgnoreCase("scorn") || cmd.getName().equalsIgnoreCase("pat") ||
	    	cmd.getName().equalsIgnoreCase("stalk")) {

	    if(getConfig().getBoolean("General.Cooldowns.Feelings.Enabled")) {
	    	  if(sender instanceof Player) {
	    		  Player p = (Player)sender;
	      		if(cooldown.containsKey(p.getPlayer())) {
	      			int cooldownTime = getConfig().getInt("General.Cooldowns.Feelings.Seconds");
	       			long secondsLeft = ((cooldown.get(p.getPlayer())/1000)+cooldownTime) - (System.currentTimeMillis()/1000);
	       			if(secondsLeft > 0) {
	       				Msgs.sendPrefix(sender, msg.getString("Cooldown-Active").replace("%time%", Long.toString(secondsLeft) + "s"));
	       				bass(p.getPlayer());
	       				return true;
	       				}
	      		}}}
	    	  
	    	  String cmdconfig = (StringUtils.capitalize(cmd.getName().toString()));
	    	  
	    	  if(args[0].equalsIgnoreCase(sender.getName())) {
	    		  if(getConfig().getBoolean("General.Prevent-Self-Feelings")) {
	    		  bass(sender);
	    		  Msgs.sendPrefix(sender, msg.getString("Sender-Is-Target").replace("%command%", cmdconfig));
	    		  return true;
	    	}}
	    	  
	    	  if(sender instanceof Player) {
	    		  Player p = (Player)sender;
	    	  if(disabledsendingworlds.contains(p.getWorld().getName())) {
	    		  bass(sender);
	    		  Msgs.sendPrefix(sender, msg.getString("Sending-World-Disabled"));
	    		  return true;
	    	  }}
	    	  
	    	  if(!emotes.getBoolean("Feelings." + cmdconfig + ".Enable")) {
	    		  bass(sender);
	    		  Msgs.sendPrefix(sender, msg.getString("Emote-Disabled"));
	    		  return true;
	    	  }
	    	  
	    	  if(args.length < 1) {
	    		  Msgs.sendPrefix(sender, msg.getString("No-Player"));
	    		  bass(sender);
	    		  return true;
	    	  }
	    	  
	    	  if(args[0].equalsIgnoreCase("console")) {
	    		  Msgs.sendPrefix(sender, msg.getString("Console-Not-Player")); 
	    		  bass(sender);
	    		  return true;
	    	  }
	    	  
	    	  
	          Player target = Bukkit.getServer().getPlayer(args[0]);
	    	  if (target == null || isVanished(target))
	          {
	           bass(sender);
	           Msgs.sendPrefix(sender, msg.getString("Player-Offline").replace("%player%", args[0].toString()));
	           return true;
	          }	    	  
	    	  
	    	  if(disabledreceivingworlds.contains(target.getWorld().getName())) {
	    		  bass(sender);
	    		  Msgs.sendPrefix(sender, msg.getString("Receiving-World-Disabled"));
	    		  return true;
	    	  }
	    	  
	    	  // Ignoring Check ----------------
	    	  if(sender instanceof Player) {
	    		  Player p = (Player)sender;
				File cache = new File(this.getDataFolder(), File.separator + "Data");
				File f = new File(cache, File.separator + "" + target.getUniqueId().toString() + ".yml");
				FileConfiguration setcache = YamlConfiguration.loadConfiguration(f);
					
				  if(f.exists()) {	
					  if(!setcache.getBoolean("Allow-Feelings")) {
						  bass(sender);
						  Msgs.sendPrefix(sender, msg.getString("Target-Is-Ignoring-All"));	
			    		  return true;
					  }
					  
						if(setcache.getStringList("Ignoring").contains(p.getUniqueId().toString())) {
				    		  bass(sender);
				    		  Msgs.sendPrefix(sender, msg.getString("Target-Is-Ignoring").replace("%player%", target.getName()));	
				    		  return true;
						}
				  }
	    	  }
				// ------------------------------------------------
	    	  
	    	  if(getConfig().getBoolean("General.Global-Feelings.Enabled")) {
	    	        for(final Player online:Bukkit.getServer().getOnlinePlayers())
	                {
	    	        	// Global 
	    	        	if(sender.getName().equalsIgnoreCase("console")) {
	    	        	  Msgs.send(online.getPlayer(), emotes.getString("Feelings." + cmdconfig + ".Msgs.Global").replace("%sender%", msg.getString("Console-Name")).replace("%target%", target.getName()));
	    	        	} else {
	    	              Msgs.send(online.getPlayer(), emotes.getString("Feelings." + cmdconfig + ".Msgs.Global").replace("%sender%", sender.getName()).replace("%target%", target.getName()));
	    	        	}
	    	        	
	                } // end of for(online)

	    	        // Global Console Broadcast Msg
    	        	if(getConfig().getBoolean("General.Global-Feelings.Broadcast-To-Console")) {
	    	        	if(sender.getName().equalsIgnoreCase("console")) {
		    	        	  Msgs.send(getServer().getConsoleSender(), emotes.getString("Feelings." + cmdconfig + ".Msgs.Global").replace("%sender%", msg.getString("Console-Name")).replace("%target%", target.getName()));
		    	        	} else {
		    	              Msgs.send(getServer().getConsoleSender(), emotes.getString("Feelings." + cmdconfig + ".Msgs.Global").replace("%sender%", sender.getName()).replace("%target%", target.getName()));	
		    	        	}}
	    	        	
	    	  } else {
	    		  // if not global
	    	  if(sender.getName().equalsIgnoreCase("console")) {
	          Msgs.send(target.getPlayer(), emotes.getString("Feelings." + cmdconfig + ".Msgs.Target").replace("%player%", msg.getString("Console-Name")));  
	    	  } else {
	    	  Msgs.send(target.getPlayer(), emotes.getString("Feelings." + cmdconfig + ".Msgs.Target").replace("%player%", sender.getName().toString()));
	    	  }
	    	  
	    	  Msgs.send(sender, emotes.getString("Feelings." + cmdconfig + ".Msgs.Sender").replace("%player%", target.getName().toString())); // sender (not global)
	    	} // end of global else
	    	  
	    	  
	    	  // Special Effect Command Handlers -----------------------------
	    	  if(getConfig().getBoolean("General.Violent-Command-Harm")) {
	    		if(cmd.getName().equalsIgnoreCase("slap") || cmd.getName().equalsIgnoreCase("bite") ||
	    		   cmd.getName().equalsIgnoreCase("shake") || cmd.getName().equalsIgnoreCase("stab") ||
	    		   cmd.getName().equalsIgnoreCase("punch") || cmd.getName().equalsIgnoreCase("murder")) {
	    			try {
	    			target.damage(0.01D);
	    			} catch (Exception err) {
	    				getLogger().warning("Unable to damage player: " + target.getName());
	    			}
	    	  }}
	    	  
	    	  // ------------------------------------------------------
	    	  
	    	  
	    	  
	    	  // Cooldown Handler ------------------------------------
	    	  if(getConfig().getBoolean("General.Cooldowns.Feelings.Enabled")) {
	    	  if(sender instanceof Player) {
	    		  Player p = (Player)sender;
    		  cooldown.put(p.getPlayer(), System.currentTimeMillis()); // Adds cooldown if they are a player.
	    	  }}
	    	  // -----------------------------------------------------
	    	  
	    	  
	    	  // Particle Handler -------------------------------------
	    	  if(getConfig().getBoolean("General.Particles")) {
	    		  try {
	    			  Particles.show(target, cmd.getName().toLowerCase());
	    		  } catch(Exception parterr) {
	    			  getLogger().warning("Couldn't display '" + cmd.getName().toUpperCase() + "' particles to " + target.getName() + ". Make sure you use 1.13.2/1.14.3+"); 
	    		  }
	    	  }
	    	  // -----------------------------------------------------
	    	  
	    	  // Sound Handler ----------------------------------------
	    	  try {
	    	  if(!emotes.getString("Feelings." + cmdconfig + ".Sounds.Sound1.Name").equalsIgnoreCase("none") && !emotes.getString("Feelings." + cmdconfig + ".Sounds.Sound1.Name").equalsIgnoreCase("off") && emotes.getString("Feelings." + cmdconfig + ".Sounds.Sound1.Name") != null && emotes.getString("Feelings." + cmdconfig + ".Sounds.Sound1.Name") != null) {
	    	  target.playSound(target.getPlayer().getLocation(), Sound.valueOf(emotes.getString("Feelings." + cmdconfig + ".Sounds.Sound1.Name")), (float) emotes.getDouble("Feelings." + cmdconfig + ".Sounds.Sound1.Volume"), (float) emotes.getDouble("Feelings." + cmdconfig + ".Sounds.Sound1.Pitch"));
	    	  	if(sender instanceof Player) {
	    		  Player p = (Player)sender;
	    		  p.playSound(p.getLocation(), Sound.valueOf(emotes.getString("Feelings." + cmdconfig + ".Sounds.Sound1.Name")), (float) emotes.getDouble("Feelings." + cmdconfig + ".Sounds.Sound1.Volume"), (float) emotes.getDouble("Feelings." + cmdconfig + ".Sounds.Sound1.Pitch"));  
	    	  	}
	    	  }
	    	  
	    	  if(!emotes.getString("Feelings." +cmdconfig + ".Sounds.Sound2.Name").equalsIgnoreCase("none") && !emotes.getString("Feelings." + cmdconfig + ".Sounds.Sound2.Name").equalsIgnoreCase("off") && emotes.getString("Feelings." +cmdconfig + ".Sounds.Sound2.Name") != null && emotes.getString("Feelings." + cmdconfig + ".Sounds.Sound2.Name") != null) {
	    	  target.playSound(target.getPlayer().getLocation(), Sound.valueOf(emotes.getString("Feelings." + cmdconfig + ".Sounds.Sound2.Name")), (float) emotes.getDouble(cmdconfig + ".Sounds.Sound2.Volume"), (float) emotes.getDouble("Feelings." + cmdconfig + ".Sounds.Sound2.Pitch"));
	    	  	if(sender instanceof Player) {
	    		  Player p = (Player)sender;
	    	  		p.playSound(p.getPlayer().getLocation(), Sound.valueOf(emotes.getString("Feelings." + cmdconfig + ".Sounds.Sound2.Name")), (float) emotes.getDouble("Feelings." + cmdconfig + ".Sounds.Sound2.Volume"), (float) emotes.getDouble("Feelings." + cmdconfig + ".Sounds.Sound2.Pitch"));
	    	  	}
	    	  }

	    	  } catch (Exception sounderr) { // err test for sounds
	    		  getLogger().info("One or more of your sounds for /" + cmdconfig + " is incorrect. See below:");
	    		  sounderr.printStackTrace();
	    		  getLogger().info("DO NOT report this error. This is a configuration related issue.");
	    		  getLogger().info("---------------------------[End of Error]---------------------------");
	    	  }
	    	  // ---------- End of Sounds
	    	  
	    	 return true;
	      }
	      
	      if (cmd.getName().equalsIgnoreCase("chatfeelings") && args.length >= 1) {
	    	  Msgs.send(sender, "");
	    	  Msgs.send(sender, "&a&lC&r&ahat &f&lF&r&feelings");
	    	  Msgs.send(sender, "&8&l> &c&lHmm. &7That command does not exist.");
	    	  Msgs.send(sender, "");
	    	 if(sender instanceof Player) {
	    		Player p = (Player)sender;
	    		bass(p.getPlayer());
	    	 }
	    	 return true;
	      }
	      
		return true;	 
	    }
	    
	    @EventHandler
	    public void onleave(PlayerQuitEvent e) {
	    	removeAll(e.getPlayer());
	    	updateLastOn(e.getPlayer());
	    }
	    
	    @EventHandler
	    public void onJoin(PlayerJoinEvent e) {
	    	if(outdatedplugin) {
	    		Msgs.sendPrefix(e.getPlayer(), "&c&lOutdated Plugin! &7Running v" + getDescription().getVersion() + " while the latest is &f&l" + outdatedpluginversion);
	    	}
	    	
	    	updateLastOn(e.getPlayer());
	    	
	    	if(e.getPlayer().getUniqueId().toString().equals("6191ff85-e092-4e9a-94bd-63df409c2079")) {
	    		Msgs.send(e.getPlayer(), "&7This server is running &fChatFeelings &6v" + getDescription().getVersion() + " &ffor " + Bukkit.getBukkitVersion());
	    	}
	    }
}