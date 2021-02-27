import org.apache.commons.cli.*;

import java.util.Optional;

public class RestMain {


    private static void P(String s) {
        System.out.println(s);
    }

    private static Optional<CommandLine> buildCmd(String[] args) {

        final Options options = new Options();
        Option port=Option.builder("p").longOpt("port").desc("Port number").numberOfArgs(1).type(Integer.class).required().build();
        Option  par=Option.builder("c").longOpt("conf").desc("Configuration file").numberOfArgs(1).required().build();
        options.addOption(port).addOption(par);
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd =  parser.parse( options, args);
            return Optional.of(cmd);
        } catch (ParseException e) {
            P(e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "java ... RestService", options);
            P("");
            P("Example:");
            P(" -c /home/sbartkowski/run/p.properties -p 9800");
            return Optional.empty();
        }
    }

    public static void main(String[] args) throws Exception {

        Optional<CommandLine> cmd = buildCmd(args);
        if (! cmd.isPresent()) return;

        String configfile = cmd.get().getOptionValue('c');
        int PORT = Integer.parseInt(cmd.get().getOptionValue('p'));

    }
}
