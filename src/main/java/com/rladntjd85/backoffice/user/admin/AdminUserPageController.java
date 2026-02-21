package com.rladntjd85.backoffice.user.admin;

import com.rladntjd85.backoffice.auth.domain.Role;
import com.rladntjd85.backoffice.common.web.admin.BaseAdminController;
import com.rladntjd85.backoffice.user.repository.UserRepository;
import com.rladntjd85.backoffice.user.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserPageController extends BaseAdminController {

    private final UserRepository userRepository;
    private final AdminUserService adminUserService;

    @GetMapping
    public String list(Model model,
                       @RequestParam(defaultValue = "ALL") AdminUserService.Tab tab,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size) {

        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        var result = adminUserService.listUsers(tab, pageable);

        model.addAttribute("page", result);
        model.addAttribute("tab", tab.name());

        return render(model, "사용자 목록", "admin/users/list");
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        return render(model, "사용자 생성", "admin/users/form");
    }

    @PostMapping("/new")
    public String create(@RequestParam String email,
                         @RequestParam String name,
                         @RequestParam(required = false) String password,
                         RedirectAttributes ra) {

        var result = adminUserService.createUser(email, name, password);
        ra.addFlashAttribute("createResult", result);
        return "redirect:/admin/users/new/result";
    }

    @GetMapping("/new/result")
    public String createResult(Model model,
                               @ModelAttribute("createResult") Object createResult) {
        if (createResult == null) return "redirect:/admin/users";
        return render(model, "생성 완료", "admin/users/create-result");
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        var u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + id));
        model.addAttribute("u", u);
        model.addAttribute("enabledAdminCount", userRepository.countByRoleAndEnabledTrue(Role.ADMIN));
        return render(model, "사용자 상세", "admin/users/detail");
    }

    // 이름 수정(ADMIN만 접근)
    @PostMapping("/{id}/name")
    public String changeName(@PathVariable Long id,
                             @RequestParam("name") String name) {
        var u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        // 간단 검증
        if (name == null || name.isBlank() || name.length() > 100) {
            return "redirect:/admin/users/" + id;
        }
        u.changeName(name);
        userRepository.save(u);
        return "redirect:/admin/users/" + id;
    }

    @PostMapping("/{id}/role")
    public String changeRole(@PathVariable Long id,
                             @RequestParam("role") String role,
                             RedirectAttributes ra,
                             jakarta.servlet.http.HttpServletRequest request,
                             org.springframework.security.core.Authentication authentication) {

        try {
            // actorId 구하기(세션 로그인은 email만 있으니 email로 조회)
            Long actorId = userRepository.findByEmail(authentication.getName())
                    .map(u -> u.getId())
                    .orElse(null);

            adminUserService.changeRole(
                    id, role,
                    actorId,
                    request.getRemoteAddr(),
                    request.getHeader("User-Agent")
            );

            ra.addFlashAttribute("msg", "권한이 변경되었습니다.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/users/" + id;
    }

    // 잠금 해제(목록/상세 공통)
    @PostMapping("/{id}/unlock")
    public String unlock(@PathVariable Long id,
                         @RequestParam(defaultValue = "ALL") String tab,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "20") int size) {
        adminUserService.unlock(id);
        return "redirect:/admin/users?tab=" + tab + "&page=" + page + "&size=" + size;
    }

    // enabled 토글(상세에서만)
    @PostMapping("/{id}/enable")
    public String enable(@PathVariable Long id) {
        adminUserService.enable(id);
        return "redirect:/admin/users/" + id;
    }

    @PostMapping("/{id}/disable")
    public String disable(@PathVariable Long id, RedirectAttributes ra) {
        try {
            adminUserService.disable(id);
            ra.addFlashAttribute("msg", "계정이 비활성화되었습니다.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users/" + id;
    }

    @PostMapping("/{id}/reset-password")
    public String resetPassword(@PathVariable Long id,
                                RedirectAttributes ra,
                                jakarta.servlet.http.HttpServletRequest request,
                                org.springframework.security.core.Authentication authentication) {

        Long actorId = userRepository.findByEmail(authentication.getName())
                .map(u -> u.getId())
                .orElse(null);

        var result = adminUserService.resetPassword(
                id,
                actorId,
                request.getRemoteAddr(),
                request.getHeader("User-Agent")
        );

        ra.addFlashAttribute("resetResult", result);
        return "redirect:/admin/users/" + id + "/reset-password/result";
    }

    @GetMapping("/{id}/reset-password/result")
    public String resetPasswordResult(@PathVariable Long id,
                                      Model model,
                                      @ModelAttribute("resetResult") Object resetResult) {
        if (resetResult == null) return "redirect:/admin/users/" + id;
        return render(model, "임시 비밀번호 발급", "admin/users/reset-password-result");
    }
}
