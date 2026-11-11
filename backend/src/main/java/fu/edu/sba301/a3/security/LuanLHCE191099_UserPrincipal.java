package fu.edu.sba301.a3.security;

import lombok.Builder;

@Builder
public record LuanLHCE191099_UserPrincipal(Long id, String email, String name, String role) {}
