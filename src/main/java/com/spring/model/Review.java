package com.spring.model;


import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Review {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long reviewId;
        private String content;
        private Integer movieRating;
        private Long dateTimeMilli;
        private Integer likes;
        @ElementCollection
        private List<String> comments;

        @ManyToOne
        private User user;

        @ManyToOne
        private Movie movie;

        @ManyToMany(mappedBy = "likedReviews")
        private Set<User> likedReviewsUsers = new HashSet<>();

        public Long getReviewId() {
                return reviewId;
        }

        public void setReviewId(Long reviewId) {
                this.reviewId = reviewId;
        }

        public User getUser() {
                return user;
        }

        public void setUser(User user) {
                this.user = user;
        }

        public String getContent() {
                return content;
        }

        public void setContent(String content) {
                this.content = content;
        }

        public Integer getMovieRating() {
                return movieRating;
        }

        public void setMovieRating(Integer movieRating) {
                this.movieRating = movieRating;
        }

        public Integer getLikes() {
                return likes;
        }

        public void setLikes(Integer likes) {
                this.likes = likes;
        }

        public List<String> getComments() {
                return comments;
        }

        public void setComments(List<String> comments) {
                this.comments = comments;
        }

        public Movie getMovie() {
                return movie;
        }

        public Long getDateTimeMilli() {
                return dateTimeMilli;
        }

        public void setDateTimeMilli(Long dateTimeMilli) {
                this.dateTimeMilli = dateTimeMilli;
        }

        public void setMovie(Movie movie) {
                this.movie = movie;
        }

        public Set<User> getLikedReviewsUsers() {
                return likedReviewsUsers;
        }
        public void setLikedReviewsUsers(Set<User> likedReviewsUsers) {
                this.likedReviewsUsers = likedReviewsUsers;
        }
        public void addUserToLikedReviewsUsers(User user) { likedReviewsUsers.add(user); }

        public String getDateTimeString(){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(getDateTimeMilli()), ZoneId.systemDefault());
                return ldt.format(formatter);
        }
}
