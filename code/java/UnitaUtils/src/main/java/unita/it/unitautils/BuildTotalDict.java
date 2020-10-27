/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unita.it.unitautils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Compute the whole dictionary for a particular feature
 * 
 * @author pierpaolo
 */
public class BuildTotalDict {
    
    static Options options;
    
    static {
        options = new Options();
        options.addOption("i", true, "Input directory of dictionary files")
                .addOption("t", true, "Feature to count (token, lemma, pos, entity)")
                .addOption("o", true, "Output dictionary");
    }
    
    static CommandLineParser cmdParser = new DefaultParser();
    
    private static final Logger LOG = Logger.getLogger(BuildTotalDict.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            CommandLine cmd = cmdParser.parse(options, args);
            if (cmd.hasOption("i") && cmd.hasOption("o") & cmd.hasOption("t")) {
                try {
                    Map<String, DictEntry> map = new HashMap<>();
                    File maindir = new File(cmd.getOptionValue("i"));
                    File[] files = maindir.listFiles();
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".dict") && file.getName().contains("_" + cmd.getOptionValue("t"))) {
                            BufferedReader reader = new BufferedReader(new FileReader(file));
                            while (reader.ready()) {
                                String[] split = reader.readLine().split("\t");
                                DictEntry entry = map.get(split[0]);
                                if (entry == null) {
                                    map.put(split[0], new DictEntry(split[0], Integer.parseInt(split[1])));
                                } else {
                                    entry.setOcc(entry.getOcc() + Integer.parseInt(split[1]));
                                }
                            }
                            reader.close();
                        }
                    }
                    List<DictEntry> list = new ArrayList<>(map.values());
                    Collections.sort(list, Collections.reverseOrder());
                    BufferedWriter out = new BufferedWriter(new FileWriter(cmd.getOptionValue("o")));
                    for (DictEntry e : list) {
                        out.append(e.getKey()).append("\t").append(String.valueOf(e.getOcc()));
                        out.newLine();
                    }
                    out.close();
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            } else {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("Compute the whole dictionary for a particular feature.", options, true);
            }
        } catch (ParseException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
    
}
