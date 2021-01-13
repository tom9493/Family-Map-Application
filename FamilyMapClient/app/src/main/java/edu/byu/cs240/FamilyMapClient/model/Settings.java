package edu.byu.cs240.FamilyMapClient.model;

public class Settings {

    private boolean storyLines;
    private boolean familyTree;
    private boolean spouse;
    private boolean father;
    private boolean mother;
    private boolean male;
    private boolean female;

    public Settings() {
        storyLines = true;
        familyTree = true;
        spouse = true;
        father = true;
        mother = true;
        male = true;
        female = true;
    }

    public boolean isStoryLines() {
        return storyLines;
    }

    public void setStoryLines(boolean storyLines) {
        this.storyLines = storyLines;
    }

    public boolean isFamilyTree() {
        return familyTree;
    }

    public void setFamilyTree(boolean familyTree) {
        this.familyTree = familyTree;
    }

    public boolean isSpouse() {
        return spouse;
    }

    public void setSpouse(boolean spouse) {
        this.spouse = spouse;
    }

    public boolean isFather() {
        return father;
    }

    public void setFather(boolean father) {
        this.father = father;
    }

    public boolean isMother() {
        return mother;
    }

    public void setMother(boolean mother) {
        this.mother = mother;
    }

    public boolean isMale() {
        return male;
    }

    public void setMale(boolean male) {
        this.male = male;
    }

    public boolean isFemale() {
        return female;
    }

    public void setFemale(boolean female) {
        this.female = female;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "storyLines=" + storyLines + '\n' +
                ", familyTree=" + familyTree + '\n' +
                ", spouse=" + spouse + '\n' +
                ", father=" + father + '\n' +
                ", mother=" + mother + '\n' +
                ", male=" + male + '\n' +
                ", female=" + female + '}';
    }
}
