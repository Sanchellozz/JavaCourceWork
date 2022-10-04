package com.spring.service;

import java.util.List;

import com.spring.model.Movie;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface MovieService {
    void saveMovie(Movie movie);

    List<Movie> getMovies();

    void deleteMovie(Movie movie);

    Movie getMovieById(Long id) throws Exception;

    Page<Movie> findMovieWithPagination(int offset, int pageSize, String sortField, String sortDirection);

}
