/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.profiler;

import java.util.ArrayList;

/**
 *
 * @author Nabil
 */
class Profile {
    private String name;
    private int age;
    private String occupation;
    private String gender;
    private ArrayList<String> interests;
    
    public Profile(){
        
    }
    
    public Profile(String name, int age, String occupation, String gender, ArrayList<String> interests){
        this.name = name;
        this.age = age;
        this.occupation = occupation;
        this.gender = gender;
        this.interests = interests;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOcupancy(String ocupancy) {
        this.occupation = ocupancy;
    }

    public ArrayList<String> getInterests() {
        return interests;
    }

    public void setInterests(ArrayList<String> interests) {
        this.interests = interests;
    }
    
    
    
}
