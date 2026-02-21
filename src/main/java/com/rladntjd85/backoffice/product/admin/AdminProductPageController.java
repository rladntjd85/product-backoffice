package com.rladntjd85.backoffice.product.admin;

import com.rladntjd85.backoffice.category.domain.Category;
import com.rladntjd85.backoffice.category.repository.CategoryRepository;
import com.rladntjd85.backoffice.common.web.admin.BaseAdminController;
import com.rladntjd85.backoffice.product.domain.Product;
import com.rladntjd85.backoffice.product.dto.ProductForm;
import com.rladntjd85.backoffice.product.service.AdminProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class AdminProductPageController extends BaseAdminController {

    private final AdminProductService adminProductService;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public String list(Model model,
                       @RequestParam(required = false) String q,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false, defaultValue = "") String status,
                       @RequestParam(required = false, defaultValue = "CREATED_DESC") String sort,
                       @RequestParam(defaultValue = "0") int page) {

        Page<?> result = adminProductService.search(q, categoryId, status, sort, page, 20);

        List<Category> categories = categoryRepository.findAll();

        model.addAttribute("result", result);
        model.addAttribute("categories", categories);

        model.addAttribute("q", q);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("status", status);
        model.addAttribute("sort", sort);

        model.addAttribute("totalCount", adminProductService.countAll(q, categoryId));
        model.addAttribute("activeCount", adminProductService.countByStatus(q, categoryId, "ACTIVE"));
        model.addAttribute("hiddenCount", adminProductService.countByStatus(q, categoryId, "HIDDEN"));
        model.addAttribute("soldOutCount", adminProductService.countByStatus(q, categoryId, "SOLD_OUT"));
        model.addAttribute("deletedCount", adminProductService.countByStatus(q, categoryId, "DELETED"));

        return render(model, "상품 목록", "admin/products/list");
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("mode", "CREATE");
        model.addAttribute("form", ProductForm.empty());
        model.addAttribute("categories", categoryRepository.findAll());
        return render(model, "상품 등록", "admin/products/form");
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Product p = adminProductService.getWithCategory(id);

        ProductForm form = new ProductForm();
        form.setName(p.getName());
        form.setCategoryId(p.getCategory().getId());
        form.setPrice(p.getPrice());
        form.setStock(p.getStock());
        form.setStatus(p.getStatus().name());

        model.addAttribute("mode", "EDIT");
        model.addAttribute("productId", id);
        model.addAttribute("form", form);
        model.addAttribute("categories", categoryRepository.findAll());

        model.addAttribute("currentThumbUrl", p.getThumbnailUrl());
        model.addAttribute("currentThumbOriginalName", p.getThumbnailOriginalName());
        model.addAttribute("currentDetailUrl", p.getDetailImageUrl());
        model.addAttribute("currentDetailOriginalName", p.getDetailOriginalName());

        return render(model, "상품 수정", "admin/products/form");
    }

    // 등록
    @PostMapping
    public String create(@Valid @ModelAttribute("form") ProductForm form,
                         BindingResult br,
                         Model model,
                         Authentication auth,
                         HttpServletRequest request) {
        if (br.hasErrors()) {
            model.addAttribute("mode", "CREATE");
            model.addAttribute("categories", categoryRepository.findAll());
            return render(model, "상품 등록", "admin/products/form");
        }

        try {
            adminProductService.create(form, currentUserId(auth), clientIp(request), userAgent(request));
            return "redirect:/admin/products";
        } catch (IllegalArgumentException | MultipartException e) {
            model.addAttribute("mode", "CREATE");
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("error", e.getMessage());
            return render(model, "상품 등록", "admin/products/form");
        }
    }

    // 수정
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") ProductForm form,
                         BindingResult br,
                         Model model,
                         Authentication auth,
                         HttpServletRequest request) {
        if (br.hasErrors()) {
            Product p = adminProductService.getWithCategory(id);

            model.addAttribute("mode", "EDIT");
            model.addAttribute("productId", id);
            model.addAttribute("categories", categoryRepository.findAll());

            model.addAttribute("currentThumbUrl", p.getThumbnailUrl());
            model.addAttribute("currentThumbOriginalName", p.getThumbnailOriginalName());
            model.addAttribute("currentDetailUrl", p.getDetailImageUrl());
            model.addAttribute("currentDetailOriginalName", p.getDetailOriginalName());

            return render(model, "상품 수정", "admin/products/form");
        }

        try {
            adminProductService.update(id, form, currentUserId(auth), clientIp(request), userAgent(request));
            return "redirect:/admin/products";
        } catch (IllegalArgumentException | MultipartException e) {
            Product p = adminProductService.getWithCategory(id);

            model.addAttribute("mode", "EDIT");
            model.addAttribute("productId", id);
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("error", e.getMessage());

            model.addAttribute("currentThumbUrl", p.getThumbnailUrl());
            model.addAttribute("currentThumbOriginalName", p.getThumbnailOriginalName());
            model.addAttribute("currentDetailUrl", p.getDetailImageUrl());
            model.addAttribute("currentDetailOriginalName", p.getDetailOriginalName());

            return render(model, "상품 수정", "admin/products/form");
        }
    }

    @GetMapping("/{id}")
    public String redirectToEdit(@PathVariable Long id) {
        return "redirect:/admin/products/" + id + "/edit";
    }

    // 판매중지: ACTIVE -> HIDDEN
    @PostMapping("/{id}/hide")
    public String hide(@PathVariable Long id, Authentication auth, HttpServletRequest request) {
        adminProductService.hide(id, currentUserId(auth), clientIp(request), userAgent(request));
        return "redirect:/admin/products";
    }

    // 판매재개: HIDDEN -> ACTIVE (stock==0이면 SOLD_OUT)
    @PostMapping("/{id}/unhide")
    public String unhide(@PathVariable Long id, Authentication auth, HttpServletRequest request) {
        adminProductService.unhide(id, currentUserId(auth), clientIp(request), userAgent(request));
        return "redirect:/admin/products";
    }

    // 삭제(상태): -> DELETED (파일은 유지)
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Authentication auth, HttpServletRequest request) {
        adminProductService.softDelete(id, currentUserId(auth), clientIp(request), userAgent(request));
        return "redirect:/admin/products";
    }

    // 썸네일 제거(기본 이미지로)
    @PostMapping("/{id}/thumbnail/remove")
    public String removeThumbnail(@PathVariable Long id, Authentication auth, HttpServletRequest request) {
        adminProductService.removeThumbnail(id, currentUserId(auth), clientIp(request), userAgent(request));
        return "redirect:/admin/products/" + id + "/edit";
    }

    // 상세 제거(기본 이미지로)
    @PostMapping("/{id}/detail/remove")
    public String removeDetail(@PathVariable Long id, Authentication auth, HttpServletRequest request) {
        adminProductService.removeDetailImage(id, currentUserId(auth), clientIp(request), userAgent(request));
        return "redirect:/admin/products/" + id + "/edit";
    }
}