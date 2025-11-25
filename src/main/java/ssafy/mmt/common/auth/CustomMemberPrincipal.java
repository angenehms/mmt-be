package ssafy.mmt.common.auth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomMemberPrincipal implements UserDetails {

    @Getter
    private final Long memberId;
    private final String username;
    @Getter
    private final String role;
    private final String password; // 필요하면 넣음
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomMemberPrincipal(Long memberId, String username, String role, String password, Collection<? extends GrantedAuthority> authorities) {
        this.memberId = memberId;
        this.username = username;
        this.role = role;
        this.password = password;
        this.authorities = authorities;
    }

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
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

