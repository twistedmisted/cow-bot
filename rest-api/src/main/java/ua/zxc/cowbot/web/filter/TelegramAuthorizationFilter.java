package ua.zxc.cowbot.web.filter;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.json.JSONObject;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Component
public class TelegramAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getServletPath().equals("/bot")) {
            filterChain.doFilter(request, response);
        }
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (!request.getMethod().equalsIgnoreCase("get") && securityContext.getAuthentication() == null) {
            JSONObject jsonObject = getJSON(request);
            if (jsonObject != null) {
                if (checkData(jsonObject)) {
                    String username = jsonObject.getString("username");
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null,
                            List.of(new SimpleGrantedAuthority("USER")));
                    securityContext.setAuthentication(auth);
                    HttpSession session = request.getSession(true);
                    session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, securityContext);
                    response.setHeader("Set-Cookie", "JSESSIONID=" + session.getId() + "; Path=/; SameSite=None; Secure;");
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean checkData(JSONObject jsonObject) {
        Map<String, Object> data = jsonObject.toMap();
        String checkHash = (String) data.get("hash");
        data.remove("hash");
        Map<String, Object> sortedData = data.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        String dataString = sortedData.entrySet()
                .stream()
                .map(Objects::toString)
                .collect(Collectors.joining("\n"));
        String botToken = "1281688887:AAEt9BKnOzL57RdQdOfEyqhs4buEgz919A0";
        byte[] secretKey = DigestUtils.sha256(botToken);
        String hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secretKey).hmacHex(dataString);
        if (!hmac.equals(checkHash)) {
            return false;
        }
        Integer authDate = (Integer) sortedData.get("auth_date");
        long nowDate = System.currentTimeMillis() / 1000L;
        return authDate - nowDate <= 86400;
    }

    private JSONObject getJSON(HttpServletRequest request) {
        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            // throw ex;
            return null;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ignored) {

                }
            }
        }

        body = stringBuilder.toString();
        return new JSONObject(body);

    }

}
