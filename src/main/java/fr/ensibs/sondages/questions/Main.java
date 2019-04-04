package fr.ensibs.sondages.questions;

import fr.ensibs.river.RiverLookup;
import net.jini.space.JavaSpace;

import java.util.Scanner;

public class Main {

    public Poll poll;

    public Main(Poll poll) {
        this.poll = poll;
    }

    /**
     * Print required launch arguments and exit the program with status code 1
     */
    public static void usage() {
        System.err.println("Usage:  <server_host> <server_port>");
        System.exit(1);
    }

    /**
     * Start the command line user interface
     */
    public void run()
    {
        System.out.println("Enter commands:"
                + "\n QUIT                                                      to quit the application"
                + "\n ASK <question> <type>                                     to ask a new question"
                + "\n LIST                                                      to query all available questions"
                + "\n ANSWER <question_id>                                      to give an answer to the question"
                + "\n ANALYSE LIST                                              to query all analyses"
                + "\n ANALYSE GET <id>                                          to get the results of an analyse");

        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        while (!line.toLowerCase().equals("quit")) {
            String[] command = line.split(" +");
            switch (command[0].toUpperCase()) {
                case "ANALYSE":
                    switch (command[1].toUpperCase()) {
                        case "LIST":
                        case "GET":
                            System.out.println("nop");
                            break;
                        default:
                            System.err.println("Unknown command: ADD \"" + command[1] + "\"");
                    }
                    break;
                case "LIST":
                case "ASK":
                case "ANSWER":
                    System.out.println("nop");
                    break;
                default:
                    System.err.println("Unknown command: \"" + command[0] + "\"");
            }
            line = scanner.nextLine();
        }

        System.out.println("Exit");
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        final short params = 2;

        if (args.length < params) {
            usage();
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        RiverLookup rl = new RiverLookup();
        JavaSpace javaSpace = rl.lookup(host, port, JavaSpace.class);
        Poll poll = new Poll(javaSpace);
        Main p = new Main(poll);
        p.run();
    }
}
