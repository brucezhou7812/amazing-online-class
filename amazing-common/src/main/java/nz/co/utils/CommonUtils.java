package nz.co.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import nz.co.component.RabbitRequestAttributes;
import nz.co.constant.ConstantOnlineClass;
import nz.co.enums.BizCodeEnum;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Slf4j
public class CommonUtils {

    private static final Pattern MAIL_PATTERN = Pattern.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
    private static final String CANDIDATE_STRING = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static String generateUUID(){
        return UUID.randomUUID().toString();
    }
    public static long getTimestamp(){
        return System.currentTimeMillis();
    }
    public static String getRandomCode(int length){
        String codeRange = "0123456789";
        StringBuffer stringBuffer = new StringBuffer();
        Random random = new Random();
        for(int i = 0;i < length;i++){
            char c = codeRange.charAt(random.nextInt(9));
            stringBuffer.append(c);
        }
        return stringBuffer.toString();
    }
    public static String getRadomSalt(int length){
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for(int i=0;i<length;i++){
            sb.append(CANDIDATE_STRING.charAt(r.nextInt(CANDIDATE_STRING.length())));
        }
        return sb.toString();
    }
    public static void sendJsonMessage(Object message, HttpServletResponse response){
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json;charset=utf-8");
        try (PrintWriter writer = response.getWriter()){
            String msg = objectMapper.writeValueAsString(message);
            writer.println(msg);
            response.flushBuffer();
        } catch (IOException e) {
            log.warn("Object Parsing fail: "+message);
        }
    }
    /**
     * @param email
     * @return
     */
    public static  boolean isEmail(String email) {
        if (null == email || "".equals(email)) {
            return false;
        }
        Matcher m = MAIL_PATTERN.matcher(email);
        return m.matches();
    }

    /**
     * 暂时未用
     * @param phone
     * @return
     */
    public static boolean isPhone(String phone) {
        if (null == phone || "".equals(phone)) {
            return false;
        }
        Matcher m = PHONE_PATTERN.matcher(phone);
        return m.matches();

    }
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) {
                // "***.***.***.***".length()
                // = 15
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress="";
        }
        return ipAddress;
    }


    /**
     * MD5加密
     * @param data
     * @return
     */
    public static String MD5(String data)  {
        try {
            java.security.MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(data.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }

            return sb.toString().toUpperCase();
        } catch (Exception exception) {
        }
        return null;

    }
    public static boolean setTokenToContextHolder(Message message){
        if(message == null){
            log.warn("setTokenToContextHolder failed: message is null");
            return false;
        }
        String token = message.getMessageProperties().getHeader(ConstantOnlineClass.TOKEN);
        if(StringUtils.isBlank(token)){
            log.warn("setTokenToContextHolder failed: token is null");
            return false;
        }
        RabbitRequestAttributes rra = new RabbitRequestAttributes();
        rra.setAttribute(ConstantOnlineClass.TOKEN,token,0);
        RequestContextHolder.setRequestAttributes(rra);
        return true;
    }
}
