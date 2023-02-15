package com.mouken.modules.security.controller;

import com.mouken.modules.account.Account;
import com.mouken.modules.account.dto.AccountModifyForm;
import com.mouken.modules.account.service.AccountService;
import com.mouken.modules.resource.Resource;
import com.mouken.modules.resource.dto.ResourceCreateForm;
import com.mouken.modules.resource.dto.ResourceModifyForm;
import com.mouken.modules.resource.service.ResourceService;
import com.mouken.modules.role.dto.RoleCreateForm;
import com.mouken.modules.role.dto.RoleModifyForm;
import com.mouken.modules.role.entity.Role;
import com.mouken.modules.role.service.RoleService;
import com.mouken.modules.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminController { // todo refactor: 'resources' on urls to 'resource'

    private final ModelMapper modelMapper;
    private final UrlFilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource;
    private final ResourceService resourceService;
    private final RoleService roleService;
    private final AccountService accountService;

    /**
     * Account
     */
    @GetMapping("/admin")
    public String home() {
        return "admin/home";
    }

    @GetMapping("/admin/accounts")
    public String getAccounts(Model model) {
        List<Account> accountList = accountService.getAccounts();
        model.addAttribute("accounts", accountList);

        return "admin/user/list";
    }

    @PostMapping("/admin/accounts")
    public String modifyAccount(AccountModifyForm accountModifyForm) {
        Long accountId = accountModifyForm.getId();
        accountService.modifyAccount(accountId, accountModifyForm);

        return "redirect:/admin/accounts";
    }

    @GetMapping("/admin/accounts/{id}")
    public String getAccount(@PathVariable Long id, Model model) {
        AccountModifyForm accountModifyForm = accountService.getAccountForm(id);
        List<Role> roleList = roleService.getRoleList();
        model.addAttribute("accountForm", accountModifyForm);
        model.addAttribute("roles", roleList);

        return "admin/user/detail";
    }

    @GetMapping("/admin/accounts/delete/{id}") // todo feat: modify to use DELETE method
    public String deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);

        return "redirect:/admin/users";
    }

    /**
     * Role
     */

    @GetMapping("/admin/roles")
    public String getRoles(Model model) {
        List<Role> roleList = roleService.getRoleList();
        model.addAttribute("roles", roleList);
        return "admin/role/list";
    }

    @GetMapping("/admin/roles/create")
    public String createRole(Model model) { // todo check
        RoleCreateForm roleCreateForm = new RoleCreateForm();
        model.addAttribute("roleForm", roleCreateForm);
        return "admin/role/create";
    }

    @PostMapping("/admin/roles/create")
    public String createRole(RoleCreateForm roleCreateForm) {
        roleService.createRole(roleCreateForm);
        return "redirect:/admin/roles";
    }

    @PostMapping("/admin/roles")
    public String modifyRole(RoleModifyForm roleModifyForm) { // todo check
        Long roleId = roleModifyForm.getId();
        roleService.modifyRole(roleId, roleModifyForm);
        return "redirect:/admin/roles";
    }

    @GetMapping("/admin/roles/{id}")
    public String getRole(@PathVariable Long id, Model model) {
        RoleModifyForm roleModifyForm = roleService.getRoleForm(id);
        model.addAttribute("roleForm", roleModifyForm);

        return "admin/role/detail";
    }

    @GetMapping("/admin/roles/delete/{id}") // todo refactor: 연관된 계정 defalut (ROLE_USER) 로 변경
    public String deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return "redirect:/admin/roles";
    }

    /**
     * Resource
     */

    @GetMapping("/admin/resources")
    public String getResourceList(Model model) {
        List<Resource> resourceList = resourceService.getResourceList();
        model.addAttribute("resources", resourceList);
        return "admin/resource/list";
    }

    @GetMapping("/admin/resources/{id}")
    public String getResource(@PathVariable Long id, Model model) {
        List<Role> roleList = roleService.getRoleList();
        model.addAttribute("roles", roleList);

        ResourceModifyForm resourceModifyForm = resourceService.getResourceForm(id);
        model.addAttribute("resourceForm", resourceModifyForm);

        return "admin/resource/detail";
    }

    @PostMapping("/admin/resources")
    public String modifyResource(ResourceModifyForm resourceModifyForm) {
        Long resourceId = resourceModifyForm.getId();
        resourceService.modifyResource(resourceId, resourceModifyForm);

        return "redirect:/admin/resources";
    }

    @GetMapping("/admin/resources/create")
    public String createResource(Model model) {
        List<Role> roleList = roleService.getRoleList();
        model.addAttribute("roles", roleList);

        ResourceCreateForm resourceCreateForm = new ResourceCreateForm();
        model.addAttribute("resourceForm", resourceCreateForm);

        return "admin/resource/create";
    }

    @PostMapping("/admin/resources/create")
    public String createResource(ResourceCreateForm resourceCreateForm) {
        resourceService.createResource(resourceCreateForm);
        filterInvocationSecurityMetadataSource.reload();

        return "redirect:/admin/resources";
    }

    @GetMapping("/admin/resources/delete/{id}") // todo; to use DELETE method
    public String removeResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        filterInvocationSecurityMetadataSource.reload();

        return "redirect:/admin/resources";
    }
}
