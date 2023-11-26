package com.caovy2001.data_everywhere.config;

import com.caovy2001.data_everywhere.constant.ExceptionConstant;
import com.caovy2001.data_everywhere.entity.UserEntity;
import com.caovy2001.data_everywhere.repository.UserRepository;
import com.caovy2001.data_everywhere.utils.JwtUtil;
import com.caovy2001.data_everywhere.utils.ParseObjectUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        UsernamePasswordAuthenticationToken authentication = null;

        try {
            if (StringUtils.isBlank(request.getHeader("Authorization"))) {
                throw new Exception();
            }

            String authorizationHeader = request.getHeader("Authorization");
            if (!authorizationHeader.startsWith("Token ")) {
                throw new Exception();
            }

            String token = authorizationHeader.substring(6);
            if (StringUtils.isBlank(token) || !token.contains(".") || token.split("\\.").length < 2) {
                throw new Exception();
            }

            String tokenUserInfo = token.split("\\.")[1];
            if (StringUtils.isBlank(tokenUserInfo)) {
                throw new Exception();
            }

            String decodedTokenUserInfo = new String(Base64.getUrlDecoder().decode(tokenUserInfo));
            if (StringUtils.isBlank(decodedTokenUserInfo) || !decodedTokenUserInfo.contains("user")) {
                throw new Exception();
            }

            Map<String, Object> mapUser = objectMapper.readValue(decodedTokenUserInfo, Map.class);
            if (mapUser == null || mapUser.get("user") == null) {
                throw new Exception();
            }

            UserEntity userEntity = null;
            if (!decodedTokenUserInfo.contains("token")) { // Token của Data Everywhere
                Map<String, String> mapId = objectMapper.readValue((String) mapUser.get("user"), Map.class);
                if (mapId == null || mapId.get("id") == null) {
                    throw new Exception();
                }

                String tokenToCompare = JwtUtil.generateToken(ParseObjectUtil.objectToJsonString(mapId));
                if (StringUtils.isBlank(tokenToCompare)) {
                    throw new Exception();
                }

                if (!token.equals(tokenToCompare)) {
                    throw new Exception(ExceptionConstant.auth_invalid);
                }

                userEntity = userRepo.findById(mapId.get("id")).orElse(null);
            } else { // Token của chatbot service
                String username = (String) ((LinkedHashMap) mapUser.get("user")).get("username");
                if (StringUtils.isBlank(username)) {
                    throw new Exception();
                }

                userEntity = userRepo.findByUsername(username);
                if (userEntity == null) {
                    userEntity = userRepo.save(UserEntity.builder()
                            .username(username)
                            .fullName((String) ((LinkedHashMap) mapUser.get("user")).get("fullname"))
                            .build());
                }
            }

            if (userEntity != null) {
                userEntity.setPassword(null);
                Collection<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("USER"));
                authentication = new UsernamePasswordAuthenticationToken(userEntity, null,
                        authorities);
            }
            if (authentication != null) {
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        } catch (Exception e) {
            SecurityContextHolder.getContext().setAuthentication(null);
        }

        filterChain.doFilter(request, response);
    }
}
