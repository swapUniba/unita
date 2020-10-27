/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unita.it.unitautils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pierpaolo
 */
public class VerticalFormatReader implements Iterator<String[]> {

    private final File file;

    private BufferedReader reader = null;

    private StringBuilder sb;

    /**
     *
     * @param file
     */
    public VerticalFormatReader(File file) {
        this.file = file;
    }

    /**
     *
     * @throws IOException
     */
    public void open() throws IOException {
        close();
        reader = new BufferedReader(new FileReader(file));
    }

    /**
     *
     * @throws IOException
     */
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }

    @Override
    public boolean hasNext() {
        try {
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.equals("<p>") || line.equals("</p>")) {

                } else if (line.equals("<s>")) {
                    sb = new StringBuilder();
                } else if (line.equals("</s>")) {
                    break;
                } else {
                    sb.append(line).append("\n");
                }
            }
            return reader.ready();
        } catch (IOException ex) {
            Logger.getLogger(VerticalFormatReader.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public String[] next() {
        return sb.toString().trim().split("\n");
    }

}
