package com.rladntjd85.backoffice.category.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryPageController {

    @GetMapping
    public String list(Model model,
                       @RequestParam(required = false) String q,
                       @RequestParam(required = false, defaultValue = "ACTIVE") String status) {

        model.addAttribute("pageTitle", "카테고리 목록");
        model.addAttribute("content", "admin/categories/list :: content");

        // 검색값 유지용
        model.addAttribute("q", q);
        model.addAttribute("status", status);

        return "layout/admin-layout";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("pageTitle", "카테고리 등록");
        model.addAttribute("content", "admin/categories/form :: content");
        model.addAttribute("mode", "CREATE");
        return "layout/admin-layout";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "카테고리 수정");
        model.addAttribute("content", "admin/categories/form :: content");
        model.addAttribute("mode", "EDIT");
        model.addAttribute("categoryId", id);
        return "layout/admin-layout";
    }

    // 저장(등록/수정) 자리
    @PostMapping
    public String save() {
        // TODO: DTO + Service 연결
        return "redirect:/admin/categories";
    }

    // 삭제(상태변경) 자리: 실제 delete 금지 정책이면 status 변경으로 처리
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        // TODO: status=DELETED 같은 soft delete
        return "redirect:/admin/categories";
    }

    // 비활성(노출중지) 자리
    @PostMapping("/{id}/inactivate")
    public String inactivate(@PathVariable Long id) {
        // TODO: status=INACTIVE
        return "redirect:/admin/categories";
    }
}
