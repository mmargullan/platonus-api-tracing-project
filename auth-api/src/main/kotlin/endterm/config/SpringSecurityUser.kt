package endterm.config

import endterm.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class SpringSecurityUser(
    private val user: User
): UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return user.role?.let { role ->
            listOf(SimpleGrantedAuthority("ROLE_${role.toUpperCase()}"))
        } ?: emptyList()
    }

    override fun getPassword(): String = user.password ?: ""
    override fun getUsername(): String = user.login ?: ""
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true

}