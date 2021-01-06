package ca.cmpt213.a5client.model;

public class Tokimon {
    private Integer id;
    private String name;
    private Double weight;
    private Double height;
    private String ability;  // any valid String is a valid ability
    private Integer strength;
    private String color;

    public Tokimon(String name, Double weight, Double height, String ability, Integer strength, String color) {
        this.name = name;
        this.weight = weight;
        this.height = height;
        this.ability = ability;
        this.strength = strength;
        this.color = color;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public void setStrength(Integer strength) {
        this.strength = strength;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getWeight() {
        return weight;
    }

    public Double getHeight() {
        return height;
    }

    public String getAbility() {
        return ability;
    }

    public Integer getStrength() {
        return strength;
    }

    public String getColor() {
        return color;
    }
}
