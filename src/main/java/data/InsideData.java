package data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InsideData {
    private String id;
    private String name;
    private int year;
    private String color;
    private String pantone_value;

}
