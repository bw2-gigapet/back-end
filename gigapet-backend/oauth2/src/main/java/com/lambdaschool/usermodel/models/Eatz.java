package com.lambdaschool.usermodel.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lambdaschool.usermodel.logging.Loggable;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.persistence.TemporalType.TIMESTAMP;


/*
*       Eatz:
*
*   describes an individual meal for a gigapet,
*
*   as of 11/18 3 macronutrient fields are tracked, with 4th likely but blankable
*           fats :: integer
*           carbs :: integer //Carbohydrates
*           proteins :: integer
*           title :: string
*
*   id & createddate should be self evident
*
*
* */





@Loggable
@Entity
@Table(name = "eatz")
public class Eatz extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long eatzid;

    @CreatedDate
    @Temporal(TIMESTAMP)
    private Date createdDate;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int carbs;

    @Column(nullable = false)
    private int proteins;

    @Column(nullable = false)
    private int fats;

    public Eatz() {
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public long getEatzid() {
        return eatzid;
    }

    public void setEatzid(long eatzid) {
        this.eatzid = eatzid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCarbs() {
        return carbs;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    public int getProteins() {
        return proteins;
    }

    public void setProteins(int proteins) {
        this.proteins = proteins;
    }

    public int getFats() {
        return fats;
    }

    public void setFats(int fats) {
        this.fats = fats;
    }
}
/*

public class Books  extends Auditable {



    @Column(nullable = false,
            unique = true)
    private String ISBN;

    private int copy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sectionid",
            nullable = false)
    @JsonIgnoreProperties("books")
    private Section section;

    @ManyToMany
    @JoinTable(name = "wrote",
            joinColumns = @JoinColumn(name = "bookid"),
            inverseJoinColumns = @JoinColumn(name = "authorid"))
    @JsonIgnoreProperties("authorBooks")
    private List<Authors> authors = new ArrayList<>();*/