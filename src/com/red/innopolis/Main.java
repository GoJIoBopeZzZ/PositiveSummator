package com.red.innopolis;

public class Main {
    
    public static void main(String[] args) {
        String path = "/Users/_red_/IdeaProjects/PossitiveStream/src/com/red/innopolis/Resources";
        ResourceThread test = new ResourceThread(path , 1);
        test.start();
    }

}
