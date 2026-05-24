package model.plogic;


import java.util.ArrayList;

/**
 * Represents a propositional atom as in simple propositional logic. It is represented by a single character and can be
 * a logical formula by itself.
 */

public class Atom extends Formula {

    private final String name;
    private final Integer value;
    private static ArrayList<String> valueIndex = new ArrayList<>();

    public Atom(String name) {
        this.name = name;
        this.value = assignValue(name);
    }

    private Integer assignValue(String name) {
        if(!valueIndex.contains(name)) {
            valueIndex.add(name);
        }
        return valueIndex.indexOf(name) + 1;
    }

    @Override
    public ArrayList<Formula> getElements() {
        ArrayList<Formula> list = new ArrayList<Formula>();
        list.add(this);
        return list;
    }

    @Override
    public Boolean isValid() {
        return name != null;
    }

    @Override
    public Formula toCNF() {
        return this;
    }

    @Override
    public String toString() {
        return name;
    }

    public Integer getValue() {
        return value;
    }
}
