/**
 * Created by michaelpair on 2/24/14.
 */
import java.util.Scanner;

public class Game {

    //
    // Public
    //

    // Globals
    public static final boolean DEBUGGING  = false; // Debugging flag.
    public static final int MAX_LOCALES = 9;    // Total number of rooms/locations we have in the game.
    public static int currentLocale = 0;        // Player starts in locale 0.
    public static String command;               // What the player types as he or she plays the game.
    public static boolean stillPlaying = true; // Controls the game loop.
    public static Locale[] locations;           // An uninitialized array of type Locale. See init() for initialization.
    public static int[][]  nav;                 // An uninitialized array of type int int.
    public static int moves = 0;                // Counter of the player's moves.
    public static int score = 0;                // Tracker of the player's score.
    public static Items[] inventory;
    public static String pickedUp;
    //public static int inventoryFilled = 0;
    //public static int numberRoomEnter = 0;

    public static void main(String[] args) {
        if (DEBUGGING) {
            // Display the command line args.
            System.out.println("Starting with args:");
            for (int i = 0; i < args.length; i++) {
                System.out.println(i + ":" + args[i]);
            }
        }

        // Set starting locale, if it was provided as a command line parameter.
        if (args.length > 0) {
            try {
                int startLocation = Integer.parseInt(args[0]);
                // Check that the passed-in value for startLocation is within the range of actual locations.
                if ( startLocation >= 0 && startLocation <= MAX_LOCALES) {
                    currentLocale = startLocation;
                } else {
                    System.out.println("WARNING: passed-in starting location (" + args[0] + ") is out of range.");
                }
            } catch(NumberFormatException ex) {
                System.out.println("WARNING: Invalid command line arg: " + args[0]);
                if (DEBUGGING) {
                    System.out.println(ex.toString());
                }
            }
        }

        // Get the game started.
        init();
        updateDisplay();

        // Game Loop
        while (stillPlaying) {
            getCommand();
            navigate();
            updateDisplay();
        }

        // We're done. Thank the player and exit.
        System.out.println("Thank you for playing.");

    }

    //
    // Private
    //


    private static void init() {
        // Initialize any uninitialized globals.
        command = new String();
        stillPlaying = true;   // TODO: Do we need this?
        System.out.println("Mikes Crazy World!");
        System.out.println("To see a list of commands, type \"h\" or \"help\"");


        // Set up the location instances of the Locale class.
        Locale loc0 = new Locale(0);
        loc0.setName("A Shack");
        loc0.setDesc("It's a pretty small shack, but there appears to be a map tacked to the wall.");
        loc0.setAvailableDirs("North East");
        //loc0.setHasVisited(false);

        Locale loc1 = new Locale(1);
        loc1.setName("Erebor");
        loc1.setDesc("What happened to all the gold and why are there so many mushrooms all over the place. /n They look pretty easy to take from the ground");
        loc1.setAvailableDirs("North South West");
        //loc1.setHasVisited(false);

        Locale loc2 = new Locale(2);
        loc2.setName("The Library");
        loc2.setDesc("It's so boring here... wait, are those launch codes on the table?! Those look fun!");
        loc2.setAvailableDirs("North South East West");
        //loc2.setHasVisited(false);

        Locale loc3 = new Locale(3);
        loc3.setName("The SUPER Market");
        loc3.setDesc("We have every brand you can think of! Here take this free sample.");
        loc3.setAvailableDirs("South West East");
        //loc3.setHasVisited(false);

        Locale loc4 = new Locale(4);
        loc4.setName("Rainbow Road");
        loc4.setDesc("Wiiiiiiiiiiiiii! But wait, what car do these keys go to?");
        loc4.setAvailableDirs("North East");
        //loc4.setHasVisited(false);

        Locale loc5 = new Locale(5);
        loc5.setName("Eetzeek Hagadol");
        loc5.setDesc("Shalom habibi, mah aht rotsah? Would you like some french fries?");
        loc5.setAvailableDirs("West");
        //loc5.setHasVisited(false);

        Locale loc6 = new Locale(6);
        loc6.setName("Magick Shoppe");
        loc6.setDesc("Get here, enchanted stuff, you may");
        loc6.setAvailableDirs("North");
        //loc6.setHasVisited(false);

        Space loc7 = new Space(7);
        loc7.setName("Kennedy Space Center");
        loc7.setDesc("There's a shuttle ready to launch"); //TODO: figure out how to use subclasses!!!
        loc7.setAvailableDirs("South");
        loc7.setNearestPlanet("Planet Vegeta");
        //loc7.setHasVisited(false);

        Time loc8 = new Time(8);
        loc8.setName("The DeLorean");
        loc8.setDesc("The time is set to August 15, 1969"); //TODO: find a time and date to use
        loc8.setAvailableDirs("South");
        loc8.setNearestYear("August 15, 1969");
        //loc8.setHasVisited(false);


        Items item0 = new Items(0);
        item0.setName("Map");
        item0.setDesc("Looks like a map of the area");

        Items item1 = new Items(1);
        item1.setName("Mutton");
        item1.setDesc("This thing could put someone 6 feet under if you hit them hard enough");

        Items item2 = new Items(2);
        item2.setName("Mushrooms");
        item2.setDesc("It's got a red and white top and a face. WHAT?!");

        Items item3 = new Items(3);
        item3.setName("Car Keys");
        item3.setDesc("The key fob says DMC on it");

        Items item4 = new Items(4);
        item4.setName("Launch Codes");
        item4.setDesc("156453646545");

        Items item5 = new Items(5);
        item5.setName("French Fries");
        item5.setDesc("They have the special frie salt! OH MY GOD!!!!");


        inventory  = new Items[6];
        inventory[0] = item0;
        inventory[1] = item1;
        inventory[2] = item2;
        inventory[3] = item3;
        inventory[4] = item4;
        inventory[5] = item5;


        // Set up the location array.
        locations = new Locale[9];
        locations[0] = loc0;
        locations[1] = loc1;
        locations[2] = loc2;
        locations[3] = loc3;
        locations[4] = loc4;
        locations[5] = loc5;
        locations[6] = loc6;
        locations[7] = loc7;
        locations[8] = loc8;

        if (DEBUGGING) {
            System.out.println("All game locations:");
            for (int i = 0; i < locations.length; ++i) {
                System.out.println(i + ":" + locations[i].toString());
            }
        }

        // Set up the navigation matrix.
        nav = new int[][] {
        /*N   S   E  W */
        /*0   1   2  3 */
        { 3, -1,  1, -1},
        { 2,  6, -1,  0},
        { 7,  1,  5,  3},
        {-1,  0,  2,  4},
        { 8, -1,  3, -1},
        {-1, -1, -1,  2},
        { 1, -1, -1, -1},
        {-1,  2, -1, -1},
        {-1,  4, -1, -1}
        };

    }

