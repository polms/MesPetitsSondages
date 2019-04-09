package fr.ensibs.sondages.questions;

import fr.ensibs.river.RiverLookup;
import net.jini.space.JavaSpace;

import java.util.ArrayList;
import java.util.Scanner;

public class MainAnswerer {

    public Poll poll;

    public MainAnswerer(Poll poll) {
        this.poll = poll;
    }

    /**
     * Print required launch arguments and exit the program with status code 1
     */
    public static void usage() {
        System.err.println("Usage:  <js_server_host> <js_server_port> <jms_server_host> <jms_server_port>");
        System.exit(1);
    }

    /**
     * Start the command line user interface
     */
    public void run() throws Exception {
        System.out.println("Enter commands:"
                + "\n QUIT                                                      to quit the application"
                + "\n LIST                                                      to query all available questions"
                + "\n ANSWER <question_id>                                      to give an answer to the question");

        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        while (!line.toLowerCase().equals("quit")) {
            String[] command = line.split(" +");
            switch (command[0].toUpperCase()) {
                case "LIST": {
                    ArrayList<Question> questions = this.poll.getQuestions();
                    if (questions.size() == 0)
                        System.out.println("No questions");
                    int i = 0;
                    for (Question q : questions) {
                        System.out.println("Question " + i + " :" + q.getQuestion());
                        i++;
                    }
                }
                    break;
                case "ANSWER": {
                    ArrayList<Question> questions = this.poll.getQuestions();
                    DefaultQuestion q = (DefaultQuestion)questions.get(Integer.parseInt(command[1]));
                    Answer a = q.makeResponseForm();
                    scanner = new Scanner(System.in);
                    poll.answer(a);
                }
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
        final short params = 4;

        if (args.length < params) {
            usage();
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String jms_host = args[2];
        String jms_port = args[3];

        RiverLookup rl = new RiverLookup();
        JavaSpace javaSpace = rl.lookup(host, port, JavaSpace.class);

        System.setProperty("java.naming.factory.initial", "fr.dyade.aaa.jndi2.client.NamingContextFactory");
        System.setProperty("java.naming.factory.host", jms_host);
        System.setProperty("java.naming.factory.port", jms_port);


        Poll poll = new Poll(javaSpace);
        MainAnswerer p = new MainAnswerer(poll);
        p.run();
    }
}
