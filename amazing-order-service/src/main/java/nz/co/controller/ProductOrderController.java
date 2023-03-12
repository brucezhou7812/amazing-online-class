package nz.co.controller;


import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import nz.co.config.AlipayConfig;
import nz.co.config.PayUrlConfig;
import nz.co.enums.BizCodeEnum;
import nz.co.enums.OrderClientTypeEnum;
import nz.co.enums.OrderPayTypeEnum;
import nz.co.request.GenerateOrderRequest;
import nz.co.service.ProductOrderService;
import nz.co.utils.CommonUtils;
import nz.co.utils.JsonData;
import nz.co.model.ProductOrderVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-01-24
 */
@RestController
@RequestMapping("/api/order/product_order/v1")
@Api(tags = "Product Order Service: Order")
@Slf4j
public class ProductOrderController {
    @Autowired
    private ProductOrderService productOrderService;
    @Autowired
    private PayUrlConfig payUrlConfig;
    @ApiOperation(value = "Generate Order")
    @PostMapping(value = "generate_order")
    public void generateOrder(@ApiParam(value="the request for generating order")@RequestBody GenerateOrderRequest generateOrderRequest, HttpServletResponse response){
        JsonData jsonData = productOrderService.generateOrder(generateOrderRequest);
        if(jsonData.getCode() == 0){
            String payType = generateOrderRequest.getPayType();
            String clientType = generateOrderRequest.getClientType();
            if(payType.equalsIgnoreCase(OrderPayTypeEnum.ALIPAY.name())){
                log.info("PayType is :"+payType);
                if(clientType.equalsIgnoreCase(OrderClientTypeEnum.H5.name())){
                    log.info("ClientType is :"+clientType);
                    writeData(jsonData,response);
                }else{
                    log.info("ClientType is :"+clientType);
                }

            }else if(payType.equalsIgnoreCase(OrderPayTypeEnum.WECHAT.name())){
                log.info("PayType is :"+payType);
            }else{
                log.info("PayType is :"+payType);
            }

        }else{
           log.error("fail to generate order");
        }
    }

    private void writeData(JsonData jsonData, HttpServletResponse response) {
        response.setContentType("text/html;charset=UTF8");
        try {
            PrintWriter writer = response.getWriter();
            writer.write(jsonData.getData().toString());
            writer.flush();
            writer.close();

        } catch (IOException e) {
            log.error("Fail to print html to page");
        }
    }
    @GetMapping("list")
    @ApiOperation("Query the state of the order")
    public JsonData<String> queryOrderStateBySerialNo(@ApiParam("the serial number of the order")@RequestParam("serial_no")String serialNo){
        String state = productOrderService.queryOrderState(serialNo);
        return StringUtils.isBlank(state) ? JsonData.buildResult(BizCodeEnum.ORDER_NOT_EXIST):JsonData.buildSuccess(state);
    }

    @GetMapping("listOrder")
    @ApiOperation("List order")
    public JsonData<ProductOrderVO> queryOrder(@ApiParam(value="current page")@RequestParam(value="page",required = true) int page,
                                               @ApiParam(value="orders in each page")@RequestParam(value="size",required = true) int size,
                                               @ApiParam(value="The state of order")@RequestParam(value="state",required = false)String state){
        return productOrderService.page(page,size,state);
    }
    @GetMapping("testAlipay")
    public void testAlipay(HttpServletResponse response) throws AlipayApiException, IOException {
        HashMap<String,String> content = new HashMap<>();
        String no = CommonUtils.generateUUID();
        content.put("out_trade_no",no);
        content.put("product_code","FAST_INSTANCE_TRADE_PAY");
        content.put("total_amount","100");
        content.put("subject","computer science lesson");
        content.put("timeout_express","5m");
        content.put("body","computer science lesson");
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        request.setBizContent(JSON.toJSONString(content));
        request.setNotifyUrl(payUrlConfig.getAlipayCallbackUrl());
        request.setReturnUrl(payUrlConfig.getAlipaySuccessReturnUrl());
        AlipayTradeWapPayResponse res = AlipayConfig.getInstance().pageExecute(request);
        if(res.isSuccess()){
            log.info("Success to invoke alipay sdk. ");
            String form = res.getBody();
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(form);
            response.getWriter().flush();
            response.getWriter().close();

        }else{
            log.info("Failed to invoke alipay sdk. ");

        }
    }
}

