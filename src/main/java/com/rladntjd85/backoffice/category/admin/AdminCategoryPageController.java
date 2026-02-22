package com.rladntjd85.backoffice.category.admin;

import com.rladntjd85.backoffice.category.dto.CategoryForm;
import com.rladntjd85.backoffice.category.service.AdminCategoryService;
import com.rladntjd85.backoffice.common.web.admin.BaseAdminController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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

        Page<?> result = adminCategoryService.search(q, status, page, 100);

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
        model.addAttribute("form", new CategoryForm(null, "", null));
        // 부모 카테고리 목록 전달
        model.addAttribute("parents", adminCategoryService.getParentList());
        return render(model, "카테고리 등록", "admin/categories/form");
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        var c = adminCategoryService.get(id);

        model.addAttribute("mode", "EDIT");
        model.addAttribute("categoryId", id);
        model.addAttribute("form", new CategoryForm(c.getId(), c.getName(),
                c.getParent() != null ? c.getParent().getId() : null));
        model.addAttribute("enabled", c.isEnabled());

        // 수정: 자기 자신을 제외한 부모 후보 목록 전달
        model.addAttribute("parents", adminCategoryService.getParentListExceptMe(id));

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

    @PostMapping("/{id}/edit") // 메서드 명칭과 매핑 주소를 update/edit 계열로 변경
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") CategoryForm form,
                         BindingResult br,
                         Model model) {

        if (br.hasErrors()) {
            model.addAttribute("mode", "EDIT");
            model.addAttribute("categoryId", id);
            model.addAttribute("enabled", adminCategoryService.get(id).isEnabled());
            model.addAttribute("parents", adminCategoryService.getParentList()); // 부모 목록 다시 로드 필수
            return render(model, "카테고리 수정", "admin/categories/form");
        }

        try {
            // 이름(name)만 보내지 않고 form 전체를 보내서 부모 ID까지 처리
            adminCategoryService.update(id, form);
            return "redirect:/admin/categories";
        } catch (IllegalArgumentException e) {
            var c = adminCategoryService.get(id);
            model.addAttribute("mode", "EDIT");
            model.addAttribute("categoryId", id);
            model.addAttribute("enabled", c.isEnabled());
            model.addAttribute("parents", adminCategoryService.getParentList()); // 에러 시에도 목록 필요
            model.addAttribute("error", e.getMessage());
            return render(model, "카테고리 수정", "admin/categories/form");
        }
    }

    @PostMapping("/{id}/enable")
    @ResponseBody
    public ResponseEntity<Void> enable(@PathVariable Long id) {
        adminCategoryService.enable(id);
        return ResponseEntity.ok().build();
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