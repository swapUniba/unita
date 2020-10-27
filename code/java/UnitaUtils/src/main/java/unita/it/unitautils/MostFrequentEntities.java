/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unita.it.unitautils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 * Compute the N most frequent entities
 * 
 * @author pierpaolo
 */
public class MostFrequentEntities {

    static Options options;

    static {
        options = new Options();
        options.addOption("i", true, "Input directory of dictionary files")
                .addOption("n", true, "Number of entities")
                .addOption("o", true, "Output file with the top-N entities");
    }

    static CommandLineParser cmdParser = new DefaultParser();

    private static final Logger LOG = Logger.getLogger(MostFrequentEntities.class.getName());

    private static String[] getTop(File file, int n) throws IOException {
        int c = 0;
        String[] r = new String[n];
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while (reader.ready()) {
            r[c] = reader.readLine().split("\t")[0];
            c++;
            if (c == n) {
                break;
            }
        }
        reader.close();
        return r;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            CommandLine cmd = cmdParser.parse(options, args);
            if (cmd.hasOption("i") && cmd.hasOption("o") & cmd.hasOption("n")) {
                try {
                    File dir = new File(cmd.getOptionValue("i"));
                    File[] listFiles = dir.listFiles();
                    Map<String, String[]> table = new HashMap<>();
                    int n=Integer.parseInt(cmd.getOptionValue("n"));
                    for (File f : listFiles) {
                        if (f.isFile() && f.getName().endsWith(".dict")) {
                            Pattern p = Pattern.compile("[0-9]+");
                            Matcher matcher = p.matcher(f.getName());
                            matcher.find();
                            String year = matcher.group();
                            if (f.getName().contains("_entities")) {
                                table.put(year, getTop(f,n));
                            }
                        }
                    }
                    final Appendable out = new FileWriter(cmd.getOptionValue("o"));
                    List<String> years = new ArrayList<>(table.keySet());
                    Collections.sort(years);
                    final CSVPrinter printer = CSVFormat.DEFAULT.withHeader(years.toArray(new String[years.size()])).print(out);
                    for (int i = 0; i < n; i++) {
                        for (String year : years) {
                            String[] v = table.get(year);
                            printer.print(v[i]);
                        }
                        printer.println();
                    }
                    printer.close();
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            } else {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("Compute the N most frequent entities.", options, true);
            }
        } catch (ParseException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

}
