package com.rladntjd85.backoffice.category.admin;

import com.rladntjd85.backoffice.common.web.admin.BaseAdminController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryPageController extends BaseAdminController {

    @GetMapping
    public String list(Model model,
                       @RequestParam(required = false) String q,
                       @RequestParam(required = false, defaultValue = "ACTIVE") String status) {

        model.addAttribute("q", q);
        model.addAttribute("status", status);

        return render(model, "카테고리 목록", "admin/categories/list");
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("mode", "CREATE");
        return render(model, "카테고리 등록", "admin/categories/form");
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("mode", "EDIT");
        model.addAttribute("categoryId", id);
        return render(model, "카테고리 수정", "admin/categories/form");
    }

    // 저장(등록/수정)
    @PostMapping
    public String save() {
        // TODO: DTO + Service 연결
        return "redirect:/admin/categories";
    }

    // 삭제(soft delete)
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        // TODO: status=DELETED 처리
        return "redirect:/admin/categories";
    }

    // 비활성 처리
    @PostMapping("/{id}/inactivate")
    public String inactivate(@PathVariable Long id) {
        // TODO: status=INACTIVE 처리
        return "redirect:/admin/categories";
    }
}
