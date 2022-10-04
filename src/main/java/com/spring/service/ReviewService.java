package com.spring.service;

import com.spring.model.Review;

import java.util.List;

public interface ReviewService {
    public void saveNewReview(Review review);

    public void deleteReview(Long id);

    public Review getSingleReview(Long id);



}
