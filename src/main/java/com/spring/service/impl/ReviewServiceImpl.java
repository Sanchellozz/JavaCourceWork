package com.spring.service.impl;

import com.spring.repository.ReviewRepository;
import com.spring.model.Review;
import com.spring.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    ReviewRepository reviewdao;

    @Override
    public void saveNewReview(Review review) {
        reviewdao.save(review);
    }

    @Override
    public void deleteReview(Long id) {
        reviewdao.deleteById(id);
    }

    @Override
    public Review getSingleReview(Long id) {
        return reviewdao.findById(id).orElse(new Review());
    }




}
