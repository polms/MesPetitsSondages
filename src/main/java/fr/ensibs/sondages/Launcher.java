package fr.ensibs.sondages;

import fr.ensibs.sondages.analyzer.Analyzer;
import fr.ensibs.sondages.questions.MainAnswerer;
import fr.ensibs.sondages.sounder.MainSounder;

import java.util.Arrays;

/**
 * Main laucher for the apps
 */
public class Launcher {

    /**
     * Print usage info and exit the program with code -1
     */
    public static void usage() {
        System.out.println("Usage: (sounder|answerer|analyser) [args]");
        System.exit(-1);
    }


    public static void main(String[] args) throws Exception {
        if (args.length == 0 || args[0].equals("-h")) {
            usage();
        }
        switch (args[0]) {
            case "sounder":
                MainSounder.main(Arrays.copyOfRange(args, 1, args.length));
                break;
            case "answerer":
                MainAnswerer.main(Arrays.copyOfRange(args, 1, args.length));
                break;
            case "analyser":
                Analyzer.main(Arrays.copyOfRange(args, 1, args.length));
                break;
            default:
                System.out.println("Unknown service");
                System.exit(-1);
        }
    }
}
