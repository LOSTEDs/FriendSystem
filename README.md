# FriendSystem

A **simple** and **easy-to-use** FriendSystem plugin designed for [BungeeCord](https://www.spigotmc.org/wiki/bungeecord/).

## WARNING

**❗️NOTE❗️ This FriendSystem plugin is not customizable at all; thus, for you, 
you have to change the source code to change the design to fit your favor; 
otherwise, there is no way that you can customize the design.**

## Features

- Super easy to use (Drag-n-drop, and you are ready to go)
- No dependencies required 🥶
- No database required 🥶
- Supporting basic /friend or /f commands:
   - /friend accept \<player\> or /f accept \<player\>
   - /friend add \<player\> or /f add \<player\>
   - /friend deny \<player\> or /f deny \<player\>
   - /friend help or /f help
   - /friend list or /f list
   - /friend remove \<player\> or /f remove \<player\>
   - /friend requests \<pages\> or /f requests \<pages\>

## How To (For Server Admins)

### - How to use this FriendSystem plugin?
   1. Download the latest release of the FriendSystem plugin.
   2. Then, drag-n-drop the FriendSystem plugin to your BungeeCord `~/plugins` folder.
   3. Last but not least, just start the BungeeCord server casually. 👍

## How To (For Plugin Developers)

### - How to build this FriendSystem plugin?
   1. Clone this repository by typing this (```git clone https://github.com/LOSTEDs/FriendSystem.git```) in the command prompt or terminal!
   2. Open up your IDE (in this case, I use Intellij IDEA)!
   3. Open up this project!
   4. Use Maven to build this project! (There are two ways!)
      1. Use GUI to build the Project!
         - Once you opened the FriendSystem Project.
         - There should be a `pom.xml` file, please open it! Scroll down until you see `<outputDirectory>`, then change this to your desired OutputDirectory please!
         - After that, look at right-hand side! There should be a button named `Maven`.
         - Click that `Maven` button! Then, click `Lifecycle`. Last, click `package`!
         - Thus, you should be able to build the FriendSystem Project!
      2. Use commands to build the Project!
         - Open up a command prompt or terminal!
         - `cd` to the FriendSystem directory where the `pom.xml` file is located!
         - Then, use your favorite text editor to open up the `pom.xml` file. Scroll down until you see the `<outputDirectory>`. You can change this to your desired OutputDirectory!
         - Last but not least, execute `mvn package` to build/package the Project!
         - The FriendSystem plugin will be popped up in the directory where you've set it in the `pom.xml` file previously.

### - What are the build requirements?
   - [JDK 8](https://www.java.com/en/download/) or higher❗️(Pretty much this is the only requirement I suppose?)

   ❗️I strongly welcome everyone to submit a pull request at any point of time or submit an issue!❗️

## Frequently Asked Questions

1. How/Where do you store all the required data? 
   - When the server fires up, it shall create two folders which is `FriendSystem/data` inside of the `~/plugins` folder. 
   - Once a player joins the server, it will create a JSON file named by their UUID. Also, the path to the data is `~/plugins/FriendSystem/data`.
2. What versions of BungeeCord does this FriendSystem plugin support?
   - Sorry, I've only tested this FriendSystem plugin on **BungeeCord Version 1.18 - 1.19**. **I cannot vouch for other BungeeCord versions to work perfectly.**
3. There is a bug/there are bugs, what should I do?
   - Open an issue at the [Issues](https://github.com/LOSTEDs/FriendSystem/issues) section on this GitHub repository page or direct message me on Discord **LOSTED#8754**, please! Otherwise, I will find out your home address! :)
4. May I copy and paste the code from this repository, please?
   - You may!
5. Is this plugin available on [Spigot / BungeeCord Resources](https://www.spigotmc.org/resources/categories/bungee-proxy.3/) page?
    - No, not yet! Shhh! I'm just too lazy...

## Few In-game Screenshots

❗️Since I was on a Paper server without any "ranks" plugins, it would display the player's name a bit odd. However, as long as you have a "ranks" plugin, you will be fine... Trust me!

![](https://raw.githubusercontent.com/LOSTEDs/FriendSystem/master/assets/Friend.png)
![](https://raw.githubusercontent.com/LOSTEDs/FriendSystem/master/assets/FriendHelp.png)
![](https://raw.githubusercontent.com/LOSTEDs/FriendSystem/master/assets/FriendRemoved.png)
![](https://raw.githubusercontent.com/LOSTEDs/FriendSystem/master/assets/FriendRemovedBy.png)
![](https://raw.githubusercontent.com/LOSTEDs/FriendSystem/master/assets/FriendRequestFrom.png)
![](https://raw.githubusercontent.com/LOSTEDs/FriendSystem/master/assets/FriendRequestTo.png)

## License

This FriendSystem plugin is under [MIT license](LICENSE).

## Still need help?

You may direct message me on Discord **LOSTED#8754** or open an issue at the [Issues](https://github.com/LOSTEDs/FriendSystem/issues) section on GitHub!


## TODO List...
- [ ] Add /friend notification or /f notification command
- [ ] Add /friend removeall or /f removeall command
- [ ] Add /friend toggle or /f toggle command
- [ ] Add an IgnoreSystem to this FriendSystem plugin
- [ ] Add Discord WebHook Integration
- [ ] Allow players to customize this FriendSystem plugin
- [ ] Optimize my shittyass code :D (Probably never...)
