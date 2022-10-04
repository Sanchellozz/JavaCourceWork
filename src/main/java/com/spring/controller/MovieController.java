package com.spring.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.spring.repository.UserRepository;
import com.spring.model.Movie;
import com.spring.model.Producer;
import com.spring.model.Review;
import com.spring.model.User;
import com.spring.security.MyUserDetails;
import com.spring.service.ProducerService;
import com.spring.service.AuthService;
import com.spring.service.MovieService;
import com.spring.service.ReviewService;
import com.spring.utility.FileUpload;
import com.spring.utility.constants.ImageType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Controller
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private ProducerService producerService;

    @Autowired
    private UserRepository userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ReviewService reviewService;


    @RequestMapping("/movies")
    public String getHomeMovies(Model model) {
        return getMoviesWithPagination(1,"rating", "asc", model);
    }

    @RequestMapping("/movies/{id}")
    public String getMoviePreview(@PathVariable("id") long id, Model model) {
        try {
            double z = 0, n = 0;
            Movie movie = movieService.getMovieById(id);
            List<Review> list = movie.getReviews();
            for (Review r : list) {
                z = z + r.getMovieRating();
                n = z / list.size();
            }
            movie.setRating(n);
            model.addAttribute("movie", movie);
            model.addAttribute("new_review", new Review());
            movieService.saveMovie(movie);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "user/movie/movie_preview";
    }

    @RequestMapping("/admin/movies")
    public String getMovies(Model model) {
        List<Movie> movies = movieService.getMovies();
        model.addAttribute("movies", movies);
        return "admin/movie/movies";
    }

    @RequestMapping(value = "/admin/movies/add", method = RequestMethod.GET)
    public String addMovieForm(@ModelAttribute("movie") Movie movie, Model model) {
        List<Producer> producers = producerService.getProducers();
        model.addAttribute("producers", producers);
        return "admin/movie/movie_form";
    }

    @RequestMapping(value = "/admin/movies/add", method = RequestMethod.POST)
    public String addMovie(@RequestParam(value = "file") MultipartFile file, @Valid Movie movie, BindingResult result, Model model) {
        if (result.hasErrors()) {
            List<Producer> producers = producerService.getProducers();
            model.addAttribute("producers", producers);
            return "admin/movie/movie_form";
        }
        try {
            String path = FileUpload.saveImage(ImageType.MOVIE_POSTER, movie.getName(), file);
            movie.setPoster(path);
            movieService.saveMovie(movie);
        } catch (Exception e) {
            return "admin/movie/movie_form";
        }
        return "redirect:/admin/movies/";
    }

    @RequestMapping(value = "/admin/movies/edit/{id}", method = RequestMethod.GET)
    public String updateMovieForm(@PathVariable("id") long id, Model model) {
        List<Producer> producers = producerService.getProducers();
        model.addAttribute("producers", producers);
        try {
            Movie movie = movieService.getMovieById(id);
            model.addAttribute("movieForm", movie);
        } catch (Exception e) {

            e.printStackTrace();
        }

        return "admin/movie/movie_update";
    }

    @RequestMapping(value = "/admin/movies/edit/{id}", method = RequestMethod.POST)
    public String updateMovie(@PathVariable("id") long id, Movie movie, BindingResult result, Model model,
                              @RequestParam(value = "file", required = false) MultipartFile file) {
        if (result.hasErrors()) {
            return "redirect:/admin/movie/movie_form";
        }
        try {
            if (!file.isEmpty()) {
                String path = FileUpload.saveImage(ImageType.MOVIE_POSTER, movie.getName(), file);
                movie.setPoster(path);
            } else {
                movie.setPoster(movieService.getMovieById(id).getPoster());
            }
            movieService.saveMovie(movie);
        } catch (Exception e) {
            return "admin/movie/movie_form";
        }
        return "redirect:/admin/movies/";
    }

    @RequestMapping("/admin/movies/delete/{id}")
    public String deleteMovie(@PathVariable("id") long id, Model model) {
        try {
            Movie movie = movieService.getMovieById(id);
            Set<User> fav = movie.getFavouriteMovieUsers();
            Set<User> wat = movie.getWatchlistUsers();
            List<Review> reviews = movie.getReviews();
            Set <User> users;
            for (Review r: reviews) {
                users = r.getLikedReviewsUsers();
                for (User u: users) {
                    u.removeLikedReviews(r.getReviewId());
                    reviewService.deleteReview(r.getReviewId());
                }
            }
            for (User u:fav) {
                u.removeMovieFromFavourite(id);
            }
            for (User u:wat) {
                u.removeMovieFromWatchlist(id);
            }

            movieService.deleteMovie(movie);
        } catch (Exception e) {

            e.printStackTrace();
        }

        return "redirect:/admin/movies/";
    }

    @RequestMapping(value = "/movies/{movie_id}/add-to-watchlist", method = RequestMethod.GET)
    public String addMovieToWatchlist(@AuthenticationPrincipal MyUserDetails principal, @PathVariable("movie_id") long movieId) {
        try {
            Movie movie = movieService.getMovieById(movieId);
            User user = authService.profile(principal);
            user.addMovieToWatchlist(movie);
            userService.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/movies/watch-listed-movies";
    }

    @RequestMapping(value = "/movies/{movie_id}/remove-from-watchlist", method = RequestMethod.GET)
    public String removeMovieFromWatchlist(@AuthenticationPrincipal MyUserDetails principal, @PathVariable("movie_id") long movieId) {
        try {
            Movie movie = movieService.getMovieById(movieId);
            User user = authService.profile(principal);
            user.removeMovieFromWatchlist(movie.getId());
            userService.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/movies/watch-listed-movies";
    }

    @RequestMapping(value = "/movies/watch-listed-movies/clear-all", method = RequestMethod.GET)
    public String clearWatchList(@AuthenticationPrincipal MyUserDetails principal, Model model) {
        try {
            User user = authService.profile(principal);
            user.setWatchListedMovies(new HashSet<>());
            userService.save(user);
            Set<Movie> movies = user.getWatchListedMovies();
            model.addAttribute("movies", movies);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/movies/watch-listed-movies";
    }

    @RequestMapping(value = "/movies/watch-listed-movies", method = RequestMethod.GET)
    public String showWatchListedMovies(@AuthenticationPrincipal MyUserDetails principal, Model model) {
        try {
            User user = authService.profile(principal);
            Set<Movie> movies = user.getWatchListedMovies();
            model.addAttribute("movies", movies);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "watchlist";
    }

    @RequestMapping(value = "/movies/{movie_id}/add-to-favourite", method = RequestMethod.GET)
    public String addMovieToFavourite(@AuthenticationPrincipal MyUserDetails principal, @PathVariable("movie_id") long movieId) {
        try {
            Movie movie = movieService.getMovieById(movieId);
            User user = authService.profile(principal);
            user.addMovieToFavourite(movie);
            userService.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/movies/favourite-movies";
    }


    @RequestMapping(value = "/movies/{movie_id}/remove-from-favourite", method = RequestMethod.GET)
    public String removeMovieFromFavourite(@AuthenticationPrincipal MyUserDetails principal, @PathVariable("movie_id") long movieId) {
        try {
            Movie movie = movieService.getMovieById(movieId);
            User user = authService.profile(principal);
            user.removeMovieFromFavourite(movie.getId());
            userService.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/movies/favourite-movies";
    }

    @RequestMapping(value = "/movies/favourite-movies/clear-all", method = RequestMethod.GET)
    public String clearFavouriteMovies(@AuthenticationPrincipal MyUserDetails principal, Model model) {
        try {
            User user = authService.profile(principal);
            user.setFavouriteMovies(new HashSet<>());
            userService.save(user);
            Set<Movie> movies = user.getWatchListedMovies();
            model.addAttribute("movies", movies);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/movies/favourite-movies";
    }

    @RequestMapping(value = "/movies/favourite-movies", method = RequestMethod.GET)
    public String showFavouriteMovies(@AuthenticationPrincipal MyUserDetails principal, Model model) {
        try {
            User user = authService.profile(principal);
            Set<Movie> movies = user.getFavouriteMovies();
            model.addAttribute("movies", movies);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "favourite";
    }

    @RequestMapping("/user_reviews/")
    public String getUserReviews(@AuthenticationPrincipal MyUserDetails principal, Model model) {
        User user = authService.profile(principal);
        Set<Review>  reviews = user.getReview();
        model.addAttribute("reviews", reviews);
        return "/user/movie/review/user_reviews";
    }

    @RequestMapping("/page/{pageNo}")
    public String getMoviesWithPagination(@PathVariable (value = "pageNo") int pageNo,
                                          @RequestParam("sortField") String sortField,
                                          @RequestParam("sortDir") String sortDir,
                                          Model model) {
        int pageSize = 3;
        Page<Movie> page = movieService.findMovieWithPagination(pageNo,pageSize,sortField,sortDir);
        List<Movie> movies = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc")? "desc" : "asc");

        model.addAttribute("movies", movies);
        return "user/home";
    }



}
