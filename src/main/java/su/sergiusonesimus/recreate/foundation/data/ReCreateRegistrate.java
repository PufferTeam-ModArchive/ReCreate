package su.sergiusonesimus.recreate.foundation.data;

import java.util.IdentityHashMap;
import java.util.Map;

import su.sergiusonesimus.recreate.content.AllSections;

public class ReCreateRegistrate {

    /* Section Tracking */

    private static Map<Object, AllSections> sectionLookup = new IdentityHashMap<>();
    private AllSections section;

    public ReCreateRegistrate startSection(AllSections section) {
        this.section = section;
        return this;
    }

    public AllSections currentSection() {
        return section;
    }

    public void addToSection(Object entry, AllSections section) {
        sectionLookup.put(entry, section);
    }

    public AllSections getSection(Object entry) {
        return sectionLookup.getOrDefault(entry, AllSections.UNASSIGNED);
    }

}
