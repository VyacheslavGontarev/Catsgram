package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

// Указываем, что класс PostService - является бином и его
// нужно добавить в контекст приложения
@Service
public class PostService {
    private final Map<Long, Post> posts = new HashMap<>();
    private final UserService userService;

    public PostService(UserService userService) {
        this.userService = userService;
    }

    public Collection<Post> findAll(int from, int size, String sort) {
        List<Post> postList = new ArrayList<>(posts.values());

        // Сортируем посты по дате создания
        if ("asc".equalsIgnoreCase(sort)) {
            postList.sort(Comparator.comparing(Post::getPostDate));
        } else if ("desc".equalsIgnoreCase(sort)) {
            postList.sort(Comparator.comparing(Post::getPostDate).reversed());
        }

        // Отбрасываем первые 'from' постов и берем 'size' постов
        return postList.stream()
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }


    public Optional<Post> findById(Long postId) {
        return Optional.ofNullable(posts.values().stream()
               .filter(x -> x.getId() == postId)
                .findFirst().orElseThrow(() -> new NotFoundException(String.format("Пост № %d не найден", postId))));
    }

    public Post create(Post post) {
        if (userService.findById(post.getAuthorId()).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + post.getAuthorId() + " не найден");
        }
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}