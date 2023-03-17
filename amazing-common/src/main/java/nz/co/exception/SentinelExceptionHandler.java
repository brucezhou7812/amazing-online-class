package nz.co.exception;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import nz.co.enums.BizCodeEnum;
import nz.co.utils.CommonUtils;
import nz.co.utils.JsonData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SentinelExceptionHandler implements BlockExceptionHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws Exception {
        JsonData jsonData = null;
        if(e instanceof FlowException){
            jsonData = JsonData.buildResult(BizCodeEnum.SENTINEL_FLOW_LIMITING);
        }else if(e instanceof DegradeException){
            jsonData = JsonData.buildResult(BizCodeEnum.SENTINEL_DEGRADE);
        }else if(e instanceof ParamFlowException){
            jsonData = JsonData.buildResult(BizCodeEnum.SENTINEL_PARAM_FLOW);
        }else if(e instanceof SystemBlockException){
            jsonData = JsonData.buildResult(BizCodeEnum.SENTINEL_SYSTEM_BLOCK);
        }else if(e instanceof AuthorityException){
            jsonData = JsonData.buildResult(BizCodeEnum.SENTINEL_AUTHORITY_EXCEPTION);
        }
        httpServletResponse.setStatus(200);
        CommonUtils.sendJsonMessage(jsonData,httpServletResponse);
    }
}
