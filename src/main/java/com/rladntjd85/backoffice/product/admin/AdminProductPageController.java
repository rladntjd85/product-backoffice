package com.rladntjd85.backoffice.product.admin;

import com.rladntjd85.backoffice.common.web.admin.BaseAdminController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/products")
public class AdminProductPageController extends BaseAdminController {

    @GetMapping
    public String list(Model model,
                       @RequestParam(required = false) String q,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) String status,
                       @RequestParam(required = false, defaultValue = "CREATED_DESC") String sort) {

        model.addAttribute("q", q);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("status", status);
        model.addAttribute("sort", sort);

        return render(model, "상품 목록", "admin/products/list");
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("mode", "CREATE");
        return render(model, "상품 등록", "admin/products/form");
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("mode", "EDIT");
        model.addAttribute("productId", id);
        return render(model, "상품 수정", "admin/products/form");
    }

    // 저장(등록/수정) 자리
    @PostMapping
    public String save() {
        // TODO: DTO + Service 연결
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/inactivate")
    public String inactivate(@PathVariable Long id) {
        // TODO: status=INACTIVE
        return "redirect:/admin/products";
    }

    // 삭제(상태변경) 자리 - 실제 삭제 금지 정책이면 status 변경으로 처리 예정
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        // TODO: status=DELETED 같은 soft delete 처리
        return "redirect:/admin/products";
    }
}
