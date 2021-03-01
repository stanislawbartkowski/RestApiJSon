package com.rest.main;

import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.runjson.RestRunJson;
import com.rest.runjson.executors.Python3Executor;
import com.rest.runjson.executors.SQLExecutor;
import com.rest.runjson.executors.ShellExecutor;
import org.apache.commons.cli.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class RestMainHelper {

    private static void P(String s) {
        System.out.println(s);
    }

    public static class RestParams {

        private final Path configfile;
        private final int PORT;
        private final CommandLine cmd;

        RestParams(CommandLine cmd) {
            this.cmd = cmd;
            configfile = Paths.get(cmd.getOptionValue('c'));
            PORT = Integer.parseInt(cmd.getOptionValue('p'));
        }

        public int getPORT() {
            return PORT;
        }

        public CommandLine getCmd() {
            return cmd;
        }

        public Path getConfigfile() {
            return configfile;
        }
    }

    public static Optional<RestParams> buildCmd(String[] args) {

        final Options options = new Options();
        Option port = Option.builder("p").longOpt("port").desc("Port number").numberOfArgs(1).type(Integer.class).required().build();
        Option par = Option.builder("c").longOpt("conf").desc("Configuration file").numberOfArgs(1).required().build();
        options.addOption(port).addOption(par);
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            return Optional.of(new RestParams(cmd));
        } catch (ParseException e) {
            P(e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java ... RestService", options);
            P("");
            P("Example:");
            P(" -c /home/sbartkowski/run/p.properties -p 9800");
            return Optional.empty();
        }
    }


    public static void registerExecutors(int proc) throws RestError {
        switch (proc) {
            case IRestActionJSON.SQL:
                RestRunJson.registerExecutor(IRestActionJSON.SQL, new SQLExecutor());
                break;
            case IRestActionJSON.PYTHON3:
                RestRunJson.registerExecutor(IRestActionJSON.PYTHON3, new Python3Executor());
                break;
            case IRestActionJSON.SHELL:
                RestRunJson.registerExecutor(IRestActionJSON.SHELL, new ShellExecutor());
                break;
        }
    }

}
