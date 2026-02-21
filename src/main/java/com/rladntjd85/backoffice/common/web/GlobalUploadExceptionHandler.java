package com.rladntjd85.backoffice.common.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalUploadExceptionHandler {

    @ExceptionHandler({
            MaxUploadSizeExceededException.class,
            MultipartException.class,
            InvalidCsrfTokenException.class   // 👈 이거 추가
    })
    public String handleUpload(Exception e,
                               HttpServletRequest request,
                               RedirectAttributes ra) {

        ra.addFlashAttribute("error",
                "파일 용량은 최대 5MB까지 업로드할 수 있습니다.");

        String uri = request.getRequestURI();

        // 등록
        if ("/admin/products".equals(uri)) {
            return "redirect:/admin/products/new";
        }

        // 수정
        if (uri.matches("^/admin/products/\\d+$")) {
            String id = uri.substring(uri.lastIndexOf("/") + 1);
            return "redirect:/admin/products/" + id + "/edit";
        }

        return "redirect:/admin/products";
    }
}