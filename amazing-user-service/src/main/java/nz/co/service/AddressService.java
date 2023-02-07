package nz.co.service;

import nz.co.model.AddressDO;
import nz.co.request.AddressAddRequest;
import nz.co.model.AddressVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Bruce Zhou
 * @since 2022-12-04
 */
public interface AddressService{
    AddressVO detail(Long id);

    AddressDO add(AddressAddRequest addressAddRequest);

    int delete(Long address_id);

    List<AddressVO> listAll();
}
