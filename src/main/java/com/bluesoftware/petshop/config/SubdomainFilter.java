package com.bluesoftware.petshop.config;

import com.bluesoftware.petshop.entities.Veterinarian;
import com.bluesoftware.petshop.repositories.VeterinarianRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SubdomainFilter implements Filter {

    private final VeterinarianRepository veterinarianRepository;

    public SubdomainFilter(VeterinarianRepository veterinarianRepository) {
        this.veterinarianRepository = veterinarianRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            String host = ((HttpServletRequest) request).getHeader("Host");
            if (host != null) {
                String subdomain = extractSubdomain(host);
                if (subdomain != null) {
                    veterinarianRepository.findBySlug(subdomain)
                            .ifPresent(TenantContext::setCurrentVet);
                }
            }
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private String extractSubdomain(String host) {
        String lower = host.toLowerCase();

        // Remove port
        if (lower.contains(":")) {
            lower = lower.substring(0, lower.indexOf(":"));
        }

        // Check for localhost - subdomain.localhost
        if (lower.endsWith(".localhost")) {
            String sub = lower.substring(0, lower.length() - ".localhost".length());
            return sub.isEmpty() || sub.contains(".") ? null : sub;
        }

        // Check for custom domain: subdomain.miapp.com
        if (lower.endsWith(".miapp.com") || lower.endsWith(".miapp.com.ar")) {
            String sub = lower.substring(0, lower.indexOf("."));
            return sub.isEmpty() || sub.equals("www") ? null : sub;
        }

        return null;
    }
}
