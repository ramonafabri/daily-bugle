package hu.progmasters.dailybugle.controller;

import hu.progmasters.dailybugle.dto.outgoing.HomePageResponse;
import hu.progmasters.dailybugle.service.HomeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/home")
@Slf4j
public class HomeController {


    private final HomeService homeService;


    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping
    public ResponseEntity<HomePageResponse> getHomePage() {
        log.info("Get home page");
        HomePageResponse result = homeService.getHomePage();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }




}
