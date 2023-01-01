package nz.co.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nz.co.exception.BizCodeException;
import nz.co.model.AddressDO;
import nz.co.service.AddressService;
import nz.co.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/find/{address_id}")
    public JsonData detail(@ApiParam(value="Address id",required = true)
                                @PathVariable("address_id")Long id) {
        AddressDO addressDO = addressService.detail(id);
        //int i = 1/0;
        if(id == 1){
            throw new BizCodeException(-1,"The address id is 1.");
        }
        return JsonData.buildSuccess(addressDO);
    }
}

