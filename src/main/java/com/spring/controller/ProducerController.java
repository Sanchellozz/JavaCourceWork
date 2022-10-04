package com.spring.controller;

import com.spring.model.Producer;
import com.spring.service.ProducerService;
import com.spring.utility.FileUpload;
import com.spring.utility.constants.ImageType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin/producers")
public class ProducerController {

    @Autowired
    private ProducerService producerService;

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("producers", producerService.getProducers());
        return "admin/movie/producer/producers";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createProducerForm(@ModelAttribute("producer") Producer producer) {
        return "admin/movie/producer/producer_form";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createProducer(@RequestParam(value = "file") MultipartFile file, @Valid Producer producer, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/movie/producer/producer_form";
        }
        try {
            String path = FileUpload.saveImage(ImageType.CAST_DP, producer.getName(), file);
            producer.setImageUrl(path);
            producerService.saveProducer(producer);
        } catch (Exception e) {
            return "redirect:/admin/producers/";
        }
        return "redirect:/admin/producers/";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String updateProducerForm(@PathVariable("id") long id, Model model) {

        try {
            Producer producer = producerService.getProducerById(id);
            model.addAttribute("producerForm", producer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "admin/movie/producer/producer_update";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
    public String updateProducer(@PathVariable("id") long id, @Validated Producer producer, BindingResult result, Model model,
            @RequestParam(value = "file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            producer.setId(id);
            redirectAttributes.addFlashAttribute(producer);
            return "admin/movie/producer/producer_form";
        }

        try {
            if (!file.isEmpty()){
                String path = FileUpload.saveImage(ImageType.CAST_DP, producer.getName(), file);
                producer.setImageUrl(path);
            }
            else {
                producer.setImageUrl(producerService.getProducerById(id).getImageUrl());
            }
            producerService.saveProducer(producer);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(producer);
            return "admin/movie/producer/producer_form";
        }
        return "redirect:/admin/producers/";
    }

    @RequestMapping(value = "/delete/{id}")
    public String deleteProducer(@PathVariable("id") long id, RedirectAttributes redirAttr) {
        try {
            Producer producer = producerService.getProducerById(id);
            producerService.deleteProducer(producer);
        } catch (Exception e) {
            redirAttr.addFlashAttribute("error", "Нельзя удалить продюсера, который привязан к фильму");
            return "redirect:/admin/producers/";
        }

        return "redirect:/admin/producers/";
    }
}
