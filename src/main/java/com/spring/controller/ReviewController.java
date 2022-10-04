package com.spring.controller;

import com.spring.repository.UserRepository;
import com.spring.model.Movie;
import com.spring.model.Review;
import com.spring.model.User;
import com.spring.security.MyUserDetails;
import com.spring.service.AuthService;
import com.spring.service.MovieService;
import com.spring.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Set;

@Controller
@RequestMapping("/movies/{movie_id}/review")
public class ReviewController {
        @Autowired
        private ReviewService reviewService;

        @Autowired
        private MovieService movieService;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private AuthService authService;

        @Autowired
        private UserRepository userService;

        @PostMapping("/new/{user_id}")
        public String addNewReview(@AuthenticationPrincipal MyUserDetails principal,@PathVariable(value = "movie_id") Long movie_id,
                        @PathVariable(value = "user_id") Long user_id, @ModelAttribute("new_review") Review review) {
                try {
                        User user = authService.profile(principal);
                        Set<Review> reviews = user.getReview();
                        for (Review r: reviews) {
                               Movie movie = r.getMovie();
                               if (movie.getId() == movie_id){
                                       return "redirect:/movies/" + movie_id + "/review/edit/" + r.getReviewId();
                               }
                        }
                        review.setDateTimeMilli(System.currentTimeMillis());
                        review.setMovie(movieService.getMovieById(movie_id));
                        review.setUser(user);
                        review.setLikes(0);
                        user.setReview(review);
                        reviewService.saveNewReview(review);
                        userService.save(user);
                } catch (Exception e) {
                        e.printStackTrace();
                }
                return "redirect:/movies/" + movie_id;
        }

        @GetMapping("/edit/{review_id}")
        public String editReviewView(@PathVariable(value = "movie_id") Long movie_id,
                                     @PathVariable(value = "review_id") Long review_id, Model model, HttpSession session) {
                try {
                        Review review = reviewService.getSingleReview(review_id);
                        session.setAttribute("session_review", review);
                        model.addAttribute("review", review);
                        model.addAttribute("movie", movieService.getMovieById(movie_id));
                } catch (Exception e) {
                        e.printStackTrace();
                }
                return "user/movie/review/edit_review";
        }

        @PostMapping("/edit/")
        public String editReview(@PathVariable(value = "movie_id") Long movie_id, @ModelAttribute("review") Review review, HttpSession session) {
                Review temp_review = (Review) session.getAttribute("session_review");
                review.setReviewId(temp_review.getReviewId());
                review.setUser(temp_review.getUser());
                review.setDateTimeMilli(temp_review.getDateTimeMilli());
                review.setLikes(temp_review.getLikes());
                review.setMovie(temp_review.getMovie());

                reviewService.saveNewReview(review);
                return "redirect:/movies/" + movie_id;
        }

        @GetMapping("/delete/{review_id}")
        public String deleteReview(@PathVariable(value = "movie_id") Long movie_id,
                        @PathVariable(value = "review_id") Long review_id) {
                Review review = reviewService.getSingleReview(review_id);
                Set<User> fav = review.getLikedReviewsUsers();
                for (User u:fav) {
                        u.removeLikedReviews(review_id);
                }
                reviewService.deleteReview(review_id);
                return "redirect:/movies/" + movie_id;
        }

        @GetMapping("/like/{review_id}")
        public String LikeReview(@AuthenticationPrincipal MyUserDetails principal, @PathVariable(value = "movie_id") Long movie_id,
                                 @PathVariable(value = "review_id") Long review_id) {
                try {
                        int l = 0;
                        Review review = reviewService.getSingleReview(review_id);
                        User user = authService.profile(principal);
                        Set<Review> set = user.getLikedReviews();
                        l = review.getLikes();
                        for (Review r:set) {
                                if (r.getReviewId() == review.getReviewId()){
                                        if(review.getLikes() != 0){
                                        review.setLikes(l-1);
                                        }
                                        user.removeLikedReviews(review_id);
                                        reviewService.saveNewReview(review);
                                        userService.save(user);
                                        return "redirect:/movies/" + movie_id;
                                }
                        }
                        review.setLikes(l+1);
                        user.addReviewToLikedReviews(review);
                        reviewService.saveNewReview(review);
                        userService.save(user);
                } catch (Exception e) {
                        e.printStackTrace();
                }
                return "redirect:/movies/" + movie_id;
        }

        @RequestMapping("/reviews/")
        public String getUserReviews(@AuthenticationPrincipal MyUserDetails principal, Model model) {
                User user = authService.profile(principal);
                Set<Review>  reviews = user.getReview();
                model.addAttribute("reviews", reviews);
                return "ureviews";
        }


}
