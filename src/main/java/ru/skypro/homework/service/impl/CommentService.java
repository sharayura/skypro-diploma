package ru.skypro.homework.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CreateCommentDto;
import ru.skypro.homework.dto.ResponseWrapperComment;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    public final CommentMapper commentMapper;
    public final CommentRepository commentRepository;
    public final UserRepository userRepository;
    public final UserService userService;
    public final AdRepository adRepository;

    public CommentService(CommentMapper commentMapper, CommentRepository commentRepository, UserRepository userRepository, UserService userService, AdRepository adRepository) {
        this.commentMapper = commentMapper;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.adRepository = adRepository;
    }

    @Transactional(readOnly = true)
    public ResponseWrapperComment getComments(Integer id) {
        ResponseWrapperComment responseWrapperComment = new ResponseWrapperComment();
        List<Comment> commentList = commentRepository.findAllByAdId(id);
        responseWrapperComment.setResults(commentMapper.commentListToCommentDtoList(commentList));
        responseWrapperComment.setCount(commentList.size());
        return responseWrapperComment;
    }

    @Transactional
    public CommentDto addComment(Integer id, CreateCommentDto createCommentDto, Authentication authentication) {
        Comment comment = commentMapper.toComment(createCommentDto);
        comment.setAd(adRepository.findById(id).orElse(null));
        comment.setUser(userRepository.findByUsername(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found")));
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment);
        return commentMapper.toCommentDto(comment);
    }

    @Transactional
    public void deleteComment(int commentId) {
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public CommentDto updateComment(int commentId, CommentDto commentDto) {
        Comment updatedComment = commentRepository.findById(commentId).orElseThrow();
        updatedComment.setText(commentDto.getText());
        return commentMapper.toCommentDto(commentRepository.save(updatedComment));
    }

    @Transactional
    public void deleteCommentsByAdId(Integer adId) {
        commentRepository.deleteCommentsByAdId(adId);
    }

    public boolean hasCommentAccess(Integer CommentId) {
        Comment comment = commentRepository.findById(CommentId).orElseThrow();
        String currentUserRole = userService.getCurrentUserRole();
        String commentCreatorUsername = comment.getUser().getUsername();
        String currentUsername = userService.getCurrentUsername();
        return currentUserRole.equals("ADMIN") || commentCreatorUsername.equals(currentUsername);
    }

    public ResponseEntity<String> checkAccessComment (Integer id) {
        Comment comment = commentRepository.findById(id).orElseThrow();
        String currentUserRole = userService.getCurrentUserRole();
        String commentCreatorUsername = comment.getUser().getUsername();
        String currentUsername = userService.getCurrentUsername();

        if (!(currentUserRole.equals("ADMIN") || commentCreatorUsername.equals(currentUsername))) {
            return null;
        } else {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }
    }
}
