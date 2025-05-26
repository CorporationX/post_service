//package faang.school.postservice.validator;
//
//import faang.school.postservice.dto.comment.CommentDto;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//public class CommentValidator {
//
//    @Autowired
//    private List<CommentValidationFilter> validationFilters;
//
//    public void validate(CommentDto commentDto) {
//        for (CommentValidationFilter filter : validationFilters) {
//            if(filter.isApplicable(commentDto.getStatus())){
//                filter.apply(commentDto);
//            }
//        }
//    }
//}
