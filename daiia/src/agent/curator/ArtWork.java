/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.curator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author nightwish
 */
public class ArtWork {
    private String name ;
    private String auteur;
    private List<String> tags = new ArrayList<String>();
    private String description;

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
    
    public void addTag(String tag) {
            this.tags.add(tag);
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getAuteur() {
        return auteur;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getDescription() {
        return description;
    }
    
    public ArtWork() {
    }
    public ArtWork(String nameIn) {
        name = nameIn;
    }
    public ArtWork(String nameIn, String descriptionIn, String auteurIn, String tagsIn) {
        name = nameIn;
        description=descriptionIn;
        auteur = auteurIn;
        tags = Arrays.asList(tagsIn.split(";"));
    }

    @Override
    public String toString() {
        return name + "/" + auteur + "/" + description;
    }

    
    
    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj == null || obj.getClass() != getClass()) {
            result = false;
        } else {
            ArtWork aw = (ArtWork) obj;
            if (this.name == aw.getName()) {
                result = true;
            }
        }
        return result;
    }

    
}
