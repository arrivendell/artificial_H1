package agent.curator;

import static java.lang.Math.round;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author nightwish
 */
public class ArtWork {
    
    private static final double _factorThreshold = 0.7;
    
    private String name ;
    private String auteur;
    private List<String> tags = new ArrayList<String>();
    private String description;
    private long price;

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

    public long getPrice() {
        return price;
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
    public ArtWork(String nameIn, String descriptionIn, String auteurIn, String tagsIn, long myPrice) {
        name = nameIn;
        description=descriptionIn;
        auteur = auteurIn;
        tags = Arrays.asList(tagsIn.split(";"));
        this.price = round(myPrice*_factorThreshold);
    }

    @Override
    public String toString() {
        return name + "/" + auteur + "/" + description;
    }

    
    
    @Override
    // We consider 2 artwork equals by their name */
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj == null || obj.getClass() != getClass()) {
            result = false;
        } else {
            ArtWork aw = (ArtWork) obj;
            if (this.name.equals(aw.getName())) {
                result = true;
            }
        }
        return result;
    }

    
}
