package commons;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long tagId;
    private String name;
    private String colour;

    /**
     * Constructor of tag
     * @param name name of the tag
     * @param colour colour of the tag
     */
    public Tag(String name, String colour) {
        this.name = name;
        this.colour = colour;
    }

    /**
     * Empty constructor
     */
    public Tag() {

    }

    /**
     * Turns the Tag into a human readable String
     * @return the name of the string
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Equals method.
     * @param o Tag to test equality on.
     * @return True or false depending on equality.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(tagId, tag.tagId);
    }

    /**
     * Getter of a name
     * @return name of the tag
     */
    public String getName() {
        return name;
    }

    /**
     * sets the name of the tag
     * @param name new name of the tag
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter of colour
     * @return the colour of the tag
     */
    public String getColour() {
        return colour;
    }

    /**
     * Sets the colour of the tag
     * @param colour new colour
     */
    public void setColour(String colour) {
        this.colour = colour;
    }

    /**
     * Getter method
     * @return id
     */
    public Long getTagId() {
        return tagId;
    }

    /**
     * Setter method
     * @param id id of tag
     */
    public void setTagId(Long id) {
        this.tagId = id;
    }

    /**
     * Equals method that only checks name.
     * @param o Tag to test equality on.
     * @return True or false depending on equality.
     */
    public boolean nameEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.name);
    }

    /**
     * Hashcode
     * @return the hashcode of the tag.
     */
    @Override
    public int hashCode() {
        return Objects.hash(tagId);
    }
}



