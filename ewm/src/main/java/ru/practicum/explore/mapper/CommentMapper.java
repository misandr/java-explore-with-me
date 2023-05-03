package ru.practicum.explore.mapper;


import ru.practicum.explore.dto.CommentDto;
import ru.practicum.explore.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {

        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setEvent(comment.getEvent().getId());
        commentDto.setCreated(comment.getCreated());
        commentDto.setEdited(comment.getEdited());
        commentDto.setAuthorName(comment.getAuthor().getName());

        return commentDto;
    }

    public static List<CommentDto> toCommentsDto(List<Comment> comments) {
        List<CommentDto> commentsDto = new ArrayList<>();

        for (Comment comment : comments) {
            commentsDto.add(CommentMapper.toCommentDto(comment));
        }

        return commentsDto;
    }
}
