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
 * Build occurrences per year for tokens, lemmas and entities.
 * 
 * @author pierpaolo
 */
public class BuildOccTable {

    static Options options;

    static {
        options = new Options();
        options.addOption("i", true, "Input directory of dictionary files")
                .addOption("o", true, "Output directory");
    }

    static CommandLineParser cmdParser = new DefaultParser();

    private static final Logger LOG = Logger.getLogger(BuildOccTable.class.getName());

    private static int count(File file) throws IOException {
        int c = 0;
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while (reader.ready()) {
            c += Integer.parseInt(reader.readLine().split("\t")[1]);
        }
        reader.close();
        return c;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            CommandLine cmd = cmdParser.parse(options, args);
            if (cmd.hasOption("i") && cmd.hasOption("o")) {
                try {
                    File dir = new File(cmd.getOptionValue("i"));
                    File[] listFiles = dir.listFiles();
                    Map<String, int[]> table = new HashMap<>();
                    for (File f : listFiles) {
                        if (f.isFile() && f.getName().endsWith(".dict")) {
                            Pattern p = Pattern.compile("[0-9]+");
                            Matcher matcher = p.matcher(f.getName());
                            matcher.find();
                            String year = matcher.group();
                            int[] v = table.get(year);
                            if (v == null) {
                                v = new int[3];
                                table.put(year, v);
                            }
                            if (f.getName().contains("_token")) {
                                v[0] = count(f);
                            } else if (f.getName().contains("_lemmas")) {
                                v[1] = count(f);
                            } else if (f.getName().contains("_entities")) {
                                v[2] = count(f);
                            }
                        }
                    }
                    final Appendable out = new FileWriter(cmd.getOptionValue("o"));
                    final CSVPrinter printer = CSVFormat.TDF.withHeader("year", "#token", "#lemma", "#entity").print(out);
                    List<String> years = new ArrayList<>(table.keySet());
                    Collections.sort(years);
                    for (String year : years) {
                        int[] v = table.get(year);
                        printer.print(year);
                        for (int k : v) {
                            printer.print(k);
                        }
                        printer.println();
                    }
                    printer.close();
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            } else {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("Build occurrences per year for tokens, lemmas and entities.", options, true);
            }
        } catch (ParseException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

}
