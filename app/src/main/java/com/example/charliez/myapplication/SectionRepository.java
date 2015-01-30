package com.example.charliez.myapplication;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

public class SectionRepository {
    private static SectionRepository ourInstance = new SectionRepository();

    public static SectionRepository getInstance() {
        return ourInstance;
    }

    /**
     * Gets a hashtable of sections
     * @return
     */
    public Hashtable<String, Section> getSections() {
        return mSections;
    }

    private Hashtable<String,Section> mSections;

    private SectionRepository() {
        mSections = new Hashtable<String, Section>();
    }

    public Section get(int index) {
        // Java sucks
        Iterator<Section> values = mSections.values().iterator();
        Section result = null;
        for(int i = 0; i < index; i++) {
            if (!values.hasNext()) {
                break;
            }
            result = values.next();
        }

        return result;
    }
}
