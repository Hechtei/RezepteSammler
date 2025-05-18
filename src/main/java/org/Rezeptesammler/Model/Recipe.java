package org.Rezeptesammler.Model;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;

@Data
@Getter
@Setter
public class Recipe {

    @Id
    @GeneratedValue
    private String _id;

    private String _rev;

    private int name;


}