    private static void updateDisplay() {
        System.out.println("-------------------------------------------");
        System.out.println(locations[currentLocale].getText());
        System.out.println("[" + moves + " moves, score " + score + "] ");
        System.out.println("-------------------------------------------");
    }

    private static void getCommand() {
        Scanner inputReader = new Scanner(System.in);
        command = inputReader.nextLine();  // command is global.
    }

    private static void navigate() {
        final int INVALID = -1;
        int dir = INVALID;  // This will get set to a value > 0 if a direction command was entered.

        if (        command.equalsIgnoreCase("north")    || command.equalsIgnoreCase("n") ) {
            dir = 0;
        } else if ( command.equalsIgnoreCase("south")    || command.equalsIgnoreCase("s") ) {
            dir = 1;
        } else if ( command.equalsIgnoreCase("east")     || command.equalsIgnoreCase("e") ) {
            dir = 2;
        } else if ( command.equalsIgnoreCase("west")     || command.equalsIgnoreCase("w") ) {
            dir = 3;
        } else if ( command.equalsIgnoreCase("map")      || command.equalsIgnoreCase("m") ) {
            showMap();
        } else if ( command.equalsIgnoreCase("inventory")|| command.equalsIgnoreCase("i") ) {
            showInventory();
        } else if ( command.equalsIgnoreCase("take")     || command.equalsIgnoreCase("t") ) {
            takeItem();
        } else if ( command.equalsIgnoreCase("quit")     || command.equalsIgnoreCase("q") ) {
            quit();
        } else if ( command.equalsIgnoreCase("help")     || command.equalsIgnoreCase("h") ) {
            help();
        };

        if (dir > -1) {   // This means a dir was set.
            int newLocation = nav[currentLocale][dir];
            if (newLocation == INVALID) {
                System.out.println("-------------------------------------------");
                System.out.println("You cannot go that way.");
                System.out.println("-------------------------------------------");
            } else {
                currentLocale = newLocation;
                moves = moves + 1;
                locations[currentLocale].numberRoomEnter = locations[currentLocale].numberRoomEnter + 1;

                if (locations[currentLocale].numberRoomEnter <= 1) {
                    locations[currentLocale].setHasVisited(false);
                    score = score + 5;
                } else if (locations[currentLocale].numberRoomEnter > 1) {
                    locations[currentLocale].setHasVisited(true);
                }
            }
        }
    }

    private static void help() {
        System.out.println("-------------------------------------------");;
        System.out.println("The commands are as follows:");
        System.out.println("   n/north");
        System.out.println("   s/south");
        System.out.println("   e/east");
        System.out.println("   w/west");
        System.out.println("   i/inventory");
        System.out.println("   m/map");
        System.out.println("   t/take");
        System.out.println("   h/help");
        System.out.println("   q/quit");
        System.out.println("-------------------------------------------");
    }

