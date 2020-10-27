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
public class DictFloatEntry implements Comparable<DictFloatEntry> {

    private String key;

    private float occ;

    /**
     *
     * @param key
     * @param occ
     */
    public DictFloatEntry(String key, float occ) {
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
    public float getOcc() {
        return occ;
    }

    /**
     *
     * @param occ
     */
    public void setOcc(float occ) {
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
        final DictFloatEntry other = (DictFloatEntry) obj;
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
    public int compareTo(DictFloatEntry o) {
        return Float.compare(occ, o.occ);
    }

}
