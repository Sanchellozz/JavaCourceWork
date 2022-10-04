package com.spring.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;

@Entity(name = "producer")
@Table(name = "producer", uniqueConstraints = @UniqueConstraint(name = "producer_name_unique", columnNames = "name"))

public class Producer {

    @Id
    @SequenceGenerator(name = "producer_id_sequence", sequenceName = "producer_id_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "producer_id_sequence")
    @Column(name = "id", updatable = false)
    private long id;

    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(255)")
    @NotEmpty(message = "Заполните имя")
    private String name;

    @Column(name = "imageUrl", nullable = true, columnDefinition = "TEXT")
    private String imageUrl;

    @ManyToMany(mappedBy = "producers")
    private Set<Movie> movies;

    public Producer() {
    }

    public Producer(String name, String imageUrl, Set<Movie> movies) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.movies = movies;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Set<Movie> getMovies() {
        return movies;
    }

    public void setMovies(Set<Movie> movies) {
        this.movies = movies;
    }

    @Override
    public String toString() {
        return "Actor [id=" + id + ", name=" + name + ", imageUrl=" + imageUrl + "]";
    }

}
