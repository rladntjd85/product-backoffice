package com.rladntjd85.backoffice.category.admin;

import com.rladntjd85.backoffice.category.dto.CategoryForm;
import com.rladntjd85.backoffice.category.service.AdminCategoryService;
import com.rladntjd85.backoffice.common.web.admin.BaseAdminController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryPageController extends BaseAdminController {

    private final AdminCategoryService adminCategoryService;

    @GetMapping
    public String list(Model model,
                       @RequestParam(required = false) String q,
                       @RequestParam(required = false, defaultValue = "") String status,
                       @RequestParam(defaultValue = "0") int page) {

        Page<?> result = adminCategoryService.search(q, status, page, 20);

        model.addAttribute("result", result);
        model.addAttribute("totalCount", adminCategoryService.totalCount(q));
        model.addAttribute("activeCount", adminCategoryService.activeCount(q));
        model.addAttribute("inactiveCount", adminCategoryService.inactiveCount(q));
        model.addAttribute("q", q);
        model.addAttribute("status", status);

        return render(model, "카테고리 목록", "admin/categories/list");
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("mode", "CREATE");
        model.addAttribute("form", new CategoryForm(null, ""));
        return render(model, "카테고리 등록", "admin/categories/form");
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        var c = adminCategoryService.get(id);

        model.addAttribute("mode", "EDIT");
        model.addAttribute("categoryId", id);
        model.addAttribute("form", new CategoryForm(c.getId(), c.getName()));
        model.addAttribute("enabled", c.isEnabled());

        return render(model, "카테고리 수정", "admin/categories/form");
    }

    // 등록
    @PostMapping
    public String create(@Valid @ModelAttribute("form") CategoryForm form,
                         BindingResult br,
                         Model model) {

        if (br.hasErrors()) {
            model.addAttribute("mode", "CREATE");
            return render(model, "카테고리 등록", "admin/categories/form");
        }

        try {
            adminCategoryService.create(form);
            return "redirect:/admin/categories";
        } catch (IllegalArgumentException e) {
            model.addAttribute("mode", "CREATE");
            model.addAttribute("error", e.getMessage());
            return render(model, "카테고리 등록", "admin/categories/form");
        }
    }

    @PostMapping("/{id}/rename")
    public String rename(@PathVariable Long id,
                         @Valid @ModelAttribute("form") CategoryForm form,
                         BindingResult br,
                         Model model) {

        if (br.hasErrors()) {
            model.addAttribute("mode", "EDIT");
            model.addAttribute("categoryId", id);
            model.addAttribute("enabled", adminCategoryService.get(id).isEnabled());
            return render(model, "카테고리 수정", "admin/categories/form");
        }

        try {
            adminCategoryService.rename(id, form.name());
            return "redirect:/admin/categories";
        } catch (IllegalArgumentException e) {
            var c = adminCategoryService.get(id);
            model.addAttribute("mode", "EDIT");
            model.addAttribute("categoryId", id);
            model.addAttribute("enabled", c.isEnabled());
            model.addAttribute("error", e.getMessage());
            return render(model, "카테고리 수정", "admin/categories/form");
        }
    }

    @PostMapping("/{id}/enable")
    public String enable(@PathVariable Long id) {
        adminCategoryService.enable(id);
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/disable")
    public String disable(@PathVariable Long id) {
        adminCategoryService.disable(id);
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        adminCategoryService.delete(id);
        return "redirect:/admin/categories";
    }
}