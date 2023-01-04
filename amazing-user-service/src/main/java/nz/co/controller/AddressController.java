package nz.co.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nz.co.enums.BizCodeEnum;
import nz.co.exception.BizCodeException;
import nz.co.model.AddressDO;
import nz.co.request.AddressAddRequest;
import nz.co.service.AddressService;
import nz.co.utils.JsonData;
import nz.co.vo.AddressVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Bruce Zhou
 * @since 2022-12-04
 */
@RestController
@Api(tags="Customer Address Module")
@RequestMapping("/api/address/v1")
public class AddressController {
    @Autowired
    private AddressService addressService;
    @ApiOperation("Query customer address according to address id")
    @GetMapping("find/{address_id}")
    public JsonData detail(@ApiParam(value="Address id",required = true)
                                @PathVariable("address_id")Long id) {
        AddressVO addressvO = addressService.detail(id);

        return addressvO!=null ?JsonData.buildSuccess(addressvO):JsonData.buildResult(BizCodeEnum.ADDRESS_NOT_EXIST);
    }
    @ApiOperation("delete address according to address id")
    @DeleteMapping("delete/{address_id}")
    public JsonData delete(@ApiParam(value="Address_id",required = true)
                        @PathVariable("address_id")Long address_id){
        int row = addressService.delete(address_id);
        return row == 1?JsonData.buildSuccess():JsonData.buildResult(BizCodeEnum.ADDRESS_NOT_EXIST);
    }

    @ApiOperation("find all addresses of the specific user")
    @GetMapping("list_all")
    public JsonData listAll(){
        List<AddressVO> addressVOList = addressService.listAll();
        return addressVOList==null || addressVOList.isEmpty() ?JsonData.buildResult(BizCodeEnum.ADDRESS_NOT_EXIST):JsonData.buildSuccess(addressVOList);
    }

    @ApiOperation("add an address to receive goods")
    @PostMapping("add_address")
    public JsonData addAddress(@ApiParam(value="the address to add",required = true) @RequestBody AddressAddRequest addressAddRequest){
        AddressDO addressDO = addressService.add(addressAddRequest);
        return JsonData.buildSuccess(addressDO);
    }
}

