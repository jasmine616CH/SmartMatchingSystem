package config.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
public class loginUser implements UserDetails {

    /** 账号 */
    private String userName;
    /** 密码（加密后）*/
    private String password;
    /** 权限标识存储容器 */
    private Collection<? extends GrantedAuthority> authorities;
    /** 是否启用 */
    private boolean enabled;
    /** 是否未锁定 */
    private boolean accountNonLocked;
    /** 是否未过期 */
    private boolean accountNonExpired;
    /** 密码是否未过期 */
    private boolean credentialsNonExpired;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }
    @Override
    public boolean isAccountNonExpired() {
        // 返回成员变量，不再写死
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