    private static void showMap() {
        System.out.println("The function that displays the map has been called");
        Map();
    }

    private static void showInventory(){
        System.out.println("The function that displays the inventory has been called");

        String satchel="";
        if (inventory[0].itemFound()){
            satchel= satchel+inventory[0].toString()+ "\n";
        }
        if(inventory[1].itemFound()){
            satchel =satchel+inventory[1].toString()+ "\n";
        }
        if(inventory[2].itemFound()){
            satchel =satchel+inventory[2].toString()+ "\n";
        }
        if(inventory[3].itemFound()){
            satchel =satchel+inventory[3].toString()+ "\n";
        }
        if(inventory[4].itemFound()){
            satchel =satchel+inventory[4].toString()+ "\n";
        }
        if(inventory[5].itemFound()){
            satchel =satchel+inventory[4].toString()+ "\n";
        }
        System.out.println(satchel);

    }

    private static void takeItem(){
        if (locations[currentLocale] == locations[0]) {
            inventory[0].setFound(true);
            pickedUp = inventory[0].getName();
            locations[currentLocale].setDesc("It's a pretty small shack");
        }
        if (locations[currentLocale] == locations[3]) {
            inventory[1].setFound(true);
            pickedUp = inventory[1].getName();
            locations[currentLocale].setDesc("What happened to all the gold?");
        }
        if (locations[currentLocale] == locations[1]) {
            inventory[2].setFound(true);
            pickedUp = inventory[2].getName();
            locations[currentLocale].setDesc("We have every brand you can think of!");
        }
        if (locations[currentLocale] == locations[4]) {
            inventory[3].setFound(true);
            pickedUp = inventory[3].getName();
            locations[currentLocale].setDesc("It's so boring here...");
        }
        if (locations[currentLocale] == locations[2]) {
            inventory[4].setFound(true);
            pickedUp = inventory[4].getName();
            locations[currentLocale].setDesc("Wiiiiiiiiiiiiii!");
        }
        if (locations[currentLocale] == locations[5]) {
            inventory[5].setFound(true);
            pickedUp = inventory[5].getName();
            locations[currentLocale].setDesc("Shalom habibi, mah aht rotsah?");
        }
        System.out.println("The " + pickedUp + " was placed in your handy dandy belt Satchel!");

    }

    private static void quit() {
        stillPlaying = false;
    }

    private static void Map() {
        System.out.println("++++++++++++++++++                                    +++++++++++++++++");
        System.out.println("++++++++++++++++++                                    +++++Kennedy+++++");
        System.out.println("+++The DeLorean+++                                    ++++++Space++++++");
        System.out.println("++++++++++++++++++                                    ++++++Center+++++");
        System.out.println("++++++++++++++++++                                    +++++++++++++++++");
        System.out.println("       ||                                                    ||");
        System.out.println("       ||                                                    ||");
        System.out.println("       ||                                                    ||");
        System.out.println("       ||                                                    ||");
        System.out.println("++++++++++++++++++         ++++++++++++++++++         ++++++++++++++++++         ++++++++++++++++++");
        System.out.println("++++++++++++++++++         ++++++++The+++++++         ++++++++++++++++++         ++++++Etzeek++++++");
        System.out.println("+++Rainbow Road+++=========+++++++SUPER++++++=========++++The Library+++=========++++++++++++++++++");
        System.out.println("++++++++++++++++++         +++++++Market+++++         ++++++++++++++++++         ++++++Hagadol+++++");
        System.out.println("++++++++++++++++++         ++++++++++++++++++         ++++++++++++++++++         ++++++++++++++++++");
        System.out.println("                                                              ||                         ||");
        System.out.println("                                                              ||                         ||");
        System.out.println("                                                              ||                         ||");
        System.out.println("                                                              ||                         ||");
        System.out.println("                                                      ++++++++++++++++++         +++++++++++++++++");
        System.out.println("                                                      ++++++++++++++++++         +++++++++++++++++");
        System.out.println("                                                      ++++++A Shack+++++=========++++++Erebor+++++");
        System.out.println("                                                      ++++++++++++++++++         +++++++++++++++++");
        System.out.println("                                                      ++++++++++++++++++         +++++++++++++++++");
        System.out.println("                                                              ||");
        System.out.println("                                                              ||");
        System.out.println("                                                              ||");
        System.out.println("                                                              ||");
        System.out.println("                                                      +++++++++++++++++");
        System.out.println("                                                      ++++++Magick+++++");
        System.out.println("                                                      +++++++++++++++++");
        System.out.println("                                                      ++++++Shoppe+++++");
        System.out.println("                                                      +++++++++++++++++");
    }


}