/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unita.it.unitautils;

import java.util.Objects;

/**
 *
 * @author pierpaolo
 */
public class DictEntry implements Comparable<DictEntry> {

    private String key;

    private int occ;

    /**
     *
     * @param key
     * @param occ
     */
    public DictEntry(String key, int occ) {
        this.key = key;
        this.occ = occ;
    }

    /**
     *
     * @return
     */
    public String getKey() {
        return key;
    }

    /**
     *
     * @param key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     *
     * @return
     */
    public int getOcc() {
        return occ;
    }

    /**
     *
     * @param occ
     */
    public void setOcc(int occ) {
        this.occ = occ;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.key);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DictEntry other = (DictEntry) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DictEntry{" + "key=" + key + ", occ=" + occ + '}';
    }

    @Override
    public int compareTo(DictEntry o) {
        return Integer.compare(occ, o.occ);
    }

}
