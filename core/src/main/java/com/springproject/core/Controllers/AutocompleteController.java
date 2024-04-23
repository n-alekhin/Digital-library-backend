package com.springproject.core.Controllers;

import com.springproject.core.Services.AutocompleteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/autocomplete")
@RequiredArgsConstructor
public class AutocompleteController {
    private final AutocompleteService autocompleteService;
    @GetMapping("/title/{part}")
    public List<String> autocompleteTitle(
            @PathVariable String part
    ) {
        //String name = ((JwtAuthentication) SecurityContextHolder.getContext().getAuthentication()).getName();
        return autocompleteService.autocompleteTitle(part);
    }

    @GetMapping("/authors/{authors}")
    public List<String> autocompleteAuthors(
            @PathVariable String authors
    ) {
        //String name = ((JwtAuthentication) SecurityContextHolder.getContext().getAuthentication()).getName();
        return autocompleteService.autocompleteAuthors(authors);
    }
}
