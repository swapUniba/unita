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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
 * Build a CSV file with information about the pos-tags distribution over time
 * 
 * @author pierpaolo
 */
public class BuildPoSdistribution {
    
    static Options options;
    
    static {
        options = new Options();
        options.addOption("i", true, "Input directory of dictionary files")
                .addOption("o", true, "Output directory");
    }
    
    static CommandLineParser cmdParser = new DefaultParser();
    
    private static final Logger LOG = Logger.getLogger(BuildPoSdistribution.class.getName());
    
    /**
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static Map<String, Float> getMap(File file) throws IOException {
        Map<String, Float> map = new HashMap<>();
        float tot = 0;
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while (reader.ready()) {
            String[] split = reader.readLine().split("\t");
            float v = Float.parseFloat(split[1]);
            map.put(split[0], v);
            tot += v;
            
        }
        for (String k : map.keySet()) {
            map.put(k, map.get(k) / tot);
        }
        reader.close();
        return map;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            CommandLine cmd = cmdParser.parse(options, args);
            if (cmd.hasOption("i") && cmd.hasOption("o")) {
                try {
                    Map<String, Map<String, Float>> map = new HashMap<>();
                    Set<String> pos = new HashSet<>();
                    File dir = new File(cmd.getOptionValue("i"));
                    File[] files = dir.listFiles();
                    for (File f : files) {
                        if (f.isFile() && f.getName().endsWith("_pos.dict")) {
                            Pattern p = Pattern.compile("[0-9]+");
                            Matcher matcher = p.matcher(f.getName());
                            matcher.find();
                            String year = matcher.group();
                            Map<String, Float> d = getMap(f);
                            map.put(year, d);
                            pos.addAll(d.keySet());
                        }
                    }
                    List<String> years = new ArrayList<>(map.keySet());
                    Collections.sort(years);
                    String[] header = new String[years.size() + 1];
                    header[0] = "PoS";
                    System.arraycopy(years.toArray(new String[years.size()]), 0, header, 1, years.size());
                    final Appendable out = new FileWriter(cmd.getOptionValue("o"));
                    final CSVPrinter printer = CSVFormat.DEFAULT.withHeader(header).print(out);
                    List<String> posLabel = new ArrayList<>(pos);
                    Collections.sort(posLabel);
                    for (String p : posLabel) {
                        printer.print(p);
                        for (String y : years) {
                            Float f = map.get(y).get(p);
                            if (f != null) {
                                printer.print(f);
                            } else {
                                printer.print(0f);
                            }
                        }
                        printer.println();
                    }
                    printer.close(true);
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            } else {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("Build a CSV file with information about the pos-tags distribution over time.", options, true);
            }
        } catch (ParseException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
    
}
