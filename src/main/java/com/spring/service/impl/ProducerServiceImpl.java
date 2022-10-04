package com.spring.service.impl;

import java.util.List;

import com.spring.repository.ProducerRepository;
import com.spring.model.Producer;
import com.spring.service.ProducerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProducerServiceImpl implements ProducerService {

    @Autowired
    private ProducerRepository producerRepository;

    @Override
    public void saveProducer(Producer cast) {
        producerRepository.save(cast);

    }

    @Override
    public List<Producer> getProducers() {
        return producerRepository.findAll();
    }

    @Override
    public void deleteProducer(Producer producer) {

        producerRepository.delete(producer);

    }

    @Override
    public Producer getProducerById(Long id) {

        return producerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Prod id: " + id));
    }

}
