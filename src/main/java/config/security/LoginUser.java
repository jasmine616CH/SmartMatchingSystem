package config.security;

import lombok.Data;
import module.system.entity.SysUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
public class LoginUser implements UserDetails {

    private SysUser sysUser;

    private List<String> roleKeys = new ArrayList<>();

    public LoginUser(SysUser sysUser) {
        this.sysUser = sysUser;
    }

    /**
     * 组装权限角色信息
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roleKeys == null || roleKeys.isEmpty()) {
            return Collections.emptyList();
        }
        return roleKeys.stream()
                .map(key -> new SimpleGrantedAuthority("ROLE_" + key))
                .toList();
    }

    @Override
    public String getPassword() {
        return sysUser.getPassword();
    }

    @Override
    public String getUsername() {
        return sysUser.getUsername();
    }

    // ========== 账号状态控制 ==========
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // status=1正常；0冻结
        return Integer.valueOf(1).equals(sysUser.getStatus());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Integer.valueOf(1).equals(sysUser.getStatus());
    }
}