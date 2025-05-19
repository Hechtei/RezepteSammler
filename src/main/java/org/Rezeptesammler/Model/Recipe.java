package org.Rezeptesammler.Model;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;

@Data
@Getter
@Setter
public class Recipe {

    @Id
    private String _id;

    private String _rev;

    private String name;

    private String link;

    private String caption;

    private String thumbnail;


    public Recipe(String _id, String _rev, String name, String link, String caption, String thumbnail) {
        this._id = _id;
        this._rev = _rev;
        this.name = name;
        this.link = link;
        this.caption = caption;
        this.thumbnail = thumbnail;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_rev() {
        return _rev;
    }

    public void set_rev(String _rev) {
        this._rev = _rev;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Recipe() {
    }


}
