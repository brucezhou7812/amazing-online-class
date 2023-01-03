package nz.co.interceptor;

import com.mysql.cj.util.StringUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import nz.co.enums.BizCodeEnum;
import nz.co.model.UserLoginModel;
import nz.co.utils.CommonUtils;
import nz.co.utils.JWTUtils;
import nz.co.utils.JsonData;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserLoginModel> threadLocalUserLoginModel = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = getTokenFromRequest(request);
        log.info("LoginInterceptor.preHandle(): token:"+token);
        if(StringUtils.isNullOrEmpty(token)){
            CommonUtils.sendJsonMessage(JsonData.buildResult(BizCodeEnum.ACCOUNT_UNREGISTER),response);
            return false;
        }else{
            Claims claims = JWTUtils.checkToken(token);
            if(claims != null){
                UserLoginModel userLoginModel = new UserLoginModel();
                Integer id1 = (Integer)claims.get("id");
                String name = (String)claims.get("name");
                String headImg = (String)claims.get("head_img");
                String mail = (String)claims.get("mail");
                userLoginModel.setHeadImg(headImg);
                userLoginModel.setId(Long.valueOf(id1));
                userLoginModel.setMail(mail);
                userLoginModel.setName(name);
                //request.setAttribute("loginUser",userLoginModel);
                threadLocalUserLoginModel.set(userLoginModel);
                return true;
            }else{
                log.info("LoginInterceptor.preHandle(): token is invalid");
                CommonUtils.sendJsonMessage(JsonData.buildResult(BizCodeEnum.ACCOUNT_UNREGISTER),response);
                return false;
            }

        }

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
    private String getTokenFromRequest(HttpServletRequest request){
        String token = request.getHeader("token");
        if(StringUtils.isNullOrEmpty(token)){
            token = request.getParameter("token");
        }
        return token;
    }
}
