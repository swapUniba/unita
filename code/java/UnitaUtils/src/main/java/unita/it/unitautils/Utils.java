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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author pierpaolo
 */
public class Utils {

    /**
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static Map<String, Integer> loadDict(File file) throws IOException {
        Map<String, Integer> dict = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while (reader.ready()) {
            String[] s = reader.readLine().split("\t");
            dict.put(s[0], Integer.parseInt(s[1]));
        }
        reader.close();
        return dict;
    }

    /**
     *
     * @param file
     * @param dict
     * @throws IOException
     */
    public static void saveDict(File file, Map<String, Integer> dict) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (Map.Entry<String, Integer> e : dict.entrySet()) {
            writer.append(e.getKey()).append("\t").append(e.getValue().toString());
            writer.newLine();
        }
        writer.close();
    }

    /**
     *
     * @param file
     * @param list
     * @throws IOException
     */
    public static void saveDictEntryList(File file, List<DictEntry> list) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (DictEntry e : list) {
            writer.append(e.getKey()).append("\t").append(String.valueOf(e.getOcc()));
            writer.newLine();
        }
        writer.close();
    }

    /**
     *
     * @param dict
     * @return
     */
    public static List<DictEntry> dictMap2List(Map<String, Integer> dict) {
        List<DictEntry> list = new ArrayList<>(dict.size());
        for (Map.Entry<String, Integer> e : dict.entrySet()) {
            list.add(new DictEntry(e.getKey(), e.getValue()));
        }
        Collections.sort(list, Collections.reverseOrder());
        return list;
    }

    /**
     *
     * @param dictdir
     * @param startyear
     * @param endyear
     * @param feature
     * @param minocc
     * @return
     * @throws IOException
     */
    public static Map<String, Integer> mergedicts(File dictdir, int startyear, int endyear, String feature, int minocc) throws IOException {
        Map<String, Integer> dict = new HashMap<>();
        File[] files = dictdir.listFiles();
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".dict") && file.getName().contains("_" + feature)) {
                Pattern p = Pattern.compile("[0-9]+");
                Matcher matcher = p.matcher(file.getName());
                matcher.find();
                int year = Integer.parseInt(matcher.group());
                if (year >= startyear && year <= endyear) {
                    Map<String, Integer> loadDict = loadDict(file);
                    for (String k : loadDict.keySet()) {
                        Integer v = loadDict.get(k);
                        if (v >= minocc) {
                            Integer get = dict.get(k);
                            if (get == null) {
                                dict.put(k, v);
                            } else {
                                dict.put(k, get + v);
                            }
                        }
                    }
                }
            }
        }
        return dict;
    }

    /**
     *
     * @param dict1
     * @param dict2
     * @return
     */
    public static Map<String, Integer> dictIntersection(Map<String, Integer> dict1, Map<String, Integer> dict2) {
        Map<String, Integer> r = new HashMap<>();
        for (String k : dict1.keySet()) {
            if (dict2.containsKey(k)) {
                r.put(k, dict1.get(k) + dict2.get(k));
            }
        }
        return r;
    }

    /**
     *
     * @param dict1
     * @param dict2
     * @return
     */
    public static Map<String, Integer> dictDiff(Map<String, Integer> dict1, Map<String, Integer> dict2) {
        Map<String, Integer> r = new HashMap<>();
        for (String k : dict1.keySet()) {
            if (!dict2.containsKey(k)) {
                r.put(k, dict1.get(k));
            }
        }
        return r;
    }

}
