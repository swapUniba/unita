/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unita.it.unitautils;

import java.io.BufferedWriter;
import java.io.File;
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
 * Build dictionaries per year for tokens, lemmas, pos-tags and entities
 * 
 * @author pierpaolo
 */
public class ComputeDictionaries {

    static Options options;

    static {
        options = new Options();
        options.addOption("i", true, "Input directory of spacy files")
                .addOption("o", true, "Output directory");
    }

    static CommandLineParser cmdParser = new DefaultParser();

    private static final Logger LOG = Logger.getLogger(ComputeDictionaries.class.getName());

    private static void saveDict(Map<String, Integer> dict, File file) throws IOException {
        List<DictEntry> list = new ArrayList<>(dict.size());
        for (Map.Entry<String, Integer> e : dict.entrySet()) {
            list.add(new DictEntry(e.getKey(), e.getValue()));
        }
        Collections.sort(list, Collections.reverseOrder());
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (DictEntry e : list) {
            writer.append(e.getKey()).append("\t").append(String.valueOf(e.getOcc()));
            writer.newLine();
        }
        writer.close();
    }

    private static void addDict(Map<String, Integer> dict, String key) {
        Integer get = dict.get(key);
        if (get == null) {
            dict.put(key, 1);
        } else {
            dict.put(key, get + 1);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            CommandLine cmd = cmdParser.parse(options, args);
            if (cmd.hasOption("i") && cmd.hasOption("o")) {
                File maindir = new File(cmd.getOptionValue("i"));
                File[] listFiles = maindir.listFiles();
                for (File file : listFiles) {
                    if (file.isFile() && file.getName().endsWith(".spacy")) {
                        LOG.log(Level.INFO, "Working on: {0}", file.getName());
                        Map<String, Integer> tokens = new HashMap<>();
                        Map<String, Integer> lemmas = new HashMap<>();
                        Map<String, Integer> pos = new HashMap<>();
                        Map<String, Integer> entities = new HashMap<>();
                        try {
                            VerticalFormatReader r = new VerticalFormatReader(file);
                            r.open();
                            while (r.hasNext()) {
                                String[] rows = r.next();
                                StringBuilder sb = new StringBuilder();
                                for (String row : rows) {
                                    // 1:token 2:lemma 3:pos 7:iob
                                    String[] split = row.split("\t");
                                    addDict(tokens, split[1]);
                                    addDict(lemmas, split[2]);
                                    addDict(pos, split[3]);
                                    if (split[7].startsWith("B-")) {
                                        if (sb.length() > 0) {
                                            addDict(entities, sb.toString());
                                        }
                                        sb = new StringBuilder();
                                        sb.append(split[1]);
                                    } else if (split[7].startsWith("I-")) {
                                        sb.append(" ").append(split[1]);
                                    }
                                }
                                if (sb.length() > 0) {
                                    addDict(entities, sb.toString());
                                }
                            }
                            r.close();
                            LOG.info("Save dictionaries...");
                            saveDict(tokens, new File(cmd.getOptionValue("o") + "/" + file.getName().replace(".spacy", "") + "_token.dict"));
                            saveDict(lemmas, new File(cmd.getOptionValue("o") + "/" + file.getName().replace(".spacy", "") + "_lemmas.dict"));
                            saveDict(pos, new File(cmd.getOptionValue("o") + "/" + file.getName().replace(".spacy", "") + "_pos.dict"));
                            saveDict(entities, new File(cmd.getOptionValue("o") + "/" + file.getName().replace(".spacy", "") + "_entities.dict"));
                        } catch (IOException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                    }
                }
            } else {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("Build dictionaries per year for tokens, lemmas, pos-tags and entities.", options, true);
            }
        } catch (ParseException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

}
