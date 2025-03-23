package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Collection<Post> findAll(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sort) {

        // Проверка, что size больше нуля
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than zero.");
        }

        // Проверка на допустимые значения для sort
        if (!"asc".equalsIgnoreCase(sort) && !"desc".equalsIgnoreCase(sort)) {
            throw new IllegalArgumentException("Sort must be either 'asc' or 'desc'.");
        }
        return postService.findAll(from, size, sort);
    }

    @GetMapping("/posts/{postId}")
    public Post findById(@PathVariable Long postId) {
        Optional<Post> post = postService.findById(postId);
        if (post.isEmpty()) {
            return null;
        }
        return post.get();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }
}