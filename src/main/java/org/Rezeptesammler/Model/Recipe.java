package org.Rezeptesammler.Model;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;

import java.util.List;

@Data
public class Recipe {

    @Id
    private String _id;

    private String _rev;

    private String name;

    private String link;

    private String caption;

    private String thumbnail;

    private String category;

    private String difficulty;

    private String duration;

    private String summary;

    private List<String> ingredients;

    private List<String> steps;


    public Recipe(String _id, String _rev, String name, String link, String caption, String thumbnail, String category, String difficulty, String duration, String summary, List<String> ingredients, List<String> steps) {
        this._id = _id;
        this._rev = _rev;
        this.name = name;
        this.link = link;
        this.caption = caption;
        this.thumbnail = thumbnail;
        this.category = category;
        this.difficulty = difficulty;
        this.duration = duration;
        this.summary = summary;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
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

    @Override
    public String toString() {
        return "Recipe{" +
                "_id='" + _id + '\'' +
                ", _rev='" + _rev + '\'' +
                ", name='" + name + '\'' +
                ", link='" + link + '\'' +
                ", caption='" + caption + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", category='" + category + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", duration='" + duration + '\'' +
                ", summary='" + summary + '\'' +
                ", ingredients=" + ingredients +
                ", steps=" + steps +
                '}';
    }
}
