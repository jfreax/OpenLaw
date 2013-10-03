package de.jdsoft.law.data.helper;

public class LawHeadline {


    public LawHeadline(int d, String h) {
        depth = d;
        headline = h;
    }

    public String headline;
    public int depth;

    public int pseudoDepth = -1;
    public int intend = -1;
    public int color = -1;
    public int padding = 0;
}
