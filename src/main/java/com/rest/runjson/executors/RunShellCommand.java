package com.rest.runjson.executors;

import com.rest.restservice.RestLogger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.logging.Level;


class RunShellCommand {

    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            try {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        consumer.accept(line);
                    }
                }
            } catch (IOException e) {
                String message = "Error while reading line from the process ";
                RestLogger.L.log(Level.SEVERE, message, e);
            }
        }
    }

    static int run(File shellhome, Map<String, String> env, StringBuffer output, List<String> pars) throws IOException, InterruptedException {

        // array to list
        ProcessBuilder builder = new ProcessBuilder(pars);
        builder.environment().putAll(env);
        builder.directory(shellhome);
        // prepare string for logging
        StringBuffer co = new StringBuffer();
        for (String s : pars) {
            co.append(s);
            co.append(' ');
        }
        RestLogger.info(co.toString());

        builder.redirectErrorStream(true);
        // collect the result
//        List<String> output = new ArrayList<>();
        Process process = builder.start();
        StreamGobbler streamGobbler =
                new StreamGobbler(process.getInputStream(), s -> {
                    RestLogger.info(s);
                    if (output.length() != 0) output.append('\n');
                    output.append(s);
                });
        ExecutorService exe = Executors.newSingleThreadExecutor();
        try {
            exe.submit(streamGobbler).get();
        } catch (ExecutionException e) {
            String message = "Error while getting input result";
            RestLogger.L.log(Level.SEVERE, message, e);
            throw new IOException(e);
        }
        process.waitFor();
        int exitval = process.exitValue();
        RestLogger.info("Exit code " + exitval);
        return exitval;
    }
}
