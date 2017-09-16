package application.ucsearch;

import java.util.ArrayList;

public class NamePicsUrl {

    public ArrayList<String> nameArrayList = new ArrayList<>();
    public ArrayList<String> avatarArrayList = new ArrayList<>();
    public ArrayList<String> urlArrayList = new ArrayList<>();

    public NamePicsUrl() {

    }

    public void add(String name, String avatar, String url) {
        nameArrayList.add(name);
        avatarArrayList.add(avatar);
        urlArrayList.add(url);
    }

    //method to arrange array alphabetically

}